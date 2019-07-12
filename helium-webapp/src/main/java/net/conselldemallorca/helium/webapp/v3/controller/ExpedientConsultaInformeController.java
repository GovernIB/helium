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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
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

import net.conselldemallorca.helium.report.FieldValue;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.helper.InformeHelper;
import net.conselldemallorca.helium.webapp.v3.helper.InformeHelper.Estat;
import net.conselldemallorca.helium.webapp.v3.helper.InformeHelper.InformeInfo;
import net.conselldemallorca.helium.webapp.v3.helper.JasperReportsHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient/consulta")
public class ExpedientConsultaInformeController extends BaseExpedientController {
	
	@Resource
	InformeHelper informeHelper;
	@Resource
	JasperReportsHelper jasperReportsHelper;
	@Autowired
	ExpedientTipusService expedientTipusService;

	/** maxim  **/
	private final static int MAX_ORDER_COLUMNS = 5;

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
		model.addAttribute("consultaId", consultaId);
		return "v3/expedientConsultaInformeParams";
	}
	
	/** Mètode post amb un formulari de paràmetres. */
	@RequestMapping(value = "/{consultaId}/informeParams", method = RequestMethod.POST)
	@ResponseBody
	public  InformeInfo  informeParamsAsync(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			@Valid @ModelAttribute("expedientInformeParametrosCommand") Object parametrosCommand,			
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			HttpSession session,
			Model model)  throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {

		// Mira si l'usuari ja té una generació d'informe en curs
		InformeInfo ret = informeHelper.getConsulta(session.getId());
		
		if (Estat.NO_TROBAT.equals(ret.getEstat())) {
			ret.setId(request.getSession().getId());
			ret.setEstat(Estat.INICIALITZANT);
			informeHelper.setConsulta(session.getId(), ret);
			
			Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
					expedientService.findConsultaInformeParams(consultaId),
					parametrosCommand,
					true);
			InformeInfo info = new InformeHelper().new InformeInfo();
			info.setEstat(Estat.INICIALITZANT);
			info.setId(request.getSession().getId());
			informeHelper.setConsulta(info.getId(), info);
			
			info =	generarReport(
							info,
							consultaId,
							model,
							request,
							valors);		
		}
		return ret;
	}
	
	/** Generació de l'informe asíncrona. Retorna un objecte JSON amb informaicó per consultar com va la generació de 
	 * l'informe.
	 */
	@RequestMapping(value = "/{consultaId}/informeAsync", method = RequestMethod.GET)
	@ResponseBody
	public InformeInfo informeAsync(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		
		// Mira si l'usuari ja té una generació d'informe en curs
		InformeInfo ret = informeHelper.getConsulta(session.getId());
		
		if (Estat.NO_TROBAT.equals(ret.getEstat())) {
			ret.setId(request.getSession().getId());
			ret.setEstat(Estat.INICIALITZANT);
			informeHelper.setConsulta(session.getId(), ret);
			ret = generarReport(
					ret,
					consultaId,
					model,
					request,
					null);
		}
		return ret;
	}
	
	/** Mètode per consultar la informació de l'estat actual de la generació de l'informe.
	 */
	@RequestMapping(value = "/{consultaId}/informeAsync/info", method = RequestMethod.GET)
	@ResponseBody
	public InformeInfo informeAsyncInfo(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		
		InformeInfo info = informeHelper.getConsulta(request.getSession().getId());
		switch(info.getEstat()) {
			case CANCELLAT:
			case ERROR:
				informeHelper.setConsulta(info.getId(), null);
				jasperReportsHelper.setJasperPrint(info, null);
				break;
			default:
				break;
			}
		return info;
	}	
	
	/** Mètode per cancel·lar la generació d'una consulta de forma asíncrona.
	 */
	@RequestMapping(value = "/{consultaId}/informeAsync/cancellar", method = RequestMethod.GET)
	@ResponseBody
	public InformeInfo informeAsyncCancellar(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {

		InformeInfo info = informeHelper.getConsulta(request.getSession().getId());
		try {
			if (Estat.GENERANT.equals(info.getEstat())) {
				info = jasperReportsHelper.cancellar(info);
			}
			info.setEstat(Estat.CANCELLAT);
			informeHelper.setConsulta(info.getId(), null);
			jasperReportsHelper.setJasperPrint(info, null);
		} catch (JRException e) {
			String msg = getMessage(request, "error.consulta.informe.cancelacio", new Object[] {e.getMessage()});
			logger.error(msg, e);
			info.setEstat(Estat.ERROR);
			info.setMissatge(msg);
		}
		return info;
	}	
	
	/** Mètode per realitzar l'exportació de l'informe i esborrar la informació associada
	 */
	@RequestMapping(value = "/{consultaId}/informeAsync/exportar", method = RequestMethod.GET)
	@ResponseBody
	public void informeAsyncExportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			HttpSession session,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {

		InformeInfo info = informeHelper.getConsulta(request.getSession().getId());
		try {
			jasperReportsHelper.exportarReport(
					info,
					response);
			// Esborra la informació referent a la generació
			informeHelper.setConsulta(session.getId(), null);
			jasperReportsHelper.setJasperPrint(info, null);
		} catch(Exception e) {
			String msg = "Error exportant el report: " + e.getMessage();
			logger.error(msg, e);
			MissatgesHelper.error(request, msg);
			response.sendRedirect(request.getContextPath() +"/v3/expedient/consulta/" + consultaId);
		}
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
		int colNum = 0;
		for (ExpedientConsultaDissenyDto  expedientConsultaDissenyDto : expedientsConsultaDissenyDto) {
			try {
				HSSFRow xlsRow = sheet.createRow(rowNum++);
				colNum = 0;
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

				for (TascaDadaDto camp: informeCamps) {
					if(dades.containsKey(camp.getVarCodi())) {
						DadaIndexadaDto dada = dades.get(camp.getVarCodi());
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
		for(int i=0; i<colNum; i++)
			sheet.autoSizeColumn(i);

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

	/** Mètode per iniciar la generació d'un report. Consulta les dades i crida al JasperReportsHelper
	 * per iniciar en 2n pla la generació.
	 * @param session
	 * @param consultaId
	 * @param model
	 * @param request
	 * @return Retorna un objecte amb la informació de la generació de l'informe.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	private InformeInfo generarReport(
			InformeInfo info,
			Long consultaId,
			Model model,
			HttpServletRequest request,
			Map<String, Object> parametres) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		
		// Realitza la consulta de les dades
		Object filtreCommand = SessionHelper.getAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
		List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
		Map<String, Object> filtreValors = TascaFormHelper.getValorsFromCommand(
				campsFiltre,
				filtreCommand,
				true);
		SessionManager sessionManager = SessionHelper.getSessionManager(request);

		// Obté la llista d'ids d'expedients
		Set<Long> expedientsIds = sessionManager.getSeleccioInforme(consultaId);	
		List<Long> ids;
		if (expedientsIds == null || expedientsIds.size() == 0) {
			// Consulta amb filtre
			PaginaDto<Long> paginaIds = expedientService.consultaFindNomesIdsPaginat(
					consultaId,
					processarValorsFiltre(filtreCommand, campsFiltre, filtreValors),
					(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesPersonals"),
					(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesGrup"),
					(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesMeves"),
					(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
					false, //nomesErrors
					MostrarAnulatsDto.NO, //mostrarAnulats
					PaginacioHelper.getPaginacioDtoTotsElsResultats());
			ids = paginaIds.getContingut();
			expedientsIds = new HashSet<Long>(ids);
		} else {
			ids = new ArrayList<Long>(expedientsIds);
		}
		// Comprova que hi hagi resultats
		if (ids.isEmpty()) {
			String msg = getMessage(request, "error.consulta.informe.expedients.nonhiha");
			MissatgesHelper.error(request, msg);
			info.setEstat(Estat.ERROR);
			info.setMissatge(msg);
			return info;
		}		
		
		info.setNumeroRegistres(ids.size());
		
		// Fa una comporovació de que el procés no s'hagi cancel·lat
		if (Estat.CANCELLAT.equals(info.getEstat()))
				return info;
		
		// Consulta les dades
		// Itera sobre els resultats i recupera les dades
		int n = 0;
		int total = ids.size();
		int inc = 2000;
		PaginaDto<ExpedientConsultaDissenyDto> paginaExpedients;
		List<Map<String, FieldValue>> dadesDataSource =  new ArrayList<Map<String, FieldValue>>();
		
		// Afegeix l'ordre d'ordenació segons les columnes definides a la consulta
		PaginacioParamsDto paginacio = PaginacioHelper.getPaginacioDtoTotsElsResultats();
		List<TascaDadaDto> campsConsulta = expedientService.findConsultaInforme(consultaId);	
		
		for (int i = 0; i < ExpedientConsultaInformeController.MAX_ORDER_COLUMNS; i++) {
			if(i >= campsConsulta.size() || campsConsulta.isEmpty())
				break;
			paginacio.afegirOrdre(campsConsulta.get(i).getVarCodi(),OrdreDireccioDto.ASCENDENT);
		}

		while (n < total) {
			expedientsIds = new HashSet<Long>(ids.subList(n, Math.min(n + inc, total)));
			n = n + inc;
			paginaExpedients = expedientService.consultaFindPaginat(
					consultaId,
					processarValorsFiltre(filtreCommand, campsFiltre, filtreValors),
					expedientsIds,
					(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesPersonals"),
					(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesTasquesGrup"),
					(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesMeves"),
					(Boolean)PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
					false, //nomesErrors
					MostrarAnulatsDto.NO, //mostrarAnulats
					paginacio);
			dadesDataSource.addAll(
					getDadesDatasource(request, paginaExpedients.getContingut()));
		}
		
		byte[] report;
		HashMap<String, byte[]> subreports = null;
		ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
		String extensio = consulta.getInformeNom().substring(
				consulta.getInformeNom().lastIndexOf(".") + 1).toLowerCase();
		String nom = consulta.getInformeNom().substring(0,	
				consulta.getInformeNom().lastIndexOf("."));
		
		if ("zip".equals(extensio)) {
			HashMap<String, byte[]> reports = unZipReports(consulta
					.getInformeContingut());
			report = reports.get(nom + ".jrxml");
			reports.remove(nom + ".jrxml");
			subreports = reports;
		} else {
			report = consulta.getInformeContingut();
		}
		
		try {
			info = jasperReportsHelper.generarReport(
				info,
				consulta.getCodi(),
				dadesDataSource,
				report,
				subreports,
				parametres,
				consulta.getFormatExport());
		} catch (Exception e) {
			String msg = getMessage(request, "error.consulta.informe.generacio", 
						new Object[] {(consulta != null? consulta.getCodi() : "[null]"), e.getMessage()});
			logger.error(msg, e);
			info.setEstat(Estat.ERROR);
			info.setMissatge(msg);
		}
		return info;
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

//			// Per simular x20 les dades recuperades
//			Map<String, FieldValue> mapAux = new HashMap<String, FieldValue>();
//			for (int i=0;i<19;i++){
//				mapAux = new HashMap<String, FieldValue>();
//				for (String key : mapFila.keySet())
//					mapAux.put(key, mapFila.get(key));
//				
//				dadesDataSource.add(mapAux);
//			}
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
