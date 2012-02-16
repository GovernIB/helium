/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

/**
 * Controlador per la gestió de tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusConsultaController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;


	@Autowired
	public ExpedientTipusConsultaController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginService pluginService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}


	@ModelAttribute("command")
	public Consulta populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id) {
		if (id != null)
			return dissenyService.getConsultaById(id);
		return new Consulta();
	}
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = false) Long id) {
		if (id != null) {
			return dissenyService.getExpedientTipusById(id);
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/consultaLlistat")
	public String llistat(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute(
					"llistat",
					dissenyService.findConsultesAmbEntornIExpedientTipus(
							entorn.getId(),
							expedientTipusId));
			model.addAttribute("tipusFiltre", ConsultaCamp.TipusConsultaCamp.FILTRE);
			model.addAttribute("tipusInforme", ConsultaCamp.TipusConsultaCamp.INFORME);
			return "expedientTipus/consultaLlistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/consultaForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);			
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				return "expedientTipus/consultaForm";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedientTipus/consultaForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "informeContingut", required = false) final MultipartFile multipartFile,
			@RequestParam(value = "informeContingut_deleted", required = false) final String deleted,
			@ModelAttribute("command") Consulta command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					command.setEntorn(entorn);
					command.setExpedientTipus(expedientTipus);
					command.setInformeNom(null);
					command.setInformeContingut(null);
					if (multipartFile != null && multipartFile.getSize() > 0) {
						try {
							command.setInformeContingut(multipartFile.getBytes());
							command.setInformeNom(multipartFile.getOriginalFilename());
						} catch (Exception ignored) {}
					}
					annotationValidator.validate(command, result);
			        if (result.hasErrors()) {
			        	return "expedientTipus/consultaForm";
			        }
			        try {
			        	if (command.getId() == null)
			        		dissenyService.createConsulta(command);
			        	else
			        		dissenyService.updateConsulta(command, "deleted".equalsIgnoreCase(deleted));
			        	missatgeInfo(request, getMessage("info.consulta.guardat") );
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "expedientTipus/consultaForm";
			        }
				}
				return "redirect:/expedientTipus/consultaLlistat.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/consultaEsborrar")
	public String delete(
			HttpServletRequest request,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				try {
					dissenyService.deleteConsulta(consultaId);
					missatgeInfo(request, getMessage("info.consulta.esborrat"));
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.esborrar.consulta"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar la consulta", ex);
				}
				return "redirect:/expedientTipus/consultaLlistat.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/consulta/valorsPujar")
	public String pujarValor(
			HttpServletRequest request,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.goUpConsulta(expedientTipusId, consultaId);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.ordre.consulta"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre del valor de la consulta", ex);
	        }
			return "redirect:/expedientTipus/consultaLlistat.html?expedientTipusId=" + expedientTipusId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/consulta/valorsBaixar")
	public String baixarValor(
			HttpServletRequest request,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.goDownConsulta(expedientTipusId, consultaId);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.ordre.consulta"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre del valor de la consulta", ex);
	        }
			return "redirect:/expedientTipus/consultaLlistat.html?expedientTipusId=" + expedientTipusId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}


	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private boolean potDissenyarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		if (potDissenyarEntorn(entorn))
			return true;
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusConsultaController.class);

}
