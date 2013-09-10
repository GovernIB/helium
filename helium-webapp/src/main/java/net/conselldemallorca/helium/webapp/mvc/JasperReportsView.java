package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.core.model.service.AdminService;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;

/**
 * Vista per a generar un report amb Jasper Reports
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JasperReportsView implements View {

	public static final String HEADER_PRAGMA = "Pragma";
	public static final String HEADER_EXPIRES = "Expires";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";

	public static final String MODEL_ATTRIBUTE_REPORTDATA = "mapCollectionData";
	public static final String MODEL_ATTRIBUTE_REPORTCONTENT = "reportContent";
	public static final String MODEL_ATTRIBUTE_SUBREPORTS = "subreports";
	public static final String MODEL_ATTRIBUTE_SUBREPORTDATA_PREFIX = "subreportFile_";

	public static final String MODEL_ATTRIBUTE_CONSULTA = "reportConsulta";


	private AdminService adminService;
	
	@Autowired
	public JasperReportsView(AdminService adminService) {
		this.adminService = adminService;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void render(
			Map model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader(HEADER_PRAGMA, "");
		response.setHeader(HEADER_EXPIRES, "");
		response.setHeader(HEADER_CACHE_CONTROL, "");

		JRBeanCollectionDataSource datasource = null;
		if (model.get(MODEL_ATTRIBUTE_REPORTDATA) != null)
			datasource = new JRBeanCollectionDataSource(
					(List<Map<String, Object>>)model.get(MODEL_ATTRIBUTE_REPORTDATA));
		if (datasource != null) {
			adminService.getMesuresTemporalsHelper().mesuraIniciar("INFORME: " + (String)model.get(MODEL_ATTRIBUTE_CONSULTA) + " (REPORT)", "report");
			JasperReport report = null;
			report = JasperCompileManager.compileReport(
					new ByteArrayInputStream(
							(byte[])model.get(MODEL_ATTRIBUTE_REPORTCONTENT)));
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			JasperReport subreport = null;
			HashMap<String, byte[]> subreports = (HashMap<String, byte[]>)model.get(MODEL_ATTRIBUTE_SUBREPORTS);
			if (subreports!=null)
			if (!subreports.isEmpty()) {
				Iterator it = subreports.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();
					subreport = JasperCompileManager.compileReport(
							new ByteArrayInputStream(
									(byte[]) e.getValue()));
					String nom = (String) e.getKey();
					nom = nom.substring(0, nom.lastIndexOf("."));
					params.put(nom, subreport);
				}
				//params.put("datasource",datasource);
			}
//			if (subreports != null) {
//				for (String subreportCodi: subreports) {
//					JRDataSource subDatasource = new JRMapCollectionDataSource(
//							(List<Map<String, Object>>)model.get(MODEL_ATTRIBUTE_REPORTDATA));
//					params.put("helds$" + subreportCodi, subDatasource);
//				}
//			}
			
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					report,
					params,
					datasource);
			
			Object exp = (String)request.getAttribute("formatJR");

			if(exp.equals("PDF")){
				//exportar PDF		
				response.setHeader("Content-Disposition","attachment; filename=\"informe.pdf\"");
				//response.setContentType(new MimetypesFileTypeMap().getContentType("informe.pdf"));
				response.setContentType("application/pdf");
				response.getOutputStream().write(
						JasperExportManager.exportReportToPdf(jasperPrint));
			}
			else if(exp.equals("ODT")){
				//exportar ODT
				response.setHeader("Content-Disposition","attachment; filename=\"informe.odt\"");
				response.setContentType("application/vnd.oasis.opendocument.text");
				File odtFile = new File("filename=\"informe.odt\"");
				JROdtExporter loOdtExp = new JROdtExporter();
				loOdtExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
				loOdtExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
				loOdtExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				loOdtExp.setParameter(JRExporterParameter.OUTPUT_FILE, odtFile);
				loOdtExp.exportReport();
			}
			else if(exp.equals("RTF")){
				//exportar RTF
				response.setHeader("Content-Disposition","attachment; filename=\"informe.rtf\"");
				response.setContentType("application/rtf");
				File rtfFile = new File("filename=\"informe.rtf\"");
				JRRtfExporter loRtfExp = new JRRtfExporter();
				loRtfExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
				loRtfExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
				loRtfExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				loRtfExp.setParameter(JRExporterParameter.OUTPUT_FILE, rtfFile);
				loRtfExp.exportReport();
			}
			else if(exp.equals("CSV")){
				//exportar CSV
				response.setHeader("Content-Disposition","attachment; filename=\"informe.csv\"");
				response.setContentType("Content-type: text/csv");
				File csvFile = new File("filename=\"informe.csv\"");
				JRCsvExporter loCsvExp = new JRCsvExporter();
				loCsvExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
				loCsvExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
				loCsvExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				loCsvExp.setParameter(JRExporterParameter.OUTPUT_FILE, csvFile);
				loCsvExp.exportReport();
			}
			else if(exp.equals("HTML")){
				//exportar HTML		
				response.setHeader("Content-Disposition","attachment; filename=\"informe.html\"");
				response.setContentType("text/html");
				File htmlFile = new File("filename=\"informe.html\"");
				final JRHtmlExporter loHtmlExp = new JRHtmlExporter();  
				loHtmlExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);  
				loHtmlExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());  
				loHtmlExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
				loHtmlExp.setParameter(JRExporterParameter.OUTPUT_FILE, htmlFile);
				loHtmlExp.exportReport();  
			}
			else if(exp.equals("XML")){
				//exportar XML		
				response.setHeader("Content-Disposition","attachment; filename=\"informe.xml\"");
				response.setContentType("text/xml");
				JRXmlExporter xmlExporter = new JRXmlExporter();
				File xmlFile = new File("filename=\"informe.xml\"");
				xmlExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				xmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());  
				xmlExporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
				xmlExporter.setParameter(JRExporterParameter.OUTPUT_FILE, xmlFile);
				xmlExporter.exportReport();

			}
			else if(exp.equals("XLS")){
				//exportar XLS		
				response.setHeader("Content-Disposition","attachment; filename=\"informe.xls\"");
				response.setContentType("application/excel");
				JRXlsExporter loXlsExp = new JRXlsExporter();
				loXlsExp.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				loXlsExp.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
				loXlsExp.setParameter(JRXlsExporterParameter.CHARACTER_ENCODING, "UTF-8");
				loXlsExp.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, response.getOutputStream());
				loXlsExp.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				loXlsExp.exportReport();

			}
			
			adminService.getMesuresTemporalsHelper().mesuraCalcular("INFORME: " + (String)model.get(MODEL_ATTRIBUTE_CONSULTA) + " (REPORT)");
		}
	}

	public String getContentType() {
		return null;
	}

}
