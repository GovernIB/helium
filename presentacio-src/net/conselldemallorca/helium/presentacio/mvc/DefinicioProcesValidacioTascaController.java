/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Tasca;
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
 * Controlador per la gestió de les validacions de les tasques d'una definició de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class DefinicioProcesValidacioTascaController extends BaseController {

	private DissenyService dissenyService;



	@Autowired
	public DefinicioProcesValidacioTascaController(
			DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@ModelAttribute("command")
	public ValidacioTascaCommand populateCommand(
			@RequestParam(value = "tascaId", required = false) Long tascaId) {
		if (tascaId == null)
			return null;
		ValidacioTascaCommand command = new ValidacioTascaCommand();
		command.setTascaId(tascaId);
		return command;
	}
	@ModelAttribute("validacions")
	public List<Validacio> populateCamps(
			@RequestParam(value = "tascaId", required = false) Long tascaId) {
		if (tascaId == null)
			return null;
		return dissenyService.findValidacionsAmbTasca(tascaId);
	}

	@RequestMapping(value = "/definicioProces/tascaValidacions", method = RequestMethod.GET)
	public String llistatCamps(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "tascaId", required = true) Long tascaId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			Tasca tasca = dissenyService.getTascaById(tascaId);
			model.addAttribute("tasca", tasca);
			return "definicioProces/tascaValidacions";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaValidacions", method = RequestMethod.POST)
	public String afegirCamp(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@ModelAttribute("command") ValidacioTascaCommand command,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
				if (command.getExpressio() == null || command.getExpressio().length() == 0) {
					missatgeError(request, "S'ha d'especificar una expressió de validació");
					return "redirect:/definicioProces/tascaValidacions.html?tascaId=" + command.getTascaId() + "&definicioProcesId=" + definicioProcesId;
				}
				if (command.getMissatge() == null || command.getMissatge().length() == 0) {
					missatgeError(request, "S'ha d'especificar un missatge de validació");
					return "redirect:/definicioProces/tascaValidacions.html?tascaId=" + command.getTascaId() + "&definicioProcesId=" + definicioProcesId;
				}
				DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
				model.addAttribute("definicioProces", definicioProces);
		        try {
		        	dissenyService.createValidacioTasca(
		        			command.getTascaId(),
		        			command.getExpressio(),
		        			command.getMissatge());
		        	missatgeInfo(request, "S'ha afegit la validació a la tasca");
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, "No s'ha pogut afegir la validació a la tasca", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el registre", ex);
		        	return "definicioProces/tascaValidacions";
		        }
		        return "redirect:/definicioProces/tascaValidacions.html?tascaId=" + command.getTascaId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				return "redirect:/definicioProces/tascaLlistat.html?definicioProcesId=" + definicioProcesId;
			}
			
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaValidacioEsborrar")
	public String esborrarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Validacio validacio = dissenyService.getValidacioById(id);
			try {
				dissenyService.deleteValidacio(id);
				missatgeInfo(request, "S'ha esborrat la validació de la tasca");
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut esborrar la validació de la tasca", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar la validació de la tasca", ex);
	        }
			return "redirect:/definicioProces/tascaValidacions.html?tascaId=" + validacio.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaValidacioPujar")
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
			return "redirect:/definicioProces/tascaValidacions.html?tascaId=" + validacio.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaValidacioBaixar")
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
			return "redirect:/definicioProces/tascaValidacions.html?tascaId=" + validacio.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}



	private static final Log logger = LogFactory.getLog(DefinicioProcesValidacioTascaController.class);

}
