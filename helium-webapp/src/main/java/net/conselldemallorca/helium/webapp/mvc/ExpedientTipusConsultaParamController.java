/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusParamConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controlador per la gestió dels paràmetres de les consultes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusConsultaParamController extends BaseController {

	private DissenyService dissenyService;
	private Validator annotationValidator;

	@Autowired
	public ExpedientTipusConsultaParamController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
	}

	@ModelAttribute("tipusParam")
	public TipusParamConsultaCamp[] populateTipusParamConsultaCamp() {
		return TipusParamConsultaCamp.values();
	}

	@ModelAttribute("command")
	public ConsultaParamCommand populateCommand(
			HttpServletRequest request) {
		return new ConsultaParamCommand();
	}
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = false) Long id) {
		if (id != null) {
			return dissenyService.getExpedientTipusById(id);
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/consultaParams", method = RequestMethod.GET)
	public String params(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute(
					"consulta",
					dissenyService.getConsultaById(id));
			model.addAttribute(
					"llistat",
					dissenyService.findConsultaCampAmbConsultaITipus(
							id,
							TipusConsultaCamp.PARAM));
			return "expedientTipus/consultaParams";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedientTipus/consultaParams", method = RequestMethod.POST)
	public String paramsPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ConsultaParamCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (("submit".equals(submit)) || (submit.length() == 0)) {
				annotationValidator.validate(command, result);
				if (result.hasErrors()) {
					model.addAttribute(
							"llistat",
							dissenyService.findConsultaCampAmbConsultaITipus(
									id,
									TipusConsultaCamp.PARAM));
		        	return "consulta/params";
		        }
				try {
					dissenyService.addConsultaParam(
							id,
							command.getCodi(),
							command.getDescripcio(),
							command.getTipus());
					missatgeInfo(request, getMessage("info.param.consulta.afegit"));
					status.setComplete();
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
					logger.error("No s'ha pogut afegir el paràmetre a la consulta", ex);
					return "redirect:/consulta/params.html?id=" + id;
				}
				return "redirect:/expedientTipus/consultaParams.html?id=" + id + "&expedientTipusId=" + expedientTipusId;
			} else {
				return "redirect:/expedientTipus/consultaLlistat.html?expedientTipusId=" + expedientTipusId;
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/consultaParamsDelete", method = RequestMethod.GET)
	public String paramDelete(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "paramId", required = false) Long paramId,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.deleteConsultaParam(id, paramId);
				missatgeInfo(request, getMessage("info.param.consulta.esborrat"));
			} catch (Exception ex) {
				missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				logger.error("No s'ha pogut esborrar el paràmetre de la consulta", ex);
			}
			return "redirect:/expedientTipus/consultaParams.html?id=" + id + "&expedientTipusId=" + expedientTipusId;
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private static final Log logger = LogFactory.getLog(ExpedientTipusConsultaParamController.class);

}
