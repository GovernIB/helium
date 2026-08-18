package es.caib.helium.back.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import es.caib.helium.commons.dto.*;
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

import es.caib.helium.back.command.ReassignacioTasquesCommand;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.SessionHelper.SessionManager;
import es.caib.helium.commons.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.ExecucioMassivaService;

/**
 * Controlador per reassignacio massiva de tasques
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/tasca")
public class MassivaTascaReassignacioController extends BaseExpedientController {

	@Autowired
	private AplicacioService aplicacioService;
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

		return "tasquesReassignacio";
	}

	@RequestMapping(value = "/persona/suggest/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public List<CodiNom> personaSuggest(
			@PathVariable String text,
			Model model) {
		String textDecoded = null;
		try {
			textDecoded = URLDecoder.decode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + textDecoded + ": " + e.getMessage());
		}
		List<PersonaDto> lista = aplicacioService.findPersonaLikeNomSencer(textDecoded);
		List<CodiNom> resposta = new ArrayList<CodiNom>();
		for (PersonaDto persona: lista) {
			resposta.add(CodiNom
							.builder()
							.codi(persona.getCodi())
							.nom(persona.getNomSencerCodi())
							.build());
		}
		return resposta;
	}

	@RequestMapping(value = "/persona/suggestInici/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public CodiNom personaSuggestInici(
			@PathVariable String text,
			Model model) {
		String textDecoded = null;
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		try {
			textDecoded = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + textDecoded + ": " + e.getMessage());
		}
		PersonaDto persona = aplicacioService.findPersonaAmbCodi(textDecoded);
		if (persona != null) {
			return CodiNom
					.builder()
					.codi(persona.getCodi())
					.nom(persona.getNomSencerCodi())
					.build();
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
			return "tasquesReassignacio";
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
			MissatgesHelper.error(
					request,
					getMessage(request, "error.no.massiu"),
					e);
			logger.error("Error al programar les accions massives", e);
			return "tasquesReassignacio";
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
