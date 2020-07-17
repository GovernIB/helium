package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEstadisticaCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment d'entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "entornEstadisticaControllerV3")
@RequestMapping("/v3/estadistica")
public class ExpedientTipusEstadisticaController extends BaseController {

	@Resource
	private ExpedientTipusService expedientTipusService;
	@Autowired
	protected DissenyService dissenyService;


	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new ExpedientTipusEstadisticaCommand());		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		List<ExpedientTipusDto> expedientsTipus = expedientTipusService.findAmbEntornPermisDissenyar(entornActual.getId());
		model.addAttribute("expedientsTipus", expedientsTipus);
		Boolean potAdministrarEntorn = SessionHelper.getSessionManager(request).getPotAdministrarEntorn();
		if (!Boolean.TRUE.equals(potAdministrarEntorn)) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"error.permis.administracio.entorn"));

		}
		
		ExpedientTipusEstadisticaCommand filtreCommand = new ExpedientTipusEstadisticaCommand();
		
		
		return form(request, filtreCommand,null,"",model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String form(
			HttpServletRequest request,
			ExpedientTipusEstadisticaCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		
		if ("netejar".equals(accio))
			filtreCommand = new ExpedientTipusEstadisticaCommand();
		
		Boolean anulats = null;
		if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NOMES_ANULATS))
			anulats = true;
		if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NO))
			anulats = false;
		List<ExpedientTipusEstadisticaDto> et = expedientTipusService.findEstadisticaByFiltre(
																							filtreCommand.getAnyInicial(), 
																							filtreCommand.getAnyFinal(), 
																							EntornActual.getEntornId(), 
																							filtreCommand.getExpedientTipusId(), 
																							anulats, 
																							filtreCommand.getNumero(), 
																							filtreCommand.getTitol(), 
																							filtreCommand.getEstatTipus(), 
																							filtreCommand.getAturat());
				/*findEstadisticaByFiltre(
																				filtreCommand.getAnyInicial(), 
																				filtreCommand.getAnyFinal(), 
																				EntornActual.getEntornId(), 
																				filtreCommand.getExpedientTipusId(), 
																				anulats,
																				);*/
		TreeSet<String> anys = new TreeSet<String>();
		Map<String, String> titols = new HashMap<String, String>();  
		Map<String, Map<String, Object>> ete = new TreeMap<String, Map<String, Object>>();
		Map<String, Long> totalTipus = new  HashMap<String, Long>();
		for(ExpedientTipusEstadisticaDto element : et) {
			if(filtreCommand.getExpedientTipusId() == null || filtreCommand.getExpedientTipusId().equals(element.getId())) {
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
			}
		}
		
		//List<String> anys = generateSetOfAnys(filtreCommand.getDataIniciInicial(), filtreCommand.getDataIniciFinal());
		Map<String, Object> totalAny =  totalPerAny(ete);
		request.setAttribute("tableData", ete);
		request.setAttribute("totalTipus", totalTipus);
		request.setAttribute("anys", anys);
		request.setAttribute("totalAny", totalAny);
		request.setAttribute("titols", titols);
		
		// Torna a posar el command al model
		model.addAttribute(filtreCommand);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		List<ExpedientTipusDto> expedientsTipus = new ArrayList<ExpedientTipusDto>();
		expedientsTipus.addAll(expedientTipusService.findAmbEntorn(entornActual.getId()));

		model.addAttribute("expedientsTipus", expedientsTipus);
		
		List<EstatDto> estats = new ArrayList<EstatDto>();
		
		estats.add(
				0,
				new EstatDto(
						0L,
						"INICIAT",
						getMessage(
								request,
								"expedient.consulta.iniciat")));
		estats.add(
				new EstatDto(
						-1L,
						"FINALITZAT",
						getMessage(
								request,
								"expedient.consulta.finalitzat")));
		model.addAttribute(
				"estatsList",
				estats);
		
		/*for(ExpedientTipusDto etip : expedientsTipus) {
			List<EstatDto> e = expedientTipusService.estatFindAll(etip.getId(), true);
			estats.addAll(e);
		}*/

		return "v3/estadisticaEntorns";
	}
	
	@RequestMapping(value = "excel", method = RequestMethod.POST)
	public void excel(
			HttpServletRequest request,
			HttpServletResponse response,
			ExpedientTipusEstadisticaCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		Boolean anulats = null;
		if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NOMES_ANULATS))
			anulats = true;
		if(filtreCommand.getMostrarAnulats().equals(MostrarAnulatsDto.NO))
			anulats = false;
		/*List<ExpedientTipusEstadisticaDto> et = expedientTipusService.findEstadisticaByFiltre(
																				filtreCommand.getAnyInicial(), 
																				filtreCommand.getAnyFinal(), 
																				EntornActual.getEntornId(), 
																				filtreCommand.getExpedientTipusId(), 
																				anulats);*/
		List<ExpedientTipusEstadisticaDto> et = expedientTipusService.findEstadisticaByFiltre(
																			filtreCommand.getAnyInicial(), 
																			filtreCommand.getAnyFinal(), 
																			EntornActual.getEntornId(), 
																			filtreCommand.getExpedientTipusId(), 
																			anulats, 
																			filtreCommand.getNumero(), 
																			filtreCommand.getTitol(), 
																			filtreCommand.getEstatTipus(), 
																			filtreCommand.getAturat());
		TreeSet<String> anys = new TreeSet<String>();
		Map<String, String> titols = new HashMap<String, String>();  
		Map<String, Map<String, Object>> ete = new TreeMap<String, Map<String, Object>>();
		Map<String, Long> totalTipus = new  HashMap<String, Long>();
		for(ExpedientTipusEstadisticaDto element : et) {
			if(filtreCommand.getExpedientTipusId() == null || filtreCommand.getExpedientTipusId().equals(element.getId())) {
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
			}
		}
		
		Map<String, Object> totalAny =  totalPerAny(ete);
		Map<String, Object> estadistica =  new HashMap<String, Object>();
		estadistica.put("tableData", ete);
		estadistica.put("totalTipus", totalTipus);
		estadistica.put("anys", anys);
		estadistica.put("totalAny", totalAny);
		estadistica.put("titols", titols);
		
		// Torna a posar el command al model
		model.addAttribute(filtreCommand);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		List<ExpedientTipusDto> expedientsTipus = new ArrayList<ExpedientTipusDto>();
		expedientsTipus.addAll(expedientTipusService.findAmbEntorn(entornActual.getId()));

		gererarExcel(request, response, estadistica, expedientsTipus);
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
	
	private void gererarExcel(
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
		
		HSSFFont deaderBold;
		deaderBold = wb.createFont();
		deaderBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		deaderBold.setColor(HSSFColor.WHITE.index);
		HSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
		headerStyle.setFont(deaderBold);
		
		HSSFFont boldFont = wb.createFont();
		boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
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
	public List<ParellaCodiValorDto> populateEstats(HttpServletRequest request) {
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
