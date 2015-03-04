/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

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
	private TascaService tascaService;

	@Autowired
	private VariableHelper variableHelper;

	@RequestMapping(value = "/{expedientId}/dada") //, method = RequestMethod.GET)
	public String dades(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value = "ambOcults", required = false) Boolean ambOcults,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"dades");
		}
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		model.addAttribute(
				"expedient",
				expedient);
		/*model.addAttribute(
				"dades",
				expedientService.findDadesPerInstanciaProces(
						expedientId,
						null));
		model.addAttribute(
				"agrupacions",
				expedientService.findAgrupacionsDadesPerInstanciaProces(
						expedientId,
						null));*/
		// Obtenim l'arbre de processos, per a poder mostrar la informació de tots els processos
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, Map<CampAgrupacioDto, List<ExpedientDadaDto>>> dades = new LinkedHashMap<InstanciaProcesDto, Map<CampAgrupacioDto,List<ExpedientDadaDto>>>();
		// Per a cada instància de procés ordenem les dades per agrupació  
		// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			Map<CampAgrupacioDto, List<ExpedientDadaDto>> dadesInstancia = null;
			if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				dadesInstancia = getDadesInstanciaProces(expedientId, instanciaProces.getId());
			}
			dades.put(instanciaProces, dadesInstancia);
		}
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("arbreProcessos", arbreProcessos);
		model.addAttribute("dades", dades);
		boolean isAdmin = expedient.isPermisAdministration(); 
		if (!isAdmin) 
			ambOcults = false;
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("ambOcults", ambOcults == null ? false : ambOcults);
		return "v3/expedientDades";
	}

	@RequestMapping(value = "/{expedientId}/dades/{procesId}") //, method = RequestMethod.GET)
	public String dadesProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@RequestParam(value = "ambOcults", required = false) Boolean ambOcults,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		boolean isAdmin = expedient.isPermisAdministration(); 
		if (!isAdmin) 
			ambOcults = false;
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("ambOcults", ambOcults == null ? false : ambOcults);
		model.addAttribute("dades", getDadesInstanciaProces(expedientId, procesId));
		return "v3/procesDades";
	}

	@RequestMapping(value = "/{expedientId}/dades/{procesId}/delete/{varCodi}", method = RequestMethod.GET)
	@ResponseBody
	public boolean dadaBorrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			Model model) {
		try {
			expedientService.deleteVariable(expedientId, procesId, varCodi);
			MissatgesHelper.info(request, getMessage(request, "info.dada.proces.esborrada") );
			return true;
		} catch (NotAllowedException ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.info.permis.no") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.dada.borrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la variable '" + varCodi + "' de l'expedient amb id '" + expedientId + "' (proces: " + procesId + ")", ex);
		}
		
		return false;
	}

	@ModelAttribute("modificarVariableCommand")
	public Object populateCommand(
			HttpServletRequest request,
			String procesId,
			String varCodi,			
			Model model) {
		if (procesId != null && !"".equals(procesId) && varCodi != null && !"".equals(varCodi)) {
			try {
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
				
//				CampDto camp = null;
//				for (CampDto c : expedientService.getCampsInstanciaProcesById(procesId)){
//					if (!CampTipusDto.ACCIO.equals(c.getTipus()) && varCodi.equals(c.getCodi())) {
//						camp = c;
//					}
//				}
				List<TascaDadaDto> llistTasca = new ArrayList<TascaDadaDto>();
				TascaDadaDto tascaDada = variableHelper.getTascaDadaDtoFromExpedientDadaDto(
						variableHelper.getDadaPerInstanciaProces(procesId, varCodi, true));
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
			//catch (Exception ignored) {}
		}
		return null;
	}

	@RequestMapping(value = "/{expedientId}/dades/{procesId}/edit/{varCodi}", method = RequestMethod.GET)
	public String modificarVariablesGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			Model model) {
		Object command = populateCommand(request, procesId, varCodi, model);
		model.addAttribute("modificarVariableCommand", command);
		return "v3/expedientDadaModificar";
	}

	@RequestMapping(value = "/{expedientId}/dades/{procesId}/edit/{varCodi}", method = RequestMethod.POST)
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
			List<TascaDadaDto> tascaDadas = new ArrayList<TascaDadaDto>();
//			ExpedientDadaDto expedientDada = variableHelper.getDadaPerInstanciaProces(procesId, varCodi, true);
			TascaDadaDto tascaDada = TascaFormHelper.toTascaDadaDto(variableHelper.getDadaPerInstanciaProces(procesId, varCodi, true));
			tascaDadas.add(tascaDada);
			
