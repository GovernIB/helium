/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.ui.Model;

/**
 * Controlador base per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientController extends BaseController {

	@Autowired
	protected PermissionService permissionService;
	@Autowired
	protected DissenyService dissenyService;

	protected String mostrarInformacioExpedientPerPipella(
			HttpServletRequest request,
			Long expedientId,
			Model model,
			String pipellaActiva,
			ExpedientService expedientService) {
		ExpedientDto expedient = expedientService.findById(expedientId);
		model.addAttribute("expedient", expedient);
		model.addAttribute(
				"participants",
				expedientService.findParticipants(expedientId));
		/*List<AccioDto> accions = expedientService.findAccionsVisibles(expedientId);
		Iterator<AccioDto> it = accions.iterator();
		while (it.hasNext()) {
			AccioDto accio = it.next();
			String rols = accio.getRols();
			if (accio.isPublica()) {
				hiHaAccionsPubliques = true;
				break;
			} else if (rols != null && rols.length() > 0) {
				boolean permesa = false;
				String[] llistaRols = rols.split(",");
				for (String rol: llistaRols) {
					if (request.isUserInRole(rol)) {
						permesa = true;
						break;
					}
				}
				if (!permesa)
					it.remove();
			}
		}
		model.addAttribute("accions", accions);*/
		model.addAttribute(
				"accions",
				expedientService.findAccionsVisibles(expedientId));
		model.addAttribute(
				"relacionats",
				expedientService.findRelacionats(expedientId));
		/*DefinicioProcesDto def = dissenyService.getById(instanciaProces.getDefinicioProces().getId());
		model.addAttribute("definicioProcesJbpmId",def.getId());
		model.addAttribute("definicioProcesDescripcio",def.getEtiqueta());
		model.addAttribute("definicionsProces",def.getJbpmIdsAmbDescripcio());
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
		else
			model.addAttribute("pipellaActiva", "tasques");*/
		return "v3/expedientPipelles";
	}

	protected boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.isGrantedAny(expedient.getTipus().getId(), ExpedientTipus.class, new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE});
	}

	protected boolean potAdministrarExpedient(ExpedientDto expedient) {
		return permissionService.isGrantedAny(expedient.getTipus().getId(), ExpedientTipus.class, new Permission[] {
					ExtendedPermission.ADMINISTRATION});
	}

	protected boolean potModificarOReassignarExpedient(ExpedientDto expedient) {
		List<ExpedientTipusDto> tipus = dissenyService.findExpedientTipusAmbEntorn(expedient.getEntorn());
		return permissionService.filterAllowed(
				tipus,
				new ObjectIdentifierExtractor<ExpedientTipusDto>() {
					public Long getObjectIdentifier(ExpedientTipusDto expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE,
					ExtendedPermission.REASSIGNMENT});
	}

	protected boolean potConsultarExpedient(ExpedientDto expedient) {
		return permissionService.isGrantedAny(expedient.getTipus().getId(), ExpedientTipus.class, new Permission[] {
			ExtendedPermission.ADMINISTRATION,
			ExtendedPermission.SUPERVISION,
			ExtendedPermission.READ});
	}

	protected boolean potIniciarExpedientTipus(ExpedientTipusDto expedientTipus) {
		return permissionService.isGrantedAny(
				expedientTipus.getId(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.CREATE});
	}
	
	protected boolean potEsborrarExpedient(ExpedientDto expedient) {
		return permissionService.isGrantedAny(expedient.getTipus().getId(), ExpedientTipus.class, new Permission[] {
			ExtendedPermission.ADMINISTRATION,
			ExtendedPermission.DELETE});
	}

}
