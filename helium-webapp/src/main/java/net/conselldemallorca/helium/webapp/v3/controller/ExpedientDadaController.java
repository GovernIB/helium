/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

/**
 * Controlador per a la pipella de dades de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientDadaController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientDadaService expedientDadaService;



	@RequestMapping(value = "/{expedientId}/dada")
	public String dades(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"dades");
		}
		omplirModelPipellaDades(
				expedientId,
				false,
				model);
		return "v3/expedientDades";
	}
	@RequestMapping(value = "/{expedientId}/dadaAmbOcults")
	public String dadesAmbOcults(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"dades");
		}
		omplirModelPipellaDades(
				expedientId,
				true,
				model);
		return "v3/expedientDades";
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada")
	public String dadesProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@RequestParam(value = "ambOcults", required = false) Boolean ambOcults,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		model.addAttribute("expedient", expedient);
		ambOcults = (ambOcults == null || !expedient.isPermisAdministration()) ? false : ambOcults;
		model.addAttribute("ambOcults", ambOcults);
		Map<CampAgrupacioDto, List<ExpedientDadaDto>> dades = getDadesInstanciaProces(expedientId,procesId,ambOcults);
		model.addAttribute("dades",dades);
		int contadorTotals = 0;
		if(dades != null)
			for(List<ExpedientDadaDto> list: dades.values()){
				contadorTotals += list.size();
			}
		model.addAttribute("contadorTotals", contadorTotals);
		model.addAttribute("procesId",procesId);
		return "v3/procesDades";
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean dadaBorrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			Model model) {
		try {
			expedientDadaService.delete(
					expedientId,
					procesId,
					URLDecoder.decode(varCodi, "UTF-8"));
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"info.dada.proces.esborrada"));
			return true;
		} catch (PermisDenegatException ex) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.info.permis.no"));
		} catch (Exception ex) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.dada.borrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la variable '" + varCodi + "' de l'expedient amb id '" + expedientId + "' (proces: " + procesId + ")", ex);
		}
		return false;
	}

	@ModelAttribute("modificarVariableCommand")
	public Object populateCommand(
			HttpServletRequest request,
			Long expedientId,
			String procesId,
			String varCodi,			
			Model model) {
		if (procesId != null && !"".equals(procesId) && varCodi != null && !"".equals(varCodi)) {
			try {
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
				List<TascaDadaDto> llistTasca = new ArrayList<TascaDadaDto>();
				TascaDadaDto tascaDada = TascaFormHelper.getTascaDadaDtoFromExpedientDadaDto(
						expedientDadaService.findOnePerInstanciaProces(
								expedientId,
								procesId,
								varCodi));
				if (tascaDada.getError() != null)
					MissatgesHelper.error(request, tascaDada.getError());
				llistTasca.add(tascaDada);
				model.addAttribute("procesId", procesId);
				model.addAttribute("varCodi", varCodi);
				model.addAttribute("dada", tascaDada);
				return TascaFormHelper.getCommandForCamps(
						llistTasca,
						null,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
			} catch (Exception ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut obtenir la informació de la dada " + varCodi + ": "  + ex.getMessage(), ex);
			} 
		}
		return null;
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/update", method = RequestMethod.GET)
	public String modificarVariablesGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			Model model) {
		Object command;
		try {
			command = populateCommand(
					request,
					expedientId,
					procesId,
					URLDecoder.decode(varCodi,"UTF-8"),
					model);
			model.addAttribute("modificarVariableCommand", command);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		
		return "v3/expedientDadaModificar";
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/update", method = RequestMethod.POST)
	public String dadaEditar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			@Valid @ModelAttribute("modificarVariableCommand") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		try {
			List<TascaDadaDto> tascaDades = new ArrayList<TascaDadaDto>();
			TascaDadaDto tascaDada = TascaFormHelper.getTascaDadaDtoFromExpedientDadaDto(
					expedientDadaService.findOnePerInstanciaProces(
							expedientId,
							procesId,
							varCodi));
			tascaDades.add(tascaDada);
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDades, command, false);
			TascaFormValidatorHelper validator = new TascaFormValidatorHelper(
					expedientService,
					tascaDades);
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			Object commandValidar = TascaFormHelper.getCommandForCamps(
					tascaDades,
					variables,
					campsAddicionals,
					campsAddicionalsClasses,
					false);
			validator.setValidarExpresions(false);
			validator.setValidarObligatoris(true);
			validator.validate(commandValidar, result);
			if (result.hasErrors()) {
				return "v3/expedientDadaModificar";
			}
			expedientDadaService.update(
					expedientId,
					procesId,
					varCodi,
					variables.get(varCodi));
			MissatgesHelper.success(request, getMessage(request, "info.dada.proces.modificada") );
		} catch (PermisDenegatException ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.info.permis.no") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.dada.modificar.error"));
			logger.error("S'ha produit un error al intentar modificar la variable '" + varCodi + "' de l'expedient amb id '" + expedientId + "' (proces: " + procesId + ")", ex);
		}
		return modalUrlTancar(false);
	}	
	
	/** Cas en que s'edita una dada de tipus acció i es prem sobre l'acció*/
	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/update/accio", method = RequestMethod.POST)
	public String dadaEditarAccio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			@RequestParam(value = "accioCamp", required = true) String accioCamp,
			@Valid @ModelAttribute("modificarVariableCommand") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {

		this.executarDadaAccio(request, expedientId, procesId, varCodi, accioCamp);
		
		// Redirigeix al formulari després d'executar l'acció
		model.asMap().clear();
		return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + procesId + "/dada/" + varCodi + "/update";
	}		

	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> valors12(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i=0; i <= 12 ; i++)		
			resposta.add(new ParellaCodiValorDto(String.valueOf(i), i));
		return resposta;
	}

	@ModelAttribute("addVariableCommand")
	public Object populateAddCommand(
			HttpServletRequest request,
			Long expedientId,
			String procesId,
			String varCodi,
			String codi,
			String valor,
			Model model) {
		if (procesId != null && !"".equals(procesId)) {
			try {
				model.addAttribute("procesId", procesId);
				model.addAttribute("varCodi", varCodi);
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				campsAddicionals.put("codi", codi);
				campsAddicionals.put("valor", valor);
				campsAddicionals.put("varCodi", varCodi);
				Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
				campsAddicionalsClasses.put("codi", String.class);
				campsAddicionalsClasses.put("valor", String.class);
				campsAddicionalsClasses.put("varCodi", String.class);
				List<TascaDadaDto> llistTasca = new ArrayList<TascaDadaDto>();
				if (varCodi != null && !"".equals(varCodi)) {
					TascaDadaDto tascaDada = TascaFormHelper.getTascaDadaDtoFromExpedientDadaDto(
							expedientDadaService.findOnePerInstanciaProces(
									expedientId,
									procesId,
									varCodi));
					llistTasca.add(tascaDada);
					model.addAttribute("dada", tascaDada);
				}
				return TascaFormHelper.getCommandBuitForCamps(
						llistTasca,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
			} catch (Exception ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut obtenir la informació de la dada " + varCodi + ": "  + ex.getMessage(), ex);
			} 
		}
		return null;
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/new", method = RequestMethod.GET)
	public String novaDadaGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			Model model) {
		return novaDadaAmbCodiGet(
				request,
				expedientId,
				procesId,
				null,
				model);
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/new", method = RequestMethod.GET)
	public String novaDadaAmbCodiGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			Model model) {
		model.addAttribute("camps", getCampsNoUtilitzats(expedientId, procesId));
		model.addAttribute(
				"addVariableCommand",
				populateAddCommand(
						request,
						expedientId,
						procesId,
						varCodi,
						null,
						null,
						model));
		if (varCodi == null)
			return "v3/expedientDadaNova";
		return "v3/expedientDadaNova";
	}
	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/new", method = RequestMethod.POST)
	public String novaDadaAmbCodiPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			@Valid @ModelAttribute("addVariableCommand") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		try {			
			if ("Buit".equals(varCodi)) {
				result.rejectValue(
						"varCodi",
						"expedient.nova.data.camp.variable.buit");
			} else if ("String".equals(varCodi)) { // Variable nova tipus String
				String codi = (String)PropertyUtils.getSimpleProperty(command, "codi");
				String valor = (String)PropertyUtils.getSimpleProperty(command, "valor");
				// Validam que el nom de la variable no comenci per majúscula seguida de minúscula
				if (codi == null) {
					result.rejectValue("codi", "error.camp.codi.buit");
				} else {
					if (codi.matches("^[A-Z]{1}[a-z]{1}.*")) {
						result.rejectValue("codi", "error.camp.codi.maymin");
					}
					if (codi.contains(".")) {
						result.rejectValue("codi", "error.camp.codi.char.nok");
					}	
					if (codi.contains(" ")) {
						result.rejectValue("codi", "error.camp.codi.char.espai");
					}	
					if (!result.hasErrors()) {
						expedientDadaService.create(
								expedientId,
								procesId,
								codi,
								valor);
					}
				}
			} else { // Variable de la definició de procés
				List<TascaDadaDto> tascaDades = new ArrayList<TascaDadaDto>();
				TascaDadaDto tascaDada = TascaFormHelper.getTascaDadaDtoFromExpedientDadaDto(
						expedientDadaService.findOnePerInstanciaProces(
								expedientId,
								procesId,
								varCodi));
				tascaDades.add(tascaDada);
				Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDades, command, false);
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
				Object commandValidar = TascaFormHelper.getCommandForCamps(
						tascaDades,
						variables,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
				TascaFormValidatorHelper validator = new TascaFormValidatorHelper(
						expedientService,
						tascaDades);
				validator.setValidarExpresions(false);
				validator.setValidarObligatoris(true);
				validator.validate(commandValidar, result);				
				if (!result.hasErrors()) {
					expedientDadaService.create(
							expedientId,
							procesId,
							varCodi,
							variables.get(varCodi));
				}
			}
			if (result.hasErrors()) {
				model.addAttribute("camps", getCampsNoUtilitzats(expedientId, procesId));
				return "v3/expedientDadaNova";
			}
			MissatgesHelper.success(request, getMessage(request, "info.dada.nova.proces.creada") );
		} catch (PermisDenegatException ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.info.permis.no") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.dada.modificar.error"));
			logger.error("S'ha produit un error al intentar modificar la variable '" + varCodi + "' de l'expedient amb id '" + expedientId + "' (proces: " + procesId + ")", ex);
		}
		return modalUrlTancar(false);
	}

	/** Cas en que es prem el botó acció en el cas que hi hagi una variable acció. */
	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/new/accio")
	public String novaDadaAmbCodiPostAccio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			@RequestParam(value = "accioCamp", required = true) String accioCamp,
			@Valid @ModelAttribute("addVariableCommand") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		
		this.executarDadaAccio(request, expedientId, procesId, varCodi, accioCamp);

		// Redirigeix al formulari després d'executar l'acció
		model.asMap().clear();
		return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + procesId + "/dada/" + varCodi + "/new";
	}

	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}



	private void omplirModelPipellaDades(
			Long expedientId,
			boolean ambOcults,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		// Obtenim l'arbre de processos, per a poder mostrar la informació de tots els processos
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, Map<CampAgrupacioDto, List<ExpedientDadaDto>>> dades = new LinkedHashMap<InstanciaProcesDto, Map<CampAgrupacioDto,List<ExpedientDadaDto>>>();
		Map<InstanciaProcesDto,Integer> totalsPerProces = new LinkedHashMap<InstanciaProcesDto, Integer>();
		// Per a cada instància de procés ordenem les dades per agrupació  
		// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			Map<CampAgrupacioDto, List<ExpedientDadaDto>> dadesInstancia = null;
			int contadorTotals = 0;
			if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				dadesInstancia = getDadesInstanciaProces(
						expedientId,
						instanciaProces.getId(),
						ambOcults);
				if (dadesInstancia != null)
					for(List<ExpedientDadaDto> list: dadesInstancia.values()){
						contadorTotals += list.size();
					}
			}
			dades.put(instanciaProces, dadesInstancia);
			totalsPerProces.put(instanciaProces, contadorTotals);
		}
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("expedient", expedient);
		model.addAttribute("arbreProcessos", arbreProcessos);
		model.addAttribute("dades", dades);
		model.addAttribute("ambOcults", !expedient.isPermisAdministration() ? false : ambOcults);
		model.addAttribute("totalsPerProces", totalsPerProces);
	}

	private List<CampDto> getCampsNoUtilitzats(Long expedientId, String procesInstanceId) {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesInstanceId);
		List<CampDto> campsNoUtilitzats = new ArrayList<CampDto>();
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		List<CampDto> camps = dissenyService.findCampsOrdenatsPerCodi(
					expedient.getTipus().getId(),
					instanciaProces.getDefinicioProces().getId(),
					true // amb herencia
				);
		List<ExpedientDadaDto> dadesInstancia = expedientDadaService.findAmbInstanciaProces(
				expedientId,
				procesInstanceId);
		if (dadesInstancia != null) {
			Collections.sort(
				dadesInstancia, 
				new Comparator<ExpedientDadaDto>() {
					public int compare(ExpedientDadaDto d1, ExpedientDadaDto d2) {
						return d1.getVarCodi().compareToIgnoreCase(d2.getVarCodi());
					}
				}
			);
			int i = 0;
			for(CampDto camp: camps) {
				while (i < (dadesInstancia.size() - 1) && camp.getCodi().compareToIgnoreCase(dadesInstancia.get(i).getVarCodi()) > 0)
					i++;
				if (dadesInstancia.isEmpty() || !camp.getCodi().equals(dadesInstancia.get(i).getVarCodi())) {
					campsNoUtilitzats.add(camp);
				} else if (i < (dadesInstancia.size() - 1)){
					i++;
				}
			}
			return campsNoUtilitzats;
		} else {
			return camps;
		}
	}

	/** Retorna les dades de la instància de procés agrupades per agrupació. S'ha de tenir en compte
	 * que les agrupacions poden estar sobreescrites, per tant preval la agrupació sobreescrita del fill
	 * i s'ha de determinar la agrupació pel codi en comptes de l'identificador. 
	 * @param expedientId
	 * @param instaciaProcesId
	 * @param ambOcults
	 * @return
	 */
	private Map<CampAgrupacioDto, List<ExpedientDadaDto>> getDadesInstanciaProces(
			Long expedientId,
			String instaciaProcesId,
			boolean ambOcults) {		
		// definirem un mapa. La clau serà el nom de l'agrupació, i el valor el llistat de variables de l'agrupació
		Map<CampAgrupacioDto, List<ExpedientDadaDto>> dadesProces = new TreeMap<CampAgrupacioDto, List<ExpedientDadaDto>>(
				// Comparador d'ordre d'agrupacions, primer la null, després heretades i finalment pròpies
				new Comparator<CampAgrupacioDto>() {
                    @Override
                    public int compare(CampAgrupacioDto a1, CampAgrupacioDto a2) {
                    	// El null va davant
                    	if (a1 == null && a2 == null)
                    		return 0;
                    	else if (a1 == null)
                    		return -1;
                    	else if (a2 == null )
                    		return 1;
                    	else {
                    		// Després van els heretats
                    		if (a1.isHeretat() && a2.isHeretat())
                    			return Integer.compare(a1.getOrdre(), a2.getOrdre());
                    		else if (a1.isHeretat() && !a2.isHeretat())
                    			return -1;
                    		else if (!a1.isHeretat() && a2.isHeretat())
                    			return 1;
                    		else
                    			// Si no retorna l'ordre normal
                    			return Integer.compare(a1.getOrdre(), a2.getOrdre());
                    	}
                    }
                });
		// Obtenim les dades de la definició de procés
		List<ExpedientDadaDto> dadesInstancia = expedientDadaService.findAmbInstanciaProces(
				expedientId,
				instaciaProcesId);
		if (dadesInstancia == null || dadesInstancia.isEmpty())
			return null;
		
		// Obtenim les agrupacions de la definició de procés o del tipus d'expedient
		List<CampAgrupacioDto> agrupacions = expedientDadaService.agrupacionsFindAmbInstanciaProces(
				expedientId,
				instaciaProcesId);

		// Construeix els maps per poder recuperar fàcilment les agrupacions per id i per codi
		Map<Long, CampAgrupacioDto> mapAgrupacionsPerId = new HashMap<Long, CampAgrupacioDto>();
		Map<String, CampAgrupacioDto> mapAgrupacionsPerCodi = new HashMap<String, CampAgrupacioDto>();
		this.resoldreAgrupacionsSobreescrites(agrupacions, mapAgrupacionsPerId, mapAgrupacionsPerCodi);
		
		// Agrupa les dades per agrupacions
		CampAgrupacioDto agrupacio;
		List<ExpedientDadaDto> dades;
		for (ExpedientDadaDto dada: dadesInstancia) {
			if (ambOcults || !dada.isCampOcult()) {
				// resol la agrupació
				if (dada.getAgrupacioId() == null)
					agrupacio = null;
				else {
					agrupacio = mapAgrupacionsPerCodi.get( mapAgrupacionsPerId.get(dada.getAgrupacioId()).getCodi() );
				}
				// Esbrina la llista
				if (dadesProces.containsKey(agrupacio)) { 
					dades = dadesProces.get(agrupacio);
				} else { //if agrupacio of current dada iteration changed or first dada iteration
					dades = new ArrayList<ExpedientDadaDto>();
					dadesProces.put(agrupacio, dades);
				}
				// Afegeix la dada
				dades.add(dada);
			}
		}
		
		return dadesProces;	
	}

	/** Mètode per posar totes les agrupacions en el map per identificador i posar només
	 * les agrupacions que no estan sobreescrites en el map per codi.
	 * @param agrupacions Totes les agrupacions
	 * @param mapAgrupacionsPerId Map on es posaran les agrupacions per id
	 * @param mapAgrupacionsPerCodi Map on es posaran per codi les agrupacions no sobreescrites
	 */
	private void resoldreAgrupacionsSobreescrites(
			List<CampAgrupacioDto> agrupacions,
			Map<Long, CampAgrupacioDto> mapAgrupacionsPerId, 
			Map<String, CampAgrupacioDto> mapAgrupacionsPerCodi) {

		// Afegeix les agrupacions per id i guarda el codi de les sobreescrites
		Set<String> codisSobreescrites = new HashSet<String>();
		for (CampAgrupacioDto agrupacio : agrupacions) {
			mapAgrupacionsPerId.put(agrupacio.getId(), agrupacio);
			if (agrupacio.isSobreescriu())
				codisSobreescrites.add(agrupacio.getCodi());
		}
		// Construeix el map de les agrupacions per codi no sobreescrites
		mapAgrupacionsPerCodi.put(null, null);
		for (CampAgrupacioDto agrupacio : agrupacions) 
			if (!codisSobreescrites.contains(agrupacio.getCodi()) || agrupacio.isSobreescriu())
				mapAgrupacionsPerCodi.put(agrupacio.getCodi(), agrupacio);
	}
	
	/** Mètode privat per executar una acció relacionada amb una variable tipus ACCIO.
	 * 
	 * @param request
	 * @param expedientId
	 * @param procesId
	 * @param varCodi
	 * @param accioCamp
	 */
	private void executarDadaAccio(
			HttpServletRequest request,
			Long expedientId,
			String procesId,
			String varCodi,
			String accioCamp) {
		try {
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
			expedientService.executarCampAccio(
					expedientId,
					instanciaProces.getId(),
					accioCamp);
			MissatgesHelper.success(request, getMessage(request, "info.accio.executat"));
		} catch (PermisDenegatException ex) {
			String errMsg = getMessage(request, "error.executar.accio.camp", new Object[] {accioCamp, varCodi, expedientId});
			MissatgesHelper.error(
	    			request,
	    			errMsg + ": " + getMessage(request, "error.permisos.modificar.expedient"));
			logger.error(errMsg + ": "+ ex.getLocalizedMessage(), ex);
		} catch (Exception ex) {
			String errMsg = getMessage(request, "error.executar.accio.camp", new Object[] {accioCamp, varCodi, expedientId});
			MissatgesHelper.error(
	    			request,
	    			errMsg + ": " + ex.getMessage());
			logger.error(errMsg + ": "+ ex.getLocalizedMessage(), ex);
		}
		
	}	

	private static final Log logger = LogFactory.getLog(ExpedientDadaController.class);

}
