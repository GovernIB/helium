package net.conselldemallorca.helium.webapp.v3.helper;

import javax.servlet.http.HttpServletRequest;



/**
 * Utilitat per a facilitar la consulta i operacions sobre URLs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UrlHelper {

	public static String getAbsoluteControllerBase(
			HttpServletRequest request,
			String webContext) {
		return	request.getScheme() + "://" +
				request.getServerName() + ":" +
				request.getServerPort() +
				request.getContextPath() +
				webContext;
	}

}
