package net.conselldemallorca.helium.tests.integracio.controller;
 
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import net.conselldemallorca.helium.tests.helper.PropietatsHelper;
import net.conselldemallorca.helium.tests.integracio.util.IntegracioFormUtil;
import net.conselldemallorca.helium.tests.integracio.util.IntegracioFormUtil.IntegracioFormPeticio;
import net.conselldemallorca.helium.tests.integracio.ws.formulari.FormulariExternIniciImpl;
import net.conselldemallorca.helium.ws.formulari.GuardarFormulari;
import net.conselldemallorca.helium.ws.formulari.ParellaCodiValor;

/** Controlador per fer les proves d'integració amb formularis externs.
 * Està relacionat amb el web service de formularis externs d'inici {@link FormulariExternIniciImpl}
 * que rep peticinons d'inici de formularis i el controlador
 * ha de mostrar
 *
 */
@Controller
public class IntegracioFormulariExternController {
	
	@Resource
	PropietatsHelper propietats;
	 
	/** Mostra el formulari recuperant les dades de la crida al ws d'inici de formulari. */
	@RequestMapping("/integracio/formulari/{formId}")
	public ModelAndView formulari(
			@PathVariable String formId) {
		
		ModelAndView mv = new ModelAndView("integracioForm");

		mv.addObject("formId", formId);
		mv.addObject("formIdValue", formId);	// per si es vol modificar el valor del formId
		IntegracioFormPeticio peticio =	IntegracioFormUtil.getInstance().getPeticio(formId);
		mv.addObject("peticio", peticio);
		mv.addObject("endPointAddress", propietats.getHeliumUrl() + "/ws/FormulariExtern");
		 
		return mv;
	}
	
	/** Recull les dades del formulari i fa la crida al WS de finalització de dades. */
	@RequestMapping(value = "/integracio/formulari/{formId}", method = RequestMethod.POST)
	public ModelAndView formulariPost(
			@PathVariable String formId,
			String formIdValue,
			String[] codi,
			String[] valor,
			String endPointAddress) {
		
		String resultat = null;
		ModelAndView mv = new ModelAndView("integracioForm");

		mv.addObject("formId", formId);
		mv.addObject("formIdValue", formIdValue);
		IntegracioFormPeticio peticio =	IntegracioFormUtil.getInstance().getPeticio(formId);
		mv.addObject("peticio", peticio);
		mv.addObject("endPointAddress", propietats.getHeliumUrl() + "/ws/FormulariExtern");
		
		List<ParellaCodiValor> valors = new ArrayList<ParellaCodiValor>();
		for( int i = 0; i < Math.min(codi.length, valor.length); i++)
			valors.add(new ParellaCodiValor(codi[i], valor[i]));
		
		try {
			System.out.print(
					"Guardant formulari (" +
					"formId=" + formIdValue + ", " +
					"endPointAddress=" + endPointAddress + ", valors=[");
			for(int i=0; i < Math.min(codi.length, valor.length); i++)
				System.out.print(codi[i] + "=" + valor[i] + "; ");
			System.out.println("])");
			
			GuardarFormulari guardarFormulariService = 
					this.getGuardarFormulariService(
							endPointAddress,
							null,
							null);
			// Crida al WS de finalització del formulari
			guardarFormulariService.guardar(formIdValue, valors);
			resultat = "Formulari finalitzat correctament.";
			IntegracioFormUtil.getInstance().removePeticio(formId);
			mv.addObject("tancar", true);
		} catch (Throwable e) {
			resultat = "Error cridant al WS de finalització: " + e.getMessage();
			e.printStackTrace();
		}
		mv.addObject("resultat", resultat);
		
		return mv;
	}	
	
	/** Mètode privat per configurar el servei cap a la finalització del formulari i per guardar
	 * els valors.
	 */
	private GuardarFormulari getGuardarFormulariService(
			String endPointAddress,
			String userName,
			String password) throws MalformedURLException {
		
		GuardarFormulari service = (GuardarFormulari) net.conselldemallorca.helium.core.util.ws.WsClientUtils.getWsClientProxy(
				GuardarFormulari.class,
				endPointAddress,
				userName,
				password,
				"NONE",
				false,
				true,
				true,
				null);
		return service;		
	}
}