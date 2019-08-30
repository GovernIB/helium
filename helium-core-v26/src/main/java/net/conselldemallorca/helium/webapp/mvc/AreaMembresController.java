/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.Carrec;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.OrganitzacioService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador pels membres d'un entorn
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class AreaMembresController extends BaseController {

	private OrganitzacioService organitzacioService;



	@Autowired
	public AreaMembresController(
			OrganitzacioService organitzacioService) {
		this.organitzacioService = organitzacioService;
	}

	@ModelAttribute("area")
	public Area populateArea(
			@RequestParam(value = "id", required = true) Long id) {
		return organitzacioService.getAreaById(id);
	}
	@ModelAttribute("membres")
	public List<PersonaDto> populateMembres(
			@RequestParam(value = "id", required = true) Long id) {
		return organitzacioService.findMembresArea(id);
	}
	@ModelAttribute("carrecsMembres")
	public Map<String, List<Carrec>> populateCarrecsMembres(
			@RequestParam(value = "id", required = true) Long id) {
		return organitzacioService.findCarrecsMapAmbArea(id);
	}
	@ModelAttribute("carrecs")
	public List<Carrec> populateCarrecs(
			@RequestParam(value = "id", required = true) Long id) {
		return organitzacioService.findCarrecsAmbArea(id);
	}
	@ModelAttribute("command")
	public AreaMembreCommand populateCommand(
			@RequestParam(value = "id", required = true) Long id) {
		AreaMembreCommand command = new AreaMembreCommand();
		command.setId(id);
		return command;
	}

	@RequestMapping(value = "/area/membres", method = RequestMethod.GET)
	public String llistatMembres(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return "area/membres";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/area/membres", method = RequestMethod.POST)
	public String afegirMembre(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") AreaMembreCommand command,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
		        try {
		        	organitzacioService.afegirMembre(
		        			command.getId(),
		        			command.getPersona(),
		        			(command.getCarrec() != null) ? command.getCarrec().getId() : null);
		        	missatgeInfo(request, getMessage("info.afegit.persona.area") );
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.afegir.persona.area"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el registre", ex);
//		        	return "area/membres";
		        }
	        	return "redirect:/area/membres.html?id=" + command.getId();
			} else {
				return "redirect:/area/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/area/membreEsborrar")
	public String esborrarMembre(
			HttpServletRequest request,
			@ModelAttribute("command") AreaMembreCommand command,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				organitzacioService.esborrarMembre(command.getId(), command.getPersona());
	        	missatgeInfo(request, getMessage("info.esborrat.persona.area") );
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.persona.area"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar la persona", ex);
	        }
	    	return "redirect:/area/membres.html?id=" + command.getId();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Carrec.class,
				new CarrecTypeEditor(organitzacioService));
	}

	public class AreaMembreCommand {
		private Long id;
		private String persona;
		private Carrec carrec;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getPersona() {
			return persona;
		}
		public void setPersona(String persona) {
			this.persona = persona;
		}
		public Carrec getCarrec() {
			return carrec;
		}
		public void setCarrec(Carrec carrec) {
			this.carrec = carrec;
		}
	}



	private static final Log logger = LogFactory.getLog(AreaMembresController.class);

}
