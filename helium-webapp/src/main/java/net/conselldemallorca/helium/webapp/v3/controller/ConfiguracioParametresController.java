package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.webapp.v3.command.ParametresCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controlador per a la pàgina de configuració de paràmetres de l'aplicació Helium.
 * De moment aquesta pàgina només es crea per al paràmetre de la redirecció de menús
 * antics cap a la nova interfície v3. Si s'ha d'amplicar es pot pensar en posar
 * els paràmetres a nivell de BBDD o crear un Map<String, Object> de paràmetres.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/configuracio/parametres")
public class ConfiguracioParametresController extends BaseController {
	
	/** Constant de la propietat de redireccionar des de menús de la interfície 2.6 cap a la nova interfície 3.*/
	private static final String APP_CONFIGURACIO_REDIRECCIONAR = "app.configuracio.redireccionar";
	
	public enum Accions {
		RESTAURAR,
		GUARDAR;
	}
	
	/** Map per guardar els valors per defecte en el cas de restaurar els valors. */
	private Map<String, String> valorsDefecte = new HashMap<String, String>();

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		
		
		ParametresCommand parametresCommand = new ParametresCommand();
		// Omple amb els valors del fitxer de propietats
		parametresCommand.setRedireccionar(isRedireccionar());
		
		model.addAttribute("parametresCommand", parametresCommand);
				
		return "v3/parametres";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post (
			HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "GUARDAR") Accions accio,
			ParametresCommand parametresCommand,
			Model model) {
		
		String messageKey;
		if (Accions.GUARDAR.equals(accio)) {
			// Guardar els valors en les propietats
			logger.info("Guardant els valors dels paràmetres: {restaurar= " + parametresCommand.isRedireccionar() + "}");
			GlobalProperties.getInstance().setProperty(APP_CONFIGURACIO_REDIRECCIONAR, String.valueOf(parametresCommand.isRedireccionar()));
			messageKey = "configuracio.parametres.accio.guardar.confirmacio";
		} else {
			// Restaurar els valors
			logger.info("Restaurant els valors per defecte dels paràmetres");
			// Restaura els valors per defecte
			for (String valorDefecte : valorsDefecte.keySet()) 
				GlobalProperties.getInstance().setProperty(valorDefecte, valorsDefecte.get(valorDefecte));
			messageKey = "configuracio.parametres.accio.restaurar.confirmacio";
		}
		MissatgesHelper.success(
				request, 
				getMessage(
						request, 
						messageKey));

		return "redirect:/modal/v3/configuracio/parametres";
	}

	/** Mètode per consultar la propietat de si s'ha de redireccionar del menú antic al nou menú. */
	private boolean isRedireccionar() {
		// Guarda el valor per defecte en cas d'haver de restaurar
		if (!valorsDefecte.containsKey(APP_CONFIGURACIO_REDIRECCIONAR)) {
			valorsDefecte.put(APP_CONFIGURACIO_REDIRECCIONAR, String.valueOf("true".equalsIgnoreCase(GlobalProperties.getInstance().getProperty(APP_CONFIGURACIO_REDIRECCIONAR))));
		}
		return "true".equalsIgnoreCase(GlobalProperties.getInstance().getProperty(APP_CONFIGURACIO_REDIRECCIONAR));
	}
	
	private static final Log logger = LogFactory.getLog(ConfiguracioParametresController.class);
}
