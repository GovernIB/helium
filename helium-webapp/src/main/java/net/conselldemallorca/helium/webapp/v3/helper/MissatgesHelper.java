/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * Utilitat per a mostrar missatges d'alerta o informació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MissatgesHelper {

	public static final String SESSION_ATTRIBUTE_ERROR = "MissatgesHelper.Error";
	public static final String SESSION_ATTRIBUTE_WARNING = "MissatgesHelper.Warning";
	public static final String SESSION_ATTRIBUTE_SUCCESS = "MissatgesHelper.Success";
	public static final String SESSION_ATTRIBUTE_INFO = "MissatgesHelper.Info";

	public static void error(
			HttpServletRequest request,
			String text) {
		error(request, text, null);
	}
	
	public static void error(
			HttpServletRequest request,
			String text,
			Throwable ex) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_ERROR,
				text,
				ex);
	}
	public static void warning(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_WARNING,
				text,
				null);
	}
	public static void success(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_SUCCESS,
				text,
				null);
	}
	public static void info(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_INFO,
				text,
				null);
	}

	public List<Alert> getErrors(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_ERROR,
				delete);
	}
	public List<Alert> getWarnings(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_WARNING,
				delete);
	}
	public List<Alert> getSuccesses(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_SUCCESS,
				delete);
	}
	public List<Alert> getInfos(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_INFO,
				delete);
	}


	@SuppressWarnings("unchecked")
	private static void newAlert(
			HttpServletRequest request,
			String attributeName,
			String text,
			Throwable ex) {
		HttpSession session = request.getSession();
		List<Alert> alerts = (List<Alert>)session.getAttribute(attributeName);
		if (alerts == null) {
			alerts = new ArrayList<Alert>();
			session.setAttribute(attributeName, alerts);
		}
		alerts.add(Alert.builder()
					.text(text)
					.trace(ex != null ? ExceptionUtils.getStackTrace(ex) : null)
					.build());
	}

	@SuppressWarnings("unchecked")
	private static List<Alert> getAlerts(
			HttpServletRequest request,
			String attributeName,
			boolean delete) {
		HttpSession session = request.getSession();
		List<Alert> alerts = (List<Alert>)session.getAttribute(attributeName);
		if (delete)
			session.removeAttribute(attributeName);
		return alerts;
	}
	
	public static void error(
			HttpServletRequest request,
			BindingResult result,
			String textValidacio) {
		for (ObjectError error: result.getAllErrors()) {
			String errorText = (error.getDefaultMessage() == null || error.getDefaultMessage().isEmpty()) ? textValidacio : error.getDefaultMessage();
			error(request, errorText, null);
		}		
	}
	
	public static void errorGlobal(
			HttpServletRequest request,
			BindingResult result,
			String textValidacio) {
		for (ObjectError error: result.getGlobalErrors()) {
			String errorText = (error.getDefaultMessage() == null || error.getDefaultMessage().isEmpty()) ? textValidacio : error.getDefaultMessage();
			error(request, errorText, null);
		}
	}

}
