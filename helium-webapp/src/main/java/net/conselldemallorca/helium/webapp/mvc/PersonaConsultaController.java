/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.core.model.service.PersonaService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió d'entorns
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class PersonaConsultaController extends BaseController {

	public static final String VARIABLE_SESSIO_COMMAND = "consultaPersonesCommand";

	private PersonaService personaService;



	@Autowired
	public PersonaConsultaController(
			PersonaService personaService) {
		this.personaService = personaService;
	}

	@RequestMapping(value = "/persona/consulta.html", method = RequestMethod.GET)
	public String consultaGet(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			ModelMap model) {
		PersonaConsultaCommand command = (PersonaConsultaCommand)session.getAttribute(VARIABLE_SESSIO_COMMAND);
		if (command != null) {
			int pagina = (page != null) ? new Integer(page).intValue() : 1;
			int firstRow = (pagina - 1) * getObjectsPerPage(objectsPerPage);
			boolean isAsc = "asc".equals(dir);
			model.addAttribute(
					"llistat",
					newPaginatedList(
							pagina,
							sort,
							isAsc,
							getObjectsPerPage(objectsPerPage),
							personaService.countPersonaUsuariFiltre(
									command.getCodi(),
									command.getNom(),
									command.getEmail()),
							personaService.findPersonaUsuariPagedAndOrderedFiltre(
									sort,
									isAsc,
									firstRow,
									getObjectsPerPage(objectsPerPage),
									command.getCodi(),
									command.getNom(),
									command.getEmail())));
			model.addAttribute("command", command);
		} else {
			model.addAttribute("command", new PersonaConsultaCommand());
		}
		return "persona/consulta";
	}

	@RequestMapping(value = "/persona/consulta.html", method = RequestMethod.POST)
	public String consultaPost(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") PersonaConsultaCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		if ("submit".equals(submit)) {
			session.setAttribute(VARIABLE_SESSIO_COMMAND, command);
			return "redirect:/persona/consulta.html";
		} else if ("clean".equals(submit)) {
			session.removeAttribute(VARIABLE_SESSIO_COMMAND);
			return "redirect:/persona/consulta.html";
		}
		return "expedient/consulta";
	}

}
