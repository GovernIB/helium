/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TerminiService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.ExpedientTerminiModificarCommand.TerminiModificacioTipus;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.acls.model.Permission;
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
 * Controlador per la gestió dels terminis als expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTerminiController extends BaseController {

	private TerminiService terminiService;
	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientTerminiController(
			TerminiService terminiService,
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.terminiService = terminiService;
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	

	@RequestMapping(value = "/expedient/terminis")
	public String terminis(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false, false, false);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
				model.addAttribute(
						"terminis",
						dissenyService.findTerminisAmbDefinicioProces(instanciaProces.getDefinicioProces().getId()));
				model.addAttribute(
						"iniciats",
						terminiService.findIniciatsAmbProcessInstanceId(id));
				return "expedient/terminis";
			} else {
				missatgeError(request, getMessage("error.permisos.consultar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/terminiIniciar")
	public String terminiIniciar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				terminiService.iniciar(
						terminiId,
						id,
						new Date(),
						false);
				missatgeInfo(request, getMessage("info.termini.iniciat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.iniciar.termini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut iniciar el termini", ex);
			}
			return "redirect:/expedient/terminis.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/terminiPausar")
	public String terminiPausar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				terminiService.pausar(terminiId, new Date());
				missatgeInfo(request, getMessage("info.termini.aturat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.aturar.termini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut aturar el termini", ex);
			}
			return "redirect:/expedient/terminis.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/terminiContinuar")
	public String terminiContinuar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				terminiService.continuar(terminiId, new Date());
				missatgeInfo(request, getMessage("info.termini.continuat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.continuar.termini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut continuar el termini", ex);
			}
			return "redirect:/expedient/terminis.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/terminiCancelar")
	public String terminiCancelar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				terminiService.cancelar(terminiId, new Date());
				missatgeInfo(request, getMessage("info.termini.cancelat") );
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.cancelar.termini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut cancel·lar el termini", ex);
			}
			return "redirect:/expedient/terminis.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/terminiModificar", method = RequestMethod.GET)
	public String terminiModificar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "terminiId", required = true) Long terminiId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TerminiIniciat terminiIniciat = getTerminiIniciat(
					id,
					terminiId);
			if (terminiIniciat != null) {
				ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
				if (potModificarExpedient(expedient)) {
					model.addAttribute(
							"expedient",
							expedient);
					model.addAttribute(
							"arbreProcessos",
							expedientService.getArbreInstanciesProces(id));
					InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false, true, true);
					model.addAttribute(
							"instanciaProces",
							instanciaProces);
					ExpedientTerminiModificarCommand command = new ExpedientTerminiModificarCommand();
					command.setTerminiId(terminiId);
					command.setAnys(terminiIniciat.getAnys());
					command.setMesos(terminiIniciat.getMesos());
					command.setDies(terminiIniciat.getDies());
					command.setDataInici(terminiIniciat.getDataInici());
					command.setDataFi(terminiIniciat.getDataFi());
					model.addAttribute("termini", terminiIniciat.getTermini());
					model.addAttribute("command", command);
					model.addAttribute("tipus", TerminiModificacioTipus.values());
					return "expedient/terminiModificar";
				} else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				}
			}
			return "redirect:/expedient/terminis.html?id=" + id;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/terminiModificar", method = RequestMethod.POST)
	public String terminiModificar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientTerminiModificarCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					try {
						TerminiIniciat terminiIniciat = getTerminiIniciat(
								id,
								command.getTerminiId());
						if (TerminiModificacioTipus.DURADA.equals(command.getTipus())) {
							Date dataInici = terminiIniciat.getDataInici();
							terminiService.cancelar(command.getTerminiId(), new Date());
							terminiService.iniciar(
									terminiIniciat.getTermini().getId(),
									id,
									dataInici,
									command.getAnys(),
									command.getMesos(),
									command.getDies(),
									false);
							missatgeInfo(request, getMessage("info.termini.modificat"));
						} else if (TerminiModificacioTipus.DATA_INICI.equals(command.getTipus())) {
							int anys = terminiIniciat.getAnys();
							int mesos = terminiIniciat.getMesos();
							int dies = terminiIniciat.getDies();
							terminiService.cancelar(command.getTerminiId(), new Date());
							terminiService.iniciar(
									terminiIniciat.getTermini().getId(),
									id,
									command.getDataInici(),
									anys,
									mesos,
									dies,
									false);
							missatgeInfo(request, getMessage("info.termini.modificat"));
						} else if (TerminiModificacioTipus.DATA_FI.equals(command.getTipus())) {
							int anys = terminiIniciat.getAnys();
							int mesos = terminiIniciat.getMesos();
							int dies = terminiIniciat.getDies();
							terminiService.cancelar(command.getTerminiId(), new Date());
							terminiService.iniciar(
									terminiIniciat.getTermini().getId(),
									id,
									command.getDataFi(),
									anys,
									mesos,
									dies,
									true);
							missatgeInfo(request, getMessage("info.termini.modificat"));
						}
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.modificar.termini"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut modificar el termini", ex);
					}
				}
				return "redirect:/expedient/terminis.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/terminis.html?id=" + id;
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}



	private TerminiIniciat getTerminiIniciat(String id, Long terminiId) {
		for (TerminiIniciat iniciat: terminiService.findIniciatsAmbProcessInstanceId(id)) {
			if (iniciat.getId().longValue() == terminiId.longValue())
				return iniciat;
		}
		return null;
	}
	private boolean potConsultarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.SUPERVISION,
					ExtendedPermission.READ}) != null;
	}
	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTerminiController.class);

}
