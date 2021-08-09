/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
		newAlert(
				request,
				SESSION_ATTRIBUTE_ERROR,
				text);
	}
	public static void warning(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_WARNING,
				text);
	}
	public static void success(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_SUCCESS,
				text);
	}
	public static void info(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_INFO,
				text);
	}

	public List<String> getErrors(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_ERROR,
				delete);
	}
	public List<String> getWarnings(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_WARNING,
				delete);
	}
	public List<String> getSuccesses(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_SUCCESS,
				delete);
	}
	public List<String> getInfos(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_INFO,
				delete);
	}

	public static List<String> getGlobalErrorsFromCommands(HttpServletRequest request) {
		List<String> response = new ArrayList<String>();
		Enumeration<String> attributeNames = request.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			if (!attributeName.contains(".") && attributeName.contains("ommand")) {
				BindingResult bindingResult = (BindingResult)request.getAttribute("org.springframework.validation.BindingResult." + attributeName);
				if (bindingResult.getGlobalErrorCount() > 0) {
					List<ObjectError> globalErrors = bindingResult.getGlobalErrors();
					for (ObjectError globalError: globalErrors) {
						response.add(globalError.getDefaultMessage());
					}
				}
			}
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private static void newAlert(
			HttpServletRequest request,
			String attributeName,
			String text) {
		HttpSession session = request.getSession();
		List<String> alerts = (List<String>)session.getAttribute(attributeName);
		if (alerts == null) {
			alerts = new ArrayList<String>();
			session.setAttribute(attributeName, alerts);
		}
		alerts.add(text);
	}

	@SuppressWarnings("unchecked")
	private static List<String> getAlerts(
			HttpServletRequest request,
			String attributeName,
			boolean delete) {
		HttpSession session = request.getSession();
		List<String> alerts = (List<String>)session.getAttribute(attributeName);
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
			error(request, errorText);
		}		
	}
	
	public static void errorGlobal(
			HttpServletRequest request,
			BindingResult result,
			String textValidacio) {
		for (ObjectError error: result.getGlobalErrors()) {
			String errorText = (error.getDefaultMessage() == null || error.getDefaultMessage().isEmpty()) ? textValidacio : error.getDefaultMessage();
			error(request, errorText);
		}
	}

}
