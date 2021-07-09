/**
 * 
 */
package es.caib.helium.back.controller;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.back.helper.AjaxHelper;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ModalHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controlador base que implementa funcionalitats comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class BaseController implements MessageSourceAware {

	public static final String ESQUEMA_PREFIX = "/helium";
	MessageSource messageSource;

	protected String modalUrlTancar(boolean refrescar) {
		if (refrescar)
			return modalUrlTancar();
		else
			return "v3/utils/modalTancar";
	}

	protected String modalUrlTancar() {
		return "v3/utils/modalTancarIRefrescar";
	}

	protected String ajaxUrlOk() {
		return "redirect:/nodeco/util/ajaxOk";
	}

	protected String getPageURI(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.substring(uri.indexOf(ESQUEMA_PREFIX) + ESQUEMA_PREFIX.length());
	}

	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							messageKey,
							messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getAjaxControllerReturnValueError(
				request,
				url,
				messageKey,
				null);
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							messageKey,
							messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueError(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected void writeFileToResponse(
			String fileName,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(
			HttpServletRequest request,
			String key,
			Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}
	protected String getMessage(
			HttpServletRequest request,
			String key) {
		return getMessage(request, key, null);
	}


	protected String redirectByModal(HttpServletRequest request, String url){
		if (ModalHelper.isModal(request)){
			url = "/modal" + url;
		}
		return "redirect:" + url;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private class HTMLCharacterEscapes extends CharacterEscapes {

		private final int[] asciiEscapes;

		public HTMLCharacterEscapes() {
			int[] esc = CharacterEscapes.standardAsciiEscapesForJSON();
			esc['<'] = CharacterEscapes.ESCAPE_STANDARD;
			esc['>'] = CharacterEscapes.ESCAPE_STANDARD;
			esc['&'] = CharacterEscapes.ESCAPE_STANDARD;
			esc['\''] = CharacterEscapes.ESCAPE_STANDARD;
			asciiEscapes = esc;
		}

		@Override
		public int[] getEscapeCodesForAscii() {
			return asciiEscapes;
		}

		@Override
		public SerializableString getEscapeSequence(int ch) {
			return null;
		}

		private static final long serialVersionUID = 7857770126102468040L;
	}

	/**
	 * Transforma un Object a String en format JSON
	 */
	protected String getObjectInJSON(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
		String result = null;
		try {
			result = mapper.writeValueAsString(object);
		} catch (Exception e) {
			log.error("Error convertint objecte a JSON", e);
		}
		return result;
	}

}
