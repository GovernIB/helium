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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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

import net.conselldemallorca.helium.report.FieldValue;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.webapp.mvc.JasperReportsView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient/consulta")
public class ExpedientConsultaInformeController extends BaseExpedientController {

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
				true);
	}

	@RequestMapping(value = "/{consultaId}/excel", method = RequestMethod.GET)
	public void excel(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		Object filtreCommand = SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
		Map<String, Object> filtreValors = TascaFormHelper.getValorsFromCommand(
				campsFiltre,
				filtreCommand,
				true);
		PaginaDto<ExpedientConsultaDissenyDto> paginaExpedients = expedientService.consultaFindPaginat(
				consultaId,
				processarValorsFiltre(filtreCommand, campsFiltre, filtreValors),
				null,
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesPersonals"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesGrup"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesMeves"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
				false, //nomesErrors
				MostrarAnulatsDto.NO, //mostrarAnulats
				PaginacioHelper.getPaginacioDtoTotsElsResultats());
		generarExcel(
				request,
				response,
				paginaExpedients.getContingut(),
				consultaId);
	}

	@RequestMapping(value = "/{consultaId}/informeParams", method = RequestMethod.GET)
	public String informeParams(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS_PARAM);
		Object parametrosCommand = getFiltreParameterCommand(request, consultaId);
		SessionHelper.setAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS_PARAM, parametrosCommand);
		model.addAttribute("expedientInformeParametrosCommand", parametrosCommand);
		model.addAttribute("campsInformeParams", expedientService.findConsultaInformeParams(consultaId));			
		return "v3/expedientConsultaInformeParams";
	}

	@RequestMapping(value = "/{consultaId}/informeParams", method = RequestMethod.POST)
	public  String  mostrarInformeParams(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@Valid @ModelAttribute("expedientInformeParametrosCommand") Object parametrosCommand,			
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			HttpSession session,
			Model model)  throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
				expedientService.findConsultaInformeParams(consultaId),
				parametrosCommand,
				true);
		model.addAttribute(JasperReportsView.MODEL_ATTRIBUTE_PARAMS, valors);
		return generarReport(
				session,
				consultaId,
				model,
				request);
	}

	@RequestMapping(value = "/{consultaId}/informe", method = RequestMethod.GET)
	public String informe(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		return generarReport(
				session,
				consultaId,
				model,
				request);
	}

	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> valors12(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i = 0; i <= 12; i++) {
			resposta.add(
					new ParellaCodiValorDto(
							String.valueOf(i),
							i));
		}
		return resposta;
	}
	@ModelAttribute("valorsBoolea")
	public List<ParellaCodiValorDto> valorsBoolea(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("true", getMessage(request, "comuns.si")));
		resposta.add(new ParellaCodiValorDto("false", getMessage(request, "comuns.no")));
		return resposta;
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



	private void generarExcel(
			HttpServletRequest request,
			HttpServletResponse response,
			List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto,
			Long consultaId) {
		
		List<TascaDadaDto> informeCamps = expedientService.findConsultaInforme(consultaId);
		
		HSSFWorkbook wb;
		HSSFCellStyle cellStyle;
		HSSFCellStyle dStyle;
		HSSFFont bold;
		HSSFCellStyle cellGreyStyle;
		HSSFCellStyle greyStyle;
		HSSFCellStyle dGreyStyle;
		HSSFFont greyFont;
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
		
		greyStyle = wb.createCellStyle();
		greyStyle.setFont(greyFont);
	
		DataFormat format = wb.createDataFormat();
		dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));
	
		dGreyStyle = wb.createCellStyle();
		dGreyStyle.setFont(greyFont);
		dGreyStyle.setDataFormat(format.getFormat("0.00"));
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		if (!expedientsConsultaDissenyDto.isEmpty())
			createHeader(
					wb,
					sheet,
					informeCamps);
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

				for (TascaDadaDto camp: informeCamps) {
					if(dades.containsKey(camp.getVarCodi())) {
						DadaIndexadaDto dada = dades.get(camp.getVarCodi());
						sheet.autoSizeColumn(colNum);
						cell = xlsRow.createCell(colNum++);
						if (camp.getCampTipus().equals(CampTipusDto.INTEGER) || camp.getCampTipus().equals(CampTipusDto.FLOAT) || camp.getCampTipus().equals(CampTipusDto.PRICE) ) {
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							if(dada.getValor() != null) {
								if( camp.getCampTipus().equals(CampTipusDto.INTEGER)) {
									cell.setCellValue((Long) dada.getValor());
								} else if (camp.getCampTipus().equals(CampTipusDto.FLOAT)) {
									cell.setCellValue((Double) dada.getValor());
									cell.setCellStyle(dStyle);
								} else if (camp.getCampTipus().equals(CampTipusDto.PRICE)) {
									cell.setCellValue(((BigDecimal) dada.getValor()).doubleValue());
									cell.setCellStyle(dStyle);
								} else {
									cell.setCellValue(dada.getValorMostrar());
								}
							}
						} else {
							cell.setCellValue(StringEscapeUtils.unescapeHtml(dada.getValorMostrar()));
							cell.setCellStyle(dStyle);
						}
					}
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

	private void createHeader(
			HSSFWorkbook wb,
			HSSFSheet sheet,
			List<TascaDadaDto> informeCamps) {
		HSSFFont bold;
		bold = wb.createFont();
		bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(HSSFColor.WHITE.index);
		HSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
		headerStyle.setFont(bold);
		int rowNum = 0;
		int colNum = 0;
		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);
		HSSFCell cell;
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize("Expedient")));
		cell.setCellStyle(headerStyle);
		for (TascaDadaDto camp : informeCamps) {
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(camp.getCampEtiqueta())));
			cell.setCellStyle(headerStyle);
		}
	}

	private String generarReport(
			HttpSession session,
			Long consultaId,
			Model model,
			HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		Object filtreCommand = SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
		Map<String, Object> filtreValors = TascaFormHelper.getValorsFromCommand(
				campsFiltre,
				filtreCommand,
				true);
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> expedientsIds = sessionManager.getSeleccioInforme(consultaId);
		PaginaDto<ExpedientConsultaDissenyDto> paginaExpedients = expedientService.consultaFindPaginat(
				consultaId,
				processarValorsFiltre(filtreCommand, campsFiltre, filtreValors),
				expedientsIds,
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesPersonals"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesGrup"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesMeves"),
				(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
				false, //nomesErrors
				MostrarAnulatsDto.NO, //mostrarAnulats
				PaginacioHelper.getPaginacioDtoTotsElsResultats());
		if (paginaExpedients.getContingut().isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.consulta.informe.expedients.nonhiha"));
			return "redirect:/v3/expedient/consulta/" + consultaId;
		}
		model.addAttribute(
				JasperReportsView.MODEL_ATTRIBUTE_REPORTDATA,
				getDadesDatasource(
						request,
						paginaExpedients.getContingut()));
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

	private HashMap<String, byte[]> unZipReports(byte[] zipContent) throws IOException {
		byte[] buffer = new byte[4096];
		HashMap<String, byte[]> docs = new HashMap<String, byte[]>();
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
		return docs;
	}
	
	private List<Map<String, FieldValue>> getDadesDatasource(
			HttpServletRequest request,
			List<ExpedientConsultaDissenyDto> expedients) {
		List<Map<String, FieldValue>> dadesDataSource = new ArrayList<Map<String, FieldValue>>();
		for (ExpedientConsultaDissenyDto dadesExpedient: expedients) {
			ExpedientDto expedient = dadesExpedient.getExpedient();
			Map<String, FieldValue> mapFila = new HashMap<String, FieldValue>();
			for (String clau: dadesExpedient.getDadesExpedient().keySet()) {
				DadaIndexadaDto dada = dadesExpedient.getDadesExpedient().get(clau);
				String fieldName = dada.getReportFieldName();
				if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT_JSP.equals(clau)) 
					fieldName = ExpedientCamps.EXPEDIENT_CAMP_ESTAT.replace('$', '%');
				mapFila.put(fieldName, toReportField(request, expedient, dada));
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

	private Map<String, Object> processarValorsFiltre(
			Object filtreCommand,
			List<TascaDadaDto> dadesFiltre,
			Map<String, Object> valors) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> valorsPerService = new HashMap<String, Object>();
		for (TascaDadaDto dada: dadesFiltre) {
			String clau = (dada.getDefinicioProcesKey() == null) ? dada.getVarCodi() : dada.getDefinicioProcesKey() + "." + dada.getVarCodi();
			clau = clau.replace(
					ExpedientCamps.EXPEDIENT_PREFIX_JSP,
					ExpedientCamps.EXPEDIENT_PREFIX);
			if (CampTipusDto.BOOLEAN.equals(dada.getCampTipus()) && PropertyUtils.isReadable(filtreCommand, dada.getVarCodi())) {
				Boolean valor = (Boolean) PropertyUtils.getSimpleProperty(
						filtreCommand,
						dada.getVarCodi());
				valors.put(
						dada.getVarCodi(),
						valor);
			}
			valorsPerService.put(
					clau,
					valors.get(dada.getVarCodi()));
		}
		return valorsPerService;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientConsultaInformeController.class);

}
