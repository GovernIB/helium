/**
 *
 */
package es.caib.helium.back.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;
import javax.servlet.http.HttpServletRequest;

/**
 * Tractament global de les excepcions en els controladors.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final String ERROR_PAGE = "utils/error";

	@ExceptionHandler(value = { AccessDeniedException.class, EJBAccessException.class })
	public ModelAndView handleAccessDeniedException(
			Exception ex) {
		ModelAndView model = new ModelAndView(ERROR_PAGE);
		model.setStatus(HttpStatus.FORBIDDEN);
		ErrorObject errorObject = new ErrorObject(
				HttpStatus.FORBIDDEN.value(),
				ex.getMessage());
		errorObject.setAccessDenied(true);
		ErrorObject.AccessDeniedSource accessDeniedSource = ErrorObject.AccessDeniedSource.SPRING;
		if (ex instanceof EJBAccessException) {
			accessDeniedSource = ErrorObject.AccessDeniedSource.EJB;
		}
		errorObject.setAccessDeniedSource(accessDeniedSource != null ? accessDeniedSource : ErrorObject.AccessDeniedSource.SPRING);
		errorObject.setThrowable(ex);
		errorObject.setStackTrace(ExceptionUtils.getStackTrace(ex));
		model.addObject("errorObject", errorObject);
		return model;
	}

	@ExceptionHandler(EJBException.class)
	public ModelAndView handleEJBException(
			EJBException ex,
			HttpServletRequest request) {
		Exception cause = ex.getCause() instanceof Exception ? (Exception) ex.getCause() : ex;
		return handleAllUncaughtException(cause, request);
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleAllUncaughtException(
			Exception ex,
			HttpServletRequest request) {
		log.error("Error al processar la petició HTTP al recurs " + request.getRequestURI(), ex);
		ModelAndView model = new ModelAndView(ERROR_PAGE);
		model.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		ErrorObject errorObject = new ErrorObject(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage());
		errorObject.setThrowable(ex);
		errorObject.setStackTrace(ExceptionUtils.getStackTrace(ex));
		model.addObject("errorObject", errorObject);
		return model;
	}

}
