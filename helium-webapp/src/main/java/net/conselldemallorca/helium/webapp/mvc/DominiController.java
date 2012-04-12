/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió de dominis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/domini")
public class DominiController extends BaseController {

	private DissenyService dissenyService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public DominiController(
			DissenyService dissenyService) {
		this.dissenyService  = dissenyService;
		this.additionalValidator = new DominiValidator();
	}

	@ModelAttribute("command")
	public Domini populateCommand(
			@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			return dissenyService.getDominiById(id);
		}
		return new Domini();
	}

	@ModelAttribute("tipusDomini")
	public TipusDomini[] populateTipusDomini() {
		return Domini.TipusDomini.values();
	}
	@ModelAttribute("tipusAuth")
	public TipusAuthDomini[] populateTipusAuthDomini() {
		return Domini.TipusAuthDomini.values();
	}
	@ModelAttribute("origenCredencials")
	public OrigenCredencials[] populateOrigenCredencials() {
		return Domini.OrigenCredencials.values();
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("llistat", dissenyService.findDominiAmbEntorn(entorn.getId()));
			return "domini/llistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return "domini/form";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Domini command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
	        	command.setEntorn(entorn);
		        annotationValidator.validate(command, result);
				additionalValidator.validate(command, result);
				if (result.hasErrors()) {
		        	return "domini/form";
		        }
				try {
		        	if (command.getId() == null)
		        		dissenyService.createDomini(command);
		        	else
		        		dissenyService.updateDomini(command);
		        	missatgeInfo(request, getMessage("info.domini.guardat") );
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el domini", ex);
		        	return "domini/form";
		        }
			}
			return "redirect:/domini/llistat.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "delete")
	public String delete(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.deleteDomini(id);
				missatgeInfo(request, getMessage("info.domini.esborrat") );
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.domini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el domini", ex);
	        	return "domini/form";
	        }
			return "redirect:/domini/llistat.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "consulta")
	public String consulta(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				model.addAttribute("domini", dissenyService.getDominiById(id));
				model.addAttribute("resultat", dissenyService.consultaDomini(id));
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.consultar.domini"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut consultar el domini", ex);
	        }
			return "domini/consulta";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private class DominiValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Camp.class);
		}
		public void validate(Object target, Errors errors) {
			Domini domini = (Domini)target;
			if (domini.getTipus()!=null) {
				if (domini.getTipus().equals(TipusDomini.CONSULTA_WS)) {
					ValidationUtils.rejectIfEmpty(errors, "url", "not.blank");
				}
				if (domini.getTipus().equals(TipusDomini.CONSULTA_SQL)) {
					ValidationUtils.rejectIfEmpty(errors, "jndiDatasource", "not.blank");
					ValidationUtils.rejectIfEmpty(errors, "sql", "not.blank");
				}
				if (!domini.getTipusAuth().equals(TipusAuthDomini.NONE)) {
					ValidationUtils.rejectIfEmpty(errors, "usuari", "not.blank");
					ValidationUtils.rejectIfEmpty(errors, "contrasenya", "not.blank");
				}
			}
		}
	}


	private static final Log logger = LogFactory.getLog(DominiController.class);

}
