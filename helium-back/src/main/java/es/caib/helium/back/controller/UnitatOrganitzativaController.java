package es.caib.helium.back.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.back.command.UnitatOrganitzativaCommand;
import es.caib.helium.back.command.UnitatOrganitzativaCommand.Creacio;
import es.caib.helium.back.command.UnitatOrganitzativaCommand.Modificacio;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.PaisDto;
import es.caib.helium.commons.dto.ParametreDto;
import es.caib.helium.commons.dto.ParellaCodiValorDto;
import es.caib.helium.commons.dto.ProvinciaDto;
import es.caib.helium.commons.dto.UnitatOrganitzativaDto;
import es.caib.helium.commons.dto.UnitatOrganitzativaEstatEnumDto;
import es.caib.helium.commons.dto.UnitatOrganitzativaFiltreDto;
import es.caib.helium.logic.intf.service.DadesExternesService;
import es.caib.helium.logic.intf.service.ParametreService;
import es.caib.helium.logic.intf.service.UnitatOrganitzativaService;
import es.caib.helium.service.helper.PluginHelper;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/unitatOrganitzativa")
public class UnitatOrganitzativaController extends BaseController {
	
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	@Autowired
	private DadesExternesService dadesExternesService;
	@Autowired
	private ParametreService parametreService;
	@Autowired
	private PluginHelper pluginHelper;
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "UnitatOrganitzativaController.session.filtre";
	
	private static  List<PaisDto> paisos = null;
	private static  List<ProvinciaDto> provincies = null;
	
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		UnitatOrganitzativaCommand filtreCommand = getFiltreCommand(request);
		
