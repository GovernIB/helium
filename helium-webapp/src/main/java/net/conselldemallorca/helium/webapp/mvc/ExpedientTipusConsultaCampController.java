/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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
public class ExpedientTipusConsultaCampController extends BaseController {

	private DissenyService dissenyService;

	@Autowired
	public ExpedientTipusConsultaCampController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
	}

	@ModelAttribute("command")
	public ConsultaCamp populateCommand(
			HttpServletRequest request) {
		return new ConsultaCamp();
	}
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = false) Long id) {
		if (id != null) {
			return dissenyService.getExpedientTipusById(id);
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/consultaCamps", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
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
					dissenyService.findDarreresAmbExpedientTipusEntorn(
							entorn.getId(),
							consulta.getExpedientTipus().getId(),
							true));
			return "expedientTipus/consultaCamps";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/consultaCamps", method = RequestMethod.POST)
	public String getForm(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ConsultaCamp command,
			BindingResult result,
			SessionStatus status,
			HttpSession session,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (("submit".equals(submit)) || (submit.length() == 0)) {
				command.setId(null);
				command.setTipus(tipus);
				command.setConsulta(dissenyService.getConsultaById(id));
				try {
					dissenyService.createConsultaCamp(command);
					session.removeAttribute(SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + id);
					missatgeInfo(request, getMessage("info.camp.consulta.afegit") );
					status.setComplete();
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut afegir el camp a la consulta", ex);
				}
				return "redirect:/expedientTipus/consultaCamps.html?id=" + id + "&tipus=" + tipus + "&expedientTipusId=" + expedientTipusId;
			} else {
				return "redirect:/expedientTipus/consultaLlistat.html?expedientTipusId=" + expedientTipusId;
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/consultaCampsDelete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			HttpSession session) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.deleteConsultaCamp(id);
				session.removeAttribute(SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
				missatgeInfo(request, getMessage("info.camp.consulta.esborrat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.esborrar.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut esborrar el registre", ex);
			}
			return "redirect:/expedientTipus/consultaCamps.html?id=" + consultaId + "&tipus=" + tipus + "&expedientTipusId=" + expedientTipusId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/consultaCampFiltrePujar")
	public String pujarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.goUpConsultaCamp(id);
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.canviar.ordre.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut canviar l'ordre del camp de la consulta", ex);
			}
			return "redirect:/expedientTipus/consultaCamps.html?id=" + consultaId + "&tipus=" + tipus + "&expedientTipusId=" + expedientTipusId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/consultaCampFiltreBaixar")
	public String baixarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "tipus", required = true) TipusConsultaCamp tipus,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.goDownConsultaCamp(id);
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.canviar.ordre.camp.consulta"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut canviar l'ordre del camp de la consulta", ex);
			}
			return "redirect:/expedientTipus/consultaCamps.html?id=" + consultaId + "&tipus=" + tipus + "&expedientTipusId=" + expedientTipusId;
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



	private static final Log logger = LogFactory.getLog(ExpedientTipusConsultaCampController.class);

}
