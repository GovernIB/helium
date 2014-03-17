/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.report.FieldValue;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.mvc.JasperReportsView;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientInformeCommand;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/informe")
public class ExpedientInformeController extends BaseExpedientController {
	private static final String VARIABLE_SESSIO_COMMAND = "consultaCommand";
	private static final String VARIABLE_SESSIO_COMMAND_VALUES = "consultaCommandValues";

	public static final String VARIABLE_FILTRE_CONSULTA_TIPUS = "filtreConsultaTipus";

	// Variables exportació
	private HSSFWorkbook wb;
	private HSSFCellStyle headerStyle;
	private HSSFCellStyle cellStyle;
	private HSSFCellStyle dStyle;
	private HSSFFont bold;
	private HSSFCellStyle cellGreyStyle;
	private HSSFCellStyle greyStyle;
	private HSSFCellStyle dGreyStyle;
	private HSSFFont greyFont;

	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private DissenyService dissenyService;
	
	@Autowired
	private TascaService tascaService;

	@RequestMapping(value = "/{expedientTipusId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@ModelAttribute("commandFiltre") Object filtreCommand,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (filtreCommand == null) {
			filtreCommand = getFiltreCommand(request, expedientTipusId);
		}
		if (filtreCommand instanceof ExpedientInformeCommand) {
			ExpedientInformeCommand command = (ExpedientInformeCommand) filtreCommand;
			command.setConsultaRealitzada(command.getConsultaId() != null && !"".equals(command.getConsultaId()));
			model.addAttribute(filtreCommand);
		}
		model.addAttribute("consultes", dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornActual.getId(),expedientTipusId));	
		return "v3/expedientInforme";
	}
	
	private ExpedientInformeCommand getFiltreCommand(
			HttpServletRequest request,
			Long expedientTipusId) {
		ExpedientInformeCommand filtreCommand = SessionHelper.getSessionManager(request).getFiltreInforme(expedientTipusId);
		if (filtreCommand == null || filtreCommand.getExpedientTipusId().longValue() != expedientTipusId.longValue()) {
			filtreCommand = new ExpedientInformeCommand();			
			filtreCommand.setExpedientTipusId(expedientTipusId);
			SessionHelper.getSessionManager(request).setFiltreInforme(
					filtreCommand,
					expedientTipusId);
		}
		filtreCommand.setConsultaRealitzada(filtreCommand.getConsultaId() != null && !"".equals(filtreCommand.getConsultaId()));
		return filtreCommand;
	}
	
	@ModelAttribute("commandFiltre")
	public Object populateCommandFiltre(
			HttpServletRequest request, 
			HttpSession session,
			Long consultaId,
			ModelMap model) {
		Object command = null;
		List<TascaDadaDto> campsFiltre = new ArrayList<TascaDadaDto>();
		List<TascaDadaDto> campsInforme = new ArrayList<TascaDadaDto>();
		if (consultaId != null) {
			ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
			model.addAttribute("consulta", consulta);
			campsFiltre = expedientService.findConsultaFiltre(consultaId);
			campsInforme = expedientService.findConsultaInforme(consultaId);
			command = TascaFormHelper.getCommandForFiltre(campsFiltre, null, null, null);
			session.setAttribute(VARIABLE_SESSIO_COMMAND, command);			

//			if (consulta.getExpedientTipus() != null) {
//				List<EstatDto> estats = dissenyService.findEstatByExpedientTipus(consulta.getExpedientTipus().getId());
//				afegirEstatsInicialIFinal(request, estats);
//				model.addAttribute("estats", estats);
//			}
		}
		model.addAttribute("campsFiltre", campsFiltre);	
		model.addAttribute("campsInforme", campsInforme);	
		return command;
	}

