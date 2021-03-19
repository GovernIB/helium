package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientRegistreService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientAltaMassivaCommand;

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
	private ExpedientService expedientService;
	@Autowired
	private ExpedientRegistreService expedientRegistreService;

	@Autowired
	private AplicacioService aplicacioService;

	/** Per donar format a les dates sense haver d'instanciar l'objecte cada cop. */
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");


	@RequestMapping(method = RequestMethod.GET)
	public String formulariGet(
			HttpServletRequest request,
			Model model) {
		ExpedientAltaMassivaCommand expedientAltaMassivaCommand = new ExpedientAltaMassivaCommand();
		model.addAttribute("expedientAltaMassivaCommand", expedientAltaMassivaCommand);
		
		// Buscar tipus d'expedient amb permís d'administració o execució d'scripts
		List<ExpedientTipusDto> expedientTipusPermesos = expedientTipusService.findAmbEntornPermisAnotacio(EntornActual.getEntornId());
		expedientTipusPermesos = expedientTipusService.findAmbEntornPermisExecucioScript(EntornActual.getEntornId());
		model.addAttribute("expedientTipusPermesos", expedientTipusPermesos);
				
		return "v3/expedientAltaMassiva";
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientAltaMassivaController.class);
}
