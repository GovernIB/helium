/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.core.model.dto.IntervalEventDto;
import net.conselldemallorca.helium.core.model.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.core.model.service.AdminService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class MesuraTemporalController extends BaseController {

	@Resource
	private AdminService adminService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/mesura/mesuresTemps", method = RequestMethod.GET)
	@ResponseBody
	public String mesuresTemps(HttpServletRequest request, String familia) {
		DecimalFormat df = new DecimalFormat( "####0.00" );
		DecimalFormat df2 = new DecimalFormat( "####0.000" );
		Map mjson = new LinkedHashMap();
		
		List<MesuraTemporalDto> mesures = adminService.findMesuresTemporals(familia);
		Set<String> llistatFamilies = new HashSet<String>();
		llistatFamilies.addAll(adminService.findFamiliesMesuresTemporals());
		
		if (adminService.isStatisticActive()) {			
			List<MesuraTemporalDto> listStatistics = adminService.getHibernateStatistics(familia, false);
			if (!listStatistics.isEmpty()) {
				mesures.addAll(listStatistics);
			}
			llistatFamilies.add("sql_helium");
			llistatFamilies.add("sql_jbpm");
		}
		
		Map mfamilies = new LinkedHashMap();
		for (String fam: llistatFamilies) {
			mfamilies.put(fam, getMessage("temps.familia." + fam));
		}
		
		if (mesures.isEmpty()) {
			mjson.put("familia", mfamilies);
		} else {
			JSONArray lclaus = new JSONArray();
			JSONArray ldarrers = new JSONArray();
			JSONArray lmitjes = new JSONArray();
			JSONArray lminimes = new JSONArray();
			JSONArray lmaximes = new JSONArray();
			JSONArray lnumMesures = new JSONArray();
			JSONArray lperiodes = new JSONArray();
			Map mseries = new LinkedHashMap();
			
			for (MesuraTemporalDto mesura: mesures) {
				lclaus.add(mesura.getClau());
				ldarrers.add(mesura.getDarrera() < 1 ? "-" : mesura.getDarrera());
				lmitjes.add(df.format(mesura.getMitja()) + " ms");
				lminimes.add(mesura.getMinima() + " ms");
				lmaximes.add(mesura.getMaxima() + " ms");
				lnumMesures.add(mesura.getNumMesures());
				lperiodes.add(df2.format(mesura.getPeriode()) + " ex/m");
				
				JSONArray lseries = new JSONArray();
				for (IntervalEventDto event: mesura.getEvents()) {
					List  levents = new LinkedList();
					levents.add(event.getDate().getTime());
					levents.add(event.getDuracio());
					lseries.add(levents);
				}
				Map mserie = new LinkedHashMap();
				mserie.put("label", mesura.getClau());
				mserie.put("data", lseries);
				mseries.put(mesura.getClau(), mserie);
			}
			
			mjson.put("clau", lclaus);
			mjson.put("darrera", ldarrers);
			mjson.put("mitja", lmitjes);
			mjson.put("minima", lminimes);
			mjson.put("maxima", lmaximes);
			mjson.put("numMesures", lnumMesures);
			mjson.put("periode", lperiodes);
			mjson.put("familia", mfamilies);
			mjson.put("series", mseries);
		}
		
		String json = JSONValue.toJSONString(mjson);
		return json;
	}
	
	@RequestMapping(value = "/mesura/mesuresTempsExport", method = RequestMethod.GET)
	public void mesuresTempsExport(HttpServletRequest request, HttpServletResponse response) {
			List<MesuraTemporalDto> mesures = adminService.findMesuresTemporals(null);
			
			mesures.addAll(adminService.getHibernateStatistics("", true));
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Mesures de temps");
        
			HSSFCellStyle cellStyle = wb.createCellStyle();
		    cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		    cellStyle.setWrapText(true);
		    
		    sheet.setColumnWidth(0, 12000);
			sheet.setColumnWidth(1, 3000);
			sheet.setColumnWidth(2, 3000);
			sheet.setColumnWidth(3, 3000);
			sheet.setColumnWidth(4, 3000);
			sheet.setColumnWidth(5, 3000);
			sheet.setColumnWidth(6, 3000);
			
			int rowNum = 0;
            int colNum = 0;
            
            // Capçalera
            
            HSSFRow xlsRow = sheet.createRow(rowNum++);

            HSSFCellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
            headerStyle.setFillBackgroundColor(HSSFColor.BLUE_GREY.index);
            HSSFFont bold = wb.createFont();
            bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            bold.setColor(HSSFColor.WHITE.index);
            headerStyle.setFont(bold);
            
            HSSFCellStyle style = wb.createCellStyle();
            DataFormat format = wb.createDataFormat();
            style.setDataFormat(format.getFormat("0.00"));
        
            HSSFCell cell = xlsRow.createCell(colNum++);
            cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.clau"))));
            cell.setCellStyle(headerStyle);
            
            cell = xlsRow.createCell(colNum++);
            cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.darrera"))));
            cell.setCellStyle(headerStyle);
            
            cell = xlsRow.createCell(colNum++);
            cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.minima"))));
            cell.setCellStyle(headerStyle);
            
            cell = xlsRow.createCell(colNum++);
            cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.maxima"))));
            cell.setCellStyle(headerStyle);
            
            cell = xlsRow.createCell(colNum++);
            cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.numMesures"))));
            cell.setCellStyle(headerStyle);
            
            cell = xlsRow.createCell(colNum++);
            cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.mitja"))));
            cell.setCellStyle(headerStyle);
            
            cell = xlsRow.createCell(colNum);
            cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.periode"))));
            cell.setCellStyle(headerStyle);
            
            for (MesuraTemporalDto mesura: mesures) {
            	xlsRow = sheet.createRow(rowNum++);
            	colNum = 0;
            	
            	cell = xlsRow.createCell(colNum++);
                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(mesura.getClau())));
                
                cell = xlsRow.createCell(colNum++);
                cell.setCellValue(mesura.getDarrera());
                
                cell = xlsRow.createCell(colNum++);
                cell.setCellValue(mesura.getMinima());
                
                cell = xlsRow.createCell(colNum++);
                cell.setCellValue(mesura.getMaxima());
                
                cell = xlsRow.createCell(colNum++);
                cell.setCellValue(mesura.getNumMesures());
                
                cell = xlsRow.createCell(colNum++);
                cell.setCellValue(mesura.getMitja());
                cell.setCellStyle(style);
                
                cell = xlsRow.createCell(colNum++);
                cell.setCellValue(mesura.getPeriode());
                
                // Series
                if ("Consultas Helium".equals(mesura.getClau()) || "Consultas Jbpm".equals(mesura.getClau())) {
                
                	int rowNumSeries = 0;
	                HSSFSheet sheetSeries;
	                try {
						String llibre = mesura.getClau().replaceAll("[]*/\\?:()]+", "-");
						if (llibre.length() > 31) {
							llibre = llibre.substring(0, 14) + "..." + llibre.substring(llibre.length() - 14, llibre.length());
						}
						sheetSeries = wb.createSheet(llibre);
	                } catch (Exception e) {
	                	sheetSeries = wb.createSheet("Mesures " + rowNum);
	                }
                
                	sheetSeries.setColumnWidth(0, 30000);
	                sheetSeries.setColumnWidth(1, 3000);
	                sheetSeries.setColumnWidth(2, 3000);
	                sheetSeries.setColumnWidth(3, 3000);
	                sheetSeries.setColumnWidth(4, 3000);
	                
	                xlsRow = sheetSeries.createRow(rowNumSeries++);
	                
	                cell = xlsRow.createCell(0);
	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.clau"))));
	                cell.setCellStyle(headerStyle);
	                
	                cell = xlsRow.createCell(1);
	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.minima"))));
	                cell.setCellStyle(headerStyle);
	                
	                cell = xlsRow.createCell(2);
	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.maxima"))));
	                cell.setCellStyle(headerStyle);
	                
	                cell = xlsRow.createCell(3);
	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.numMesures"))));
	                cell.setCellStyle(headerStyle);
	                
	                cell = xlsRow.createCell(4);
	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.mitja"))));
	                cell.setCellStyle(headerStyle);
	    			
	    			List<MesuraTemporalDto> listStatistics = new ArrayList<MesuraTemporalDto>();
	    			if ("Consultas Helium".equals(mesura.getClau())) {
	                	listStatistics = adminService.getHibernateStatistics("sql_helium", true);
	                } else if ("Consultas Jbpm".equals(mesura.getClau())) {
	                	listStatistics = adminService.getHibernateStatistics("sql_jbpm", true);
	                }
    				for (MesuraTemporalDto mesuraStats : listStatistics) {
						xlsRow = sheetSeries.createRow(rowNumSeries++);
	                	
	                	cell = xlsRow.createCell(0);
	                	cell.setCellStyle(cellStyle);
	                    cell.setCellValue(mesuraStats.getClau());
	                    
	                    cell = xlsRow.createCell(1);
	                    cell.setCellValue(mesuraStats.getMinima());
	                    
	                    cell = xlsRow.createCell(2);
	                    cell.setCellValue(mesuraStats.getMaxima());
	                    
	                    cell = xlsRow.createCell(3);
	                    cell.setCellValue(mesuraStats.getNumMesures());
	                    
	                    cell = xlsRow.createCell(4);
	                    cell.setCellValue(mesuraStats.getMitja());
    				}
                } 
//                else {
//	                sheetSeries.setColumnWidth(0, 8000);
//	                sheetSeries.setColumnWidth(1, 5000);
//	    			
//	                xlsRow = sheetSeries.createRow(rowNumSeries++);
//	                
//	                cell = xlsRow.createCell(0);
//	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(mesura.getClau())));
//	                cell.setCellStyle(headerStyle);
//	                
//	                cell = xlsRow.createCell(1);
//	                cell.setCellStyle(headerStyle);
//	                sheetSeries.addMergedRegion(new CellRangeAddress(rowNumSeries -1 , rowNumSeries -1, 0, 1));
//	                
//	                xlsRow = sheetSeries.createRow(rowNumSeries++);
//	                xlsRow = sheetSeries.createRow(rowNumSeries++);
//	                
//	                cell = xlsRow.createCell(0);
//	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.data"))));
//	                cell.setCellStyle(headerStyle);
//	                
//	                cell = xlsRow.createCell(1);
//	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.duracio"))));
//	                cell.setCellStyle(headerStyle);
//	                
//	                for (IntervalEventDto event: mesura.getEvents()) {
//	                	xlsRow = sheetSeries.createRow(rowNumSeries++);
//	                	
//	                	cell = xlsRow.createCell(0);
//	                	cell.setCellStyle(cellStyle);
//	        			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//	                    cell.setCellValue(event.getDate());
//	                    
//	                    cell = xlsRow.createCell(1);
//	                    cell.setCellValue(event.getDuracio().doubleValue());
//	    			}
//                }
            }
        try {   
			response.setHeader("Content-disposition", "attachment; filename=mesuresTemps.xls");
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
			logger.error("Mesures temporals: No s'ha pogut realitzar la exportació.");
		}
	}

	private static final Log logger = LogFactory.getLog(MesuraTemporalController.class);
}
