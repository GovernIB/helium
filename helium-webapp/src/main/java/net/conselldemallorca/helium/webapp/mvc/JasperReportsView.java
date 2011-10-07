package net.conselldemallorca.helium.webapp.mvc;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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

	public static final String MODEL_ATTRIBUTE_MAPCOLLECTIONDATA = "mapCollectionData";
	public static final String MODEL_ATTRIBUTE_REPORTFILE = "reportFile";
	public static final String MODEL_ATTRIBUTE_REPORTCONTENT = "reportContent";



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void render(
			Map model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader(HEADER_PRAGMA, "");
		response.setHeader(HEADER_EXPIRES, "");
		response.setHeader(HEADER_CACHE_CONTROL, "");

		JRDataSource dataSource = null;
		if (model.get(MODEL_ATTRIBUTE_MAPCOLLECTIONDATA) != null)
			dataSource = new JRMapCollectionDataSource(
					(List<Map<String, Object>>)model.get(MODEL_ATTRIBUTE_MAPCOLLECTIONDATA));
		if (dataSource != null) {
			JasperReport report = null;
			if (model.get(MODEL_ATTRIBUTE_REPORTFILE) != null) {
				report = JasperCompileManager.compileReport(
						new FileInputStream(
								(String)model.get(MODEL_ATTRIBUTE_REPORTFILE)));
			} else {
				report = JasperCompileManager.compileReport(
						new ByteArrayInputStream(
								(byte[])model.get(MODEL_ATTRIBUTE_REPORTCONTENT)));
			}
			Map<String, Object> params = new HashMap<String, Object>();
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					report,
					params,
					dataSource);
			response.setHeader("Content-Disposition","attachment; filename=\"informe.pdf\"");
			response.setContentType(new MimetypesFileTypeMap().getContentType("informe.pdf"));
			response.getOutputStream().write(
					JasperExportManager.exportReportToPdf(jasperPrint));
		}
	}

	public String getContentType() {
		return null;
	}

}
