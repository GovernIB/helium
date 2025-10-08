package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.sun.star.awt.Command;

import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExportException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientConsultaCommand;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientLlistatController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);		
		if (filtreCommand.isConsultaRealitzada()) {
			omplirModelGet(request, model);
		}
		return "v3/expedientLlistat";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ExpedientConsultaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.getSessionManager(request).removeFiltreConsultaGeneral();
		} else {
			ExpedientConsultaCommand filtreAnterior = SessionHelper.getSessionManager(request).getFiltreConsultaGeneral();
			if (filtreAnterior != null && filtreCommand.getExpedientTipusId() != null) {
				if (!filtreCommand.getExpedientTipusId().equals(filtreAnterior.getExpedientTipusId())) {
					// Netejar selecció d'expedients
					SessionManager sessionManager = SessionHelper.getSessionManager(request);
					Set<Long> ids = sessionManager.getSeleccioConsultaGeneral();
					if (ids != null)
						ids.clear();
				}
			}
			SessionHelper.getSessionManager(request).setFiltreConsultaGeneral(filtreCommand);
		}
		return "redirect:expedient";
	}
	
	private Set<Long> recuperarIdsAccionesMasivas(HttpServletRequest request) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		return sessionManager.getSeleccioConsultaGeneral();
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesPagina<ExpedientDto> datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		SessionHelper.getSessionManager(request).setFiltreConsultaGeneral(filtreCommand);
		DatatablesPagina<ExpedientDto> result = null;
		
		Set<Long> idsSeleccionats = null;
		if (filtreCommand.isNomesSeleccio()) {
			idsSeleccionats = recuperarIdsAccionesMasivas(request);
		}
		if (idsSeleccionats == null) {
			idsSeleccionats = new HashSet<Long>();
		}
		
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = 
	            (List<ExpedientTipusDto>) SessionHelper.getAttribute(request, SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);

		try {
			PaginaDto<ExpedientDto> pagina = expedientService.findAmbFiltrePaginat(
	                expedientTipusDtoAccessibles,
	                entornActual.getId(),
	                filtreCommand.getExpedientTipusId(),
	                filtreCommand.getTitol(),
	                filtreCommand.getNumero(),
	                filtreCommand.getUnitatOrganitzativaCodi(),
	                filtreCommand.getDataIniciInicial(),
	                filtreCommand.getDataIniciFinal(),
	                filtreCommand.getDataFiInicial(),
	                filtreCommand.getDataFiFinal(),
	                filtreCommand.getEstatTipus(),
	                filtreCommand.getEstatId(),
	                filtreCommand.getGeoPosX(),
	                filtreCommand.getGeoPosY(),
	                filtreCommand.getGeoReferencia(),
	                filtreCommand.getRegistreNumero(),
	                filtreCommand.isNomesTasquesPersonals(),
	                filtreCommand.isNomesTasquesGrup(),
	                filtreCommand.isNomesAlertes(),
	                filtreCommand.isNomesErrors(),
	                filtreCommand.isNomesErrorsArxiu(),
	                filtreCommand.getMostrarAnulats(),
	                idsSeleccionats,
	                PaginacioHelper.getPaginacioDtoFromDatatable(request)
	        );
			
	        result = PaginacioHelper.getPaginaPerDatatables(request, pagina);
			
		} catch (Exception e) {
			if (entornActual == null)
				MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"), e);
			else {
				MissatgesHelper.error(request, e.getMessage(), e);
				logger.error("No se pudo obtener la lista de expedientes", e);
			}
			result = PaginacioHelper.getPaginaPerDatatables(
					request,
					new PaginaDto<ExpedientDto>());
		}
		return result;
	}
	
	@RequestMapping(value = "/selection", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> seleccio(
			HttpServletRequest request,
			@RequestParam(value = "ids", required = false) String ids) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
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

	@RequestMapping(value = "/seleccioTots")
	@ResponseBody
	public Set<Long> seleccioTots(HttpServletRequest request) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		List<Long> ids = expedientService.findIdsAmbFiltre(
				entornActual.getId(),
				filtreCommand.getExpedientTipusId(),
				filtreCommand.getTitol(),
				filtreCommand.getNumero(),
				filtreCommand.getUnitatOrganitzativaCodi(),
				filtreCommand.getDataIniciInicial(),
				filtreCommand.getDataIniciFinal(),
				filtreCommand.getDataFiInicial(),
				filtreCommand.getDataFiFinal(),
				filtreCommand.getEstatTipus(),
				filtreCommand.getEstatId(),
				filtreCommand.getGeoPosX(),
				filtreCommand.getGeoPosY(),
				filtreCommand.getGeoReferencia(),
				filtreCommand.getRegistreNumero(),
				filtreCommand.isNomesTasquesPersonals(),
				filtreCommand.isNomesTasquesGrup(),
				filtreCommand.isNomesAlertes(),
				filtreCommand.isNomesErrors(),
				filtreCommand.isNomesErrorsArxiu(),
				filtreCommand.getMostrarAnulats());		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
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

	@ModelAttribute("anulats")
	public List<ParellaCodiValorDto> populateAnulats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.no"), MostrarAnulatsDto.NO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si"), MostrarAnulatsDto.SI));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si.only"), MostrarAnulatsDto.NOMES_ANULATS));
		return resposta;
	}
	
	@RequestMapping(value = "/consultas/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ConsultaDto> consultasTipus(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornActual.getId(),expedientTipusId);
	}

	@RequestMapping(value = "/seleccioNetejar")
	@ResponseBody
	public Set<Long> seleccioNetejar(HttpServletRequest request) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioConsultaGeneral();
		ids.clear();
		return ids;
	}

	@RequestMapping(value = "/estatsPerTipus/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public List<EstatDto> estatsPerExpedientTipus(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId) {
		return expedientTipusService.estatFindAll(expedientTipusId, true);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/descarregardades", method = RequestMethod.GET)
	@ResponseBody
	public void descarregarDades(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioConsultaGeneral();
		if (ids == null ||ids.isEmpty()) {
			String msgErr = getMessage(request, "error.no.exp.selec");
			MissatgesHelper.error(request, msgErr);
			throw new ExportException(msgErr);
		}
		if (expedientService.isDiferentsTipusExpedients(ids)) {
			String msgErr = getMessage(request, "error.no.exp.selec.diferenttipus");
			MissatgesHelper.error(request, msgErr);
			throw new ExportException(msgErr);
		}

		try {
			List<ExpedientConsultaDissenyDto> expedients = expedientService.findExpedientsExportacio(new ArrayList<Long>(ids), entornActual);
			exportXLS(response, expedients, expedients.get(0).getTipus(), entornActual);
		} catch(Exception e) {
			logger.error("Error generant excel amb les dades dels expedients", e);
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.llistat.exportacio.error",
							new Object[]{e.getLocalizedMessage()}),
					e);
			throw(e);
		}
	}

	private void exportXLS(HttpServletResponse response, List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto, ExpedientTipusDto expTipus, EntornDto entornActual) {
		
		XSSFWorkbook wb = new XSSFWorkbook();

		DataFormat format = wb.createDataFormat();
		XSSFCellStyle dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));

		DataFormat bdFormat = wb.createDataFormat();
		XSSFCellStyle bdStyle = wb.createCellStyle();
		bdStyle.setDataFormat(bdFormat.getFormat("#,##0.00"));

		DataFormat intFormat = wb.createDataFormat();
		XSSFCellStyle iStyle = wb.createCellStyle();
		iStyle.setDataFormat(intFormat.getFormat("0"));

		XSSFCellStyle cellDateStyle = wb.createCellStyle();
		CreationHelper createHelper = wb.getCreationHelper();
		cellDateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
		
		if(!expTipus.isAmbInfoPropia()) {
			

			List<DefinicioProcesDto> definicionsProces = expedientTipusService.definicioFindDefinicionsProcDarreraVersio(expTipus, entornActual);
			for(DefinicioProcesDto defProc : definicionsProces) {
				String sheetName = defProc.getJbpmKey();
				XSSFSheet sheet = wb.createSheet(sheetName);
				if(defProc.getJbpmKey().equals(expTipus.getJbpmProcessDefinitionKey())){
					wb.setSheetOrder(sheetName, 0);//Primera fulla la del procés principal
				}
				createBook(sheetName, sheet, wb, expedientsConsultaDissenyDto, dStyle, iStyle, bdStyle, cellDateStyle);
			}
		}
		else {
			String sheetName = expTipus.getJbpmProcessDefinitionKey();
			XSSFSheet sheet = wb.createSheet(sheetName);
			createBook(sheetName, sheet, wb, expedientsConsultaDissenyDto, dStyle, iStyle, bdStyle, cellDateStyle);
		}

		try {
			String fileName = "Exportacio_expedients.xls";
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentType("application/vnd.ms-excel;base64");
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			wb.write(byteArrayOutputStream);
			String encodedExcel = new String(Base64.encodeBase64(byteArrayOutputStream.toByteArray()));
			response.getOutputStream().write(encodedExcel.getBytes());
		} catch (Exception e) {
			logger.error("No s'ha pogut generar l'excel del llistat d'expedients.");
		}
	}
	
	private void createBook(String sheetName, 
			XSSFSheet sheet,
			XSSFWorkbook wb, 
			List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto, 
			XSSFCellStyle dStyle, 
			XSSFCellStyle iStyle,
			XSSFCellStyle bdStyle, 
			XSSFCellStyle cellDateStyle) {		
		int numCols = 0;
		int rowNum = 1;

		if (!expedientsConsultaDissenyDto.isEmpty()) {
			numCols = createHeader(sheet, expedientsConsultaDissenyDto, sheetName);
		}
		for (ExpedientConsultaDissenyDto  expedientConsultaDissenyDto : expedientsConsultaDissenyDto) {
			int colNum = 0;			
			try {
				XSSFRow xlsRow = sheet.createRow(rowNum++);
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

				XSSFCell cell = xlsRow.createCell(colNum++);
				cell.setCellValue(titol);
				cell.setCellStyle(dStyle);

				Iterator<Map.Entry<String, DadaIndexadaDto>> it = dades.entrySet().iterator();
				while (it.hasNext()) {		
					Map.Entry<String, DadaIndexadaDto> e = (Map.Entry<String, DadaIndexadaDto>)it.next();
					DadaIndexadaDto val = e.getValue();
					String dadaKey = e.getKey();
					if(dadaKey.contains("/")) {
						String[] spliDadatKey = (e.getKey()).split("/");
						if(spliDadatKey!=null && spliDadatKey[0].equals(sheetName)) {
							addValues(val, cell, xlsRow, colNum++, iStyle, bdStyle, cellDateStyle, dStyle, e);
						}
					}
					else {
						addValues(val, cell, xlsRow, colNum++, iStyle, bdStyle, cellDateStyle, dStyle, e);
					}
				}
				for (int c=0; c<numCols; c++) {
					sheet.autoSizeColumn(c);
				}	
			} catch (Exception e) {
				logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum + " - amb ID: " + expedientConsultaDissenyDto.getExpedient().getId(), e);
			}
		}
	}
	
	private int createHeader(XSSFSheet sheet, List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto, String sheetName) {

		XSSFFont bold = sheet.getWorkbook().createFont();
		bold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(IndexedColors.WHITE.getIndex());

		XSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
		headerStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
		headerStyle.setFont(bold);

		int rowNum = 0;
		int colNum = 0;

		// Capçalera
		XSSFRow xlsRow = sheet.createRow(rowNum++);

		XSSFCell cell;

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize("Expedient")));
		cell.setCellStyle(headerStyle);

		Iterator<Map.Entry<String, DadaIndexadaDto>> it = expedientsConsultaDissenyDto.get(0).getDadesExpedient().entrySet().iterator();
		int numCols = 1;
		while (it.hasNext()) {
			Map.Entry<String, DadaIndexadaDto> e = (Map.Entry<String, DadaIndexadaDto>)it.next();
			if(e.getKey().contains("/")) {
				String[] splitKey = (e.getKey()).split("/");
				if(splitKey!=null && splitKey[0].equals(sheetName)) {
					numCols++;
					sheet.autoSizeColumn(colNum);
					cell = xlsRow.createCell(colNum++);
					cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(e.getValue().getEtiqueta())));
					cell.setCellStyle(headerStyle);
				}
			} else {
				numCols++;
				sheet.autoSizeColumn(colNum);
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(new XSSFRichTextString(StringUtils.capitalize(e.getValue().getEtiqueta())));
				cell.setCellStyle(headerStyle);
			}
		}
		return numCols;
	}
	
	private void addValues(DadaIndexadaDto val, 
			XSSFCell cell, 
			XSSFRow xlsRow, 
			int colNum, 
			XSSFCellStyle iStyle, 
			XSSFCellStyle bdStyle, 
			XSSFCellStyle cellDateStyle,
			XSSFCellStyle dStyle,
			Map.Entry<String, DadaIndexadaDto> e) {
		if (val!=null && val.getValor()!=null) {
			try {
				if (val.getValor() instanceof BigDecimal) {
					cell = xlsRow.createCell(colNum++, Cell.CELL_TYPE_NUMERIC);
					BigDecimal valorConvertit = (BigDecimal)e.getValue().getValor();
					String valor_s = new DecimalFormat("#,##0.00").format(valorConvertit);
					valor_s = valor_s.replace(".", "").replace(",", ".");
					if (valor_s!=null && !"".equals(valor_s)) {
						cell.setCellValue(Float.parseFloat(valor_s));
						cell.setCellStyle(bdStyle);
					} else {
						cell.setCellValue("");
					}
				} else if (val.getValor() instanceof Integer) {
					cell = xlsRow.createCell(colNum++, Cell.CELL_TYPE_NUMERIC);
					Integer valorConvertit = (Integer)e.getValue().getValor();
					cell.setCellValue(valorConvertit);
					cell.setCellStyle(iStyle);
				} else if (val.getValor() instanceof Float) {
					cell = xlsRow.createCell(colNum++, Cell.CELL_TYPE_NUMERIC);
					Float valorConvertit = (Float)e.getValue().getValor();
					cell.setCellValue(valorConvertit);
					cell.setCellStyle(dStyle);
				} else if (val.getValor() instanceof Long) {
					cell = xlsRow.createCell(colNum++, Cell.CELL_TYPE_NUMERIC);
					Long valorConvertit = (Long)e.getValue().getValor();
					cell.setCellValue(valorConvertit);
					cell.setCellStyle(iStyle);
				} else if (val.getValor() instanceof Double) {
					cell = xlsRow.createCell(colNum++, Cell.CELL_TYPE_NUMERIC);
					Double valorConvertit = (Double)e.getValue().getValor();
					cell.setCellValue(valorConvertit);
				} else if (val.getValor() instanceof Date) {
					cell = xlsRow.createCell(colNum++);
					Date valorConvertit = (Date)e.getValue().getValor();
					cell.setCellValue(valorConvertit);
					cell.setCellStyle(cellDateStyle);
				} else {
					cell = xlsRow.createCell(colNum++, Cell.CELL_TYPE_STRING);
					cell.setCellValue(StringEscapeUtils.unescapeHtml(val.getValorMostrar()));		
				}
			} catch (Exception ex) {
				//En un possible error de conversió, ho posam per defecte com a String
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(StringEscapeUtils.unescapeHtml(val.getValorMostrar()));	
			}
		} else {
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue("");	
		}
	}

	private void omplirModelGet(
			HttpServletRequest request,
			Model model) {
		ExpedientConsultaCommand filtreCommand = getFiltreCommand(request);
		if (filtreCommand.getExpedientTipusId() != null) {
			model.addAttribute(
					"estats",
					expedientTipusService.estatFindAll(
							filtreCommand.getExpedientTipusId(),
							true));
		}
	}

	private ExpedientConsultaCommand getFiltreCommand(
			HttpServletRequest request) {
		ExpedientConsultaCommand filtreCommand = SessionHelper.getSessionManager(request).getFiltreConsultaGeneral();
		if (filtreCommand == null) {
			filtreCommand = new ExpedientConsultaCommand();
			filtreCommand.setConsultaRealitzada(true);
			SessionHelper.getSessionManager(request).setFiltreConsultaGeneral(filtreCommand);
		}
		ExpedientTipusDto expedientTipusActual = SessionHelper.getSessionManager(request).getExpedientTipusActual();
		if (expedientTipusActual != null) {
			filtreCommand.setExpedientTipusId(expedientTipusActual.getId());
		} else if (filtreCommand.getExpedientTipusId()==null){
			filtreCommand.setExpedientTipusId(null);
		}
		if (filtreCommand.getExpedientTipusId() != null && !UsuariActualHelper.isAdministrador(SecurityContextHolder.getContext().getAuthentication()))
			// comprova l'accès de lectura al tipus d'expedient o si existeix només pels usuaris no administradors
			try {
				EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
				expedientTipusService.findAmbIdPermisConsultar(entornActual.getId(), filtreCommand.getExpedientTipusId());
			} catch(Exception e) {
				filtreCommand.setExpedientTipusId(null);
			}
		return filtreCommand;
	}

	protected static final Log logger = LogFactory.getLog(ExpedientLlistatController.class);
}
