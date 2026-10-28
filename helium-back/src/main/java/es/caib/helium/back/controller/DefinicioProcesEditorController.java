/**
 *
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.helper.*;
import es.caib.helium.commons.config.BaseConfig;
import es.caib.helium.commons.dto.*;
import es.caib.helium.commons.dto.handlers.HandlerDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per a la pipella de l'editor de la definició de procés.
 *
 */
@Controller(value = "definicioProcesEditorControllerV3")
@RequestMapping("/definicioProces")
public class DefinicioProcesEditorController extends BaseDefinicioProcesController {

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/editor")
	public String editor(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(
					request,
					jbmpKey,
					definicioProcesId,
					model,
					"editor");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdPermisDissenyar(entornActual.getId(),
					definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			model.addAttribute("expedientTipusId", definicioProces.getExpedientTipus() != null? definicioProces.getExpedientTipus().getId() : null);
			model.addAttribute("baseUrl", (BaseConfig.BACK_CONTEXT_PREFIX + "/definicioProces/" + definicioProces.getBpmKey() + "/" + definicioProces.getId().toString()));
		}
		model.addAttribute("entornId", entornActual.getId());
		model.addAttribute("returnUrl", request.getHeader("referer"));
		return "definicioProcesEditor";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/editorXml")
	@ResponseBody
	public String editorXml(
		HttpServletRequest request,
		@PathVariable String jbmpKey,
		@PathVariable Long definicioProcesId) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			return definicioProcesService.getXml(entornActual.getId(), definicioProcesId);
		} else {
			return null;
		}
	}

	/** Retorna la llista de handlers disponibles per al select de classes Java de l'editor. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/handlers", method = RequestMethod.GET)
	@ResponseBody
	public List<HandlerDto> handlers(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId) {
		List<HandlerDto> handlers = new ArrayList<>();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			// Handlers predefinits
			try {
				handlers.addAll(dissenyService.getHandlersPredefinits());
			} catch (Exception e) {
				logger.error("Error carregant els handlers predefinits: " + e.getMessage(), e);
			}
			// Handlers propis (pujats com a JAR/class)
			try {
				List<String> handlersPropis = dissenyService.findHandlersBpmOrdenats(definicioProcesId);
				for (String classe : handlersPropis) {
					HandlerDto dto = new HandlerDto();
					dto.setClasse(classe);
					dto.setNom(classe);
					handlers.add(dto);
				}
			} catch (Exception e) {
				logger.error("Error carregant els handlers propis: " + e.getMessage(), e);
			}
		}
		return handlers;
	}

	private static final Log logger = LogFactory.getLog(DefinicioProcesEditorController.class);
}
