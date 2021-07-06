package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.logic.intf.service.ExpedientTerminiService;
import es.caib.helium.logic.util.GlobalProperties;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

/**
 * Controlador per a la de configuració de festius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/configuracio/festius")
public class ConfiguracioFestiusController extends BaseController {

	@Autowired
	private ExpedientTerminiService expedientTerminiService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@RequestParam(value = "any", required = false) Integer any,
			Model model) {
		
		Calendar cal = Calendar.getInstance();
		int anyActual = (any != null) ? any : cal.get(Calendar.YEAR);
		int[] darrerDiaMes = new int[12];
		int[] diaSetmanaIniciMes = new int[12];
		for (int i = 0; i < 12; i++) {
			cal.set(Calendar.YEAR, anyActual);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			diaSetmanaIniciMes[i] = (cal.get(Calendar.DAY_OF_WEEK) + 6) % 7;
			if (diaSetmanaIniciMes[i] == 0)
				diaSetmanaIniciMes[i] = 7;
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			darrerDiaMes[i] = cal.get(Calendar.DAY_OF_MONTH);
		}
		model.addAttribute(
				"darrerDiaMes",
				darrerDiaMes);
		model.addAttribute(
				"diaSetmanaIniciMes",
				diaSetmanaIniciMes);
		model.addAttribute(
				"anyInicial",
				new Integer(anyActual - 5));
		model.addAttribute(
				"anyActual",
				new Integer(anyActual));
		model.addAttribute(
				"anyFinal",
				new Integer(anyActual + 5));
		model.addAttribute(
				"festius",
				expedientTerminiService.festiuFindAmbAny(anyActual));
		String nolabsStr = GlobalProperties.getInstance().getProperty("app.calendari.nolabs");
		if (nolabsStr != null) {
			model.addAttribute(
					"nolabs",
					nolabsStr.split(","));
		}
		
		return "v3/festius";
	}
	
	@RequestMapping(value = "/create/{dia}/{mes}/{any}", method = RequestMethod.GET)
	@ResponseBody
	public boolean crearFestiu(
			HttpServletRequest request,
			@PathVariable(value = "dia") String dia,
			@PathVariable(value = "mes") String mes,
			@PathVariable(value = "any") String any,
			ModelMap model) {
		try {
			expedientTerminiService.festiuCreate(dia + "/" + mes + "/" + any);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@RequestMapping(value = "/delete/{dia}/{mes}/{any}", method = RequestMethod.GET)
	@ResponseBody
	public boolean esborrarFestiu(
			HttpServletRequest request,
			@PathVariable(value = "dia") String dia,
			@PathVariable(value = "mes") String mes,
			@PathVariable(value = "any") String any,
			ModelMap model) {
		try {
			expedientTerminiService.festiuDelete(dia + "/" + mes + "/" + any);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
		
//	private static final Log logger = LogFactory.getLog(ConfiguracioFestiusController.class);
}
