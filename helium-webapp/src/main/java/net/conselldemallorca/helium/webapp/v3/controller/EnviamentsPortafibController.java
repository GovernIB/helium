package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultesPortafibFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;
import net.conselldemallorca.helium.webapp.v3.command.ConsultesPortafibFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.command.NotificacioFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per visualitzar la llista de peticions enviades des d'Helium al PortaFib.
 */
@Controller
@RequestMapping("/v3/enviamentsPortafib")
public class EnviamentsPortafibController extends BaseExpedientController {
	
	@Autowired private PortasignaturesService portasignaturesService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		
		ConsultesPortafibFiltreCommand filtreCommand = getFiltreCommand(request);
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
		modelExpedientsTipus(expedientTipusDtoAccessibles, model);
		modelEstats(model);
		return "v3/consultesPortafibLlistat";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		ConsultesPortafibFiltreCommand filtreCommand = getFiltreCommand(request);
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		filtreCommand.setEntornId(entornActual.getId());
		PaginaDto<PortasignaturesDto> resultat = portasignaturesService.findAmbFiltrePaginat(
				paginacioParams,
				ConversioTipusHelper.convertir(filtreCommand, ConsultesPortafibFiltreDto.class));
		return DatatablesHelper.getDatatableResponse(request, null, resultat);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ConsultesPortafibFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:enviamentsPortafib";
	}
	
	@RequestMapping(value = "/{peticioPortafibId}/info", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long peticioPortafibId,
			Model model) {
		model.addAttribute("dto", portasignaturesService.findById(peticioPortafibId));
		return "v3/consultesPortafibInfo";
	}
	
	private ConsultesPortafibFiltreCommand getFiltreCommand(HttpServletRequest request) {
		ConsultesPortafibFiltreCommand filtreCommand = (ConsultesPortafibFiltreCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ConsultesPortafibFiltreCommand();
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	private final int MAX_FILES = 1000;
	@RequestMapping(value = "/descarregardades", method = RequestMethod.GET)
	@ResponseBody
	public void descarregarDades(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {


		try {
			ConsultesPortafibFiltreCommand filtreCommand = getFiltreCommand(request);
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			filtreCommand.setEntornId(entornActual.getId());
			
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(MAX_FILES);
			paginacioParams.afegirOrdre("dataEnviat", OrdreDireccioDto.DESCENDENT);

			PaginaDto<PortasignaturesDto> resultat = portasignaturesService.findAmbFiltrePaginat(
					paginacioParams,
					ConversioTipusHelper.convertir(filtreCommand, ConsultesPortafibFiltreDto.class));
			if (resultat.getElementsTotal() > MAX_FILES) {
				MissatgesHelper.warning(request, "S'han obtingut " + resultat.getTotal() + " resultats, només es descarregaran les " + MAX_FILES + " primeres files.");
				
			}
			List<PortasignaturesDto> enviaments = resultat.getContingut();
			exportXLS(response, enviaments);
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
	
	private void exportXLS(HttpServletResponse response, List<PortasignaturesDto> enviaments) {

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
	        cell.setCellValue("Portafib ID");
	        cell.setCellStyle(headerStyle);

	        cell = headerRow.createCell(1);
	        cell.setCellValue("Expedient Tipus");
	        cell.setCellStyle(headerStyle);
	        
	        cell = headerRow.createCell(2);
	        cell.setCellValue("Número Expedient");
	        cell.setCellStyle(headerStyle);
	        
	        cell = headerRow.createCell(3);
	        cell.setCellValue("Data d'Enviament");
	        cell.setCellStyle(headerStyle);
	        
	        cell = headerRow.createCell(4);
	        cell.setCellValue("Estat");
	        cell.setCellStyle(headerStyle);
	        
	        cell = headerRow.createCell(5);
	        cell.setCellValue("Document");
	        cell.setCellStyle(headerStyle);
	        
	        

	        // Files de dades
	        for (PortasignaturesDto dto : enviaments) {
	            Row row = sheet.createRow(rowIndex++);
	            // Assegurem no passar null a setCellValue amb es helper valor
	            row.createCell(0).setCellValue(valor(dto.getDocumentId()));
	            
	            row.createCell(1).setCellValue(valor(dto.getTipusExpedientCodi()));
	            
	            row.createCell(2).setCellValue(valor(dto.getExpedientIdentificador()));
	            
		        // Cel·la de data
	            Cell dateCell = row.createCell(3);
	            Date dataEnv = dto.getDataEnviat();
	            if (dataEnv != null) {
	                dateCell.setCellValue(dataEnv);
	                dateCell.setCellStyle(dateStyle);
	            } else {
	                dateCell.setCellValue(""); // per si ve null
	            }
	            
	            row.createCell(4).setCellValue(valor(dto.getEstat()));
	            
	            
	            Cell linkCell = row.createCell(5);
	            String documentNom = valor(dto.getDocumentNom());
	            String documentUrl = valor(dto.getSignaturaUrlVerificacio());

		        linkCell.setCellValue(documentNom);

		        if (documentUrl != null && !documentUrl.isEmpty()) {
		            CreationHelper createHelper = workbook.getCreationHelper();
		            Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
		            link.setAddress(documentUrl);

		            linkCell.setHyperlink(link);

		            CellStyle hlinkStyle = workbook.createCellStyle();
		            Font hlinkFont = workbook.createFont();
		            hlinkFont.setUnderline(Font.U_SINGLE);
		            hlinkFont.setColor(HSSFColor.BLUE.index);
		            hlinkStyle.setFont(hlinkFont);
		            linkCell.setCellStyle(hlinkStyle);
		        }

	        }

	        // Ajustar ample automàtic perquè es vegin bé
	        sheet.autoSizeColumn(0);
	        sheet.autoSizeColumn(1);
	        sheet.autoSizeColumn(2);
	        sheet.autoSizeColumn(3);
	        sheet.autoSizeColumn(4);
	        sheet.autoSizeColumn(5);


	        // Configurar response per a descarregar el fitxer
	        String fileName = "Exportacio_enviaments_Portafib.xlsx";
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
	
	private void modelExpedientsTipus(List<ExpedientTipusDto> expedientTipusDtoAccessibles, Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
			for(ExpedientTipusDto expedientTipus : expedientTipusDtoAccessibles)
				opcions.add(new ParellaCodiValorDto(expedientTipus.getId().toString(), expedientTipus.getNom()));		
		
		model.addAttribute("expedientsTipus", opcions);
	}
	
	private void modelEstats(Model model) {
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(PortafirmesEstatEnum estat : PortafirmesEstatEnum.values())
			if (!estat.equals(PortafirmesEstatEnum.BLOQUEJAT))
				opcions.add(new ParellaCodiValorDto(
						estat.name(),
						MessageHelper.getInstance().getMessage("enum.portafirmes.estat." + estat.name())));

		model.addAttribute("estats", opcions);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}	
	
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(EnviamentsPortafibController.class);
	private static final String SESSION_ATTRIBUTE_FILTRE = "EnviamentsPortafibController.session.filtre";
}
