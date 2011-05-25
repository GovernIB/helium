/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
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
 * Controlador per la gestió de les firmes de les tasques d'una
 * definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class DefinicioProcesFirmaTascaController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;



	@Autowired
	public DefinicioProcesFirmaTascaController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public FirmaTascaCommand populateCommand(
			@RequestParam(value = "tascaId", required = false) Long tascaId) {
		if (tascaId == null)
			return null;
		FirmaTascaCommand nou = new FirmaTascaCommand();
		nou.setTascaId(tascaId);
		nou.setRequired(true);
		return nou;
	}
	@ModelAttribute("documents")
	public List<Document> populateDocuments(
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		return dissenyService.findDocumentsAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
	}
	@ModelAttribute("tascaFirmes")
	public List<FirmaTasca> populateTascaFirmes(
			@RequestParam(value = "tascaId", required = false) Long tascaId) {
		if (tascaId == null)
			return null;
		return dissenyService.findFirmaTascaAmbTasca(tascaId);
	}

	@RequestMapping(value = "/definicioProces/tascaFirmes", method = RequestMethod.GET)
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
				return "definicioProces/tascaFirmes";
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaFirmes", method = RequestMethod.POST)
	public String afegirCamp(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@ModelAttribute("command") FirmaTascaCommand command,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					model.addAttribute("definicioProces", definicioProces);
			        try {
			        	dissenyService.addFirmaTasca(
			        			command.getDocumentId(),
			        			command.getTascaId(),
			        			command.isRequired());
			        	missatgeInfo(request, "S'ha afegit la firma a la tasca");
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, "No s'ha pogut afegir la firma a la tasca", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "definicioProces/tascaFirmes";
			        }
		        	return "redirect:/definicioProces/tascaFirmes.html?tascaId=" + command.getTascaId() + "&definicioProcesId=" + definicioProcesId;
				} else {
					return "redirect:/definicioProces/tascaLlistat.html?definicioProcesId=" + definicioProcesId;
				}
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaFirmaEsborrar")
	public String esborrarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				FirmaTasca firmaTasca = dissenyService.getFirmaTascaById(id);
				try {
					dissenyService.deleteFirmaTasca(id);
					missatgeInfo(request, "S'ha esborrat la firma de la tasca");
				} catch (Exception ex) {
		        	missatgeError(request, "No s'ha pogut esborrar la firma de la tasca", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el document de la tasca", ex);
		        }
				return "redirect:/definicioProces/tascaFirmes.html?tascaId=" + firmaTasca.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaFirmaPujar")
	public String pujarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				FirmaTasca firmaTasca = dissenyService.getFirmaTascaById(id);
				try {
					dissenyService.goUpFirmaTasca(id);
				} catch (Exception ex) {
		        	missatgeError(request, "No s'ha pogut canviar l'ordre de la firma", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre de la firma", ex);
		        }
				return "redirect:/definicioProces/tascaFirmes.html?tascaId=" + firmaTasca.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/tascaFirmaBaixar")
	public String baixarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				FirmaTasca firmaTasca = dissenyService.getFirmaTascaById(id);
				try {
					dissenyService.goDownFirmaTasca(id);
				} catch (Exception ex) {
		        	missatgeError(request, "No s'ha pogut canviar l'ordre de la firma", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre de la firma", ex);
		        }
				return "redirect:/definicioProces/tascaFirmes.html?tascaId=" + firmaTasca.getTasca().getId() + "&definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, "No té permisos de disseny sobre aquesta definició de procés");
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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

	private static final Log logger = LogFactory.getLog(DefinicioProcesFirmaTascaController.class);

}
