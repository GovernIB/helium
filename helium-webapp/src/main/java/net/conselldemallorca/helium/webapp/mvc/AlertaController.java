/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.AlertaService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per la gestió d'alertes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/alerta")
public class AlertaController extends BaseController {

	private AlertaService alertaService;



	@Autowired
	public AlertaController(
			AlertaService alertaService) {
		this.alertaService = alertaService;
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Alerta> alertes = alertaService.findActivesAmbEntornIUsuariAutenticat(entorn.getId());
			model.addAttribute("llistat", alertes);
			return "alerta/llistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "llegir", method = RequestMethod.GET)
	public String llegir(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				alertaService.marcarLlegida(id);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.marcar.alerta.llegida"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut marcar l'alerta com a llegida", ex);
	        }
			return "redirect:/alerta/llistat.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "nollegir", method = RequestMethod.GET)
	public String nollegir(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				alertaService.desmarcarLlegida(id);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.marcar.alerta.no.llegida"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut marcar l'alerta com a no llegida", ex);
	        }
			return "redirect:/alerta/llistat.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "esborrar", method = RequestMethod.GET)
	public String esborrar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				alertaService.marcarEsborrada(id);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.alerta"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar l'alerta", ex);
	        }
			return "redirect:/alerta/llistat.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "crearTest", method = RequestMethod.GET)
	public String crearTest(
			HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				Alerta alerta = new Alerta(
						new Date(),
						"admin",
						getMessage("salutacio"),
						entorn);
				alertaService.create(alerta);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.alerta"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut crear l'alerta", ex);
	        }
			return "redirect:/alerta/llistat.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	private static final Log logger = LogFactory.getLog(AlertaController.class);

}
