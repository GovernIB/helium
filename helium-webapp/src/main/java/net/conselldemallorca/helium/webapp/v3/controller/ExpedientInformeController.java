/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.report.FieldValue;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.JasperReportsView;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
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

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/informe")
public class ExpedientInformeController extends BaseExpedientController {
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
	
	@ModelAttribute("expedientInformeCommand")
	public Object getFiltreCommand(
			HttpServletRequest request,
			Long consultaId) {
		if (consultaId == null) 
			return null;
		Object filtreCommand = SessionHelper.getAttribute(request,SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		if (filtreCommand != null) {
			return filtreCommand;
		}
		Map<String, Object> campsAddicionals = new HashMap<String, Object>();
		Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
		UsuariPreferenciesDto preferenciesUsuari = SessionHelper.getSessionManager(request).getPreferenciesUsuari();
		boolean nomesPendents = false;
		if (preferenciesUsuari != null)
			nomesPendents = preferenciesUsuari.isFiltroTareasActivas();
		campsAddicionals.put("consultaId", consultaId);
		campsAddicionals.put("nomesPendents", nomesPendents);
		campsAddicionals.put("nomesAlertes", false);
		campsAddicionals.put("mostrarAnulats", false);
		campsAddicionals.put("mostrarTasquesPersonals", false);
		campsAddicionals.put("mostrarTasquesGrup", false);
		campsAddicionalsClasses.put("nomesPendents", Boolean.class);
		campsAddicionalsClasses.put("nomesAlertes", Boolean.class);
		campsAddicionalsClasses.put("mostrarAnulats", Boolean.class);			
		campsAddicionalsClasses.put("consultaId", Long.class);
		campsAddicionalsClasses.put("mostrarTasquesPersonals", Boolean.class);
		campsAddicionalsClasses.put("mostrarTasquesGrup", Boolean.class);
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
		return TascaFormHelper.getCommandBuitForCamps(
				campsFiltre,
				campsAddicionals,
				campsAddicionalsClasses,
				true);
	}
	
	@ModelAttribute("expedientInformeParametrosCommand")
	public Object getFiltreParameterCommand(
			HttpServletRequest request,
			Long consultaId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object filtreCommand = SessionHelper.getAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS_PARAM);
		if (filtreCommand != null)
			return filtreCommand;
		if (consultaId == null) 
			return null;
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaInformeParams(consultaId);		
		return TascaFormHelper.getCommandBuitForCamps(
				campsFiltre,
				new HashMap<String, Object>(),
				new HashMap<String, Class<?>>(),
				false);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getConsulta(
			HttpServletRequest request,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			Model model)  {
		ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute("campsFiltre", expedientService.findConsultaFiltre(consultaId));	
		model.addAttribute("campsInforme", expedientService.findConsultaInforme(consultaId));
		model.addAttribute("campsInformeParams", expedientService.findConsultaInformeParams(consultaId));
		List<EstatDto> estats = dissenyService.findEstatByExpedientTipus(consulta.getExpedientTipus().getId());
		estats.add(0, new EstatDto(0L, "0", getMessage(request, "expedient.consulta.iniciat")));
		estats.add(new EstatDto(-1L, "-1", getMessage(request, "expedient.consulta.finalitzat")));
		model.addAttribute("estats", estats);

		SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		
		Object filtreCommand = getFiltreCommand(request, consultaId);
		
		SessionHelper.setAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId, filtreCommand);

		model.addAttribute("expedientInformeCommand", filtreCommand);
		return "v3/expedientInforme";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@Valid @ModelAttribute("expedientInformeCommand") Object filtreCommand,			
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
			filtreCommand = getFiltreCommand(request, consultaId);
		} else {
			ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
			model.addAttribute("consulta", consulta);
			model.addAttribute("campsFiltre", expedientService.findConsultaFiltre(consultaId));
			model.addAttribute("campsInforme", expedientService.findConsultaInforme(consultaId));
			model.addAttribute("campsInformeParams", expedientService.findConsultaInformeParams(consultaId));
			List<EstatDto> estats = dissenyService.findEstatByExpedientTipus(consulta.getExpedientTipus().getId());
			estats.add(0, new EstatDto(0L, "0", getMessage(request, "expedient.consulta.iniciat")));
			estats.add(new EstatDto(-1L, "-1", getMessage(request, "expedient.consulta.finalitzat")));
			model.addAttribute("estats", estats);
		}
		
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId,
				filtreCommand);
		model.addAttribute("expedientInformeCommand", filtreCommand);
		return "v3/expedientInforme";
	}

	@RequestMapping(value = "/{consultaId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public  DatatablesPagina<ExpedientConsultaDissenyDto> datatable(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		Object filtreCommand = SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
				campsFiltre,
				filtreCommand,
				true);

		PaginaDto<ExpedientConsultaDissenyDto> listaExpedients = expedientService.findConsultaInformePaginat(
			consultaId,
			getValorsPerService(filtreCommand,campsFiltre, valors),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesPendents"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarAnulats"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarTasquesPersonals"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarTasquesGrup"),
			PaginacioHelper.getPaginacioDtoFromDatatable(request)
		);
		
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_SESSIO_COMMAND_VALUES+consultaId,
				valors);
		
		return PaginacioHelper.getPaginaPerDatatables(
				request,
				listaExpedients);
	}

	@RequestMapping(value = "/{consultaId}/exportar_excel", method = RequestMethod.GET)
	public void exportarExcel(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> valors = (Map<String, Object>) session.getAttribute(SessionHelper.VARIABLE_SESSIO_COMMAND_VALUES+consultaId);
		Object filtreCommand = SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto = expedientService.findConsultaDissenyPaginat(
				consultaId,
				valors,
				null,
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesPendents"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarAnulats"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarTasquesPersonals"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarTasquesGrup"));
		
		exportXLS(request, response, session, expedientsConsultaDissenyDto);
	}
	
	@RequestMapping(value = "/{consultaId}/mostrar_informe_params", method = RequestMethod.GET)
	public  String  mostrarInformeParams(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS_PARAM);
		Object parametrosCommand = getFiltreParameterCommand(request, consultaId);
		SessionHelper.setAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS_PARAM, parametrosCommand);

		model.addAttribute("expedientInformeParametrosCommand", parametrosCommand);
		model.addAttribute("campsInformeParams", expedientService.findConsultaInformeParams(consultaId));			
		return "v3/expedientInformeParams";
	}
	
	@RequestMapping(value = "/{consultaId}/mostrar_informe_params", method = RequestMethod.POST)
	public  String  mostrarInformeParams(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@Valid @ModelAttribute("expedientInformeParametrosCommand") Object parametrosCommand,			
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			HttpSession session,
			Model model)  throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
				expedientService.findConsultaInformeParams(consultaId),
				parametrosCommand,
				true);
		
		model.addAttribute(JasperReportsView.MODEL_ATTRIBUTE_PARAMS, valors);
		return generarReport(session, consultaId, model, request);
	}

	@RequestMapping(value = "/{consultaId}/mostrar_informe", method = RequestMethod.GET)
	public  String  descargar(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		return generarReport(session, consultaId, model, request);
	}
	
	private String generarReport(HttpSession session, Long consultaId, Model model, HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		@SuppressWarnings("unchecked")
		Map<String, Object> valors = (Map<String, Object>) session.getAttribute(SessionHelper.VARIABLE_SESSIO_COMMAND_VALUES+consultaId);
		
		Object filtreCommand = SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto = expedientService.findConsultaDissenyPaginat(
				consultaId,
				valors,
				null,
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesPendents"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarAnulats"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarTasquesPersonals"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarTasquesGrup"));
		
		if (expedientsConsultaDissenyDto.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.consulta.informe.expedients.nonhiha"));
			getConsulta(request,consultaId,model);
			return "redirect:/v3/informe/"+consultaId;
		}
		
		model.addAttribute(
				JasperReportsView.MODEL_ATTRIBUTE_REPORTDATA,
				getDadesDatasource(request, expedientsConsultaDissenyDto));
		
		ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
		String extensio = consulta.getInformeNom().substring(
				consulta.getInformeNom().lastIndexOf(".") + 1).toLowerCase();
		
		String nom = consulta.getInformeNom().substring(0,
				consulta.getInformeNom().lastIndexOf("."));
		
		
		String formatExportacio = consulta.getFormatExport();
		request.setAttribute("formatJR", formatExportacio);
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
		}
		model.addAttribute(
				JasperReportsView.MODEL_ATTRIBUTE_CONSULTA,
				consulta.getCodi());
		
		
		return "jasperReportsView";
	}

	private HashMap<String, byte[]> unZipReports(byte[] zipContent) {
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
			if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT_JSP.equals(field.getCampCodi())) {
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
	
	@SuppressWarnings("static-access")
	private Map<String, Object> getValorsPerService(Object filtreCommand, List<TascaDadaDto> camps, Map<String, Object> valors) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> valorsPerService = new HashMap<String, Object>();
		for (TascaDadaDto camp : camps) {
			String clau = (camp.getDefinicioProcesKey() == null) ? camp.getVarCodi() : camp.getDefinicioProcesKey() + "." + camp.getVarCodi();
			clau = camp.getVarCodi().replace(ExpedientCamps.EXPEDIENT_PREFIX_JSP, ExpedientCamps.EXPEDIENT_PREFIX);
			if (camp.getCampTipus().BOOLEAN.equals(camp.getCampTipus()) && PropertyUtils.isReadable(filtreCommand, camp.getVarCodi())) {
				Boolean valor = (Boolean) PropertyUtils.getSimpleProperty(filtreCommand, camp.getVarCodi());
				valors.put(camp.getVarCodi(), valor);
			}
			valorsPerService.put(clau, valors.get(camp.getVarCodi()));
		}
		return valorsPerService;
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
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}
	
	private void createHeader(HSSFSheet sheet, List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto) {
		int rowNum = 0;
		int colNum = 0;

		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);

		HSSFCell cell;
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize("Expedient")));
		cell.setCellStyle(headerStyle);
		
		Iterator<Entry<String, DadaIndexadaDto>> it = expedientsConsultaDissenyDto.get(0).getDadesExpedient().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, DadaIndexadaDto> e = (Map.Entry<String, DadaIndexadaDto>)it.next();
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(e.getValue().getEtiqueta())));
			cell.setCellStyle(headerStyle);
		}
	}
	
	private void exportXLS(HttpServletRequest request, HttpServletResponse response, HttpSession session, List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto) {
		wb = new HSSFWorkbook();
	
		bold = wb.createFont();
		bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(HSSFColor.WHITE.index);
		
		greyFont = wb.createFont();
		greyFont.setColor(HSSFColor.GREY_25_PERCENT.index);
		greyFont.setCharSet(HSSFFont.ANSI_CHARSET);
		
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
		HSSFSheet sheet = wb.createSheet("Hoja 1");
	
		if (!expedientsConsultaDissenyDto.isEmpty())
			createHeader(sheet, expedientsConsultaDissenyDto);
	
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
				
				sheet.autoSizeColumn(colNum);
				HSSFCell cell = xlsRow.createCell(colNum++);
				cell.setCellValue(titol);
				cell.setCellStyle(dStyle);
				
				Iterator<Entry<String, DadaIndexadaDto>> it = dades.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, DadaIndexadaDto> e = (Map.Entry<String, DadaIndexadaDto>)it.next();
					sheet.autoSizeColumn(colNum);
					cell = xlsRow.createCell(colNum++);
					DadaIndexadaDto val = e.getValue();
					cell.setCellValue(StringEscapeUtils.unescapeHtml(val.getValorMostrar()));
					cell.setCellStyle(dStyle);
				}
			} catch (Exception e) {
				logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum + " - amb ID: " + expedientConsultaDissenyDto.getExpedient().getId(), e);
			}
		}
		
		try {
			String fileName = "Informe.xls";
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
			logger.error("Mesures temporals: No s'ha pogut realitzar la exportació.");
		}
	}

	@RequestMapping(value = "/{consultaId}/selection", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> seleccio(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@RequestParam(value = "ids", required = false) String ids) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioInforme(consultaId);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioInforme(seleccio, consultaId);
		}
		if (ids != null) {
			String[] idsparts = (ids.contains(",")) ? ids.split(",") : new String[] {ids};
			for (String id: idsparts) {
				try {
					long l = Long.parseLong(id.trim());
					if (l >= 0) {
						seleccio.add(l);
					} else {
						seleccio.remove(-l);
					}
				} catch (NumberFormatException ex) {}
			}
		}
		return seleccio;
	}

	@RequestMapping(value = "/{consultaId}/seleccioTots")
	@ResponseBody
	public Set<Long> seleccionarTots(
			HttpServletRequest request,
			@PathVariable Long consultaId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		Object filtreCommand = getFiltreCommand(request, consultaId);
		
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
		
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
				campsFiltre,
				filtreCommand,
				true);

		List<Long> ids = expedientService.findIdsPerConsultaInforme(
			consultaId,
			getValorsPerService(filtreCommand,campsFiltre, valors),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesPendents"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarAnulats"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarTasquesPersonals"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarTasquesGrup"));
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioInforme(consultaId);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioInforme(seleccio, consultaId);
		}
		if (ids != null) {
			for (Long id: ids) {
				try {
					if (id >= 0) {
						seleccio.add(id);
					} else {
						seleccio.remove(-id);
					}
				} catch (NumberFormatException ex) {}
			}
			
			Iterator<Long> iterador = seleccio.iterator();
			while( iterador.hasNext() ) {
				if (!ids.contains(iterador.next())) {
					iterador.remove();
				}
			}
		}
		return seleccio;
	}

	@RequestMapping(value = "/{consultaId}/seleccioNetejar")
	@ResponseBody
	public Set<Long> seleccioNetejar(
			HttpServletRequest request,
			@PathVariable Long consultaId) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioInforme(consultaId);
		ids.clear();
		return ids;
	}

	@ModelAttribute("valorsBoolea")
	public List<ParellaCodiValorDto> valorsBoolea(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("true", getMessage(request, "comuns.si")));
		resposta.add(new ParellaCodiValorDto("false", getMessage(request, "comuns.no")));
		return resposta;
	}

	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> valors12(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i=0; i <= 12 ; i++)		
			resposta.add(new ParellaCodiValorDto(String.valueOf(i), i));
		return resposta;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInformeController.class);
}