//			@SuppressWarnings("unchecked")
//			Map<String, Object> llista =  PropertyUtils.describe(command);
//			Object varValue = llista.get(varCodi);
			
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
			Object varValue = variables.get(varCodi);
			
			//List<ExpedientDadaDto> expedientDadas = variableHelper.findDadesPerInstanciaProces(procesId);
			TascaFormValidatorHelper validator = new TascaFormValidatorHelper(expedientService);
			validator.setTasca(tascaDadas);
//			Map<String, Object> valors = new HashMap<String, Object>();
//			valors.put(varCodi, varValue);
			Object commandPerValidacio = TascaFormHelper.getCommandForCampsExpedient(
					variableHelper.findDadesPerInstanciaProces(procesId),
					variables); 
					
			validator.validate(commandPerValidacio, result);
			if (result.hasErrors()) {
				
				return "v3/expedientDadaModificar";
			}
//			ExpedientDadaDto expedientDada = variableHelper.getDadaPerInstanciaProces(procesId, varCodi, true);
			expedientService.updateVariable(expedientId, procesId, varCodi, varValue);
			MissatgesHelper.info(request, getMessage(request, "info.dada.proces.modificada") );
		} catch (NotAllowedException ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.info.permis.no") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.dada.modificar.error"));
			logger.error("S'ha produit un error al intentar modificar la variable '" + varCodi + "' de l'expedient amb id '" + expedientId + "' (proces: " + procesId + ")", ex);
		}
		
		return modalUrlTancar(false);
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
//				List<TascaDadaDto> llistTasca = variableHelper.findDadesTascaPerInstanciaProces(procesId);
				List<TascaDadaDto> llistTasca = new ArrayList<TascaDadaDto>();
				if (varCodi != null && !"".equals(varCodi)) {
					TascaDadaDto tascaDada = variableHelper.getTascaDadaDtoFromExpedientDadaDto(
							variableHelper.getDadaPerInstanciaProces(procesId, varCodi, true));
					llistTasca.add(tascaDada);
					model.addAttribute("dada", tascaDada);
				}
