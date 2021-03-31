package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientAltaMassivaCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientAltaMassivaCommand.AltaMassiva;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/** Controlador pel formulari d'alta massiva d'expedients a partir d'una fulla CSV. 
 * Programa una execució massiva per cada fila de la fulla CSV per crear un
 * expedient de forma massiva.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient/altaMassiva")
public class ExpedientAltaMassivaController extends BaseExpedientController {


	@Autowired
	private ExecucioMassivaService execucioMassivaService;

	/** Pàgina per mostrar la informació de la darrera alta d'expedient massiva segons el tipus
	 * d'expedient seleccionat i per mostrar el formulari d'alta massiva si no hi ha cap en execució actualmen.
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String formulariGet(
			HttpServletRequest request,
			Model model) {
		ExpedientAltaMassivaCommand expedientAltaMassivaCommand = new ExpedientAltaMassivaCommand();
		model.addAttribute("command", expedientAltaMassivaCommand);
						
		return "v3/expedientAltaMassiva";
	}
	
	/** 
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String formulariPost(
			HttpServletRequest request,
			@Validated(AltaMassiva.class) 
			@ModelAttribute("command")
			ExpedientAltaMassivaCommand command,
			BindingResult binding,
			Model model) {
		model.addAttribute("command", command);
		if (binding.hasErrors()) {
			return "v3/expedientAltaMassiva";
		}		
		try {
			// Programa la execució massiva
			ExecucioMassivaDto dto = new ExecucioMassivaDto();
			dto.setExpedientTipusId(command.getExpedientTipusId());
			dto.setTipus(ExecucioMassivaTipusDto.ALTA_MASSIVA);
			dto.setEnviarCorreu(false);
			dto.setParam2(command.getFile().getBytes());
			dto.setContingutCsv(command.getContingutCsv());
			execucioMassivaService.crearExecucioMassiva(dto);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.alta.massiva.form.success"));
		} catch(Exception e) {
			String errMsg = this.getMessage(request, "expedient.alta.massiva.form.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		return "redirect:/modal/v3/expedient/altaMassiva";
	}

	/** Informa el model dels expedients tipus permesos. */
	@ModelAttribute("expedientTipusPermesos")
	public List<ExpedientTipusDto> populateCommand() {
		// Buscar tipus d'expedient amb permís d'administració o execució d'scripts
		List<ExpedientTipusDto> expedientTipusPermesos = expedientTipusService.findAmbEntornPermisExecucioScript(EntornActual.getEntornId());
		return expedientTipusPermesos;
	}

	
	private static final Log logger = LogFactory.getLog(ExpedientAltaMassivaController.class);
}
