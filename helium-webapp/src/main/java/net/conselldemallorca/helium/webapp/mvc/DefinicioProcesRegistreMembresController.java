/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

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
 * Controlador per la gestió dels membres dels camps de tipus registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class DefinicioProcesRegistreMembresController extends BaseController {

	private DissenyService dissenyService;



	@Autowired
	public DefinicioProcesRegistreMembresController(
			DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@ModelAttribute("command")
	public RegistreMembreCommand populateCommand(
			@RequestParam(value = "registreId", required = false) Long registreId) {
		if (registreId == null)
			return null;
		RegistreMembreCommand command = new RegistreMembreCommand();
		command.setRegistreId(registreId);
		command.setObligatori(true);
		command.setLlistar(true);
		return command;
	}
	@ModelAttribute("camps")
	public List<Camp> populateCamps(
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "registreId", required = false) Long registreId) {
		if (registreId == null)
			return null;
		List<Camp> camps = dissenyService.findCampsAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
		Iterator<Camp> it = camps.iterator();
		while (it.hasNext()) {
			Camp camp = it.next();
			if (camp.getId().longValue() == registreId.longValue()) {
				it.remove();
			} else if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				it.remove();
			}
		}
		return camps;
	}
	@ModelAttribute("membres")
	public List<CampRegistre> populateMembres(
			@RequestParam(value = "registreId", required = false) Long registreId) {
		if (registreId == null)
			return null;
		return dissenyService.findCampMembreAmbRegistre(registreId);
	}

	@RequestMapping(value = "/definicioProces/campRegistreMembres", method = RequestMethod.GET)
	public String llistatMembres(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "registreId", required = true) Long registreId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			Camp registre = dissenyService.getCampById(registreId);
			model.addAttribute("registre", registre);
			return "definicioProces/campRegistreMembres";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campRegistreMembres", method = RequestMethod.POST)
	public String afegirMembre(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@ModelAttribute("command") RegistreMembreCommand command,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
				DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
				model.addAttribute("definicioProces", definicioProces);
		        try {
		        	dissenyService.addCampRegistre(
		        			command.getRegistreId(),
		        			command.getMembreId(),
		        			command.isObligatori(),
		        			command.isLlistar());
		        	missatgeInfo(request, "S'ha afegit el membre al registre");
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, "No s'ha pogut afegir el membre al registre", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut afegir el membre al registre", ex);
		        	return "definicioProces/campRegistreMembres";
		        }
		        return "redirect:/definicioProces/campRegistreMembres.html?registreId=" + command.getRegistreId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				return "redirect:/definicioProces/campLlistat.html?definicioProcesId=" + definicioProcesId;
			}
			
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campRegistreMembreEsborrar")
	public String esborrarMembre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			CampRegistre campRegistre = dissenyService.getCampRegistreById(id);
			try {
				dissenyService.deleteCampRegistre(id);
				missatgeInfo(request, "S'ha esborrat el membre del registre");
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut esborrar el membre del registre", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el membre del registre", ex);
	        }
			return "redirect:/definicioProces/campRegistreMembres.html?registreId=" + campRegistre.getRegistre().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campRegistreMembrePujar")
	public String pujarMembre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			CampRegistre campRegistre = dissenyService.getCampRegistreById(id);
			try {
				dissenyService.goUpCampRegistre(id);
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut canviar l'ordre dels membres del registre", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre dels membres del registre", ex);
	        }
			return "redirect:/definicioProces/campRegistreMembres.html?registreId=" + campRegistre.getRegistre().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campRegistreMembreBaixar")
	public String baixarMembre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			CampRegistre campRegistre = dissenyService.getCampRegistreById(id);
			try {
				dissenyService.goDownCampRegistre(id);
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut canviar l'ordre dels membres del registre", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre dels membres del registre", ex);
	        }
			return "redirect:/definicioProces/campRegistreMembres.html?registreId=" + campRegistre.getRegistre().getId() + "&definicioProcesId=" + definicioProcesId;
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}



	private static final Log logger = LogFactory.getLog(DefinicioProcesRegistreMembresController.class);

}
