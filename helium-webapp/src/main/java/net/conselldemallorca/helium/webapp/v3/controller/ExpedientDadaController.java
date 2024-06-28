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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFPatternFormatting;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import net.conselldemallorca.helium.v3.core.api.dto.CampInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
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


	@RequestMapping(value = "/{expedientId}/dada/datatable", method = RequestMethod.POST)
	@ResponseBody
	public DatatablesHelper.DatatablesResponse documentDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {

		Boolean totes = Boolean.parseBoolean(request.getParameter("totes"));
		Boolean ambOcults = true;
		Boolean noPendents = false;
//		Boolean ambOcults = Boolean.parseBoolean(request.getParameter("ambOcults"));
//		Boolean noPendents = Boolean.parseBoolean(request.getParameter("noPendents"));
//		String filtre = request.getParameter("filtre");
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
//		paginacioParams.setFiltre(filtre);

		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientDadaService.findDadesExpedient(expedientId, totes, ambOcults, noPendents, paginacioParams),
				"id");
	}

	@RequestMapping(value = "/{expedientId}/dades/descarregar", method = RequestMethod.GET)
	public void descarregarDades(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId)  {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			List<DadaListDto> dades = expedientDadaService.findDadesExpedient(expedientId, true, true, false, new PaginacioParamsDto());
			exportXLS(response, expedient, dades);
		} catch(Exception e) {
			logger.error("Error generant excel amb les dades de l'expedient: " + expedientId, e);
			MissatgesHelper.error(request, getMessage(request, "expedient.dada.descarregar.error", new Object[]{ e.getMessage() } ));
		}
	}

	@RequestMapping(value = "/{expedientId}/dada")
	public String dades(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		if (ExpedientTipusTipusEnumDto.ESTAT.equals(expedient.getTipus().getTipus())) {
			model.addAttribute("expedient", expedient);
			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
			if (!NodecoHelper.isNodeco(request)) {
				return mostrarInformacioExpedientPerPipella(
						request,
						expedientId,
						model,
						"dades");
			}
			return "v3/expedientDadaList";
		}

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
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
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

	@RequestMapping(value = "/{expedientId}/dada/{varCodi}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean dadaExpBorrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String varCodi,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		String procesId = expedient.getProcessInstanceId();
	return dadaBorrar(request, expedientId, procesId, varCodi, model);
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
			boolean ple, // indica si consultar l'objecte informat
			Model model) {
		// S'ha de inicialitzar el command abans de processar el RequestMapping
		// del POSTs amb les modificacions al formulari de la tasca
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
			if (ple)
				return TascaFormHelper.getCommandForCamps(
						llistTasca,
						null,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
			else 
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

	@RequestMapping(value = "/{expedientId}/dada/{varCodi}/update", method = RequestMethod.GET)
	public String modificarVariablesExpGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String varCodi,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		String procesId = expedient.getProcessInstanceId();
		return modificarVariablesGet(request, expedientId, procesId, varCodi, model);
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
					true, // emplenar
					model);
			model.addAttribute("modificarVariableCommand", command);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		
		return "v3/expedientDadaModificar";
	}

	@RequestMapping(value = "/{expedientId}/dada/{varCodi}/update", method = RequestMethod.POST)
	public String dadaExpEditar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String varCodi,
			@Valid @ModelAttribute("modificarVariableCommand") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		String procesId = expedient.getProcessInstanceId();
		return dadaEditar(request, expedientId, procesId, varCodi, command, result, status, model);
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
	@RequestMapping(value = "/{expedientId}/dada/{varCodi}/update/accio", method = RequestMethod.POST)
	public String dadaExpEditarAccio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String varCodi,
			@RequestParam(value = "accioCamp", required = true) String accioCamp,
			@Valid @ModelAttribute("modificarVariableCommand") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		String procesId = expedient.getProcessInstanceId();
		return dadaEditarAccio(request, expedientId, procesId, varCodi, accioCamp, command, result, status, model);
	}
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

	@RequestMapping(value = "/{expedientId}/dada/new", method = RequestMethod.GET)
	public String novaDadaExpGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		return novaDadaAmbCodiGet(
				request,
				expedientId,
				null,
				null,
				model);
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

	@RequestMapping(value = "/{expedientId}/dada/{varCodi}/new", method = RequestMethod.GET)
	public String novaDadaExpAmbCodiGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String varCodi,
			Model model) {
		return novaDadaAmbCodiGet(
				request,
				expedientId,
				null,
				varCodi,
				model);
	}
	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/new", method = RequestMethod.GET)
	public String novaDadaAmbCodiGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			Model model) {
		if (procesId == null) {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			procesId = expedient.getProcessInstanceId();
			model.addAttribute("camps", expedientDadaService.getCampsNoUtilitzatsPerEstats(expedientId));
		} else {
			model.addAttribute("camps", getCampsNoUtilitzats(expedientId, procesId));
		}
		String ocultarVar = request.getParameter("ocultarVar");
		model.addAttribute("ocultarSelectorVar", ocultarVar != null ? Boolean.parseBoolean(ocultarVar) : false);
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
		return "v3/expedientDadaNova";
	}

	@RequestMapping(value = "/{expedientId}/dada/{varCodi}/new", method = RequestMethod.POST)
	public String novaDadaExpAmbCodiPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String varCodi,
			@Valid @ModelAttribute("addVariableCommand") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		return novaDadaAmbCodiPost(request, expedientId, null, varCodi, command, result, status, model);
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
			boolean perEstats = procesId == null;
			if (perEstats) {
				ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
				procesId = expedient.getProcessInstanceId();
			}
			if ("Buit".equals(varCodi)) {
				result.rejectValue(
						"varCodi",
						"expedient.nova.data.camp.variable.buit");
			} else if ("__string".equals(varCodi)) { // Variable nova tipus String
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
				if (perEstats) {
					model.addAttribute("camps", expedientDadaService.getCampsNoUtilitzatsPerEstats(expedientId));
					String ocultarVar = request.getParameter("ocultarVar");
					model.addAttribute("ocultarSelectorVar", ocultarVar != null ? Boolean.parseBoolean(ocultarVar) : false);
				} else {
					model.addAttribute("camps", getCampsNoUtilitzats(expedientId, procesId));
				}
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


	@RequestMapping(value = "/{expedientId}/dada/{varCodi}/edit", method = RequestMethod.GET)
	public String dadaExpAmbCodiEdit(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String varCodi,
			Model model) {
		Object command;
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			String procesId = expedient.getProcessInstanceId();
			command = populateCommand(
					request,
					expedientId,
					procesId,
					URLDecoder.decode(varCodi,"UTF-8"),
					true, // emplenar
					model);
			model.addAttribute("modificarVariableCommand", command);
			model.addAttribute("inline", true);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			MissatgesHelper.error(request, getMessage(request, "expedient.dada.modificar.error") );
		}

		return "v3/expedientDadaEdit";
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/dada/{varCodi}/edit", method = RequestMethod.POST)
	public String dadaExpAmbCodiEditPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable String varCodi,
			@Valid @ModelAttribute("modificarVariableCommand") Object command,
			BindingResult result,
			Model model) throws Exception {
		try {
//			String procesId = expedientService.getExpedientProcessInstanceId(expedientId);
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
			if (result.hasErrors()) {
//				model.addAttribute("modificarVariableCommand", command);
				model.addAttribute("inline", true);
				return "v3/expedientDadaEdit";
			}

			if (tascaDada.getVarValor() == null) {
				expedientDadaService.create(
						expedientId,
						procesId,
						varCodi,
						variables.get(varCodi));

			} else {
				expedientDadaService.update(
						expedientId,
						procesId,
						varCodi,
						variables.get(varCodi));
			}
			DadaListDto dada = expedientDadaService.getDadaList(expedientId, procesId, varCodi);
			model.addAttribute("dada", dada);
			return "v3/expedientDadaShow";
		} catch (Exception ex) {
			logger.error(ex);
			MissatgesHelper.error(request, getMessage(request, "expedient.dada.modificar.error") + ": " + ex.getMessage() );
			throw ex;
		}

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
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
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

	private List<CampInfoDto> getCampsNoUtilitzats(Long expedientId, String procesInstanceId) {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesInstanceId);
		List<CampInfoDto> campsNoUtilitzats = new ArrayList<CampInfoDto>();
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
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
					campsNoUtilitzats.add(CampInfoDto.builder().codi(camp.getCodi()).etiqueta(camp.getEtiqueta()).build());
				} else if (i < (dadesInstancia.size() - 1)){
					i++;
				}
			}
		} else {
			for(CampDto camp: camps) {
				campsNoUtilitzats.add(CampInfoDto.builder().codi(camp.getCodi()).etiqueta(camp.getEtiqueta()).build());
			}
		}
		return campsNoUtilitzats;
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
		
		for (Map.Entry<CampAgrupacioDto, List<ExpedientDadaDto>> entry : dadesProces.entrySet()) {
		    Collections.sort(entry.getValue(), new Comparator<ExpedientDadaDto>() {
		    	@Override
		    	public int compare(ExpedientDadaDto a1, ExpedientDadaDto a2) {
		    		// El null va davant
		    		if (a1 == null && a2 == null)
		    			return 0;
		    		else if (a1 == null)
		    			return -1;
		    		else if (a2 == null )
		    			return 1;
		    		else {
		    			// Si no retorna l'ordre normal
		    			return Integer.compare(a1.getOrdre(), a2.getOrdre());
		    		}
		    	}
		    });
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

	private void exportXLS(HttpServletResponse response, ExpedientDto expedient, List<DadaListDto> dades) {
		XSSFWorkbook wb = new XSSFWorkbook();

		// GENERAL
		XSSFSheet sheet = wb.createSheet("Dades expedient");

		// ESTILS
		// Capçalera
		XSSFFont headFont = wb.createFont();
		headFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		headFont.setFontHeightInPoints((short) 16);

		XSSFCellStyle headerStyle = wb.createCellStyle();
//		headerStyle.setFillBackgroundColor(XSSFColor.GREY_80_PERCENT.index);
		headerStyle.setFont(headFont);

		// Agrupacions
		XSSFFont agFont = wb.createFont();
		agFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		agFont.setFontHeightInPoints((short) 12);

		XSSFCellStyle agStyle = wb.createCellStyle();
		agStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		agStyle.setFillPattern(HSSFPatternFormatting.SOLID_FOREGROUND);
		agStyle.setFont(agFont);

		// Nom
		XSSFFont bold = wb.createFont();
		bold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

		XSSFCellStyle nStyle = wb.createCellStyle();
		nStyle.setFont(bold);

		// Valor
		DataFormat format = wb.createDataFormat();
		XSSFCellStyle dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));
		dStyle.setWrapText(true);
		XSSFCellStyle hStyle = wb.createCellStyle();
		hStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		hStyle.setFillPattern(HSSFPatternFormatting.FINE_DOTS);

		// CONTINGUT

		// Calcular nombre màxim de columnes
		int maxColumns = 2;
		if (!dades.isEmpty()) {
			for (DadaListDto dada : dades) {
				if (dada.isRegistre()) {
					maxColumns = Math.max(maxColumns, dada.getValor().getColumnes());
				}
			}
		}

		// Capçalera
		XSSFRow xlsRow = sheet.createRow(0);
		XSSFCell cell;
		cell = xlsRow.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Expedient: " + expedient.getIdentificador()));
		cell.setCellStyle(headerStyle);
		for (int i = 1; i < (maxColumns + 2); i++)
			xlsRow.createCell(i);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColumns + 1));
		xlsRow.setHeightInPoints(24);

		int rowNum = 1;

		xlsRow = sheet.createRow(rowNum++);

		// Dades
		if (!dades.isEmpty()) {
			List<String> agrupacions = new ArrayList<String>();
			for (DadaListDto dada : dades) {

				String agrupacio = dada.getAgrupacioNom();
				if (!agrupacions.contains(agrupacio)) {
					setCellValue(sheet, agrupacio, rowNum, 0, agStyle);
					xlsRow = sheet.getRow(rowNum);
					for (int i = 1; i < (maxColumns + 2); i++)
						xlsRow.createCell(i);
					sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, maxColumns + 1));
					xlsRow.setHeightInPoints(18);
					agrupacions.add(agrupacio);
					rowNum++;
				}

				int rowInicial = rowNum;
				int numRows = 1;

				// Nom
				if (dada.isRegistre()) {
					numRows = dada.getValor().getFiles() + 1;
				} else if (dada.isMultiple()) {
					numRows = dada.getValor().getValorMultiple().size();
				}

				xlsRow = sheet.createRow(rowNum++);
				setCellValue(xlsRow, dada.getNom(), 0, nStyle);
				if (numRows > 1) {
					for (int i = 1; i < numRows; i++)
						xlsRow = sheet.createRow(rowNum++);
					sheet.addMergedRegion(new CellRangeAddress(rowInicial, rowInicial + numRows - 1, 0, 0));
				}



				// Valor
				int colNum = 1;
				if (dada.isRegistre()) {
					// Capçalera
					xlsRow = sheet.getRow(rowInicial);
					int hcol = 0;
					for(Map.Entry<String, Boolean> entry: dada.getValor().getValorHeader().entrySet()) {
						setCellValue(xlsRow, entry.getKey(), colNum + hcol, hStyle);
						hcol++;
					}
					// Dades
					List<List<String>> valors = dada.getValor().getValorBody();
					if (valors != null && !valors.isEmpty()) {
						for (int fila = 0; fila < numRows; fila++) {
							xlsRow = sheet.getRow(rowInicial + fila + 1);
							for (int col = 0; col < dada.getValor().getColumnes(); col++) {
								if (valors.size() > fila && valors.get(fila).size() > col) {
									setCellValue(xlsRow, valors.get(fila).get(col), colNum + col, dStyle);
								}
							}
						}
					}
				} else if (dada.isMultiple()) {
					for (int fila = 0; fila < numRows; fila++) {
						setCellValue(sheet, dada.getValor().getValorMultiple().get(fila), rowInicial + fila, colNum, dStyle);
					}
				} else {
					setCellValue(xlsRow, dada.getValor().getValorSimple(), colNum, dStyle);
				}
			}

			for (int colNum = 0; colNum < maxColumns; colNum++)
				sheet.autoSizeColumn(colNum);
		}

		try {
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=Dades expedient.xls");
			response.setContentType("application/vnd.ms-excel;base64");
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
			logger.error("Mesures temporals: No s'ha pogut realitzar la exportació.");
		}
	}

	private static void setCellValue(XSSFSheet sheet, String valor, int rowNum, int colNum, XSSFCellStyle style) {
		XSSFRow xlsRow = sheet.getRow(rowNum);
		if (xlsRow == null)
			xlsRow = sheet.createRow(rowNum);
		setCellValue(xlsRow, valor, colNum, style);
	}
	private static void setCellValue(XSSFRow xlsRow, String valor, int colNum, XSSFCellStyle style) {
		XSSFCell cell = xlsRow.createCell(colNum);
		cell.setCellValue(valor);
		cell.setCellStyle(style);
		
	}

	private static final Log logger = LogFactory.getLog(ExpedientDadaController.class);

}
