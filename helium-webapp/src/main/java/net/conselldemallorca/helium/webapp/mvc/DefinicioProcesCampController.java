/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió dels camps d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class DefinicioProcesCampController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public DefinicioProcesCampController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
		this.additionalValidator = new CampValidator();
	}

	@ModelAttribute("tipusCamp")
	public TipusCamp[] populateTipusCamp() {
		return Camp.TipusCamp.values();
	}
	@ModelAttribute("dominis")
	public List<Domini> populateDominis(HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (definicioProcesId != null){
				DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
				if (definicioProces != null && definicioProces.getExpedientTipus() != null){
					return dissenyService.findDominiAmbEntornITipusExpONull(entorn.getId(), definicioProces.getExpedientTipus().getId());
				}
			}
			return dissenyService.findDominiAmbEntornITipusExpONull(entorn.getId(), null);
		}
		return null;

	}
	@ModelAttribute("consultes")
	public List<Consulta> populateConsultes(HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (definicioProcesId != null){		
				DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
				if (definicioProces != null && definicioProces.getExpedientTipus() != null)
					return dissenyService.findConsultesAmbEntornIExpedientTipusOrdenat(entorn.getId(), definicioProces.getExpedientTipus().getId());
			}	
			return dissenyService.findConsultesAmbEntorn(entorn.getId());
		}
		return null;
	}
	@ModelAttribute("enumeracions")
    public List<Enumeracio> populateEnumeracions(HttpServletRequest request,
            @RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
            @RequestParam(value = "definicioProces", required = false) Long definicioProces) {
        Entorn entorn = getEntornActiu(request);
        if (entorn != null) {
            Long id = (definicioProcesId != null) ? definicioProcesId : definicioProces;
            if (id != null){
                DefinicioProcesDto definicioProcesDto = dissenyService.getByIdAmbComprovacio(entorn.getId(), id);
                if (definicioProcesDto != null && definicioProcesDto.getExpedientTipus() != null)
                    return dissenyService.findEnumeracionsAmbEntornAmbOSenseTipusExp(entorn.getId(), definicioProcesDto.getExpedientTipus().getId());
            }
            return dissenyService.findEnumeracionsAmbEntornAmbOSenseTipusExp(entorn.getId(), null);
        }
        return null;
    } 
	@ModelAttribute("agrupacions")
	public List<CampAgrupacio> populateAgrupacions(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Long id = (definicioProcesId != null) ? definicioProcesId : definicioProces;
			return dissenyService.findCampAgrupacioAmbDefinicioProces(id);
		}
		return null;
	}
	@ModelAttribute("accionsJbpm")
	public List<String> populateAccionsJbpm(
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		if (definicioProcesId != null)
			return dissenyService.findAccionsJbpmOrdenades(definicioProcesId);
		if (definicioProces != null)
			return dissenyService.findAccionsJbpmOrdenades(definicioProces);
		return null;
	}

	@ModelAttribute("command")
	public Camp populateCommand(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces) {
		if (id != null) {
			return dissenyService.getCampById(id);
		}
		Camp nou = new Camp();
		if (definicioProcesId != null)
			nou.setDefinicioProces(dissenyService.getById(definicioProcesId, false));
		if (definicioProces != null)
			nou.setDefinicioProces(dissenyService.getById(definicioProces, false));
		return nou;
	}

	@RequestMapping(value = "/definicioProces/campLlistat", method = RequestMethod.GET)
	public String campLlistat(
			HttpServletRequest request,
			ModelMap model,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				model.addAttribute("definicioProces", definicioProces);
				model.addAttribute("camps", dissenyService.findCampsAmbDefinicioProces(definicioProces.getId()));
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
		return "definicioProces/campLlistat";
	}

	@RequestMapping(value = "/definicioProces/campForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProces", required = false) Long definicioProces,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProcesDto = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProcesDto)) {
				model.addAttribute("definicioProces", definicioProcesDto);
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
		return "definicioProces/campForm";
	}
	@RequestMapping(value = "/definicioProces/campForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "definicioProces", required = false) Long definicioProcesId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProces,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Camp command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProcesDto = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProcesDto)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					annotationValidator.validate(command, result);
					additionalValidator.validate(command, result);
			        if (result.hasErrors()) {
						model.addAttribute("definicioProces", definicioProcesDto);
			        	return "definicioProces/campForm";
			        }
			        try {
			        	if (command.getId() == null)
			        		dissenyService.createCamp(command);
			        	else
			        		dissenyService.updateCamp(command);
			        	missatgeInfo(request, getMessage("info.camp.guardat") );
			        	status.setComplete();
			        } catch (Exception ex) {
						model.addAttribute("definicioProces", definicioProcesDto);
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "definicioProces/campForm";
			        }
			        return "redirect:/definicioProces/campLlistat.html?definicioProcesId=" + definicioProcesId;
				}
				return "redirect:/definicioProces/campLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/definicioProces/campDelete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					dissenyService.deleteCamp(id);
					missatgeInfo(request, getMessage("info.camp.esborrat") );
				} catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar el registre", ex);
		        }
				return "redirect:/definicioProces/campLlistat.html?definicioProcesId=" + definicioProcesId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	/*@RequestMapping(value = "/definicioProces/consultaCamp")
	public String consultaCamp(
			HttpServletRequest request,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			@RequestParam(value = "codi", required = true) String codi,
			@RequestParam(value = "q", required = false) String textInicial,
			@RequestParam(value = "tipus", required = true) String tipus,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DefinicioProcesDto definicioProces = dissenyService.getByIdAmbComprovacio(entorn.getId(), definicioProcesId);
			if (potDissenyarDefinicioProces(entorn, definicioProces)) {
				try {
					model.addAttribute(
							"camp",
							dissenyService.findCampAmbDefinicioProcesICodi(definicioProcesId, codi));
					model.addAttribute(
							"resultat",
							dissenyService.consultaPerCamp(
									definicioProcesId,
									codi,
									textInicial));
				} catch (Exception ex) {
		        	logger.error("No s'ha pogut executar la consulta pel camp '" + codi + "'", ex);
		        }
				if (tipus.equals("select"))
					return "definicioProces/consultaCampSelect";
				else
					return "definicioProces/consultaCampSuggest";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.defproc"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}*/

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				DefinicioProces.class,
				new DefinicioProcesTypeEditor(dissenyService));
		binder.registerCustomEditor(
				Domini.class,
				new DominiTypeEditor(dissenyService));
		binder.registerCustomEditor(
				Enumeracio.class,
				new EnumeracioTypeEditor(dissenyService));
		binder.registerCustomEditor(
				Consulta.class,
				new ConsultaTypeEditor(dissenyService));
		binder.registerCustomEditor(
				EnumeracioValors.class,
				new EnumeracioValorsTypeEditor(dissenyService));
		binder.registerCustomEditor(
				CampAgrupacio.class,
				new CampAgrupacioTypeEditor(dissenyService));
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}
	
	private class CampValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Camp.class);
		}
		public void validate(Object target, Errors errors) {
			Camp camp = (Camp)target;
			if (camp.getCodi().matches("^[A-Z]{1}[a-z]{1}.*")) {
				errors.rejectValue("codi", "error.camp.codi.maymin");
			}
			if (camp.getCodi().contains(".")) {
				errors.rejectValue("codi", "error.camp.codi.char.nok");
			}
			if (camp.getCodi().contains(" ")) {
				errors.rejectValue("codi", "error.camp.codi.char.espai");
			}
			if (camp.getTipus() != null) {
				if (camp.getTipus().equals(TipusCamp.ACCIO)) {
					ValidationUtils.rejectIfEmpty(errors, "jbpmAction", "not.blank");
				}
				if (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST)) {
					if ((camp.getDomini() == null && !camp.getDominiIntern()) && camp.getEnumeracio() == null && camp.getConsulta() == null) {
						errors.rejectValue("enumeracio", "error.camp.enumdomcons.buit");
						errors.rejectValue("domini", "error.camp.enumdomcons.buit");
						errors.rejectValue("consulta", "error.camp.enumdomcons.buit");
					} else	if (camp.getDomini() != null && camp.getDominiIntern()){
							errors.rejectValue("domini", "error.camp.domini");
							errors.rejectValue("dominiIntern", "error.camp.domini");
					} else {
						if(camp.getDomini() != null){
							if (camp.getEnumeracio() != null && camp.getConsulta() != null) {
								errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
								errors.rejectValue("domini", "error.camp.enumdomcons.tots");
								errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
							} else if (camp.getEnumeracio() != null) {
								errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
								errors.rejectValue("domini", "error.camp.enumdomcons.tots");
							} else if (camp.getConsulta() != null) {
								errors.rejectValue("domini", "error.camp.enumdomcons.tots");
								errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
							}
						} else {
							if(camp.getDominiIntern()){
								if (camp.getEnumeracio() != null && camp.getConsulta() != null) {
									errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
									errors.rejectValue("dominiIntern", "error.camp.enumdomcons.tots");
									errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
								} else if (camp.getEnumeracio() != null) {
									errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
									errors.rejectValue("dominiIntern", "error.camp.enumdomcons.tots");
								} else if (camp.getConsulta() != null) {
									errors.rejectValue("dominiIntern", "error.camp.enumdomcons.tots");
									errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
								} 
							} else 	if(camp.getEnumeracio() != null && camp.getConsulta() != null) {
								errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
								errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
							}
						}
						if (camp.getDomini() != null) {
							ValidationUtils.rejectIfEmpty(errors, "dominiId", "not.blank");
							ValidationUtils.rejectIfEmpty(errors, "dominiCampText", "not.blank");
							ValidationUtils.rejectIfEmpty(errors, "dominiCampValor", "not.blank");
						}
					}
				}
			}
		}
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

	private static final Log logger = LogFactory.getLog(DefinicioProcesCampController.class);

}
