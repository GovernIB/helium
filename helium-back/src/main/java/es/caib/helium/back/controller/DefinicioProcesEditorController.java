/**
 *
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.DefinicioProcesCommand;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
	public ResponseEntity<String> saveXml(
		HttpServletRequest request,
		@PathVariable String jbmpKey,
		@PathVariable Long definicioProcesId,
		@RequestBody DefinicioProcesCommand definicioProcesCommand) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		//DefinicioProcesExportacio exp = definicioProcesService.exportar(entornActual.getId(), definicioProcesId, null);
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);

		DefinicioProcesExportacio exp = new DefinicioProcesExportacio();
		exp.setContingutDeploy(Base64.getDecoder().decode(definicioProcesCommand.getXml()));
		exp.setNomDeploy(definicioProces.getJbpmKey() + ".bpmn");

//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ZipOutputStream zip = new ZipOutputStream(baos);

//		try {
//			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(exp.getContingutDeploy()));
//			ZipEntry entry;
//			byte[] data = new byte[1024];
//			while ((entry = zis.getNextEntry()) != null) {
//				if(entry.getName().endsWith(".bpmn")) {
//					ZipEntry newEntry = new ZipEntry(entry.getName());
//					zip.putNextEntry(newEntry);
//					InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(definicioProcesCommand.getXml()));
//					int count;
//					while ((count = is.read(data, 0, 1024)) != -1)
//						zip.write(data, 0, count);
//					zip.closeEntry();
//				}
//			}
//			zis.close();
//			zip.close();
//			exp.setContingutDeploy(baos.toByteArray());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		DefinicioProcesDto novaDefinicioProces = definicioProcesService.importar(
												entornActual.getId(),
												definicioProces.getExpedientTipus().getId(),
												null, //definicioProcesId,
												null,
												exp);
		return ResponseEntity.ok("ok");
	}

}