//				Map<String, Object> registres = new HashMap<String, Object>();
//				return TascaFormHelper.getCommandModelForCamps(llistTasca, campsAddicionalsClasses, registres, false);
//				return TascaFormHelper.getCommandForCamps(llistTasca, null, campsAddicionals, campsAddicionalsClasses, false);
				return TascaFormHelper.getCommandBuitForCamps(
						llistTasca,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
			} catch (Exception ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut obtenir la informació de la dada " + varCodi + ": "  + ex.getMessage(), ex);
			} 
			//catch (Exception ignored) {}
		}
		return null;
	}

	@RequestMapping(value = "/{expedientId}/novaDada/{procesId}", method = RequestMethod.GET)
	public String novaDadaGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			Model model) {
		model.addAttribute("camps", getCampsNoUtilitzats(expedientId, procesId));
		model.addAttribute("addVariableCommand", populateAddCommand(request, procesId, null, null, null, model));
		return "v3/expedientDadaNova";
	}

	@RequestMapping(value = "/{expedientId}/novaDada/{procesId}/{varCodi}", method = RequestMethod.GET)
	public String novaDadaAmbCodiGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			Model model) {
		model.addAttribute("camps", getCampsNoUtilitzats(expedientId, procesId));
		model.addAttribute("addVariableCommand", populateAddCommand(request, procesId, varCodi, null, null, model));
		return "v3/procesDadaNova";
	}

	@RequestMapping(value = "/{expedientId}/novaDada/{procesId}/{varCodi}", method = RequestMethod.POST)
	public String novaDadaDesar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			@Valid @ModelAttribute("addVariableCommand") Object command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		try {
			boolean error = false;
			
			List<TascaDadaDto> tascaDadas = new ArrayList<TascaDadaDto>();
			
			if ("Buit".equals(varCodi)) {
				result.rejectValue("varCodi", "expedient.nova.data.camp.variable.buit");
				error = true;
			}
			// Variable nova tipus String
			else if ("String".equals(varCodi)) {
				String codi = (String)PropertyUtils.getSimpleProperty(command, "codi");
				String valor = (String)PropertyUtils.getSimpleProperty(command, "valor");
				// Validam que el nom de la variable no comenci per majúscula seguida de minúscula
				if (codi == null) {
					result.rejectValue("codi", "error.camp.codi.buit");
					error = true;
				} else {
					if (codi.matches("^[A-Z]{1}[a-z]{1}.*")) {
						result.rejectValue("codi", "error.camp.codi.maymin");
						error = true;
					}
					if (codi.contains(".")) {
						result.rejectValue("codi", "error.camp.codi.char.nok");
						error = true;
					}
					
					if (!error) {
						expedientService.createVariable(expedientId, procesId, codi, valor);
					}
				}
			} 
			// Variable de la definició de procés
			else {
				TascaDadaDto tascaDada = TascaFormHelper.toTascaDadaDto(variableHelper.getDadaPerInstanciaProces(procesId, varCodi, true));
				tascaDadas.add(tascaDada);
				
				Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
				Object varValue = variables.get(varCodi);
			
				TascaFormValidatorHelper validator = new TascaFormValidatorHelper(expedientService);
				validator.setTasca(tascaDadas);
			
				Object commandPerValidacio = TascaFormHelper.getCommandForCampsExpedient(
						variableHelper.findDadesPerInstanciaProces(procesId),
						variables);
				
				validator.validate(commandPerValidacio, result);
				
				if (result.hasErrors()) {
					error = true;
				} else {
					expedientService.createVariable(expedientId, procesId, varCodi, varValue);
				}
			}
			if (error) {
				model.addAttribute("camps", getCampsNoUtilitzats(expedientId, procesId));
				return "v3/expedientDadaNova";
			}
			
			MissatgesHelper.info(request, getMessage(request, "info.dada.nova.proces.creada") );
		} catch (NotAllowedException ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.info.permis.no") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.dada.modificar.error"));
			logger.error("S'ha produit un error al intentar modificar la variable '" + varCodi + "' de l'expedient amb id '" + expedientId + "' (proces: " + procesId + ")", ex);
		}
		
		return modalUrlTancar(false);
	}

	private List<CampDto> getCampsNoUtilitzats(Long expedientId, String procesId) {
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
			List<CampDto> campsNoUtilitzats = new ArrayList<CampDto>();
			List<CampDto> camps = dissenyService.findCampsAmbDefinicioProcesOrdenatsPerCodi(instanciaProces.getDefinicioProces().getId());
			List<ExpedientDadaDto> dadesInstancia = expedientService.findDadesPerInstanciaProces(expedientId, procesId);
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

	private Map<CampAgrupacioDto, List<ExpedientDadaDto>> getDadesInstanciaProces(Long expedientId, String instaciaProcesId) {
		
		// definirem un mapa. La clau serà el nom de l'agrupació, i el valor el llistat de variables de l'agrupació
		Map<CampAgrupacioDto, List<ExpedientDadaDto>> dadesProces = new LinkedHashMap<CampAgrupacioDto, List<ExpedientDadaDto>>();
		
		// Obtenim les dades de la definició de procés
		List<ExpedientDadaDto> dadesInstancia = expedientService.findDadesPerInstanciaProces(expedientId, instaciaProcesId);
		if (dadesInstancia == null || dadesInstancia.isEmpty())
			return null;
		Collections.sort(
				dadesInstancia, 
				new Comparator<ExpedientDadaDto>() {
					public int compare(ExpedientDadaDto d1, ExpedientDadaDto d2) {
						if (d1.getAgrupacioId() == null && d2.getAgrupacioId() == null)
							return 0;
						if (d1.getAgrupacioId() == null ^ d2.getAgrupacioId() == null)
							return (d1.getAgrupacioId() == null ? -1 : 1);
						int c = d1.getAgrupacioId().compareTo(d2.getAgrupacioId());
						if (c != 0) 
							return c;
						else 
							return d1.getCampEtiqueta().compareToIgnoreCase(d2.getCampEtiqueta());
					}
				});
		
		// Obtenim les agrupacions de la definició de procés
		// Les posam amb un map per a que obtenir el nom sigui directe
		List<CampAgrupacioDto> agrupacions = expedientService.findAgrupacionsDadesPerInstanciaProces(expedientId, instaciaProcesId);
		Map<Long,CampAgrupacioDto> magrupacions = new HashMap<Long, CampAgrupacioDto>();
		for (CampAgrupacioDto agrupacio : agrupacions) {
			magrupacions.put(agrupacio.getId(), agrupacio);
		}
		magrupacions.put(null, null);
		
		Long agrupacioId = null;
		List<ExpedientDadaDto> dadesAgrupacio = new ArrayList<ExpedientDadaDto>();
		
		for (ExpedientDadaDto dada: dadesInstancia) {
			if ((agrupacioId == null && dada.getAgrupacioId() == null) || dada.getAgrupacioId().equals(agrupacioId)) {
				dadesAgrupacio.add(dada);
			} else {
				if (!dadesAgrupacio.isEmpty()) {
					dadesProces.put(magrupacions.get(agrupacioId), dadesAgrupacio);
					dadesAgrupacio = new ArrayList<ExpedientDadaDto>();
				}
				agrupacioId = dada.getAgrupacioId();
				dadesAgrupacio.add(dada);
			}
		}
		
		dadesProces.put(magrupacions.get(agrupacioId), dadesAgrupacio);
		return dadesProces;
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
//				new CustomBooleanEditor(false));
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
//		binder.registerCustomEditor(
//				TerminiDto.class,
//				new TerminiTypeEditorHelper());
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	private static final Log logger = LogFactory.getLog(ExpedientDadaController.class);

}
