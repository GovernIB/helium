/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Estat;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.acls.Permission;
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
 * Controlador per la gestió d'expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientConsultaController extends BaseController {

	public static final String VARIABLE_SESSIO_COMMAND = "consultaExpedientsCommand";

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientConsultaController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("expedientTipus")
	public List<ExpedientTipus> populateExpedientTipus(
			HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
			permissionService.filterAllowed(
					tipus,
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.READ});
			return tipus;
		}
		return null;
	}

	@ModelAttribute("estats")
	public List<Estat> populateEstats(
			HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Estat> resposta = dissenyService.findEstatAmbEntorn(entorn.getId());
			Estat iniciat = new Estat();
			iniciat.setId(new Long(0));
			iniciat.setNom("Iniciat");
			resposta.add(0, iniciat);
			Estat finalitzat = new Estat();
			finalitzat.setId(new Long(-1));
			finalitzat.setNom("Finalitzat");
			resposta.add(finalitzat);
			return resposta;
		}
		return null;
	}

	@RequestMapping(value = "/expedient/consulta", method = RequestMethod.GET)
	public String consultaGet(
			HttpServletRequest request,
			HttpSession session,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientConsultaGeneralCommand command = (ExpedientConsultaGeneralCommand)session.getAttribute(VARIABLE_SESSIO_COMMAND);
			if (command != null) {
				List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
				permissionService.filterAllowed(
						tipus,
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.READ});
				boolean iniciat = false;
				boolean finalitzat = false;
				Long estatId = null;
				if (command.getEstat() != null) {
					iniciat = (command.getEstat() == 0);
					finalitzat = (command.getEstat() == -1);
					estatId = (!iniciat && !finalitzat) ? command.getEstat() : null;
				}
				List<ExpedientDto> expedients = expedientService.findAmbEntornConsultaGeneral(
						entorn.getId(),
						command.getTitol(),
						command.getNumero(),
						command.getDataInici1(),
						command.getDataInici2(),
						(command.getExpedientTipus() != null) ? command.getExpedientTipus().getId() : null,
						estatId,
						iniciat,
						finalitzat);
				Iterator<ExpedientDto> it = expedients.iterator();
				while (it.hasNext()) {
					ExpedientDto expedient = it.next();
					if (!tipus.contains(expedient.getTipus()))
						it.remove();
				}
				model.addAttribute("command", command);
				model.addAttribute("llistat",expedients);
			} else {
				model.addAttribute("command", new ExpedientConsultaGeneralCommand());
			}
			return "expedient/consulta";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/consulta", method = RequestMethod.POST)
	public String consultaPost(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientConsultaGeneralCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit)) {
				session.setAttribute(VARIABLE_SESSIO_COMMAND, command);
				return "redirect:/expedient/consulta.html";
			} else if ("clean".equals(submit)) {
				session.removeAttribute(VARIABLE_SESSIO_COMMAND);
				return "redirect:/expedient/consulta.html";
			}
			return "expedient/consulta";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				ExpedientTipus.class,
				new ExpedientTipusTypeEditor(dissenyService));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}



	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(ExpedientConsultaController.class);

}
