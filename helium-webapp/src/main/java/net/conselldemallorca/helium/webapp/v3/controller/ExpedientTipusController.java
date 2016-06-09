package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusControllerV3")
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusController extends BaseExpedientController {

	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private DissenyService dissenyService;

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
				expedientTipusService.findTipusAmbFiltrePaginat(
						entornActual.getId(),
						paginacioParams.getFiltre(),
						paginacioParams));
	}	

	/** Acció per consultar la informació del tipus d'expedient. */
	@RequestMapping(value = "/{expedientTipusId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findTipusAmbId(
				entornActual.getId(),
				expedientTipusId);
		model.addAttribute("expedientTipus", expedientTipus);
		if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
		else
			model.addAttribute("pipellaActiva", "dades");
		// Responsable per defecte
		model.addAttribute(
				"responsableDefecte",
				aplicacioService.findPersonaAmbCodi(
						expedientTipus.getResponsableDefecteCodi()));
		// Definició de procés inicial
		model.addAttribute(
				"definicioProcesInicial",
				dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId));
		// Permisos per a les accions
		model.addAttribute("potEscriure", 
				expedientTipusService.potEscriure(
						entornActual.getId(), 
						expedientTipusId)); 
		model.addAttribute("potEsborrar", 
				expedientTipusService.potEsborrar(
						entornActual.getId(), 
						expedientTipusId));

		return "v3/expedientTipusInfo";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			Model model) {

		model.addAttribute("expedientTipusCommand", new ExpedientTipusCommand());
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute("potAdministrar", 
				expedientTipusService.potAdministrar(
						entornActual.getId(), 
						null)); // permís 16

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
        	ExpedientTipusDto tipus = expedientTipusService.create(
        			entornActual.getId(), 
        			command.getCodi(), 
        			command.getNom(), 
        			command.isTeTitol(), 
        			command.isDemanaTitol(), 
        			command.isTeNumero(), 
        			command.isDemanaNumero(), 
        			command.getExpressioNumero(), 
        			command.isReiniciarCadaAny(),
        			sequenciesAny,
        			sequenciesValor,
        			command.getSequencia(), 
        			command.getResponsableDefecteCodi(), 
        			command.isRestringirPerGrup(), 
        			command.isSeleccionarAny(), 
        			command.isAmbRetroaccio());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + tipus.getId(),
					"expedient.tipus.missatge.creat");
        }		
	}
	
	@RequestMapping(value = "/{id}/modificar", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findTipusAmbId(
				entornActual.getId(),
				id);
		ExpedientTipusCommand command = new ExpedientTipusCommand();
		
		command.setId(expedientTipus.getId());
		command.setCodi(expedientTipus.getCodi());
		command.setNom(expedientTipus.getNom());
		command.setTeTitol(expedientTipus.isTeTitol());
		command.setTeNumero(expedientTipus.isTeNumero());
		command.setDemanaTitol(expedientTipus.isDemanaTitol());
		command.setDemanaNumero(expedientTipus.isDemanaNumero());
		command.setExpressioNumero(expedientTipus.getExpressioNumero());
		command.setSequencia(expedientTipus.getSequencia());
		command.setSequenciaDefault(expedientTipus.getSequenciaDefault());
		command.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		for(Integer key : expedientTipus.getSequenciaAny().keySet()) {
			SequenciaAnyDto any = expedientTipus.getSequenciaAny().get(key);
			command.getSequenciesAny().add(any.getAny().toString());
			command.getSequenciesValor().add(any.getSequencia().toString());
		}
		command.setAnyActual(expedientTipus.getAnyActual());
		command.setResponsableDefecteCodi(expedientTipus.getResponsableDefecteCodi());
		command.setRestringirPerGrup(expedientTipus.isRestringirPerGrup());
		command.setTramitacioMassiva(expedientTipus.isTramitacioMassiva());
		command.setSeleccionarAny(expedientTipus.isSeleccionarAny());
		command.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());		
		
		model.addAttribute("expedientTipusCommand",command);
		model.addAttribute("potAdministrar", 
				expedientTipusService.potAdministrar(
						entornActual.getId(), 
						id)); // permís 16
		
		return "v3/expedientTipusForm";
	}
	
	@RequestMapping(value = "/{id}/modificar", method = RequestMethod.POST)
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
        			id,
        			command.getNom(), 
        			command.isTeTitol(), 
        			command.isDemanaTitol(), 
        			command.isTeNumero(), 
        			command.isDemanaNumero(), 
        			command.getExpressioNumero(), 
        			command.isReiniciarCadaAny(), 
        			sequenciesAny,
        			sequenciesValor,
        			command.getSequencia(), 
        			command.getResponsableDefecteCodi(), 
        			command.isRestringirPerGrup(), 
        			command.isSeleccionarAny(), 
        			command.isAmbRetroaccio());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + id,
					"expedient.tipus.missatge.modificat");
        }		
	}
		
	
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		
		// Compta els expedients d'aquest tipus
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (! expedientService.existsExpedientAmbEntornTipusITitol(
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
							"expedient.tipus.missatge.eliminat"));
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.missatge.eliminar.expedients.relacionats"));
		}			
		return "redirect:/v3/expedientTipus";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	protected static final Log logger = LogFactory.getLog(ExpedientTipusController.class);
}
