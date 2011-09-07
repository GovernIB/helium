/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
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
 * Controlador per la gestió dels camps dels formularis d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class DefinicioProcesCampTascaController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;



	@Autowired
	public DefinicioProcesCampTascaController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public CampTascaCommand populateCommand(
			@RequestParam(value = "tascaId", required = false) Long tascaId) {
		if (tascaId == null)
			return null;
		CampTascaCommand nou = new CampTascaCommand();
		nou.setTascaId(tascaId);
		nou.setReadFrom(true);
		nou.setWriteTo(true);
		return nou;
	}
	@ModelAttribute("camps")
	public List<Camp> populateCamps(
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		return dissenyService.findCampsAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
	}
	@ModelAttribute("campsTasca")
	public List<CampTasca> populateCampsForm(
			@RequestParam(value = "tascaId", required = false) Long tascaId) {
		if (tascaId == null)
			return null;
		return dissenyService.findCampTascaAmbTasca(tascaId);
	}

	@RequestMapping(value = "/definicioProces/tascaCamps", method = RequestMethod.GET)
	public String llistatCamps(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "tascaId", required = true) Long tascaId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				Tasca tasca = dissenyService.getTascaById(tascaId);
				model.addAttribute("tasca", tasca);
				return "definicioProces/tascaCamps";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaCamps", method = RequestMethod.POST)
	public String afegirCamp(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@ModelAttribute("command") CampTascaCommand command,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					model.addAttribute("definicioProces", definicioProces);
			        try {
			        	dissenyService.addCampTasca(
			        			command.getTascaId(),
			        			command.getCampId(),
			        			command.isReadFrom(),
			        			command.isWriteTo(),
			        			command.isRequired(),
			        			command.isReadOnly());
			        	missatgeInfo(request, getMessage("info.camp.tasca.afegit") );
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.afegir.camp.tasca"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "definicioProces/tascaCamps";
			        }
		        	return "redirect:/definicioProces/tascaCamps.html?tascaId=" + command.getTascaId() + "&definicioProcesId=" + definicioProcesId;
				} else {
					return "redirect:/definicioProces/tascaLlistat.html?definicioProcesId=" + definicioProcesId;
				}
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaCampEsborrar")
	public String esborrarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				CampTasca campTasca = dissenyService.getCampTascaById(id);
				try {
					dissenyService.deleteCampTasca(id);
					missatgeInfo(request, getMessage("info.camp.tasca.esborrat") );
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.esborrar.camp.tasca"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el camp de la tasca", ex);
		        }
				return "redirect:/definicioProces/tascaCamps.html?tascaId=" + campTasca.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaCampPujar")
	public String pujarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				CampTasca campTasca = dissenyService.getCampTascaById(id);
				try {
					dissenyService.goUpCampTasca(id);
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.ordre.camp.tasca"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre del camp", ex);
		        }
				return "redirect:/definicioProces/tascaCamps.html?tascaId=" + campTasca.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaCampBaixar")
	public String baixarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				CampTasca campTasca = dissenyService.getCampTascaById(id);
				try {
					dissenyService.goDownCampTasca(id);
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.ordre.camp.tasca"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre del camp", ex);
		        }
				return "redirect:/definicioProces/tascaCamps.html?tascaId=" + campTasca.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				DefinicioProces.class,
				new DefinicioProcesTypeEditor(dissenyService));
	}



	private boolean potDissenyarDefinicioProces(Entorn entorn, DefinicioProcesDto definicioProces) {
		if (potDissenyarEntorn(entorn))
			return true;
		if (definicioProces.getExpedientTipus() != null) {
			return permissionService.filterAllowed(
					definicioProces.getExpedientTipus(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.DESIGN}) != null;
		}
		return false;
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

	private static final Log logger = LogFactory.getLog(DefinicioProcesCampTascaController.class);

}
