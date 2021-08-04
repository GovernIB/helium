package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusEstadisticaCommand;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusEstadisticaDto;
import es.caib.helium.logic.intf.dto.MostrarAnulatsDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Controlador per al manteniment d'entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/estadistica")
public class ExpedientTipusEstadisticaController extends BaseController {

	@Resource
	private ExpedientTipusService expedientTipusService;
	@Autowired
	protected DissenyService dissenyService;


	/** Resposta GET i POST de la pàgina i formulari d'estadístiques.
	 * 
	 * @param request
	 * @param model
	 * @return Retorna com si fos una crida post de consulta.
	 */
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
	public String estadistiques(
			HttpServletRequest request,
			ExpedientTipusEstadisticaCommand filtreCommand,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		
		if (accio != null && "netejar".equals(accio))
			filtreCommand = new ExpedientTipusEstadisticaCommand();
		
		// Torna a omplir el model
		model.addAttribute("expedientTipusEstadisticaCommand", filtreCommand);
		// Valida l'accés a l'entorn
		Boolean potAdministrarEntorn = SessionHelper.getSessionManager(request).getPotAdministrarEntorn();
		if (potAdministrarEntorn != null && !potAdministrarEntorn) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"error.permis.administracio.entorn"));
			return "v3/estadisticaEntorns";

		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		// Afegeix els diferents tipus d'expedient al model
		List<ExpedientTipusDto> expedientsTipus = expedientTipusService.findAmbEntornPermisDissenyar(entornActual.getId());
		model.addAttribute("expedientsTipus", expedientsTipus);
		// Estats
		model.addAttribute("estats", this.getEstatsModel(request, filtreCommand.getExpedientTipusId()));

		
		Boolean anulats = null;
		if (filtreCommand.getMostrarAnulats() != null) {
			if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NOMES_ANULATS))
				anulats = true;
			else if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NO))
				anulats = false;			
		}
		List<ExpedientTipusEstadisticaDto> et = expedientTipusService.findEstadisticaByFiltre(
																							filtreCommand.getAnyInicial(), 
																							filtreCommand.getAnyFinal(), 
																							entornActual.getId(),
																							filtreCommand.getExpedientTipusId(), 
																							anulats, 
																							filtreCommand.getNumero(), 
																							filtreCommand.getTitol(), 
																							this.getEstatTipus(filtreCommand.getEstat()), 
																							this.getEstatId(filtreCommand.getEstat()),
																							filtreCommand.getAturat());
		Map<String, Object> estadistica = this.calcEstadistiques(et);
		model.addAllAttributes(estadistica);
		
		return "v3/estadisticaEntorns";
	}
	
	/** Obté la llista de parelles codi valor pel model d'estats amb l'estat inicialitzat i finalitzat
	 * 
	 * @param request Petició per saber l'idioma dels missatges.
	 * @param expedientTipusId
	 * @return
	 */
	private List<ParellaCodiValorDto> getEstatsModel(HttpServletRequest request, Long expedientTipusId) {
		List<ParellaCodiValorDto> estats = new ArrayList<ParellaCodiValorDto>();
		// INICIAT
		estats.add(new ParellaCodiValorDto(EstatTipusDto.INICIAT.toString(), getMessage(
								request,
								"expedient.consulta.iniciat")));
		if (expedientTipusId != null 
				&& expedientTipusId > 0) {		
			// Estats tipus d'expedient
			for(EstatDto e : expedientTipusService.estatFindAll(expedientTipusId, true)){
				estats.add(new ParellaCodiValorDto(String.valueOf(e.getId()), e.getNom()));
			}
		}
		// FINALITZAT
		estats.add(new ParellaCodiValorDto(EstatTipusDto.FINALITZAT.toString(), getMessage(
				request,
				"expedient.consulta.finalitzat")));
		return estats;
	}

	/** Mètode per consultar els estats per tipus d'expedient quan canviï la selecció del tipus d'expedient.
	 * 
	 * @param request
	 * @param expedientTipusId
	 * @return
	 */
	@RequestMapping(value = "/estatsPerTipus/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> estatsPerExpedientTipus(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId) {
		return this.getEstatsModel(request, expedientTipusId);
	}


	/** Si estat és un identificador llavors retorna tipus CUSTOM, si no el corresponent a INICIAT o FINALITZAT.
	 * 
	 * @param estat Conté el text del tipus o l'id.
	 * @return
	 */
	private EstatTipusDto getEstatTipus(String estat) {
		EstatTipusDto estatTipus = null;
		if (estat != null && !estat.isEmpty())
			try {
				estatTipus = EstatTipusDto.valueOf(estat);
			} catch(Exception e) {
				estatTipus = EstatTipusDto.CUSTOM;
			}
		return estatTipus;
	}

	/** Si estat conté un identificador llavors el retorna, si no null.
	 * 
	 * @param estat
	 * @return
	 */
	private Long getEstatId(String estat) {
		Long estatId = null;
		if (estat != null && !estat.isEmpty())
			try {
				estatId = Long.parseLong(estat);
			} catch (Exception e) {
				estatId = null;
			}
		return estatId;
	}

	
	@RequestMapping(value = "excel", method = RequestMethod.POST)
	public void excel(
			HttpServletRequest request,
			HttpServletResponse response,
			ExpedientTipusEstadisticaCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		
		// Valida l'accés a l'entorn
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		Boolean potAdministrarEntorn = SessionHelper.getSessionManager(request).getPotAdministrarEntorn();
		if (potAdministrarEntorn != null && !potAdministrarEntorn) {
			try {
				response.sendRedirect("/helium/v3/estadistica");
			} catch (IOException e) {
				throw new RuntimeException("Error de permís generant l'XLS d'estadístiques.", e);
			}
			return;
		}
		
		Boolean anulats = null;
		if (filtreCommand.getMostrarAnulats() != null) {
			if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NOMES_ANULATS))
				anulats = true;
			else if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NO))
				anulats = false;			
		}
		List<ExpedientTipusEstadisticaDto> et = expedientTipusService.findEstadisticaByFiltre(
																							filtreCommand.getAnyInicial(), 
																							filtreCommand.getAnyFinal(), 
																							entornActual.getId(),
																							filtreCommand.getExpedientTipusId(), 
																							anulats, 
																							filtreCommand.getNumero(), 
																							filtreCommand.getTitol(), 
																							this.getEstatTipus(filtreCommand.getEstat()), 
																							this.getEstatId(filtreCommand.getEstat()),
																							filtreCommand.getAturat());
		// Calcula les estadístiques a partir de les dades
		Map<String, Object> estadistica = this.calcEstadistiques(et);
		
		// Genera l'a fulla
		List<ExpedientTipusDto> expedientsTipus = expedientTipusService.findAmbEntorn(entornActual.getId());
		generarExcel(request, response, estadistica, expedientsTipus);
	}
	
	/** Calcula les estadístiques i posa les dades en diferents entrades en un Map<String, Object>
	 * 
	 * @param et Dades per calcular les estadístiques per entorn.
	 * @return
	 */
	private Map<String, Object> calcEstadistiques(List<ExpedientTipusEstadisticaDto> et) {

		Map<String, Object> estadistica = new HashMap<String, Object>();
		
		TreeSet<String> anys = new TreeSet<String>();
		Map<String, String> titols = new HashMap<String, String>();  
		Map<String, Map<String, Object>> ete = new TreeMap<String, Map<String, Object>>();
		Map<String, Long> totalTipus = new  HashMap<String, Long>();
		for(ExpedientTipusEstadisticaDto element : et) {
//			if(filtreCommand.getExpedientTipusId() == null || filtreCommand.getExpedientTipusId().equals(element.getId())) {
				String nom = element.getCodi();
				if(!titols.containsKey(nom))
					titols.put(nom, element.getNom());
				if(!ete.containsKey(nom))
					ete.put(nom, new TreeMap<String,Object>());
				if(!anys.contains(element.getAnyInici()))
					anys.add(element.getAnyInici());
				if(!totalTipus.containsKey(nom)) {				
					totalTipus.put(nom, element.getN());
				}else {
					totalTipus.put(nom, totalTipus.get(nom) + element.getN());
				}
				ete.get(nom).put(element.getAnyInici(), element.getN());
//			}
		}
		
		Map<String, Object> totalAny =  totalPerAny(ete);
		estadistica =  new HashMap<String, Object>();
		estadistica.put("tableData", ete);
		estadistica.put("totalTipus", totalTipus);
		estadistica.put("anys", anys);
		estadistica.put("totalAny", totalAny);
		estadistica.put("titols", titols);
		return estadistica;
	}

	private Map<String, Object> totalPerAny(Map<String, Map<String, Object>> ete){
		Map<String, Object> totalAny = new HashMap<String, Object>();
		for(Map.Entry<String, Map<String, Object>> entry : ete.entrySet()) {
			for(Map.Entry<String, Object> any : entry.getValue().entrySet()) {
				
				if(!totalAny.containsKey(any.getKey())) {
					totalAny.put(any.getKey(), any.getValue());
				}else {
					totalAny.put(any.getKey(), ((Long)totalAny.get(any.getKey()) + ((Long) any.getValue())));
				}
			}
		}
		
		return totalAny;
	}
	
	@SuppressWarnings("unchecked")
	private void generarExcel(
			HttpServletRequest request,
			HttpServletResponse response,
			Map<String, Object> estadistica,
			List<ExpedientTipusDto> expedientsTipus
			) {
		
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
		bold.setBold(true);
		bold.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		
		greyFont = wb.createFont();
		greyFont.setColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
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
		
		HSSFFont deaderBold;
		deaderBold = wb.createFont();
		deaderBold.setBold(true);
		deaderBold.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		HSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(FillPatternType.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.GREY_80_PERCENT.getIndex());
		headerStyle.setFont(deaderBold);
		
		HSSFFont boldFont = wb.createFont();
		boldFont.setBold(true);
		HSSFCellStyle boldText = wb.createCellStyle();
		boldText.setFont(boldFont);
		
		int rowNum = 0;
		int colNum = 0;
		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);
		HSSFCell cell;
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize("Codi")));
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize("Tipus d'expedient")));
		sheet.autoSizeColumn(colNum);
		cell.setCellStyle(headerStyle);
		for (String any : (TreeSet<String>) estadistica.get("anys")) {
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(any)));
			cell.setCellStyle(headerStyle);
		}
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize("Total tipus")));
		sheet.autoSizeColumn(colNum);
		cell.setCellStyle(headerStyle);
		
		TreeMap<String, Object> tableData = (TreeMap<String, Object>) estadistica.get("tableData");
		Map<String, Long> totalTipus = (Map<String, Long>) estadistica.get("totalTipus");
		Map<String, Long> totalAny = (Map<String, Long>) estadistica.get("totalAny");
		long total = 0;
		for(ExpedientTipusDto expTip : expedientsTipus) {
			if(!tableData.containsKey(expTip.getCodi()))
				continue;
			colNum = 0;
			xlsRow = sheet.createRow(rowNum++);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(expTip.getCodi())));
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(expTip.getNom())));
			sheet.autoSizeColumn(colNum);
			
			for (String any : (TreeSet<String>) estadistica.get("anys")) {
				Long t = (((Map<String,Object>) tableData.get(expTip.getCodi())).containsKey(any))? (Long) ((Map<String,Object>) tableData.get(expTip.getCodi())).get(any): new Long(0);
				sheet.autoSizeColumn(colNum);
				cell = xlsRow.createCell(colNum++);
				cell.setCellValue(t);
			}
			Long tt = (Long) totalTipus.get(expTip.getCodi());
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(tt);
			cell.setCellStyle(boldText);
		}
			
		colNum = 0;
		xlsRow = sheet.createRow(rowNum++);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize("Total per any")));
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue("");
		cell.setCellStyle(headerStyle);
		sheet.autoSizeColumn(colNum);
		
		for (String any : (TreeSet<String>) estadistica.get("anys")) {
			Long t = totalAny.containsKey(any)? totalAny.get(any) : new Long(0);
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(t);
			cell.setCellStyle(boldText);
			total += t.longValue();
		}
		
		sheet.autoSizeColumn(colNum);
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(total);
		cell.setCellStyle(boldText);
		
		for(int i=0; i<colNum; i++)
			sheet.autoSizeColumn(i);
		
		try {
			String fileName = "Estadistica.ods";
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
		}
	}
				
	@ModelAttribute("anulats")
	public List<ParellaCodiValorDto> populateAnulats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.no"), MostrarAnulatsDto.NO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si"), MostrarAnulatsDto.SI));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si.only"), MostrarAnulatsDto.NOMES_ANULATS));
		return resposta;
	}
	
	@ModelAttribute("aturats")
	public List<ParellaCodiValorDto> populateAturats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.no"), false));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "enum.si"), true));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "comu.totes"), null));
		return resposta;
	}
}
