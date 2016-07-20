package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.command.PermisCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusControllerV3")
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusController extends BaseExpedientTipusController {

	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;



	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "v3/expedientTipusLlistat";
	}

	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.findPerDatatable(
						entornActual.getId(),
						paginacioParams.getFiltre(),
						paginacioParams));
	}	

	@RequestMapping(value = "/{expedientTipusId}", method = RequestMethod.GET)
	public String pipelles(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		return mostrarInformacioExpedientTipusPerPipelles(
				request,
				expedientTipusId,
				model,
				null);
	}
	
	/** Pipella d'informació. */
	@RequestMapping(value = "/{expedientTipusId}/informacio")
	public String informacio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"informacio");
		}
		omplirModelPestanyaInformacio(
				request,
				expedientTipusId,
				model);		
		return "v3/expedientTipusInformacio";
	}
	private void omplirModelPestanyaInformacio(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			// Responsable per defecte
			if (expedientTipus.getResponsableDefecteCodi() != null) {
				model.addAttribute(
						"responsableDefecte",
						aplicacioService.findPersonaAmbCodi(
								expedientTipus.getResponsableDefecteCodi()));
			}
			model.addAttribute(
					"definicioProcesInicial",
					dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId));
		}
		// Permisos per a les accions
		boolean potEscriure;
		if (entornActual.isPermisDesign()) {
			potEscriure = true;
		} else {
			try {
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId, 
						false, 
						true,  // comprovarPermisWrite 
						false, 
						false, 
						false);
				potEscriure = true;
			} catch (Exception e){
				potEscriure = false;
			}
		}
		model.addAttribute("potEscriure", potEscriure);
		
		boolean potEsborrar;
		if (entornActual.isPermisDesign()) {
			potEsborrar = true;
		} else {
			try {
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
																expedientTipusId, 
																false, 
																false,   
																false, 
																true,	// comprovarPermisDelete 
																false);
				potEsborrar = true;
			} catch (Exception e){
				potEsborrar = false;
			}
		}		
		model.addAttribute("potEsborrar", potEsborrar);
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			Model model) {
		model.addAttribute("expedientTipusCommand", new ExpedientTipusCommand());
		return "v3/expedientTipusForm";
	}
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@Validated(Creacio.class) ExpedientTipusCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusForm";
        } else {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		// Transforma els llistats d'anys i valors 
    		List<Integer> sequenciesAny = new ArrayList<Integer>();
    		List<Long> sequenciesValor = new ArrayList<Long>();
    		for (int i = 0; i < command.getSequenciesAny().size(); i++ ){
    			sequenciesAny.add(Integer.parseInt(command.getSequenciesAny().get(i)));
    			sequenciesValor.add(Long.parseLong(command.getSequenciesValor().get(i)));
    		}
    		expedientTipusService.create(
    				entornActual.getId(),
    				conversioTipusHelper.convertir(
    						command,
    						ExpedientTipusDto.class),
    				sequenciesAny,
        			sequenciesValor);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus",
					"expedient.tipus.controller.creat");
        }
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto dto = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				id);
		ExpedientTipusCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusCommand.class);
		for (Integer key: dto.getSequenciaAny().keySet()) {
			SequenciaAnyDto any = dto.getSequenciaAny().get(key);
			command.getSequenciesAny().add(any.getAny().toString());
			command.getSequenciesValor().add(any.getSequencia().toString());
		}
		model.addAttribute("expedientTipusCommand", command);
		return "v3/expedientTipusForm";
	}
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Validated(Modificacio.class) ExpedientTipusCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusForm";
        } else {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		// Transforma els llistats d'anys i valors 
    		List<Integer> sequenciesAny = new ArrayList<Integer>();
    		List<Long> sequenciesValor = new ArrayList<Long>();
    		for (int i = 0; i < command.getSequenciesAny().size(); i++ ){
    			sequenciesAny.add(Integer.parseInt(command.getSequenciesAny().get(i)));
    			sequenciesValor.add(Long.parseLong(command.getSequenciesValor().get(i)));
    		}
        	expedientTipusService.update(
        			entornActual.getId(),
        			conversioTipusHelper.convertir(
    						command,
    						ExpedientTipusDto.class),
        			sequenciesAny,
        			sequenciesValor);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus",
					"expedient.tipus.controller.modificat");
        }
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (!expedientService.existsExpedientAmbEntornTipusITitol(
				entornActual.getId(),
				id,
				null)) {
			expedientTipusService.delete(
					entornActual.getId(),
					id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.controller.eliminat"));
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.controller.eliminar.expedients.relacionats"));
		}			
		return "redirect:/v3/expedientTipus";
	}

	@RequestMapping(value = "/{id}/permis", method = RequestMethod.GET)
	public String permisGet(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		return "v3/expedientTipusPermis";
	}

	@RequestMapping(value = "/{id}/permis/new", method = RequestMethod.GET)
	public String permisNewGet(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		model.addAttribute(new PermisCommand());
		return "v3/expedientTipusPermisForm";
	}
	@RequestMapping(value = "/{id}/permis/new", method = RequestMethod.POST)
	public String permisNewPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		return permisUpdatePost(
				request,
				id,
				null,
				command,
				bindingResult,
				model);
	}
	@RequestMapping(value = "/{id}/permis/{permisId}", method = RequestMethod.GET)
	public String permisUpdateGet(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long permisId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		PermisDto permis = expedientTipusService.permisFindById(
				entornActual.getId(),
				id,
				permisId);
		model.addAttribute(
				conversioTipusHelper.convertir(
						permis,
						PermisCommand.class));
		return "v3/expedientTipusPermisForm";
	}
	@RequestMapping(value = "/{id}/permis/{permisId}", method = RequestMethod.POST)
	public String permisUpdatePost(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long permisId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
        if (bindingResult.hasErrors()) {
        	model.addAttribute(
    				"expedientTipus",
    				expedientTipusService.findAmbIdPermisDissenyar(
    						entornActual.getId(),
    						id));
        	return "v3/expedientTipusPermisForm";
        } else {
    		expedientTipusService.permisUpdate(
    				entornActual.getId(),
    				id,
    				conversioTipusHelper.convertir(
    						command,
    						PermisDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + id + "/permis",
					"expedient.tipus.controller.permis.actualitzat");
        }
	}

	@RequestMapping(value = "/{id}/permis/{permisId}/delete")
	public String permisDelete(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long permisId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		expedientTipusService.permisDelete(
				entornActual.getId(),
				id,
				permisId);
		model.addAttribute(
				"expedientTipus",
				expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						id));
		model.addAttribute(new PermisCommand());
		return "redirect:/v3/expedientTipus/" + id + "/permis";
	}

	@RequestMapping(value = "/{id}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse permisDatatable(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.permisFindAll(
						entornActual.getId(),
						id));
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
}
