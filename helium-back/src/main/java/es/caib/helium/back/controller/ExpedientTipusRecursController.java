/**
 *
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.DefinicioProcesDesplegarCommand;
import es.caib.helium.back.command.DefinicioProcesDesplegarCommand.Desplegament;
import es.caib.helium.back.helper.*;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.mvc.ArxiuView;
import es.caib.helium.commons.dto.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controlador per a la pipella de redireccions del tipus d'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientTipus")
public class ExpedientTipusRecursController extends BaseExpedientTipusController {

	@RequestMapping(value = "/{expedientTipusId}/recursos")
	public String recursos(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"recursos");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "expedientTipusRecurs";
	}

	@RequestMapping(value="/{expedientTipusId}/recurs/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.recursFindPerDatatable(
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}

	@RequestMapping(value = "/{expedientTipusId}/recursos/new", method = RequestMethod.GET)
	public String nouRecurs(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				expedientTipusId);
		DefinicioProcesDesplegarCommand command = new DefinicioProcesDesplegarCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setEntornId(entornActual.getId());
		command.setAccio(DefinicioProcesDesplegarCommand.ACCIO_PROCES.PROCES_DESPLEGAR);
		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("command", command);
		return "expedientTipusRecursForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/recursos/new", method = RequestMethod.POST)
	public String nouRecursPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@ModelAttribute("command") @Validated(Desplegament.class) DefinicioProcesDesplegarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		command.setExpedientTipusId(expedientTipusId);
		boolean error = false;
		if (bindingResult.hasErrors()) {
			error = true;
		} else {
			try {
				List<String> recursos = dissenyService.updateHandlersAccions(
						expedientTipusId,
						command.getFile().getOriginalFilename(),
						command.getFile().getBytes());
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"expedient.tipus.accio.desplegar.form.success",
								new Object[] {recursos.size(), recursos}));
			} catch (Exception e) {
				logger.error("Error : (" + e.getClass() + ") " + e.getLocalizedMessage(), e);
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"definicio.proces.actualitzar.excepcio",
								new Object[] {e.getMessage()}),
						e);
				error = true;
			}
		}
		if (error) {
			model.addAttribute(
					"expedientTipus",
					expedientTipusService.findAmbIdPermisDissenyar(
							entornActual.getId(),
							expedientTipusId));
			return "expedientTipusRecursForm";
		} else {
			return modalUrlTancar(false);
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/recurs/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id) {
		try {
			expedientTipusService.recursDelete(expedientTipusId, id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.recurs.llistat.accio.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.recurs.llistat.accio.esborrar.error"),
					e);
			logger.error("S'ha produit un error al intentar eliminar el recurs amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/recurs/{id}/descarregar")
	public View recursDescarregar(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable Long expedientTipusId,
		@PathVariable Long id,
		@RequestParam String nom,
		Model model) throws IOException {
		expedientTipusService.getRecursContingut(expedientTipusId, id);
		model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME,nom);
		model.addAttribute(
			ArxiuView.MODEL_ATTRIBUTE_DATA,
			expedientTipusService.getRecursContingut(expedientTipusId, id));
		return arxiuView;
	}

	/** Mètode per crear un .zip i descarregar el .par per una versió de la definició de procés.
	 *
	 * @param request
	 * @param expedientTipusId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/recurs/par")
	public View recursDescarregarPar(
		HttpServletRequest request,
		@PathVariable Long expedientTipusId,
		Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisConsultar(entornActual.getId(), expedientTipusId);
		model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, expedientTipus.getCodi() + ".par");
		model.addAttribute(
			ArxiuView.MODEL_ATTRIBUTE_DATA,
			expedientTipusService.getParContingut(expedientTipusId));
		return arxiuView;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusRecursController.class);

}
