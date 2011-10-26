/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * Controlador per la gestió de les subconsultes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ConsultaSubconsultaController extends BaseController {

	private DissenyService dissenyService;

	@Autowired
	public ConsultaSubconsultaController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
	}

	@ModelAttribute("command")
	public ConsultaSubconsultaCommand populateCommand(
			HttpServletRequest request) {
		return new ConsultaSubconsultaCommand();
	}

	@RequestMapping(value = "/consulta/subconsultes", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Consulta consulta = dissenyService.getConsultaById(id);
			List<Consulta> consultes = dissenyService.findConsultesAmbEntornIExpedientTipus(
					entorn.getId(),
					consulta.getExpedientTipus().getId());
			consultes.remove(consulta);
			for (Consulta subconsulta: consulta.getSubConsultes())
				consultes.remove(subconsulta);
			model.addAttribute("consulta", consulta);
			model.addAttribute("candidates", consultes);
			return "consulta/subconsultes";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/subconsultes", method = RequestMethod.POST)
	public String getForm(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ConsultaSubconsultaCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (("submit".equals(submit)) || (submit.length() == 0)) {
				try {
					dissenyService.consultaAfegirSubconsulta(
							command.getId(),
							command.getSubconsultaId());
					missatgeInfo(request, getMessage("consulta.subconsulta.afegit") );
					status.setComplete();
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut afegir la subconsulta", ex);
				}
				return "redirect:/consulta/subconsultes.html?id=" + command.getId();
			} else {
				return "redirect:/consulta/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/subconsultaEsborrar")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "subconsultaId", required = true) Long subconsultaId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.consultaEsborrarSubconsulta(
						id,
						subconsultaId);
				missatgeInfo(request, getMessage("consulta.subconsulta.esborrat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut esborrar el registre", ex);
			}
			return "redirect:/consulta/subconsultes.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	private static final Log logger = LogFactory.getLog(ConsultaSubconsultaController.class);

}
