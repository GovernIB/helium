package net.conselldemallorca.helium.webapp.mvc;

import java.io.ObjectOutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * Vista per guardar un objecte serialitzat a dins un arxiu
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SerialitzarView extends AbstractView {

	private static final String HEADER_PRAGMA = "Pragma";
	private static final String HEADER_EXPIRES = "Expires";
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";

	@SuppressWarnings("unchecked")
	public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader(HEADER_PRAGMA, "");
		response.setHeader(HEADER_EXPIRES, "");
		response.setHeader(HEADER_CACHE_CONTROL, "");
		String filename = (String)model.get("filename");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		super.render(model, request, response);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(
			Map model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ObjectOutputStream output = new ObjectOutputStream(response.getOutputStream());
		Object objecte = model.get("data");
		if (objecte != null)
			output.writeObject(objecte);
		output.close();
	}

}
