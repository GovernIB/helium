package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.webapp.v3.command.ReassignacioTasquesCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

/**
 * Controlador per reassignacio massiva de tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/tasca")
public class MassivaTascaReassignacioController extends BaseExpedientController {
	
	@Autowired
	private AplicacioService aplicacioService;
	
	@Resource(name="execucioMassivaServiceV3")
	private ExecucioMassivaService execucioMassivaService;
	
	@RequestMapping(value = "/massivaReassignacioTasca", method = RequestMethod.GET)
	public String massivaTramitacio(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) boolean correu,
			@RequestParam(value = "massiva", required = true) boolean massiva,
			Model model) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.tasc.selec"));
			return modalUrlTancar(false);
		}
		if (massiva) {
			model.addAttribute("inici", inici);
			model.addAttribute("enviarCorreu", correu);
		}
		model.addAttribute("massiva", massiva);		
		model.addAttribute("reassignacioTasquesCommand", new ReassignacioTasquesCommand());
		
		return "v3/tasquesReassignacio";
	}

	@RequestMapping(value = "/persona/suggest/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String personaSuggest(
			@PathVariable String text,
			Model model) {
		String textDecoded = text;
//		try {
//			textDecoded = URLDecoder.decode(text, "UTF-8");
//		} catch (UnsupportedEncodingException ex) {
//			logger.error("MassivaTascaReassignacioController.personaSuggest --> " + ex.getMessage());
//		}
		List<PersonaDto> lista = aplicacioService.findPersonaLikeNomSencer(textDecoded);
		String json = "[";
		for (PersonaDto persona: lista) {
			json += "{\"codi\":\"" + persona.getCodi() + "\", \"nom\":\"" + persona.getNomSencer() + "\"},";
		}
		if (json.length() > 1) json = json.substring(0, json.length() - 1);
		json += "]";
		return json;
	}

	@RequestMapping(value = "/persona/suggestInici/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String personaSuggestInici(
			@PathVariable String text,
			Model model) {
		PersonaDto persona = aplicacioService.findPersonaAmbCodi(text);
		if (persona != null) {
			return "{\"codi\":\"" + persona.getCodi() + "\", \"nom\":\"" + persona.getNomSencer() + "\"}";
		}
		return null;
	}

	@RequestMapping(value = "massivaReassignacioTasca", method = RequestMethod.POST)
	public String accioReassignar(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "enviarCorreu", required = false) String enviarCorreu,
			@RequestParam(value = "massiva", required = true) boolean massiva,
			@ModelAttribute("reassignacioTasquesCommand") ReassignacioTasquesCommand reassignacioTasquesCommand,
			BindingResult result, 
			SessionStatus status, 
			Model model) {	
		inici = inici.replaceAll("undefined,", "");
		
		model.addAttribute("inici", inici);
		model.addAttribute("enviarCorreu", enviarCorreu);
		model.addAttribute("massiva", massiva);		
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> ids = sessionManager.getSeleccioConsultaTasca();
		if (ids == null || ids.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.tasc.selec"));
			return modalUrlTancar();
		}
		String tipus = request.getParameter("tipusExpressio"); 
		ReassignarValidator validator = new ReassignarValidator();
		validator.setTipus(tipus);
		validator.validate(reassignacioTasquesCommand, result);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.executar.reassignacio"));
			return "v3/tasquesReassignacio";
        }

		Date dInici = new Date();
		if (inici != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				Date d = sdf.parse(inici);
				dInici = d;
			} catch (ParseException e) {}
		}

		String expression = reassignacioTasquesCommand.getExpression();
		if ("user".equals(tipus)) {
			expression = "user(" + reassignacioTasquesCommand.getUsuari() + ")";
		} else if ("grup".equals(tipus)) {
//			AreaDto grup = dissenyService.findAreaById(reassignacioTasquesCommand.getGrup());
//			expression = "group(" + grup.getCodi() + ")";
			expression = "group(" + reassignacioTasquesCommand.getGrup() + ")";
		}
		try {
			EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
//			Authentication massiuAuthentication = SecurityContextHolder.getContext().getAuthentication();
			
			ExecucioMassivaDto dto = new ExecucioMassivaDto();
			dto.setDataInici(dInici);
			dto.setEnviarCorreu(enviarCorreu != null);
			Set<String> idsAsString = new HashSet<String>();
			for (Long id: ids) {
				idsAsString.add(id.toString());
			}
			dto.setTascaIds(idsAsString.toArray(new String[idsAsString.size()]));
			dto.setTipus(ExecucioMassivaTipusDto.REASSIGNAR);
			dto.setParam1(expression);
			
			Object[] params = new Object[1];
			params[0] = entorn.getId();
//			params[1] = massiuAuthentication.getCredentials();
//			List<String> rols = new ArrayList<String>();
//			for (GrantedAuthority gauth : massiuAuthentication.getAuthorities()) {
//				rols.add(gauth.getAuthority());
//			}
//			params[2] = rols;
			
			dto.setParam2(execucioMassivaService.serialize(params));
			execucioMassivaService.crearExecucioMassiva(dto);
			MissatgesHelper.success(request, getMessage(request, "info.accio.massiu.reassignat", new Object[] {ids.size()}));
			ids.clear();
		} catch (Exception e) {
			MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
			logger.error("Error al programar les accions massives", e);
			return "v3/tasquesReassignacio";
		}
		return modalUrlTancar(false);
	}
	
	private class ReassignarValidator implements Validator {		
		private String tipus;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ReassignacioTasquesCommand.class);
		}
		public void validate(Object obj, Errors errors) {
			if ("user".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "usuari", "not.blank");
			} else if ("grup".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "grup", "not.blank");
			} else if ("expr".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "expression", "not.blank");
			}
		}
		public void setTipus(String tipus) {
			this.tipus = tipus;
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	private static final Log logger = LogFactory.getLog(MassivaTascaReassignacioController.class);
}
