/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Alerta;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.AlertaService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

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
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
@RequestMapping("/alerta/*.html")
public class AlertaController extends BaseController {

	private AlertaService alertaService;
	private TascaService tascaService;



	@Autowired
	public AlertaController(
			AlertaService alertaService,
			TascaService tascaService) {
		this.alertaService = alertaService;
		this.tascaService = tascaService;
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<Alerta> alertes = alertaService.findActivesAmbEntornIUsuariAutenticat(entorn.getId());
			model.addAttribute("llistat", alertes);
			Map<String, TascaDto> tasques = new HashMap<String, TascaDto>();
			for (Alerta alerta: alertes) {
				if (alerta.getTaskInstanceId() != null) {
					tasques.put(
							alerta.getTaskInstanceId(),
							tascaService.getById(entorn.getId(), alerta.getTaskInstanceId()));
				}
			}
			model.addAttribute("tasques", tasques);
			return "alerta/llistat";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
	        	missatgeError(request, "No s'ha pogut marcar l'alerta com a llegida", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut marcar l'alerta com a llegida", ex);
	        }
			return "redirect:/alerta/llistat.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
	        	missatgeError(request, "No s'ha pogut marcar l'alerta com a no llegida", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut marcar l'alerta com a no llegida", ex);
	        }
			return "redirect:/alerta/llistat.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
	        	missatgeError(request, "No s'ha pogut esborrar l'alerta", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar l'alerta", ex);
	        }
			return "redirect:/alerta/llistat.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
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
						"Bon dia",
						entorn);
				alertaService.create(alerta);
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut crear l'alerta", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut crear l'alerta", ex);
	        }
			return "redirect:/alerta/llistat.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}



	private static final Log logger = LogFactory.getLog(AlertaController.class);

}
