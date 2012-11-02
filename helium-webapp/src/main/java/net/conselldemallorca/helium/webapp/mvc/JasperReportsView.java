package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

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
			JasperReport report = null;
			report = JasperCompileManager.compileReport(
					new ByteArrayInputStream(
							(byte[])model.get(MODEL_ATTRIBUTE_REPORTCONTENT)));
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			JasperReport subreport = null;
			HashMap<String, byte[]> subreports = (HashMap<String, byte[]>)model.get(MODEL_ATTRIBUTE_SUBREPORTS);
			
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
			response.setHeader("Content-Disposition","attachment; filename=\"informe.pdf\"");
			//response.setContentType(new MimetypesFileTypeMap().getContentType("informe.pdf"));
			response.setContentType("application/pdf");
			response.getOutputStream().write(
					JasperExportManager.exportReportToPdf(jasperPrint));
		}
	}

	public String getContentType() {
		return null;
	}

}
