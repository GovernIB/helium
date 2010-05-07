/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Validacio;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió de les validacions dels camps d'una definició de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class DefinicioProcesValidacioCampController extends BaseController {

	private DissenyService dissenyService;



	@Autowired
	public DefinicioProcesValidacioCampController(
			DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@ModelAttribute("command")
	public ValidacioCampCommand populateCommand(
			@RequestParam(value = "campId", required = false) Long campId) {
		if (campId == null)
			return null;
		ValidacioCampCommand command = new ValidacioCampCommand();
		command.setCampId(campId);
		return command;
	}
	@ModelAttribute("validacions")
	public List<Validacio> populateCamps(
			@RequestParam(value = "campId", required = false) Long campId) {
		if (campId == null)
			return null;
		return dissenyService.findValidacionsAmbCamp(campId);
	}

	@RequestMapping(value = "/definicioProces/campValidacions", method = RequestMethod.GET)
	public String llistatCamps(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "campId", required = true) Long campId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			Camp camp = dissenyService.getCampById(campId);
			model.addAttribute("camp", camp);
			return "definicioProces/campValidacions";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campValidacions", method = RequestMethod.POST)
	public String afegirCamp(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@ModelAttribute("command") ValidacioCampCommand command,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
				if (command.getExpressio() == null || command.getExpressio().length() == 0) {
					missatgeError(request, "S'ha d'especificar una expressió de validació");
					return "redirect:/definicioProces/campValidacions.html?campId=" + command.getCampId() + "&definicioProcesId=" + definicioProcesId;
				}
				if (command.getMissatge() == null || command.getMissatge().length() == 0) {
					missatgeError(request, "S'ha d'especificar un missatge de validació");
					return "redirect:/definicioProces/campValidacions.html?campId=" + command.getCampId() + "&definicioProcesId=" + definicioProcesId;
				}
				DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
				model.addAttribute("definicioProces", definicioProces);
		        try {
		        	dissenyService.createValidacioCamp(
		        			command.getCampId(),
		        			command.getExpressio(),
		        			command.getMissatge());
		        	missatgeInfo(request, "S'ha afegit la validació al camp");
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, "No s'ha pogut afegir la validació al camp", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el registre", ex);
		        	return "definicioProces/campValidacions";
		        }
		        return "redirect:/definicioProces/campValidacions.html?campId=" + command.getCampId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				return "redirect:/definicioProces/campLlistat.html?definicioProcesId=" + definicioProcesId;
			}
			
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campValidacioEsborrar")
	public String esborrarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Validacio validacio = dissenyService.getValidacioById(id);
			try {
				dissenyService.deleteValidacio(id);
				missatgeInfo(request, "S'ha esborrat la validació del camp");
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut esborrar la validació del camp", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar la validació del camp", ex);
	        }
			return "redirect:/definicioProces/campValidacions.html?campId=" + validacio.getCamp().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campValidacioPujar")
	public String pujarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Validacio validacio = dissenyService.getValidacioById(id);
			try {
				dissenyService.goUpValidacio(id);
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut canviar l'ordre de la validació", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre de la validació", ex);
	        }
			return "redirect:/definicioProces/campValidacions.html?campId=" + validacio.getCamp().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campValidacioBaixar")
	public String baixarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Validacio validacio = dissenyService.getValidacioById(id);
			try {
				dissenyService.goDownValidacio(id);
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut canviar l'ordre de la validació", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre de la validació", ex);
	        }
			return "redirect:/definicioProces/campValidacions.html?campId=" + validacio.getCamp().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}



	private static final Log logger = LogFactory.getLog(DefinicioProcesValidacioCampController.class);

}
