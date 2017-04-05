/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusRedireccioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella de redireccions del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusRedireccioController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(value = "/{expedientTipusId}/redireccions")
	public String redireccions(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"redireccions");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusRedireccio";
	}
	
	@RequestMapping(value="/{expedientTipusId}/redireccio/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.reassignacioFindPerDatatable(
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
			
	@RequestMapping(value = "/{expedientTipusId}/redireccio/new", method = RequestMethod.GET)
	public String nova(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		ExpedientTipusRedireccioCommand command = new ExpedientTipusRedireccioCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusRedireccioCommand", command);
		this.omplirModelPersones(request, expedientTipusId, model);
		return "v3/expedientTipusRedireccioForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/redireccio/new", method = RequestMethod.POST)
	public String novaPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusRedireccioCommand.Creacio.class) ExpedientTipusRedireccioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		this.omplirModelPersones(request, expedientTipusId, model);
        	return "v3/expedientTipusRedireccioForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.reassignacioCreate(
    				expedientTipusId,
        			ExpedientTipusRedireccioCommand.asReassignacioDto(command));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.redireccio.controller.creat"));
			return modalUrlTancar(false);			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/redireccio/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		ReassignacioDto dto = expedientTipusService.reassignacioFindAmbId(id);
		ExpedientTipusRedireccioCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusRedireccioCommand.class);
		model.addAttribute("expedientTipusRedireccioCommand", command);
		this.omplirModelPersones(request, expedientTipusId, model);
		return "v3/expedientTipusRedireccioForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/redireccio/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusRedireccioCommand.Modificacio.class) ExpedientTipusRedireccioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		this.omplirModelPersones(request, expedientTipusId, model);
        	return "v3/expedientTipusRedireccioForm";
        } else {
        	expedientTipusService.reassignacioUpdate(
        			ExpedientTipusRedireccioCommand.asReassignacioDto(command));
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.redireccio.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/redireccio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		try {
			expedientTipusService.reassignacioDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.redireccio.llistat.accio.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.redireccio.llistat.accio.esborrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la redireccio amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}
		
	private void omplirModelPersones(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();

		try {
			for (PersonaDto p : expedientTipusService.personaFindAll(entornActual.getId(), expedientTipusId)) {
				resposta.add(new ParellaCodiValorDto(p.getCodi(), p.getNomSencer()));
			}
		} catch (Exception e) {
    		MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.redireccio.controller.getPersones.error",
							new Object[] {e.getMessage()}));
		}
		model.addAttribute("persones", resposta);		
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusRedireccioController.class);
}
