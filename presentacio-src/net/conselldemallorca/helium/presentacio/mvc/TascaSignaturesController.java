/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.exception.NotFoundException;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.presentacio.mvc.util.TascaFormUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per la gestió de signatures a les tasques
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class TascaSignaturesController extends BaseController {

	private TascaService tascaService;



	@Autowired
	public TascaSignaturesController(
			TascaService tascaService) {
		this.tascaService = tascaService;
	}

	@SuppressWarnings("unchecked")
	@ModelAttribute("commandReadOnly")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				TascaDto tasca = tascaService.getById(entorn.getId(), id);
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				campsAddicionals.put("id", id);
				campsAddicionals.put("entornId", entorn.getId());
				campsAddicionals.put("procesScope", null);
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				campsAddicionalsClasses.put("id", String.class);
				campsAddicionalsClasses.put("entornId", Long.class);
				campsAddicionalsClasses.put("procesScope", Map.class);
				Object command = TascaFormUtil.getCommandForTasca(
						tasca,
						campsAddicionals,
						campsAddicionalsClasses);
				return command;
			} catch (NotFoundException ignored) {}
		}
		return null;
	}

	@RequestMapping(value = "/tasca/signatures")
	public String signatures(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (model.get("commandReadOnly") == null) {
				missatgeError(request, "Aquesta tasca ja no està disponible");
				return "redirect:/tasca/personaLlistat.html";
			}
			TascaDto tasca = tascaService.getById(entorn.getId(), id);
			model.addAttribute("tasca", tasca);
			return "tasca/signatures";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

}
