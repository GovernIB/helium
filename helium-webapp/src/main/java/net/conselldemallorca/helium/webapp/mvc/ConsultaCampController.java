/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per la gestió dels camps de les consultes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ConsultaCampController extends BaseController {

	private DissenyService dissenyService;

	@Autowired
	public ConsultaCampController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
	}

	@ModelAttribute("command")
	public ConsultaCamp populateCommand(
			HttpServletRequest request) {
		return new ConsultaCamp();
	}

	@RequestMapping(value = "/consulta/camps", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Consulta consulta = dissenyService.getConsultaById(id);
			model.addAttribute("llistat", dissenyService.findCampsConsulta(id, tipus));
			model.addAttribute("camps", dissenyService.findCampsPerCampsConsulta(
					id,
					tipus,
					false));
			model.addAttribute("consulta", consulta);
			model.addAttribute(
					"definicionsProces",
					dissenyService.findDarreresAmbExpedientTipusIGlobalsEntorn(
							entorn.getId(),
							consulta.getExpedientTipus().getId()));
			return "consulta/camps";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/camps", method = RequestMethod.POST)
	public String getForm(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ConsultaCamp command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (("submit".equals(submit)) || (submit.length() == 0)) {
				command.setId(null);
				command.setTipus(tipus);
				command.setConsulta(dissenyService.getConsultaById(id));
				try {
					dissenyService.createConsultaCamp(command);
					missatgeInfo(request, getMessage("info.camp.consulta.afegit") );
					status.setComplete();
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut afegir el camp a la consulta", ex);
					return "redirect:/consulta/camps.html?id=" + id + "&tipus=" + tipus;
				}
				return "redirect:/consulta/camps.html?id=" + id + "&tipus=" + tipus;
			} else {
				return "redirect:/consulta/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/campDelete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.deleteConsultaCamp(id);
				missatgeInfo(request, getMessage("info.camp.consulta.esborrat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.esborrar.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut esborrar el registre", ex);
			}
			return "redirect:/consulta/camps.html?id=" + consultaId + "&tipus=" + tipus;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/campFiltrePujar")
	public String pujarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "consultaId", required = true) Long consultaId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.goUpConsultaCamp(id);
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.canviar.ordre.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut canviar l'ordre del camp de la consulta", ex);
			}
			return "redirect:/consulta/camps.html?id=" + consultaId + "&tipus=" + tipus;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/campFiltreBaixar")
	public String baixarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "consultaId", required = true) Long consultaId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.goDownConsultaCamp(id);
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.canviar.ordre.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut canviar l'ordre del camp de la consulta", ex);
			}
			return "redirect:/consulta/camps.html?id=" + consultaId + "&tipus=" + tipus;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				ExpedientTipus.class,
				new ExpedientTipusTypeEditor(dissenyService));
	}



	private static final Log logger = LogFactory.getLog(ConsultaCampController.class);

}
