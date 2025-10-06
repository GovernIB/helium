package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExportException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.NotificacioService;
import net.conselldemallorca.helium.webapp.v3.command.NotificacioFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import java.io.*;
import java.sql.*;
/**
 * Controlador per visualitzar la llista notificacions enviades a NOTIB.
 */
@Controller
@RequestMapping("/v3/notificacionsNotib")
public class NotificacionsController extends BaseExpedientController {
	
	@Autowired
	private NotificacioHelper notificacioHelper;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;

	private static final String SESSION_ATTRIBUTE_FILTRE = "NotificacionsController.session.filtre";
	
	/** Accés al llistat de notificacions des del menú Consultar a la capçalera. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(HttpServletRequest request, Model model) {
		
		NotificacioFiltreCommand filtreCommand = getFiltreCommand(request);
		List<ExpedientTipusDto> expedientTipusDtoAccessibles = null;
		
		ExpedientTipusDto expedientTipusActual = SessionHelper.getSessionManager(request).getExpedientTipusActual();
		if (expedientTipusActual != null) {
			filtreCommand.setTipusId(expedientTipusActual.getId());
		}
		
		//Si ets admin, no filtra per entorn actual.
		//ELs tipus de expedient del filtre son tots els accessibles
		if (UsuariActualHelper.isAdministrador(SecurityContextHolder.getContext().getAuthentication())) {
			
			expedientTipusDtoAccessibles = SessionHelper.getSessionManager(request).getExpedientTipusAccessibles();
			
		} else {
		
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			if (entornActual != null) {
				filtreCommand.setEntornId(entornActual.getId());
				expedientTipusDtoAccessibles = expedientTipusService.findAmbEntornPermisAdmin(entornActual.getId());
			}
	
			if (expedientTipusDtoAccessibles==null || expedientTipusDtoAccessibles.size()==0) {
				MissatgesHelper.error(request, "No teniu permís d'administració sobre cap tipus d'expedient dins l'entorn actual.");
				return "redirect:/";
			}
		}
		
		model.addAttribute(filtreCommand);
		this.modelEstats(model);
		this.modelTipusEnviament(model);
		this.modelExpedientsTipus(expedientTipusDtoAccessibles, model);
		return "v3/notificacionsNotibLlistat";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		
		NotificacioFiltreCommand filtreCommand = getFiltreCommand(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		filtreCommand.setEntornId(entornActual.getId());
		
		Map<String, String[]> mapeigOrdenacions = new HashMap<String, String[]>();
		mapeigOrdenacions.put("interessatFullNomNif", new String[] {"titularNom", "titularLlinatge1", "titularLlinatge2"});
		mapeigOrdenacions.put("destinatariNomILlinatges", new String[] {"destinatariNom", "destinatariLlinatge1", "destinatariLlinatge2"});
		mapeigOrdenacions.put("organEmissorCodiAndNom", new String[] {"emisorDir3Codi"});

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request, null, mapeigOrdenacions);
		PaginaDto<DocumentNotificacioDto> resultat = notificacioService.findAmbFiltrePaginat(
				ConversioTipusHelper.convertir(filtreCommand, DocumentNotificacioDto.class),
				paginacioParams);
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}
	
	/** Mètode quan s'envia el formulari del filtre. Actualitza el filtre en sessió. */
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid NotificacioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:notificacionsNotib";
	}
	
	@RequestMapping(value = "/{enviamentNotibId}/info", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long enviamentNotibId,
			Model model) {
		DocumentNotificacioDto dto = notificacioService.findAmbId(enviamentNotibId);
		notificacioHelper.completarDocumentNotificacioDto(dto);
		model.addAttribute("dto", dto);
		return "v3/notificacioNotibInfo";
	}
	
	@RequestMapping(value = "/{enviamentNotibId}/consultarEstat", method = RequestMethod.GET)
	public String consultarEstat(
			HttpServletRequest request,
			@PathVariable Long enviamentNotibId,
			Model model) {
		DocumentNotificacioDto dto = notificacioService.findAmbId(enviamentNotibId);
		//Si volem obtenir els documents dins zip hem de mirar les notificacions de l'expedient
//		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(dto.getExpedient().getId());
//		List<DadesNotificacioDto> notificacions = expedientService.findNotificacionsNotibPerExpedientId(expedient.getId());	
		if (dto != null) {
			try {
				// Processa el canvi d'estat
				expedientDocumentService.notificacioActualitzarEstat(
						dto.getEnviamentIdentificador(), 
						dto.getEnviamentReferencia());
				MissatgesHelper.success(request, getMessage(request, "expedient.notificacio.consultar.estat.success"));
			} catch (Exception e) {				
				String errMsg = getMessage(request, "expedient.notificacio.consultar.estat.error", new Object[] {e.getMessage()});
				logger.error(errMsg, e);
				MissatgesHelper.error(request, errMsg, e);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "expedient.notificacio.consultar.estat.not.found", new Object[] {enviamentNotibId}));
		}
		return "redirect:" + request.getHeader("referer");
	}
	
	/** Posa els expedients tipus al model als quals l'usuari té permís per consultar a l'entorn
	 * @param entornActual */
	private void modelExpedientsTipus(List<ExpedientTipusDto> expedientTipusDtoAccessibles, Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
			for(ExpedientTipusDto expedientTipus : expedientTipusDtoAccessibles)
				if (expedientTipus.isDistribucioActiu())
					opcions.add(new ParellaCodiValorDto(
							expedientTipus.getId().toString(),
							String.format("%s - %s", expedientTipus.getCodi(), expedientTipus.getNom())));		
		
		model.addAttribute("expedientsTipus", opcions);
	}
	
	
	/** Mètode per obtenir o inicialitzar el filtre del formulari de cerca.
	 * @param request
	 * @return
	 */
	private NotificacioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		NotificacioFiltreCommand filtreCommand = (NotificacioFiltreCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new NotificacioFiltreCommand();
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	/** Posa els valors de l'enumeració estats en el model */
	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(NotificacioEstatEnumDto estat : NotificacioEstatEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					estat.name(),
					MessageHelper.getInstance().getMessage("notificacio.etst.enum." + estat.name())));		

		model.addAttribute("estats", opcions);
	}
	
	/** Posa els valors de l'enumeració dels tipus d'enviament en el model */
	private void modelTipusEnviament(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(EnviamentTipusEnumDto tipus : EnviamentTipusEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					tipus.name(),
					MessageHelper.getInstance().getMessage("notifica.enviament.tipus.enum." + tipus.name())));		

		model.addAttribute("tipusEnviaments", opcions);
	}
	
	/** Mètode pel suggest d'expedients inicial
	 * 
	 * @param text
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/suggest/expedient/inici/{expedientId}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public Map<String, String> suggestExpedientInici(
			@PathVariable String expedientId,
			Model model) {
		Map<String, String> resultat = null;
		ExpedientDto expedient = null;
		if (expedientId != null) {
			expedient = expedientService.findAmbId(Long.valueOf(expedientId));
			if (expedient != null) {
				resultat = new HashMap<String, String>();
				resultat.put("codi", String.valueOf(expedient.getId()));
				resultat.put("nom", expedient.getIdentificadorLimitat());
			}
		}
		return resultat;
	}

	/** Mètode per cercar un expedient per número o títol per a un control de tipus suggest
	 * 
	 * @param text
	 * 			Tetxt per filtrar.
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/suggest/expedient/llista/{expedientTipusId}/**", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public List<Map<String, String>> suggestExpedientLlista(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		List<Map<String, String>> resultat = new ArrayList<Map<String,String>>();
		Map<String, String> item;
		List<ExpedientDto> llista = new ArrayList<ExpedientDto>();
		if (expedientTipusId != null) {
			String requestURL = request.getRequestURL().toString();
			String[] text = requestURL.split("/suggest/expedient/llista/" + expedientTipusId + "/");
			if (text.length > 1) {
				llista = expedientService.findPerSuggest(expedientTipusId, text[1]);
			}
		}
		for (ExpedientDto expedientDto: llista) {
			item = new HashMap<String, String>();
			item.put("codi", String.valueOf(expedientDto.getId()));
			item.put("nom", expedientDto.getIdentificadorLimitat());
			resultat.add(item);
		}
		return resultat;

	}
	
	private final int MAX_FILES = 1000;
	@RequestMapping(value = "/descarregardades", method = RequestMethod.GET)
	@ResponseBody
	public void descarregarDades(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {


		try {
			NotificacioFiltreCommand filtreCommand = getFiltreCommand(request);
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			filtreCommand.setEntornId(entornActual.getId());
			
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(MAX_FILES);
			paginacioParams.afegirOrdre("enviatData", OrdreDireccioDto.ASCENDENT);
			PaginaDto<DocumentNotificacioDto> resultat = notificacioService.findAmbFiltrePaginat(
					ConversioTipusHelper.convertir(filtreCommand, DocumentNotificacioDto.class),
					paginacioParams);
			if (resultat.getElementsTotal() > MAX_FILES) {
				MissatgesHelper.warning(request, "S'han obtingut " + resultat.getTotal() + " resultats, només es descarregaran les " + MAX_FILES + " primeres files.");
				
			}
			List<DocumentNotificacioDto> notificacions = resultat.getContingut();
			exportXLS(response, notificacions);
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
	
	private void exportXLS(HttpServletResponse response, List<DocumentNotificacioDto> notificacions) {

		Workbook workbook = null;
		OutputStream out = null;

		try {
			// Crear workbook y fulla
			workbook = new XSSFWorkbook();                     // XSSF = formato .xlsx
			Sheet sheet = workbook.createSheet("Notificacions");

			//Estil per Capçaleres
			Font headerFont = workbook.createFont();
			headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD); // Apache POI 3.7: setBoldweight

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(headerFont);
			
			// Estil per a dates
			CellStyle dateStyle = workbook.createCellStyle();
			short dateFormat = workbook.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss");
			dateStyle.setDataFormat(dateFormat);

			// Capçalera
			int rowIndex = 0;
			Row headerRow = sheet.createRow(rowIndex++);
			Cell cell;

			cell = headerRow.createCell(0);
			cell.setCellValue("Expedient Tipus Nom");
			cell.setCellStyle(headerStyle);

			cell = headerRow.createCell(1);
			cell.setCellValue("Numero Expedient");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(2);
			cell.setCellValue("Data Enviament");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(3);
			cell.setCellValue("Òrgan Emissor");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(4);
			cell.setCellValue("Interessat");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(5);
			cell.setCellValue("Tipus Enviament");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(6);
			cell.setCellValue("Concepte");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(7);
			cell.setCellValue("Nom Document");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(8);
			cell.setCellValue("Estat");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(9);
			cell.setCellValue("Estat Enviament");
			cell.setCellStyle(headerStyle);
			
			cell = headerRow.createCell(10);
			cell.setCellValue("Justificant");
			cell.setCellStyle(headerStyle);
			
			

			// Files de dades
			for (DocumentNotificacioDto dto : notificacions) {
				Row row = sheet.createRow(rowIndex++);
				// Assegurem no passar null a setCellValue amb es helper valor
				row.createCell(0).setCellValue(valor(dto.getExpedientTipusNom()));
				
			// Columna Expedient (codi dins de [] i en majúscules + identificador sense primer '[')
				if (dto.getExpedient() != null) {
					String codi = dto.getExpedientTipusCodi() != null ? dto.getExpedientTipusCodi().toUpperCase() : "";
					String identificador = dto.getExpedient().getIdentificador();
					
					// Eliminar el primer '[' si existeix
					if (identificador != null && identificador.startsWith("[")) {
						identificador = identificador.substring(1);
					}

					// Format final: [CODI NUMERO] Titol
					String valor = "[" + codi + " " + identificador;
					row.createCell(1).setCellValue(valor);
				} else {
					row.createCell(1).setCellValue("");
				}


				
			// Cel·la de data
				Cell dateCell = row.createCell(2);
				Date dataEnv = dto.getEnviatData();
				if (dataEnv != null) {
					dateCell.setCellValue(dataEnv);
					dateCell.setCellStyle(dateStyle);
				} else {
					dateCell.setCellValue(""); // por si ve null
				}
				
				row.createCell(3).setCellValue(valor(dto.getOrganEmissorCodiAndNom()));
				row.createCell(4).setCellValue(valor(dto.getInteressatFullNomNif()));

				EnviamentTipusEnumDto tipus = dto.getTipus();
				if (tipus != null) {
					row.createCell(5).setCellValue(tipus.name());
				} else {
					row.createCell(5).setCellValue("");
				}
				row.createCell(6).setCellValue(valor(dto.getConcepte()));
				row.createCell(7).setCellValue(valor(dto.getNomDocument()));
				
				NotificacioEstatEnumDto estat = dto.getEstat();
				if (estat != null) {
					row.createCell(8).setCellValue(estat.name());
				} else {
					row.createCell(8).setCellValue("");
				}
				
				NotificacioEnviamentEstatEnumDto enviamentEstat = dto.getEnviamentDatatEstat();
				if (enviamentEstat != null) {
					row.createCell(9).setCellValue(enviamentEstat.name());
				} else {
					row.createCell(9).setCellValue("");
				}

				row.createCell(10).setCellValue(valor(dto.getJustificantArxiuNom()));

			}

			// Ajustar ample automàtic perquè es vegin bé
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);


			// Configurar response per a descarregar el fitxer
			String fileName = "Exportacio_notificacions.xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			// Escriure el workbook a OutputStream de la resposta
			out = response.getOutputStream();
			workbook.write(out);
			out.flush();

		} catch (Exception e) {
			logger.error("No s'ha pogut generar l'excel del llistat de notificacions.", e);
		} finally {
			// Tancar recursos a ma (Java 7, sin try-with-resources)
			if (out != null) {
				try { out.close(); } catch (Exception ignore) {}
			}
			
			// En POI 3.7 Workbook no te close(); alliberem la referència i deixem que el GC faci el seu treball
			workbook = null;
		}
	}

	//=========================
	//Helper simple per evitar NPE i convertir null a string
	//=========================
	private String valor(Object o) {
		if (o == null) return "";
		if (o instanceof String) return (String) o;
		// if (o instanceof Enum<?>) return ((Enum<?>) o).name();
		// if (o instanceof Date) return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((Date) o);
		return o.toString();
	}
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	private static final Log logger = LogFactory.getLog(NotificacionsController.class);
}
