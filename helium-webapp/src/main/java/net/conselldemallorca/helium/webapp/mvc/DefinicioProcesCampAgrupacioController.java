/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió d'agrupacions de camps d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class DefinicioProcesCampAgrupacioController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;



	@Autowired
	public DefinicioProcesCampAgrupacioController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public CampAgrupacio populateCommand(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		if (id != null)
			return dissenyService.getCampAgrupacioById(id);
		CampAgrupacio nou = new CampAgrupacio();
		if (definicioProcesId != null)
			nou.setDefinicioProces(dissenyService.getById(definicioProcesId, false));
		if (definicioProces != null)
			nou.setDefinicioProces(dissenyService.getById(definicioProces, false));
		return nou;
	}

	@RequestMapping(value = "/definicioProces/campAgrupacioLlistat", method = RequestMethod.GET)
	public String campLlistat(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				model.addAttribute("agrupacions", dissenyService.findCampAgrupacioAmbDefinicioProces(definicioProces.getId()));
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
		return "definicioProces/campAgrupacioLlistat";
	}

	@RequestMapping(value = "/definicioProces/campAgrupacioForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
		return "definicioProces/campAgrupacioForm";
	}
	@RequestMapping(value = "/definicioProces/campAgrupacioForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "definicioProces", required = true) Long definicioProcesId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") CampAgrupacio command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				if ("submit".equals(submit) || submit.length() == 0) {
					annotationValidator.validate(command, result);
			        if (result.hasErrors()) {
			        	return "definicioProces/campAgrupacioForm";
			        }
			        try {
			        	if (command.getId() == null)
			        		dissenyService.createCampAgrupacio(command);
			        	else
			        		dissenyService.updateCampAgrupacio(command);
			        	missatgeInfo(request, getMessage("info.agrup.guardat") );
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "definicioProces/campAgrupacioLlistat";
			        }
			        return "redirect:/definicioProces/campAgrupacioLlistat.html?definicioProcesId=" + definicioProcesId;
				}
				return "redirect:/definicioProces/campAgrupacioLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/campAgrupacioDelete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.deleteCampAgrupacio(id);
					missatgeInfo(request, getMessage("info.agrup.esborrat") );
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
		        }
				return "redirect:/definicioProces/campAgrupacioLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/campAgrupacioPujar")
	public String pujar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.goUpCampAgrupacio(id);
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.canviar.ordre.agrup"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre de l'agrupació", ex);
		        }
				return "redirect:/definicioProces/campAgrupacioLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campAgrupacioBaixar")
	public String baixar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.goDownCampAgrupacio(id);
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.canviar.ordre.agrup"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre de l'agrupació", ex);
		        }
				return "redirect:/definicioProces/campAgrupacioLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/campAgrupacioOrdre", method = RequestMethod.GET)
	public String variablesLlistat(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "agrupacioCodi", required = true) String agrupacioCodi,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				CampAgrupacio campAgrupacio = dissenyService.findCampAgrupacioPerId(definicioProcesId, agrupacioCodi);
				model.addAttribute("agrupacio", campAgrupacio);
				model.addAttribute("definicioProces", definicioProces);
				model.addAttribute("agrupacions", dissenyService.findCampAgrupacioAmbDefinicioProces(definicioProces.getId()));
				List<Camp> camps = new ArrayList<Camp>();
				for (Camp c : campAgrupacio.getCamps()) {
					camps.add(c);
				}
				Comparator<Camp> comparador = new Comparator<Camp>() {
					public int compare(Camp c1, Camp c2) {
						try {
							return c1.getOrdre().compareTo(c2.getOrdre());
						} catch (Exception e) {
							return 0;
						}
					}
				};
				Collections.sort(camps, comparador);
				model.addAttribute("camps", camps);
				model.addAttribute("variables", dissenyService.getVariablesSenseAgruapcio(definicioProcesId));
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
		return "definicioProces/campAgrupacioOrdre";
	}
	@RequestMapping(value = "/definicioProces/campAgrupacioOrdre", method = RequestMethod.POST)
	public String formCampPost(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "agrupacioCodi", required = true) String agrupacioCodi,
			@RequestParam(value = "id", required = true) Long id,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				if ("submit".equals(submit) || submit.length() == 0) {
			        try {
			        	dissenyService.afegirCampAgrupacio(definicioProcesId, agrupacioCodi, id);
			        	missatgeInfo(request, getMessage("info.camp.agrup.guardat"));
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
//			        	return "definicioProces/campAgrupacioLlistat";
			        }
			        return "redirect:/definicioProces/campAgrupacioOrdre.html?definicioProcesId=" + definicioProcesId + "&agrupacioCodi=" + agrupacioCodi;
				}
				return "redirect:/definicioProces/campAgrupacioLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.agrup") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/campAgrupacioOrdrePujar")
	public String pujarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "agrupacioCodi", required = true) String agrupacioCodi) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.goUpCamp(id, agrupacioCodi);
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.canviar.ordre.camp.agrup"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre del camp dins l'agrupació", ex);
		        }
				return "redirect:/definicioProces/campAgrupacioOrdre.html?definicioProcesId=" + definicioProcesId + "&agrupacioCodi=" + agrupacioCodi;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/definicioProces/campAgrupacioOrdreBaixar")
	public String baixarCamp(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "agrupacioCodi", required = true) String agrupacioCodi) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.goDownCamp(id, agrupacioCodi);
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.canviar.ordre.camp.agrup"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut canviar l'ordre del camps dins l'agrupació", ex);
		        }
				return "redirect:/definicioProces/campAgrupacioOrdre.html?definicioProcesId=" + definicioProcesId + "&agrupacioCodi=" + agrupacioCodi;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/campAgrupacioOrdreDelete")
	public String deleteCampAction(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "agrupacioCodi", required = true) String agrupacioCodi,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.deleteCampFromAgrupacio(id);
					missatgeInfo(request, getMessage("info.camp.agrup.esborrat") );
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
		        }
				return "redirect:/definicioProces/campAgrupacioOrdre.html?definicioProcesId=" + definicioProcesId + "&agrupacioCodi=" + agrupacioCodi;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc") );
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



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
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

	private static final Log logger = LogFactory.getLog(DefinicioProcesCampAgrupacioController.class);

}
