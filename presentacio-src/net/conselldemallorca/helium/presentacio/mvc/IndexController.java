/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Alerta;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.AlertaService;
import net.conselldemallorca.helium.model.service.EntornService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.presentacio.mvc.util.Regtest;
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
	private AlertaService alertaService;
	private PermissionService permissionService;



	@Autowired
	public IndexController(
			EntornService entornService,
			TascaService tascaService,
			AlertaService alertaService,
			PermissionService permissionService) {
		this.entornService = entornService;
		this.tascaService = tascaService;
		this.alertaService = alertaService;
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
			model.addAttribute("alertesLlistat", alertaService.findActivesAmbEntornIUsuariAutenticat(entorn.getId()));
		} else {
			Map<Entorn, List<TascaDto>> tasquesPersonaEntorn = new HashMap<Entorn, List<TascaDto>>();
			Map<Entorn, List<TascaDto>> tasquesGrupEntorn = new HashMap<Entorn, List<TascaDto>>();
			Map<Entorn, List<Alerta>> alertesEntorn = new HashMap<Entorn, List<Alerta>>();
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
				alertesEntorn.put(ent, alertaService.findActivesAmbEntornIUsuariAutenticat(ent.getId()));
			}
			model.addAttribute("entornsActius", entornsActius);
			model.addAttribute("tasquesPersonaEntorn", tasquesPersonaEntorn);
			model.addAttribute("tasquesGrupEntorn", tasquesGrupEntorn);
			model.addAttribute("alertesEntorn", alertesEntorn);
		}
		return "index";
	}

}
