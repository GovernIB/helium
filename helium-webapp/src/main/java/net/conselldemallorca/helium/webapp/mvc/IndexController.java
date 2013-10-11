/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.AdminService;
import net.conselldemallorca.helium.core.model.service.AlertaService;
import net.conselldemallorca.helium.core.model.service.EntornService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina inicial
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class IndexController extends BaseController {

	private EntornService entornService;
	private TascaService tascaService;
	private AlertaService alertaService;
	private PermissionService permissionService;
	private AdminService adminService;


	@Autowired
	public IndexController(
			EntornService entornService,
			TascaService tascaService,
			AlertaService alertaService,
			PermissionService permissionService,
			AdminService adminService) {
		this.entornService = entornService;
		this.tascaService = tascaService;
		this.alertaService = alertaService;
		this.permissionService = permissionService;
		this.adminService = adminService;
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			adminService.getMesuresTemporalsHelper().mesuraIniciar("Index", "general");
			model.addAttribute("countPersonaLlistat", tascaService.findCountTasquesPersonalsIndex(entorn.getId()));
			model.addAttribute("countGrupLlistat", tascaService.findCountTasquesGrupIndex(entorn.getId()));
			model.addAttribute("alertesLlistat", alertaService.findActivesAmbEntornIUsuariAutenticat(entorn.getId()));
			adminService.getMesuresTemporalsHelper().mesuraCalcular("Index", "general");
		} else {
			adminService.getMesuresTemporalsHelper().mesuraIniciar("Index sense entorn actiu", "general");
			Map<Entorn, List<TascaLlistatDto>> tasquesPersonaEntorn = new HashMap<Entorn, List<TascaLlistatDto>>();
			Map<Entorn, List<TascaLlistatDto>> tasquesGrupEntorn = new HashMap<Entorn, List<TascaLlistatDto>>();
			Map<Entorn, List<Alerta>> alertesEntorn = new HashMap<Entorn, List<Alerta>>();
			List<Entorn> entornsActius = entornService.findActius();
			permissionService.filterAllowed(
					entornsActius,
					Entorn.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.READ});
			for (Entorn ent: entornsActius) {
				tasquesPersonaEntorn.put(ent, tascaService.findTasquesPersonalsIndex(ent.getId()));
				tasquesGrupEntorn.put(ent, tascaService.findTasquesGrupIndex(ent.getId()));
				alertesEntorn.put(ent, alertaService.findActivesAmbEntornIUsuariAutenticat(ent.getId()));
			}
			model.addAttribute("entornsActius", entornsActius);
			model.addAttribute("tasquesPersonaEntorn", tasquesPersonaEntorn);
			model.addAttribute("tasquesGrupEntorn", tasquesGrupEntorn);
			model.addAttribute("alertesEntorn", alertesEntorn);
			adminService.getMesuresTemporalsHelper().mesuraCalcular("Index sense entorn actiu", "general");
		}
		return "index";
	}

}