		ParametreDto param = parametreService.findByCodi(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
		model.addAttribute("codiUnitatArrel", param != null ? param.getValor() : "-" );
		param = parametreService.findByCodi(ParametreService.APP_CONFIGURACIO_DATA_SINCRONITZACIO_UO);
		model.addAttribute("dataSincronitzacio", param != null ? param.getValor() : "-" );
		param = parametreService.findByCodi(ParametreService.APP_CONFIGURACIO_DATA_ACTUALITZACIO_UO);
		model.addAttribute("dataActualitzacio", param != null ? param.getValor() : "-");
		
		this.modelEstats(model);
		model.addAttribute(filtreCommand);
		return "unitatOrganitzativa";
	}
	
	
	/** Posa els valors de l'enumeració estats en el model */
	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(UnitatOrganitzativaEstatEnumDto estat : UnitatOrganitzativaEstatEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					estat.name(),
					MessageHelper.getInstance().getMessage("unitat.organitzativa.estat.enum." + estat.name())));		

		model.addAttribute("estats", opcions);
	}

	@RequestMapping(value = "/suggest/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public List<Map<String, String>> unitatsSuggest(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		String textDecoded = null;
		try {
			textDecoded = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + textDecoded + ": " + e.getMessage());
		}
		List<UnitatOrganitzativaDto> unitats = unitatOrganitzativaService.findByCodiAndDenominacioFiltre(textDecoded);
		List<Map<String, String>> resposta = new ArrayList<Map<String, String>>();
		if (unitats != null && !unitats.isEmpty() && textDecoded!=null) {
			for (UnitatOrganitzativaDto unitat: unitats) {
				Map<String, String> unitatJson = new HashMap<String, String>();
				String noVigent = "V".equals(unitat.getEstat()) ? "" : " " + getMessage(request, "unitat.controller.suggest.uo.no_vigent");
				unitatJson.put("codi", unitat.getCodi());
				unitatJson.put("nom", unitat.getCodiAndNom().replace("\"", "\\\"") + noVigent);
				unitatJson.put("estat", getMessage(request, "expedient.tipus.metadades.nti.unitat.organitzativa.estat." + unitat.getEstat()));
				resposta.add(unitatJson);
			}
		} else {
			Map<String, String> unitatJson = new HashMap<String, String>();
			unitatJson.put("codi", textDecoded);
			unitatJson.put("nom", textDecoded + " (No trobat en l'arbre intern d'unitats organitzatives)");
			unitatJson.put("estat", getMessage(request, "expedient.tipus.metadades.nti.unitat.organitzativa.estat.E"));
			resposta.add(unitatJson);
		}
		return resposta;
	}

	@RequestMapping(value = "/suggestInici/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String unitatsSuggestInici(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
	
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + text + ": " + e.getMessage());
		}
		UnitatOrganitzativaDto unitatDto = unitatOrganitzativaService.findByCodi(decodedToUTF8);
		if(unitatDto!=null)
			return "{\"codi\":\"" + unitatDto.getCodi() + "\", \"nom\":\"" + unitatDto.getCodiAndNom() + "\"}";
		else
			return "{\"codi\":\"" + decodedToUTF8 + "\", \"nom\":\"" +decodedToUTF8  + " (No trobat en l'arbre intern d'unitats organitzatives)"+ "\"}";

	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid UnitatOrganitzativaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:unitatOrganitzativa";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		UnitatOrganitzativaCommand filtreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				unitatOrganitzativaService.findAmbFiltrePaginat(
						ConversioTipus.convertir(filtreCommand, UnitatOrganitzativaFiltreDto.class),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");		
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(Model model) {
		return get(null, model);
	}
	@RequestMapping(value = "/{unitatOrganitzativaId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long unitatOrganitzativaId,
			Model model) {
		UnitatOrganitzativaDto unitatOrganitzativa = null;
		if (unitatOrganitzativaId != null)
			unitatOrganitzativa = unitatOrganitzativaService.findById(unitatOrganitzativaId);
		if (unitatOrganitzativa != null) {
			model.addAttribute(UnitatOrganitzativaCommand.asCommand(unitatOrganitzativa));
		} else {
			UnitatOrganitzativaCommand unitatOrganitzativaCommand = new UnitatOrganitzativaCommand();
			model.addAttribute(unitatOrganitzativaCommand);
		}
		return "unitatOrganitzativaForm";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@Validated(Creacio.class) UnitatOrganitzativaCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "unitatOrganitzativaForm";
        } else {
    		unitatOrganitzativaService.create(
    				ConversioTipus.convertir(
    						command,
    						UnitatOrganitzativaDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/unitatOrganitzativa",
					"unitat.organitzativa.controller.creat");
        }
	}
	
	@RequestMapping(value = "/{unitatOrganitzativaId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long unitatOrganitzativaId,
			Model model) {
		UnitatOrganitzativaDto dto = unitatOrganitzativaService.findById(
				unitatOrganitzativaId);
		model.addAttribute(
				ConversioTipus.convertir(
						dto,
						UnitatOrganitzativaCommand.class));
		return "unitatOrganitzativaForm";
	}
	
	@RequestMapping(value = "/{unitatOrganitzativaId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long unitatOrganitzativaId,
			@Validated(Modificacio.class) UnitatOrganitzativaCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "unitatOrganitzativaForm";
        } else {
//        	command.setId(unitatOrganitzativaId);
        	unitatOrganitzativaService.update(
        			ConversioTipus.convertir(
    						command,
    						UnitatOrganitzativaDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/unitatOrganitzativa",
					"unitat.organitzativa.controller.modificat");
        }
	}
	
	@RequestMapping(value = "/synchronizeGet", method = RequestMethod.GET)
	public String synchronizeGet(
			HttpServletRequest request,
			Model model) {
	
		try {
			MultiHashMap splitMap = new MultiHashMap();
			MultiHashMap mergeOrSubstMap = new MultiHashMap();
			MultiHashMap mergeMap = new MultiHashMap();
			MultiHashMap substMap = new MultiHashMap();
			List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<UnitatOrganitzativaDto>();
			List<UnitatOrganitzativaDto> unitatsVigentsFirstSincro = new ArrayList<UnitatOrganitzativaDto>();
			List<UnitatOrganitzativaDto> unitatsNew = new ArrayList<UnitatOrganitzativaDto>();
			ParametreDto parametreArrel = parametreService.findByCodi(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
			UnitatOrganitzativaDto unitatDto = unitatOrganitzativaService.findByCodi(parametreArrel.getValor());
			boolean isFirstSincronization = unitatDto==null;
			if(unitatDto==null) {
				 unitatDto =pluginHelper.findUnidad(
						 parametreArrel.getValor(),
						new Timestamp(System.currentTimeMillis()), 
						new Timestamp(System.currentTimeMillis()));
				unitatOrganitzativaService.create(unitatDto);
			}

			if(isFirstSincronization){
				unitatsVigentsFirstSincro = unitatOrganitzativaService.predictFirstSynchronization(unitatDto.getId());
			} else {
				try {
					
		            //Getting list of unitats that are now vigent in db but syncronization is marking them as obsolete
					List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = unitatOrganitzativaService
							.getObsoletesFromWS(unitatDto.getId());
		
					// differentiate between split and (subst or merge)
					for (UnitatOrganitzativaDto vigentObsolete : unitatsVigentObsoleteDto) {
						if (vigentObsolete.getLastHistoricosUnitats().size() > 1) {
							for (UnitatOrganitzativaDto hist : vigentObsolete.getLastHistoricosUnitats()) {
								splitMap.put(vigentObsolete, hist);
							}
						} else if (vigentObsolete.getLastHistoricosUnitats().size() == 1) {
							// check if the map already contains key with this codi
							UnitatOrganitzativaDto mergeOrSubstKeyWS = vigentObsolete.getLastHistoricosUnitats().get(0);
							UnitatOrganitzativaDto keyWithTheSameCodi = null;
							Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
							for (UnitatOrganitzativaDto mergeOrSubstKeyMap : keysMergeOrSubst) {
								if (mergeOrSubstKeyMap.getCodi().equals(mergeOrSubstKeyWS.getCodi())) {
									keyWithTheSameCodi = mergeOrSubstKeyMap;
								}
							}
							// if it contains already key with the same codi, assign
							// found key
							if (keyWithTheSameCodi != null) {
								mergeOrSubstMap.put(keyWithTheSameCodi, vigentObsolete);
							} else {
								mergeOrSubstMap.put(mergeOrSubstKeyWS, vigentObsolete);
							}
						}
					}
		
		
					// differantiate between substitution and merge
					Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
					for (UnitatOrganitzativaDto mergeOrSubstKey : keysMergeOrSubst) {
						List<UnitatOrganitzativaDto> values = (List<UnitatOrganitzativaDto>) mergeOrSubstMap
								.get(mergeOrSubstKey);
						if (values.size() > 1) {
							for (UnitatOrganitzativaDto value : values) {
								mergeMap.put(mergeOrSubstKey, value);
							}
						} else {
							substMap.put(mergeOrSubstKey, values.get(0));
						}
					}
		
					// Getting list of unitats that are now vigent in db and in syncronization are also vigent but with properties changed
					unitatsVigents = unitatOrganitzativaService
							.getVigentsFromWebService(unitatDto.getId());
					
					
					// Getting list of unitats that are totally new (doesnt exist in database)
					unitatsNew = unitatOrganitzativaService
							.getNewFromWS(unitatDto.getId());
						
				} catch(Exception ex) {
					String missatgeError = "unitat.controller.synchronize.error";
					logger.warn(missatgeError);
					MissatgesHelper.error(
										request,
										getMessage(request, missatgeError) + " : " + 
											(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()),
										ex);
					
				}	
			}
		
			model.addAttribute("isFirstSincronization", isFirstSincronization);
			model.addAttribute("unitatsVigentsFirstSincro", unitatsVigentsFirstSincro);
			
			model.addAttribute("splitMap", splitMap);
			model.addAttribute("mergeMap", mergeMap);
			model.addAttribute("substMap", substMap);
			model.addAttribute("unitatsVigents", unitatsVigents);
			model.addAttribute("unitatsNew", unitatsNew);			
		} catch(Exception e) {
			String errMsg = "Error construint la predicció de la sincronització: " + e.toString();
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg, e);
		}
		return "synchronizationPrediction";
	}
	
	@RequestMapping(value = "/saveSynchronize", method = RequestMethod.POST)
	public String synchronizePost(
			HttpServletRequest request) {
		String ret = "redirect:" + request.getHeader("Referer");
		try {
			ParametreDto parametreArrel = parametreService.findByCodi(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
			UnitatOrganitzativaDto unitatDto = unitatOrganitzativaService.findByCodi(parametreArrel.getValor());
			unitatOrganitzativaService.synchronize(unitatDto.getId());
			MissatgesHelper.success(request, getMessage(request, "unitat.controller.synchronize.ok"));
			ret = modalUrlTancar();
		} catch(Exception e) {
			String errMsg = getMessage(request, "unitat.controller.synchronize.ko", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg, e);
		}
		return ret;
	}
	
	
	@RequestMapping(value = "/mostrarArbre", method = RequestMethod.GET)
	public String mostrarArbre(
			HttpServletRequest request,
			Model model) {
		try {
			ParametreDto parametreArrel = parametreService.findByCodi(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
			UnitatOrganitzativaDto unitatDto = unitatOrganitzativaService.findByCodi(parametreArrel.getValor());
			
			if (unitatDto != null) {
				model.addAttribute(
						"arbreUnitatsOrganitzatives",
						unitatOrganitzativaService.findTree(unitatDto.getId()));
			} else {
				MissatgesHelper.warning(request, "No s'ha trobat la unitat arrel, provi a sincronitzar l'arbre primer");
			}
		} catch(Exception e) {
			String errMsg = "Error mostrant l'arbre d'unitats organtizatives: " + e.toString();
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg, e);
		}
		return "unitatArbre";
	}
	


	@RequestMapping(value = "/{unitatOrganitzativaId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long unitatOrganitzativaId) {
		unitatOrganitzativaService.delete(unitatOrganitzativaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../unitatOrganitzativa",
				"unitat.organitzativa.controller.esborrat.ok");
	}
	
	@RequestMapping(value = "/{unitatOrganitzativaId}/info", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long unitatOrganitzativaId,
			Model model) {
		try {
			UnitatOrganitzativaDto unitatDto = unitatOrganitzativaService.findById(unitatOrganitzativaId);
			UnitatOrganitzativaDto unitatArrel = unitatOrganitzativaService.findByCodi(unitatDto.getCodiUnitatArrel());
			UnitatOrganitzativaDto unitatSuperior = unitatOrganitzativaService.findByCodi(unitatDto.getCodiIDenominacioUnitatSuperior());

			model.addAttribute("unitatOrganitzativaDto", unitatDto);
			model.addAttribute("unitatArrel", unitatArrel);
			model.addAttribute("unitatSuperior", unitatSuperior);
			
			
			if(paisos==null || paisos.isEmpty()) {
				paisos = dadesExternesService.findPaisos();
			} 
			
			if(unitatDto.getNomPais()==null || unitatDto.getNomPais().isEmpty()){
				for(PaisDto pais: paisos ) {
					if(pais.getCodi().equals(unitatDto.getCodiPais())) {
						unitatDto.setNomPais(pais.getNom());
						break;
					}
				}
			}
			
			if(provincies==null || provincies.isEmpty()) {
				provincies=dadesExternesService.findProvincies();
			} 
			
			if(unitatDto.getNomProvincia()==null || unitatDto.getNomProvincia().isEmpty()){
				for(ProvinciaDto provincia:provincies) {
					if(provincia.getCodi().equals(unitatDto.getCodiProvincia())) {
						unitatDto.setNomProvincia(provincia.getNom());
						break;
					}
				}
			}
			
		} catch(Exception e) {
			String errMsg = "Error obtenint la informació de la unitat organitzativa: " +unitatOrganitzativaId +" " + e.toString();
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg, e);
		}
		return "unitatOrganitzativaInfo";
	}
	

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	/** MÃ¨tode per obtenir o inicialitzar el filtre del formulari de cerca.
	 * 
	 * @param request
	 * @return
	 */
	private UnitatOrganitzativaCommand getFiltreCommand(
			HttpServletRequest request) {
		UnitatOrganitzativaCommand filtreCommand = (UnitatOrganitzativaCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new UnitatOrganitzativaCommand();
			filtreCommand.setEstat(UnitatOrganitzativaEstatEnumDto.VIGENTE);		
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusController.class);

}
