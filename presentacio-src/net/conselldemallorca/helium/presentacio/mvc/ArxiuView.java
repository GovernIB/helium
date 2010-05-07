package net.conselldemallorca.helium.presentacio.mvc;

import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

/**
 * Vista per enviar un arxiu com a resposta
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ArxiuView implements View {

	public static final String HEADER_PRAGMA = "Pragma";
	public static final String HEADER_EXPIRES = "Expires";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";

	public static final String MODEL_ATTRIBUTE_DATA = "data";
	public static final String MODEL_ATTRIBUTE_FILENAME = "fileName";
	public static final String MODEL_ATTRIBUTE_CONTENTTYPE = "contentType";



	@SuppressWarnings("unchecked")
	public void render(
			Map model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader(HEADER_PRAGMA, "");
		response.setHeader(HEADER_EXPIRES, "");
		response.setHeader(HEADER_CACHE_CONTROL, "");
		byte[] data = (byte[])model.get(MODEL_ATTRIBUTE_DATA);
		String fileName = (String)model.get(MODEL_ATTRIBUTE_FILENAME);
		String contentType = (String)model.get(MODEL_ATTRIBUTE_CONTENTTYPE);
		if (data == null)
			data = new byte[0];
		if (fileName == null)
			fileName = "unknown";
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		if (contentType == null)
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		else
			response.setContentType(contentType);
		response.getOutputStream().write(data);
	}

	public String getContentType() {
		return null;
	}

}