//	private void afegirEstatsInicialIFinal(HttpServletRequest request, List<EstatDto> estats) {
//		EstatDto iniciat = new EstatDto();
//		iniciat.setId(0L);
//		iniciat.setCodi("0");
//		iniciat.setNom( getMessage(request, "expedient.consulta.iniciat") );
//		estats.add(0, iniciat);
//		EstatDto finalitzat = new EstatDto();
//		finalitzat.setId(-1L);
//		finalitzat.setCodi("-1");
//		finalitzat.setNom( getMessage(request, "expedient.consulta.finalitzat") );
//		estats.add(finalitzat);
//	}
	
	@RequestMapping(value = "/{expedientTipusId}", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Valid ExpedientInformeCommand filtreCommand,
			@ModelAttribute("commandFiltre") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(filtreCommand);
		List<ConsultaDto> consultes = dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(
				entornActual.getId(),
				expedientTipusId);
		model.addAttribute("consultes", consultes);	
		filtreCommand.setConsultaRealitzada(filtreCommand.getConsultaId() != null && !"".equals(filtreCommand.getConsultaId()));
		
		if (result.hasErrors()) {
			MissatgesHelper.error(
					request,
					"S'han produït errors en la validació dels camps del filtre");	
		} else {
			SessionHelper.setAttribute(
					request,
					VARIABLE_FILTRE_CONSULTA_TIPUS,
					filtreCommand);
		}				
		return "v3/expedientInforme";
	}

	@RequestMapping(value = "/{expedientTipusId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public  DatatablesPagina<ExpedientDto>  datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "nomesPendents", required = false) Boolean nomesPendents,
			@RequestParam(value = "nomesAlertes", required = false) Boolean nomesAlertes,
			@RequestParam(value = "mostrarAnulats", required = false) Boolean mostrarAnulats,
			HttpSession session,
			ModelMap model) {
		PaginaDto<ExpedientDto> paginaDto = null;
		Map<String, Object> valors = new HashMap<String, Object>();
		
		List<ExpedientDto> listaExpedients = new ArrayList<ExpedientDto>();
		ExpedientInformeCommand filtreCommand = getFiltreCommand(request, expedientTipusId);
		if (consultaId != null) {
			filtreCommand.setConsultaId(consultaId);
		}
		filtreCommand.setConsultaRealitzada(false);
		if (filtreCommand.getConsultaId() != null) {
			filtreCommand.setConsultaRealitzada(true);
			
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			List<ConsultaDto> consultes = dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("consultes", consultes);	
			filtreCommand.setConsultaRealitzada(filtreCommand.getConsultaId() != null && !"".equals(filtreCommand.getConsultaId()));
			
			Object commandFiltre = session.getAttribute(VARIABLE_SESSIO_COMMAND);
			
			List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(filtreCommand.getConsultaId());
			List<TascaDadaDto> campsInforme = expedientService.findConsultaInforme(filtreCommand.getConsultaId());
			model.addAttribute("campsFiltre", campsFiltre);
			model.addAttribute("campsInforme", campsInforme);

			valors = TascaFormHelper.getValorsFromCommand(
					campsFiltre,
					commandFiltre,
					true);
			Iterator<Entry<String, Object>> it = valors.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> e = it.next();
				if (request.getParameterMap().containsKey(e.getKey())) {
					valors.put(e.getKey(), request.getParameter(e.getKey()));
				}
			}

			listaExpedients = expedientService.findPerConsultaInformePaginat(
				entornActual.getId(),
				filtreCommand.getConsultaId(),
				expedientTipusId,
				getValorsPerService(campsFiltre, valors),
				ExpedientCamps.EXPEDIENT_CAMP_ID,
				nomesPendents,
				nomesAlertes,
				mostrarAnulats
			);
		}
		
		paginaDto = PaginacioHelper.toPaginaDto(new PageImpl<ExpedientDto>(listaExpedients));
		
		SessionHelper.setAttribute(
				request,
				VARIABLE_SESSIO_COMMAND_VALUES,
				valors);
		
		if (nomesPendents != null) filtreCommand.setNomesPendents(nomesPendents);
		if (nomesAlertes != null) filtreCommand.setNomesAlertes(nomesAlertes);
		if (mostrarAnulats != null) filtreCommand.setMostrarAnulats(mostrarAnulats);
		
		SessionHelper.setAttribute(
				request,
				VARIABLE_FILTRE_CONSULTA_TIPUS,
				filtreCommand);
				
		return PaginacioHelper.getPaginaPerDatatables(
				request,
				paginaDto);
	}

	@RequestMapping(value = "/{expedientTipusId}/exportar_excel", method = RequestMethod.GET)
	public void exportarExcel(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientTipusId,
			@RequestParam(value = "nomesPendents", required = false) Boolean nomesPendents,
			@RequestParam(value = "nomesAlertes", required = false) Boolean nomesAlertes,
			@RequestParam(value = "mostrarAnulats", required = false) Boolean mostrarAnulats,
			HttpSession session,
			ModelMap model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();		
		if (entornActual != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> valors = (Map<String, Object>) session.getAttribute(VARIABLE_SESSIO_COMMAND_VALUES);
	
			ExpedientInformeCommand filtreCommand = getFiltreCommand(request, expedientTipusId);
			ConsultaDto consulta = dissenyService.findConsulteById(filtreCommand.getConsultaId());			
			List<TascaDadaDto> campsInforme = expedientService.findConsultaInforme(filtreCommand.getConsultaId());
			
			List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto = expedientService.findAmbEntornConsultaDisseny(
					entornActual.getId(),
					consulta.getId(),
					valors,
					ExpedientCamps.EXPEDIENT_CAMP_ID,
					true);
			
			exportXLS(request, response, session, consulta, campsInforme, expedientsConsultaDissenyDto);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/mostrar_informe", method = RequestMethod.GET)
	public  String  descargar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			HttpSession session,
			ModelMap model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();		
		if (entornActual != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> valors = (Map<String, Object>) session.getAttribute(VARIABLE_SESSIO_COMMAND_VALUES);
	
			ConsultaDto consulta = dissenyService.findConsulteById(getFiltreCommand(request, expedientTipusId).getConsultaId());
						
			List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto = expedientService.findAmbEntornConsultaDisseny(
					entornActual.getId(),
					consulta.getId(),
					valors,
					ExpedientCamps.EXPEDIENT_CAMP_ID,
					true);
			
			model.addAttribute(
					JasperReportsView.MODEL_ATTRIBUTE_REPORTDATA,
					getDadesDatasource(request, expedientsConsultaDissenyDto));
			
			String extensio = consulta.getInformeNom().substring(
					consulta.getInformeNom().lastIndexOf(".") + 1).toLowerCase();
			
			String nom = consulta.getInformeNom().substring(0,
					consulta.getInformeNom().lastIndexOf("."));
			
			if ("zip".equals(extensio)) {
				HashMap<String, byte[]> reports = unZipReports(consulta
						.getInformeContingut());
				model.addAttribute(
						JasperReportsView.MODEL_ATTRIBUTE_REPORTCONTENT,
						reports.get(nom + ".jrxml"));
				reports.remove(nom + ".jrxml");
				model.addAttribute(
						JasperReportsView.MODEL_ATTRIBUTE_SUBREPORTS,
						reports);
			} else {
				model.addAttribute(
						JasperReportsView.MODEL_ATTRIBUTE_REPORTCONTENT,
						consulta.getInformeContingut());
				
				request.setAttribute("formatJR", consulta.getFormatExport());
				model.addAttribute(
						JasperReportsView.MODEL_ATTRIBUTE_CONSULTA,
						consulta.getCodi());
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
			return "redirect:/v3/";
		}
		
		return "jasperReportsView";
	}
	
	public HashMap<String, byte[]> unZipReports(byte[] zipContent) {
		byte[] buffer = new byte[4096];
		HashMap<String, byte[]> docs = new HashMap<String, byte[]>();

		try {
			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipContent));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				String fileName = ze.getName();
				byte[] fileContent;

				ByteArrayOutputStream fos = new ByteArrayOutputStream();

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				fileContent = fos.toByteArray();
				docs.put(fileName, fileContent);
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return docs;
	}
	
	private List<Map<String, FieldValue>> getDadesDatasource(HttpServletRequest request, List<ExpedientConsultaDissenyDto> expedients) {
		List<Map<String, FieldValue>> dadesDataSource = new ArrayList<Map<String, FieldValue>>();
		for (ExpedientConsultaDissenyDto dadesExpedient: expedients) {
			ExpedientDto expedient = dadesExpedient.getExpedient();
			Map<String, FieldValue> mapFila = new HashMap<String, FieldValue>();
			for (String clau: dadesExpedient.getDadesExpedient().keySet()) {
				DadaIndexadaDto dada = dadesExpedient.getDadesExpedient().get(clau);
				mapFila.put(
						dada.getReportFieldName(),
						toReportField(request, expedient, dada));
			}
			dadesDataSource.add(mapFila);
		}
		return dadesDataSource;
	}
	
	private FieldValue toReportField(HttpServletRequest request, ExpedientDto expedient, DadaIndexadaDto dadaIndex) {
		FieldValue field = new FieldValue(
				dadaIndex.getDefinicioProcesCodi(),
				dadaIndex.getReportFieldName(),
				dadaIndex.getEtiqueta());
		if (!dadaIndex.isMultiple()) {
			field.setValor(dadaIndex.getValor());
			if ("expedient%estat".equals(field.getCampCodi())) {
				if (expedient.getDataFi() != null) {
					field.setValorMostrar(getMessage(request, "expedient.consulta.finalitzat"));
				} else {
					if (expedient.getEstat() != null)
						field.setValorMostrar(expedient.getEstat().getNom());
					else
						field.setValorMostrar(getMessage(request, "expedient.consulta.iniciat"));
				}
			} else {
				field.setValorMostrar(dadaIndex.getValorMostrar());
			}
			if (dadaIndex.isOrdenarPerValorMostrar())
				field.setValorOrdre(dadaIndex.getValorMostrar());
			else
				field.setValorOrdre(dadaIndex.getValorIndex());
		} else {
			field.setValorMultiple(dadaIndex.getValorMultiple());
			field.setValorMostrarMultiple(dadaIndex.getValorMostrarMultiple());
			field.setValorOrdreMultiple(dadaIndex.getValorIndexMultiple());
			if (dadaIndex.isOrdenarPerValorMostrar()){
				field.setValorOrdreMultiple(dadaIndex.getValorMostrarMultiple());
			}
			else {
				field.setValorOrdreMultiple(dadaIndex.getValorIndexMultiple());
			}
		}
		field.setMultiple(dadaIndex.isMultiple());
		return field;
	}
	
	private Map<String, Object> getValorsPerService(List<TascaDadaDto> camps, Map<String, Object> valors) {
		Map<String, Object> valorsPerService = new HashMap<String, Object>();
		for (TascaDadaDto camp : camps) {
			valorsPerService.put(camp.getVarCodi(), valors.get(camp.getVarCodi()));
		}
		return valorsPerService;
	}

	@RequestMapping(value = "/seleccionar/{expedientId}", method = RequestMethod.GET)
	@ResponseBody
	public Set<Long> seleccionar(
			HttpServletRequest request,
			@PathVariable String expedientId) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
		}
		String[] ids = (expedientId.contains(",")) ? expedientId.split(",") : new String[] {expedientId};
		for (String id: ids) {
			try {
				seleccio.add(Long.parseLong(id));
			} catch (NumberFormatException ex) {}
		}
		return seleccio;
	}
	
	@RequestMapping(value = "/deseleccionar/{expedientId}", method = RequestMethod.GET)
	@ResponseBody
	public Set<Long> deseleccionar(
			HttpServletRequest request,
			@PathVariable String expedientId) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
		}
		String[] ids = (expedientId.contains(",")) ? expedientId.split(",") : new String[] {expedientId};
		for (String id: ids) {
			try {
				seleccio.remove(Long.parseLong(id));
			} catch (NumberFormatException ex) {}
		}
		return seleccio;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAutoGrowNestedPaths(false);
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
	}
	
	private void createHeader(HSSFSheet sheet, List<TascaDadaDto> campsInforme) {
		int rowNum = 0;
		int colNum = 0;

		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);

		HSSFCell cell;
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize("Expedient")));
		cell.setCellStyle(headerStyle);
		
		for (TascaDadaDto campInforme : campsInforme) {
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(campInforme.getCampEtiqueta())));
			cell.setCellStyle(headerStyle);
		}
	}
	
	private void exportXLS(HttpServletRequest request, HttpServletResponse response, HttpSession session, ConsultaDto consulta, List<TascaDadaDto> campsInforme, List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto) {
		wb = new HSSFWorkbook();
	
		bold = wb.createFont();
		bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(HSSFColor.WHITE.index);
		greyFont = wb.createFont();
		greyFont.setColor(HSSFColor.GREY_25_PERCENT.index);
	
		cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellStyle.setWrapText(true);
		cellGreyStyle = wb.createCellStyle();
		cellGreyStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellGreyStyle.setWrapText(true);
		cellGreyStyle.setFont(greyFont);
		
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
		headerStyle.setFont(bold);
	
		greyStyle = wb.createCellStyle();
		greyStyle.setFont(greyFont);
	
		DataFormat format = wb.createDataFormat();
		dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));
	
		dGreyStyle = wb.createCellStyle();
		dGreyStyle.setFont(greyFont);
		dGreyStyle.setDataFormat(format.getFormat("0.00"));
	
		// GENERAL
		HSSFSheet sheet = wb.createSheet(consulta.getNom());
	
		createHeader(sheet, campsInforme);
	
		int rowNum = 1;
		
		for (ExpedientConsultaDissenyDto  expedientConsultaDissenyDto : expedientsConsultaDissenyDto) {
			try {
				HSSFRow xlsRow = sheet.createRow(rowNum++);
				int colNum = 0;
				
				ExpedientDto exp = expedientConsultaDissenyDto.getExpedient();
				Map<String, DadaIndexadaDto> dades = expedientConsultaDissenyDto.getDadesExpedient();
				
				String titol = "";
				if (exp != null) { 
					if (exp.getNumero() != null)
						titol = "[" + exp.getNumero() + "]";
		    		if (exp.getTitol() != null) 
		    			titol += (titol.length() > 0 ? " " : "") + exp.getTitol();
		    		if (titol.length() == 0)
		    			titol = exp.getNumeroDefault();
				}
				
				HSSFCell cell = xlsRow.createCell(colNum++);
				cell.setCellValue(titol);
				cell.setCellStyle(dStyle);
				
				Iterator<Entry<String, DadaIndexadaDto>> it = dades.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, DadaIndexadaDto> e = (Map.Entry<String, DadaIndexadaDto>)it.next();
					cell = xlsRow.createCell(colNum++);
					DadaIndexadaDto val = e.getValue();
					cell.setCellValue((val == null || val.getValorMostrar() == null) ? "" : val.getValorMostrar());
					cell.setCellStyle(dStyle);
				}
			} catch (Exception e) {
				logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum + " - amb ID: " + expedientConsultaDissenyDto.getExpedient().getId(), e);
			}
		}
		
		try {   
			wb.write( response.getOutputStream() );		
			writeFileToResponse("informe.xls", wb.getBytes(), response);	
		} catch (Exception e) {
			logger.error("Mesures temporals: No s'ha pogut realitzar la exportació.");
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInformeController.class);
}
