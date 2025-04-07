package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.reflect.Parameter;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.ParametreDto;
import net.conselldemallorca.helium.v3.core.api.service.ParametreService;
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
	
	@Autowired
	private ParametreService parametreService;
	
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
		List<ParametreDto> parametres = parametreService.findAll();
		for(ParametreDto parametre: parametres) {	
			if(parametre.getCodi()!=null && parametre.getCodi().equals(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS)) {
				parametresCommand.setPropagarEsborratExpedients("0".equals(parametre.getValor()) ? false : true);
			}
			if(parametre.getCodi()!=null && parametre.getCodi().equals(ParametreService.APP_CONFIGURACIO_FITXER_MIDA_MAXIM)) {
				parametresCommand.setFitxerMidaMaxim(parametre.getValor());
			}
		}
		model.addAttribute("parametres", parametres);
		model.addAttribute("parametresCommand", parametresCommand);		
		return "v3/parametres";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post (
			HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "GUARDAR") Accions accio,
			ParametresCommand parametresCommand,
			Model model) {
		String messageKey="configuracio.parametres.accio.restaurar.confirmacio";
		List<ParametreDto> parametres = parametreService.findAll();
		if (Accions.GUARDAR.equals(accio)) {	
			for(ParametreDto parametre: parametres) { //De moment només serà configurable des de l'app Helium el paràmetre Propagar Esborrat expedients(els demés via BBDD només)	
				if(parametre.getCodi()!=null && parametre.getCodi().equals(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS)) {
					parametre.setValor(parametresCommand.isPropagarEsborratExpedients() ? "1" : "0");
					parametreService.update(parametre);
					messageKey = "configuracio.parametres.accio.guardar.confirmacio";
//					Guardar els valors en les propietats
//					logger.info("Guardant els valors dels paràmetres: {propagar_esborrat_expedients: "+parametresCommand.isPropagarEsborratExpedients() +"}");
//					GlobalProperties.getInstance().setProperty(parametre.getCodi(), String.valueOf(parametresCommand.isPropagarEsborratExpedients()));		
				}
				if(parametre.getCodi()!=null && parametre.getCodi().equals(ParametreService.APP_CONFIGURACIO_FITXER_MIDA_MAXIM)) {
					parametre.setValor(parametresCommand.getFitxerMidaMaxim());
					parametreService.update(parametre);
				}
			}	
		} else {
			if(valorsDefecte==null || valorsDefecte.isEmpty()) {
				// Omple amb els valors del fitxer de propietats per defecte
				guardarValorsPerDefecte(parametresCommand);
			} 
			// Restaurar els valors
			logger.info("Restaurant els valors per defecte dels paràmetres");
			// Restaura els valors per defecte
			for (String valorDefecte : valorsDefecte.keySet()){ 
				for(ParametreDto parametre: parametres) {
						//en el cas de la data de darrera sincronització no hi serà al fitxer de properties
						if (valorsDefecte.containsKey(parametre.getCodi()) && !ParametreService.APP_CONFIGURACIO_DATA_SINCRONITZACIO_UO.equals(parametre.getCodi())) {
							if(parametre.getCodi()!=null && parametre.getCodi().equals(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS)) {
								parametre.setValor(valorsDefecte.get(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS).equals("true") ? "1" : "0");
							} else { 
								parametre.setValor(valorsDefecte.get(valorDefecte));//en els demés casos no és boolean
							}
							GlobalProperties.getInstance().setProperty(valorDefecte, valorsDefecte.get(valorDefecte));	
							parametreService.update(parametre);
						}
				}
			}
		}
		MissatgesHelper.success(
				request, 
				getMessage(
						request, 
						messageKey));

		return "redirect:/modal/v3/configuracio/parametres";
	}

	/** Mètode per guardar el valor per defecte de la propietat de propagació d'esborrat d'expedients.*/
	private void guardarValorsPerDefecte(ParametresCommand parametresCommand) {
		// Guarda el valor per defecte del fitxer de propietats
		if (!valorsDefecte.containsKey(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS)) {
			valorsDefecte.put(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS, String.valueOf("true".equalsIgnoreCase(GlobalProperties.getInstance().getProperty(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS))));
			parametresCommand.setPropagarEsborratExpedients(Boolean.valueOf(valorsDefecte.get(GlobalProperties.getInstance().getProperty(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS))));
		} else if (!valorsDefecte.containsKey(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO)) {
			valorsDefecte.put(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO, String.valueOf("true".equalsIgnoreCase(GlobalProperties.getInstance().getProperty(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO))));
			parametresCommand.setCodi(valorsDefecte.get(GlobalProperties.getInstance().getProperty(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO)));
		}
	}
	
	private static final Log logger = LogFactory.getLog(ConfiguracioParametresController.class);
}
