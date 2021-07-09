package es.caib.helium.back.view;

import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.service.AdminService;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.export.HtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOdtExporterConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	public static final String MODEL_ATTRIBUTE_PARAMS = "reportParams";


	private AdminService adminService;

//	@Autowired
//	private MetricRegistry metricRegistry;

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
		
		// #1130 Per un error en les consultes d'INIPAR es posa una mètrica per saber el temps
		// d'execució de les consultes.
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		// TODO: Mètriques
//		final Timer timerTotal = metricRegistry.timer(
//				MetricRegistry.name(
//						JasperReportsView.class,
//						"informe" ));
//		final Timer.Context contextTotal = timerTotal.time();
//		Counter countTotal = metricRegistry.counter(
//				MetricRegistry.name(
//						JasperReportsView.class,
//						"informe.count"));
//		countTotal.inc();
//		final Timer timerEntorn = metricRegistry.timer(
//				MetricRegistry.name(
//						JasperReportsView.class,
//						"informe." + (String)model.get(MODEL_ATTRIBUTE_CONSULTA),
//						entorn.getCodi()));
//		final Timer.Context contextEntorn = timerEntorn.time();
//		Counter countEntorn = metricRegistry.counter(
//				MetricRegistry.name(
//						JasperReportsView.class,
//						"informe." + (String)model.get(MODEL_ATTRIBUTE_CONSULTA)+ ".count",
//						entorn.getCodi()));
//		countEntorn.inc();
		
		try {			
			JRBeanCollectionDataSource datasource = null;
			if (model.get(MODEL_ATTRIBUTE_REPORTDATA) != null)
				datasource = new JRBeanCollectionDataSource((List<Map<String, Object>>)model.get(MODEL_ATTRIBUTE_REPORTDATA));
			if (datasource != null) {
				adminService.mesuraTemporalIniciar("INFORME: " + (String)model.get(MODEL_ATTRIBUTE_CONSULTA), "report", null, null, "REPORT");
				JasperReport report = null;
				
				report = JasperCompileManager.compileReport(new ByteArrayInputStream((byte[])model.get(MODEL_ATTRIBUTE_REPORTCONTENT)));
				
				Map<String, Object> params = new HashMap<String, Object>();

				// Per particionar l'informe de sortida en el cas que sigui molt gran
				JRFileVirtualizer virtualizer = new JRFileVirtualizer (100, System.getProperty("java.io.tmpdir"));
				virtualizer.setReadOnly(true);
				params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
				
				JasperReport subreport = null;
				HashMap<String, byte[]> subreports = (HashMap<String, byte[]>)model.get(MODEL_ATTRIBUTE_SUBREPORTS);
				if (subreports!=null)
				if (!subreports.isEmpty()) {
					Iterator it = subreports.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry e = (Map.Entry)it.next();
						subreport = JasperCompileManager.compileReport(new ByteArrayInputStream((byte[]) e.getValue()));
						String nom = (String) e.getKey();
						nom = nom.substring(0, nom.lastIndexOf("."));
						params.put(nom, subreport);
						params.put("ds_" + nom, new JRBeanCollectionDataSource((List<Map<String, Object>>)model.get(MODEL_ATTRIBUTE_REPORTDATA)));
					}
				}			

				Map<String, Object> paramsModel = (Map<String, Object>)model.get(
						MODEL_ATTRIBUTE_PARAMS);
				if (paramsModel != null)
					params.putAll(paramsModel);

				JasperPrint jasperPrint = JasperFillManager.fillReport(
						report,
						params,
						datasource);

				Object exp = (String)request.getAttribute("formatJR");

				if("PDF".equals(exp)){
					//exportar PDF		
					response.setHeader("Content-Disposition","attachment; filename=\"informe.pdf\"");
					response.setContentType("application/pdf");
					response.getOutputStream().write(
							JasperExportManager.exportReportToPdf(jasperPrint));
				}
				else if("ODT".equals(exp)){
					//exportar ODT
					response.setHeader("Content-Disposition","attachment; filename=\"informe.odt\"");
					response.setContentType("application/vnd.oasis.opendocument.text");
					File odtFile = new File("filename=\"informe.odt\"");
					JROdtExporter loOdtExp = new JROdtExporter();
					loOdtExp.setExporterInput(new SimpleExporterInput(jasperPrint));
//					loOdtExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					loOdtExp.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
//					loOdtExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
					loOdtExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
					loOdtExp.setParameter(JRExporterParameter.OUTPUT_FILE, odtFile);
					SimpleOdtExporterConfiguration exporterConfiguration = new SimpleOdtExporterConfiguration();
					loOdtExp.setConfiguration(exporterConfiguration);
					loOdtExp.exportReport();
				}
				else if("RTF".equals(exp)){
					//exportar RTF
					response.setHeader("Content-Disposition","attachment; filename=\"informe.rtf\"");
					response.setContentType("application/rtf");
					File rtfFile = new File("filename=\"informe.rtf\"");
					JRRtfExporter loRtfExp = new JRRtfExporter();
					loRtfExp.setExporterInput(new SimpleExporterInput(jasperPrint));
//					loRtfExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					loRtfExp.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
//					loRtfExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
					loRtfExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
					loRtfExp.setParameter(JRExporterParameter.OUTPUT_FILE, rtfFile);
					loRtfExp.exportReport();
				}
				else if("CSV".equals(exp)){
					//exportar CSV
					response.setHeader("Content-Disposition","attachment; filename=\"informe.csv\"");
					response.setContentType("text/csv");
					File csvFile = new File("filename=\"informe.csv\"");
					JRCsvExporter loCsvExp = new JRCsvExporter();
					loCsvExp.setExporterInput(new SimpleExporterInput(jasperPrint));
//					loCsvExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					loCsvExp.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
//					loCsvExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
					loCsvExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
					loCsvExp.setParameter(JRExporterParameter.OUTPUT_FILE, csvFile);
					loCsvExp.exportReport();
				}
				else if("HTML".equals(exp)){
					//exportar HTML		
					response.setHeader("Content-Disposition","attachment; filename=\"informe.html\"");
					response.setContentType("text/html");
					File htmlFile = new File("filename=\"informe.html\"");
					final HtmlExporter loHtmlExp = new HtmlExporter();
					loHtmlExp.setExporterInput(new SimpleExporterInput(jasperPrint));
//					loHtmlExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					loHtmlExp.setExporterOutput(new SimpleHtmlExporterOutput(response.getOutputStream()));
//					loHtmlExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
					loHtmlExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
					loHtmlExp.setParameter(JRExporterParameter.OUTPUT_FILE, htmlFile);
					HtmlExporterConfiguration configuration = new SimpleHtmlExporterConfiguration();
					loHtmlExp.setConfiguration(configuration);
					loHtmlExp.exportReport();  
				}
				else if("XML".equals(exp)){
					//exportar XML		
					response.setHeader("Content-Disposition","attachment; filename=\"informe.xml\"");
					response.setContentType("text/xml");
					JRXmlExporter xmlExporter = new JRXmlExporter();
					File xmlFile = new File("filename=\"informe.xml\"");
					xmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//					xmlExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					xmlExporter.setExporterOutput(new SimpleXmlExporterOutput(response.getOutputStream()));
//					xmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
					xmlExporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
					xmlExporter.setParameter(JRExporterParameter.OUTPUT_FILE, xmlFile);
					xmlExporter.exportReport();

				}
				else if("XLS".equals(exp)){
					//exportar XLS		
					response.setHeader("Content-Disposition","attachment; filename=\"informe.xls\"");
					response.setContentType("application/excel");
					JRXlsExporter loXlsExp = new JRXlsExporter();
					loXlsExp.setExporterInput(new SimpleExporterInput(jasperPrint));
//					loXlsExp.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
					loXlsExp.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
//					loXlsExp.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, response.getOutputStream());
					SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
					configuration.setRemoveEmptySpaceBetweenRows(true);
//					loXlsExp.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
					configuration.setDetectCellType(true);
//					loXlsExp.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
					loXlsExp.setConfiguration(configuration);
					loXlsExp.setParameter(JRXlsExporterParameter.CHARACTER_ENCODING, "UTF-8");
					loXlsExp.exportReport();
				} else {
					//exportar PDF		
					response.setHeader("Content-Disposition","attachment; filename=\"informe.pdf\"");
					response.setContentType("application/pdf");
					response.getOutputStream().write(
							JasperExportManager.exportReportToPdf(jasperPrint));
				}				
				adminService.mesuraTemporalCalcular("INFORME: " + (String)model.get(MODEL_ATTRIBUTE_CONSULTA), "report", null, null, "REPORT");			
			}
		} finally {
//			contextTotal.stop();
//			contextEntorn.stop();
		}
	}

	public String getContentType() {
		return null;
	}
}
