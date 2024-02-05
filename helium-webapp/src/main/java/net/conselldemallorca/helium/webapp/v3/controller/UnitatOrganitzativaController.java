package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
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

import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaFiltreDto;
import net.conselldemallorca.helium.v3.core.api.service.ParametreService;
import net.conselldemallorca.helium.v3.core.api.service.UnitatOrganitzativaService;
import net.conselldemallorca.helium.webapp.v3.command.UnitatOrganitzativaCommand;
import net.conselldemallorca.helium.webapp.v3.command.UnitatOrganitzativaCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.UnitatOrganitzativaCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/unitatOrganitzativa")
public class UnitatOrganitzativaController extends BaseController {
	
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	@Autowired
	private ParametreService parametreService;
	@Autowired
	private PluginHelper pluginHelper;
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "UnitatOrganitzativaController.session.filtre";
	private static final String APP_CONFIGURACIO_ARREL = "app.net.caib.helium.unitats.organitzatives.arrel.codi";
	
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		UnitatOrganitzativaCommand filtreCommand = getFiltreCommand(request);
		List<ParametreDto> parametres = parametreService.findAll();
		for(ParametreDto parametre: parametres) {	
			if("app.net.caib.helium.unitats.organitzatives.arrel.codi".equals(parametre.getCodi())) {
				model.addAttribute("codiUnitatArrel", parametre.getValor());
			} else if("app.net.caib.helium.unitats.organitzatives.data.darrera.sincronitzacio".equals(parametre.getCodi())) {
				model.addAttribute("dataSincronitzacio", parametre.getValor());
			}
		}
		this.modelEstats(model);
		model.addAttribute(filtreCommand);
		return "v3/unitatOrganitzativa";
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
	public String unitatsSuggest(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		String textDecoded = text;
		List<UnitatOrganitzativaDto> unitats = unitatOrganitzativaService
				.findByCodiAndDenominacioFiltre(textDecoded);
		
		String json = "[";
		for (UnitatOrganitzativaDto unitat: unitats) {
			json += "{\"codi\":\"" + unitat.getCodi() + "\", \"nom\":\"" + unitat.getNom()+ "\"},";
		}
		if (json.length() > 1) json = json.substring(0, json.length() - 1);
		json += "]";
		return json;
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
		return "{\"codi\":\"" + unitatDto.getCodi() + "\", \"nom\":\"" + unitatDto.getNom() + "\"}";

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
						ConversioTipusHelper.convertir(filtreCommand, UnitatOrganitzativaFiltreDto.class),
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
		return "v3/unitatOrganitzativaForm";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@Validated(Creacio.class) UnitatOrganitzativaCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/unitatOrganitzativaForm";
        } else {
    		unitatOrganitzativaService.create(
    				ConversioTipusHelper.convertir(
    						command,
    						UnitatOrganitzativaDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/unitatOrganitzativa",
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
				ConversioTipusHelper.convertir(
						dto,
						UnitatOrganitzativaCommand.class));
		return "v3/unitatOrganitzativaForm";
	}
	
	@RequestMapping(value = "/{unitatOrganitzativaId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long unitatOrganitzativaId,
			@Validated(Modificacio.class) UnitatOrganitzativaCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/unitatOrganitzativaForm";
        } else {
//        	command.setId(unitatOrganitzativaId);
        	unitatOrganitzativaService.update(
        			ConversioTipusHelper.convertir(
    						command,
    						UnitatOrganitzativaDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/unitatOrganitzativa",
					"unitat.organitzativa.controller.modificat");
        }
	}
	
	@RequestMapping(value = "/synchronizeGet", method = RequestMethod.GET)
	public String synchronizeGet(
			HttpServletRequest request,
			Model model) {
	
		MultiMap splitMap = new MultiHashMap();
		MultiMap mergeOrSubstMap = new MultiHashMap();
		MultiMap mergeMap = new MultiHashMap();
		MultiMap substMap = new MultiHashMap();
		List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<UnitatOrganitzativaDto>();
		List<UnitatOrganitzativaDto> unitatsVigentsFirstSincro = new ArrayList<UnitatOrganitzativaDto>();
		List<UnitatOrganitzativaDto> unitatsNew = new ArrayList<UnitatOrganitzativaDto>();
		ParametreDto parametreArrel = parametreService.findByCodi(APP_CONFIGURACIO_ARREL);
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
				        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
				
			}	
		}
	
		model.addAttribute("isFirstSincronization", isFirstSincronization);
		model.addAttribute("unitatsVigentsFirstSincro", unitatsVigentsFirstSincro);
		
		model.addAttribute("splitMap", splitMap);
		model.addAttribute("mergeMap", mergeMap);
		model.addAttribute("substMap", substMap);
		model.addAttribute("unitatsVigents", unitatsVigents);
		model.addAttribute("unitatsNew", unitatsNew);
		
		
		return "v3/synchronizationPrediction";
	}
	
	@RequestMapping(value = "/saveSynchronize", method = RequestMethod.POST)
	public String synchronizePost(
			HttpServletRequest request) {
		ParametreDto parametreArrel = parametreService.findByCodi(APP_CONFIGURACIO_ARREL);
		UnitatOrganitzativaDto unitatDto = unitatOrganitzativaService.findByCodi(parametreArrel.getValor());
		unitatOrganitzativaService.synchronize(unitatDto.getId());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:unitatOrganitzativa",
				"unitat.controller.synchronize.ok");
	}
	
	
	@RequestMapping(value = "/mostrarArbre", method = RequestMethod.GET)
	public String mostrarArbre(
			HttpServletRequest request,
			Model model) {
		ParametreDto parametreArrel = parametreService.findByCodi(APP_CONFIGURACIO_ARREL);
		UnitatOrganitzativaDto unitatDto = unitatOrganitzativaService.findByCodi(parametreArrel.getValor());
		
		model.addAttribute(
				"arbreUnitatsOrganitzatives",
				unitatOrganitzativaService.findTree(unitatDto.getId()));
		
		return "v3/unitatArbre";
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
