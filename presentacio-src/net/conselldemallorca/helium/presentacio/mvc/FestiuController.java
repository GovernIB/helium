/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.service.TerminiService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per la gestió de dies festius
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class FestiuController extends BaseController {

	private TerminiService terminiService;



	@Autowired
	public FestiuController(
			TerminiService terminiService) {
		this.terminiService = terminiService;
	}

	@RequestMapping(value = "/festiu/calendari")
	public String llistat(
			HttpServletRequest request,
			@RequestParam(value = "any", required = false) Integer any,
			ModelMap model) {
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
				terminiService.findFestiuAmbAny(anyActual));
		String nolabsStr = GlobalProperties.getInstance().getProperty("app.calendari.nolabs");
		if (nolabsStr != null) {
			model.addAttribute(
					"nolabs",
					nolabsStr.split(","));
		}
		return "festiu/calendari";
	}

}
