/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.integracio.domini.ParellaCodiValor;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.Consulta;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.presentacio.mvc.util.TascaFormUtil;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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
 * Controlador per la gestió de consultes dissenyades
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientConsultaDissenyController extends BaseController {

	private static final String VARIABLE_SESSIO_SELCON_COMMAND = "expedientTipusConsultaDissenyCommand";
	private static final String VARIABLE_SESSIO_FILTRE_COMMAND = "expedientTipusConsultaFiltreCommand";

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientConsultaDissenyController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}


	@ModelAttribute("commandSeleccioConsulta")
	public ExpedientConsultaDissenyCommand populateCommandSeleccioConsulta(
			HttpSession session,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "consultaId", required = false) Long consultaId,
			@RequestParam(value = "canviForm", required = false) Boolean canviForm) {
		ExpedientConsultaDissenyCommand command = (ExpedientConsultaDissenyCommand)session.getAttribute(VARIABLE_SESSIO_SELCON_COMMAND);
		if (command == null)
			command = new ExpedientConsultaDissenyCommand();
		if (canviForm != null && canviForm.booleanValue()) {
			command.setConsultaId(consultaId);
			if (expedientTipusId == null || !expedientTipusId.equals(command.getExpedientTipusId()))
				command.setConsultaId(null);
			command.setExpedientTipusId(expedientTipusId);
			session.removeAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);
		}
		session.setAttribute(VARIABLE_SESSIO_SELCON_COMMAND, command);
		return command;
	}
	@ModelAttribute("commandFiltre")
	public Object populateCommandFiltre(HttpSession session) {
		ExpedientConsultaDissenyCommand commandSeleccioConsulta =
			(ExpedientConsultaDissenyCommand)session.getAttribute(VARIABLE_SESSIO_SELCON_COMMAND);
		if (commandSeleccioConsulta != null && commandSeleccioConsulta.getConsultaId() != null) {
			List<Camp> camps = dissenyService.findCampsPerConsulta(commandSeleccioConsulta.getConsultaId());
			Object command = TascaFormUtil.getCommandForFiltre(
					camps,
					null,
					null,
					null);
			return command;
		}
		return null;
	}
	@ModelAttribute("valorsBoolea")
	public List<ParellaCodiValor> valorsBoolea() {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		resposta.add(new ParellaCodiValor("true", "Si"));
		resposta.add(new ParellaCodiValor("false", "No"));
		return resposta;
	}

	@RequestMapping(value = "/expedient/consultaDisseny")
	public String consultaDisseny(
			HttpServletRequest request,
			HttpSession session,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientConsultaDissenyCommand commandSeleccio =
				(ExpedientConsultaDissenyCommand)model.get("commandSeleccioConsulta");
			populateModelCommon(entorn, model, commandSeleccio);
			Object commandFiltre = session.getAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);
			if (commandFiltre != null) {
				model.addAttribute("commandFiltre", commandFiltre);
				List<Camp> camps = dissenyService.findCampsPerConsulta(commandSeleccio.getConsultaId());
				Map<String, Object> valors = TascaFormUtil.getValorsFromCommand(
						camps,
						commandFiltre,
						true,
						true);
				Map<String, Object> valorsPerService = new HashMap<String, Object>();
				for (String clau: valors.keySet()) {
					String clauPerService = clau.replaceFirst("_", ".");
					Object valor = valors.get(clau);
					/*if (valor instanceof Object[])
						System.out.println("@@@ " + clauPerService + ": [" + ((Object[])valor)[0] + ", " + ((Object[])valor)[1] + "]");
					else
						System.out.println("@@@ " + clauPerService + ": " + valor);*/
					valorsPerService.put(
							clauPerService,
							valor);
				}
				model.addAttribute(
						"expedients",
						expedientService.findAmbEntornConsultaFiltre(
								entorn.getId(),
								commandSeleccio.getConsultaId(),
								valorsPerService));
			}
			return "expedient/consultaDisseny";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/consultaDissenyResultat", method = RequestMethod.POST)
	public String consultaDissenyResultat(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("commandFiltre") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientConsultaDissenyCommand commandSeleccio =
				(ExpedientConsultaDissenyCommand)model.get("commandSeleccioConsulta");
			if ("submit".equals(submit) || submit.length() == 0) {
				if (result.hasErrors()) {
					populateModelCommon(entorn, model, commandSeleccio);
					return "expedient/consultaDisseny";
				} else {
					session.setAttribute(VARIABLE_SESSIO_FILTRE_COMMAND, command);
				}
			} else if ("netejar".equals(submit)) {
				session.removeAttribute(VARIABLE_SESSIO_FILTRE_COMMAND);
			}
			return "redirect:/expedient/consultaDisseny.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,###.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Termini.class,
				new TerminiTypeEditor());
	}

	public class ExpedientConsultaDissenyCommand {
		private Long expedientTipusId;
		private Long consultaId;
		public Long getExpedientTipusId() {
			return expedientTipusId;
		}
		public void setExpedientTipusId(Long expedientTipusId) {
			this.expedientTipusId = expedientTipusId;
		}
		public Long getConsultaId() {
			return consultaId;
		}
		public void setConsultaId(Long consultaId) {
			this.consultaId = consultaId;
		}
	}



	private void populateModelCommon(
			Entorn entorn,
			ModelMap model,
			ExpedientConsultaDissenyCommand commandSeleccio) {
		List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
		permissionService.filterAllowed(
				tipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.READ});
		model.addAttribute("expedientTipus", tipus);
		if (commandSeleccio != null) {
			List<Consulta> consultes = dissenyService.findConsultesAmbEntornIExpedientTipus(
					entorn.getId(),
					commandSeleccio.getExpedientTipusId());
			Iterator<Consulta> it = consultes.iterator();
			while (it.hasNext()) {
				Consulta consulta = it.next();
				boolean hiEs = false;
				for (ExpedientTipus expedientTipus: tipus) {
					if (consulta.getExpedientTipus().equals(expedientTipus)) {
						hiEs = true;
						break;
					}
				}
				if (!hiEs)
					it.remove();
			}
			model.addAttribute("consultes", consultes);
			if (commandSeleccio.getConsultaId() != null) {
				model.addAttribute(
						"camps",
						dissenyService.findCampsPerConsulta(commandSeleccio.getConsultaId()));
				model.addAttribute(
						"consulta",
						dissenyService.getConsultaById(commandSeleccio.getConsultaId()));
			}
		}
	}

}
