package net.conselldemallorca.helium.webapp.v3.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.report.FieldValue;
import net.conselldemallorca.helium.webapp.v3.helper.InformeHelper.Estat;
import net.conselldemallorca.helium.webapp.v3.helper.InformeHelper.InformeInfo;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
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
import net.sf.jasperreports.engine.fill.AsynchronousFillHandle;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;

/**
 * Vista per a generar un report amb Jasper Reports
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class JasperReportsHelper {

	/** Mapeig de les diferents consultes per identificador de sessió. */
	private Map<String, JasperReportInfo> jasperInfo = new HashMap<String, JasperReportInfo>();
	
	public static final String HEADER_PRAGMA = "Pragma";
	public static final String HEADER_EXPIRES = "Expires";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";

	@Resource
	private InformeHelper informeHelper;

	/** Mètode per iniciar la generació en 2n pla.
	 * 
	 * @param info
	 * 			Referència a l'objecte amb la informació de la generació que s'ha d'anar actualitzant.
	 * @param nomConsulta
	 * @param dadesDataSource
	 * @param report
	 * @param subreports
	 * @param formatExportacio 
	 * @return
	 * @throws Pot llençar una excepció de compilació dels reports o d'accés a les dades.
	 */
	public InformeInfo generarReport(
			InformeInfo info, 
			String nomConsulta, 
			List<Map<String, FieldValue>> dadesDataSource,
			byte[] reportContingut, 
			HashMap<String, byte[]> subreports,
			Map<String, Object> parametres, 
			String formatExportacio) throws Exception{
		
		JRBeanCollectionDataSource datasource = null;
		if (dadesDataSource != null)
			datasource = new JRBeanCollectionDataSource(dadesDataSource);
		if (datasource != null) {
			JasperReport report = null;
			
			report = JasperCompileManager.compileReport(new ByteArrayInputStream(reportContingut));
			
			Map<String, Object> params = new HashMap<String, Object>();

			// Per particionar l'informe de sortida en el cas que sigui molt gran
			JRFileVirtualizer virtualizer = new JRFileVirtualizer (1000, System.getProperty("java.io.tmpdir"));
			virtualizer.setReadOnly(true);
			params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			

			JasperReport subreport = null;
			if (subreports!=null && !subreports.isEmpty()) {
				Iterator<Entry<String, byte[]>> it = subreports.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, byte[]> e = it.next();
					subreport = JasperCompileManager.compileReport(new ByteArrayInputStream((byte[]) e.getValue()));
					String nom = (String) e.getKey();
					nom = nom.substring(0, nom.lastIndexOf("."));
					params.put(nom, subreport);
					params.put("ds_" + nom, new JRBeanCollectionDataSource(dadesDataSource));
				}
			}			

			if (parametres != null)
				params.putAll(parametres);
			
			AsynchronousFillHandle handle = AsynchronousFillHandle.createHandle(report, params, datasource);
			JasperReportsAsyncFillListener listener = new JasperReportsAsyncFillListener(info, this);
			handle.addListener(listener);
			
			// Guarda la informació associada
			jasperInfo.put(
						info.getId(), 
						new JasperReportInfo(
								handle, 
								listener,
								virtualizer,
								info));
			
			// Fa una comporovació de que el procés no s'hagi cancel·lat
			if (Estat.CANCELLAT.equals(info.getEstat()))
					return info;

			// Comença a omlir el report
			handle.startFill();
			
			info.setEstat(Estat.GENERANT);
			info.setFormatExportacio(formatExportacio);
		} else {
			info.setEstat(Estat.ERROR);
			info.setMissatge("No hi ha dades per generar l'informe.");
		}
		info.setConsulta(nomConsulta);
		return info;
	}

	
	/** Mètode per exportar el resultat.
	 * 
	 * @param info
	 * 			Referència a l'objecte amb la informació de la generació que s'ha d'anar actualitzant.
	 * @param nomConsulta
	 * @param dadesDataSource
	 * @param report
	 * @param subreports
	 * @return
	 * @throws JRException 
	 * @throws IOException 
	 */
	public void exportarReport(
			InformeInfo info, 
			HttpServletResponse response) throws IOException, JRException {
		
		response.setHeader(HEADER_PRAGMA, "");
		response.setHeader(HEADER_EXPIRES, "");
		response.setHeader(HEADER_CACHE_CONTROL, "");
		
		JasperPrint jasperPrint = this.jasperInfo.get(info.getId()).getJasperPrint();
		
		Object exp = info.getFormatExportacio();

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
			loOdtExp.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
			loOdtExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
			loOdtExp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			loOdtExp.setParameter(JRExporterParameter.OUTPUT_FILE, odtFile);
			loOdtExp.exportReport();
		}
		else if("RTF".equals(exp)){
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
		else if("CSV".equals(exp)){
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
		else if("HTML".equals(exp)){
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
		else if("XML".equals(exp)){
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
		else if("XLS".equals(exp)){
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
		} else {
			//exportar PDF		
			response.setHeader("Content-Disposition","attachment; filename=\"informe.pdf\"");
			response.setContentType("application/pdf");
			response.getOutputStream().write(
					JasperExportManager.exportReportToPdf(jasperPrint));
		}
	}

	/** Mètode que espera a que acabi el thread de generació de l'informe. */
	public void esperarGeneracio(InformeInfo info) {
		JasperReportInfo jasperInfo = this.jasperInfo.get(info.getId());
		if (jasperInfo != null)
			synchronized(jasperInfo.o) {
				try {
					jasperInfo.o.wait();
				} catch (InterruptedException e) {}
			}
	}	

	/** Mètode que notifica a qui esperi a la informació que ja s'ha acabat.*/
	public void notificarFinalitzat(InformeInfo info) {
		JasperReportInfo jasperInfo = this.jasperInfo.get(info.getId());
		if (jasperInfo != null) {
			synchronized(jasperInfo.o) {
				try {
					jasperInfo.notifyAll();
				} catch (Exception ignored) {}
			}
			if (jasperInfo.virtualizer != null)
				jasperInfo.virtualizer.cleanup();
		}
	}

	
	
	/** Mètode per cancel·lar la generació d'un report.  
	 * @throws JRException */
	public InformeInfo cancellar(InformeInfo info) throws JRException {
		JasperReportInfo infoJasper = this.jasperInfo.get(info.getId());
		if (infoJasper != null && infoJasper.getHandle() != null) {
			infoJasper.getHandle().cancellFill();
			info.setEstat(Estat.CANCELLAT);
		}
		return info;
	}	
	
	/** Classe per guardar la informació de la generació */
	public class JasperReportInfo {
		
		private AsynchronousFillHandle handle = null;
		private JasperReportsAsyncFillListener listener = null;
		private JasperPrint jasperPrint = null;
		private JRVirtualizer virtualizer = null;;
		private InformeInfo info;
		// Objecte per esperar la fi de la generació
		private Object o = new Object();
		
		public JasperReportInfo( 		
				AsynchronousFillHandle handle,
				JasperReportsAsyncFillListener listener,
				JRVirtualizer virtualizer,
				InformeInfo info) {
			this.handle = handle;
			this.listener = listener;
			this.virtualizer = virtualizer;
			this.jasperPrint = null;
			this.info = info;
		}

		public AsynchronousFillHandle getHandle() {
			return handle;
		}
		public JasperReportsAsyncFillListener getListener() {
			return listener;
		}
		public JasperPrint getJasperPrint() {
			return jasperPrint;
		}
		public JRVirtualizer getVirualizer() {
			return virtualizer;
		}
		public InformeInfo getInformeInfo() {
			return info;
		}
	}
	
	/** Es crida des del listener. Informe el jasperPrint un cop acaba d'omplir el report. */
	public void setJasperPrint(InformeInfo informeInfo, JasperPrint jasperPrint) {
		JasperReportInfo jasperInfo = this.jasperInfo.get(informeInfo.getId());
		if (jasperInfo != null)
			jasperInfo.jasperPrint = jasperPrint;
	}	
}
