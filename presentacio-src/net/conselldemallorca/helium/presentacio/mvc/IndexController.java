/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.EntornService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina inicial
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class IndexController extends BaseController {

	private EntornService entornService;
	private TascaService tascaService;
	private PermissionService permissionService;



	@Autowired
	public IndexController(
			EntornService entornService,
			TascaService tascaService,
			PermissionService permissionService) {
		this.entornService = entornService;
		this.tascaService = tascaService;
		this.permissionService = permissionService;
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("personaLlistat", tascaService.findTasquesPersonals(entorn.getId()));
			model.addAttribute("grupLlistat", tascaService.findTasquesGrup(entorn.getId()));
		} else {
			Map<Entorn, List<TascaDto>> tasquesPersonaEntorn = new HashMap<Entorn, List<TascaDto>>();
			Map<Entorn, List<TascaDto>> tasquesGrupEntorn = new HashMap<Entorn, List<TascaDto>>();
			List<Entorn> entornsActius = entornService.findActius();
			permissionService.filterAllowed(
					entornsActius,
					Entorn.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.READ});
			for (Entorn ent: entornsActius) {
				tasquesPersonaEntorn.put(ent, tascaService.findTasquesPersonals(ent.getId()));
				tasquesGrupEntorn.put(ent, tascaService.findTasquesGrup(ent.getId()));
			}
			model.addAttribute("entornsActius", entornsActius);
			model.addAttribute("tasquesPersonaEntorn", tasquesPersonaEntorn);
			model.addAttribute("tasquesGrupEntorn", tasquesGrupEntorn);
		}
		return "index";
	}

}
