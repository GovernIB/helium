/**
 *
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.DefinicioProcesCommand;
import es.caib.helium.back.command.ExpedientTipusAccioDesplegarCommand;
import es.caib.helium.back.helper.*;
import es.caib.helium.commons.config.BaseConfig;
import es.caib.helium.commons.dto.*;
import es.caib.helium.commons.exportacio.DefinicioProcesExportacio;
import es.caib.helium.commons.exportacio.DefinicioProcesExportacioCommandDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
			model.addAttribute("baseUrl", (BaseConfig.BACK_CONTEXT_PREFIX + "/definicioProces/" + definicioProces.getJbpmKey() + "/" + definicioProces.getId().toString()));
		}
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

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/save", method = RequestMethod.POST)
	@ResponseBody
	public String saveXml(
		HttpServletRequest request,
		@PathVariable String jbmpKey,
		@PathVariable Long definicioProcesId,
		@ModelAttribute("command") ExpedientTipusAccioDesplegarCommand command) throws IOException {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);

		DefinicioProcesExportacio exp = new DefinicioProcesExportacio();
		exp.setContingutDeploy(command.getFile().getBytes());
		exp.setNomDeploy(definicioProces.getJbpmKey() + ".bpmn");

		definicioProcesService.importar(entornActual.getId(),
										definicioProces.getExpedientTipus().getId(),
										null, //definicioProcesId,
										null,
										exp);
		return "ok";
	}

}
