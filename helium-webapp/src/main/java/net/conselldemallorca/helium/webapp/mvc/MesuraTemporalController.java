/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.core.model.dto.IntervalEventDto;
import net.conselldemallorca.helium.core.model.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.core.model.service.AdminService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.lang.StringEscapeUtils;
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
import org.apache.poi.ss.util.CellRangeAddress;
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

	@RequestMapping(value = "/mesura/mesuresTemps", method = RequestMethod.GET)
	@ResponseBody
	public String mesuresTemps(HttpServletRequest request, String familia) {
		DecimalFormat df = new DecimalFormat( "####0.00" );
		
		String json = "";
		String claus = "";
		String darrers = "";
		String mitjes = "";
		String minimes = "";
		String maximes = "";
		String numMesures = "";
		String periodes = "";
		String series = "";
		
		List<MesuraTemporalDto> mesures = adminService.findMesuresTemporals(familia);
		Set<String> llistatFamilies = adminService.findFamiliesMesuresTemporals();
		
		if (adminService.isStatisticActive()) {			
			List<MesuraTemporalDto> listStatistics = adminService.getHibernateStatistics(familia, false);
			if (!listStatistics.isEmpty()) {
				mesures.addAll(listStatistics);
			}
			
			llistatFamilies.add("sql_helium");
			llistatFamilies.add("sql_jbpm");
		}
		
		if (mesures.isEmpty())
			return "{}";
		
		for (MesuraTemporalDto mesura: mesures) {
			claus += "\"" + StringEscapeUtils.escapeJavaScript(mesura.getClau()) + "\",";
			if (mesura.getDarrera() < 1) {
				darrers += "\"-\",";
			} else {
				darrers += "\"" + mesura.getDarrera() + " ms\",";
			}
			mitjes += "\"" + df.format(mesura.getMitja()) + " ms\",";
			minimes += "\"" + mesura.getMinima() + " ms\",";
			maximes += "\"" + mesura.getMaxima() + " ms\",";
			numMesures += "\"" + mesura.getNumMesures() + "\",";
			
			if (mesura.getPeriode() < 1) {
				periodes += "\"-\",";
			} else {
				periodes += "\"" + mesura.getPeriode() + " ms\",";
			}
			
			series += "\"" + StringEscapeUtils.escapeJavaScript(mesura.getClau()) + "\": {\"label\": \"" + StringEscapeUtils.escapeJavaScript(mesura.getClau()) + "\", \"data\": [";
			for (IntervalEventDto event: mesura.getEvents()) {
				series += "[" + event.getDate().getTime() + ", " + event.getDuracio() + "],";
			}
			if (!mesura.getEvents().isEmpty())
				series = series.substring(0, series.length() -1);
			series += "]},";
		}
		
		String families = "";
		for (String fam: llistatFamilies) {
			families += "\"" + fam + "\": \"" + getMessage("temps.familia." + fam) + "\",";
		}
		
		json += "{\"clau\": [" + claus.substring(0, claus.length() -1) + "],";
		json += "\"darrera\": [" + darrers.substring(0, darrers.length() -1) + "],";
		json += "\"mitja\": [" + mitjes.substring(0, mitjes.length() -1) + "],";
		json += "\"minima\": [" + minimes.substring(0, minimes.length() -1) + "],";
		json += "\"maxima\": [" + maximes.substring(0, maximes.length() -1) + "],";
		json += "\"numMesures\": [" + numMesures.substring(0, numMesures.length() -1) + "],";
		json += "\"periode\": [" + periodes.substring(0, periodes.length() -1) + "],";
		json += "\"familia\": {" + families.substring(0, families.length() -1) + "},";
		json += "\"series\": {" + series.substring(0, series.length() -1) + "}}";
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
                
                if ("Consultas Helium".equals(mesura.getClau()) || "Consultas Jbpm".equals(mesura.getClau())) {
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
                } else {
	                sheetSeries.setColumnWidth(0, 8000);
	                sheetSeries.setColumnWidth(1, 5000);
	    			
	                xlsRow = sheetSeries.createRow(rowNumSeries++);
	                
	                cell = xlsRow.createCell(0);
	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(mesura.getClau())));
	                cell.setCellStyle(headerStyle);
	                
	                cell = xlsRow.createCell(1);
	                cell.setCellStyle(headerStyle);
	                sheetSeries.addMergedRegion(new CellRangeAddress(rowNumSeries -1 , rowNumSeries -1, 0, 1));
	                
	                xlsRow = sheetSeries.createRow(rowNumSeries++);
	                xlsRow = sheetSeries.createRow(rowNumSeries++);
	                
	                cell = xlsRow.createCell(0);
	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.data"))));
	                cell.setCellStyle(headerStyle);
	                
	                cell = xlsRow.createCell(1);
	                cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(getMessage("temps.duracio"))));
	                cell.setCellStyle(headerStyle);
	                
	                for (IntervalEventDto event: mesura.getEvents()) {
	                	xlsRow = sheetSeries.createRow(rowNumSeries++);
	                	
	                	cell = xlsRow.createCell(0);
	                	cell.setCellStyle(cellStyle);
	        			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	                    cell.setCellValue(event.getDate());
	                    
	                    cell = xlsRow.createCell(1);
	                    cell.setCellValue(event.getDuracio().doubleValue());
	    			}
                }
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
