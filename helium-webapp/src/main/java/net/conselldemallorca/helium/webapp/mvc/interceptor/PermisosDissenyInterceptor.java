/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per a controlar si un usuari té permisos per
 * accedir al menu de disseny per als tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosDissenyInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_SESSION_PERMISOS_DISSENY = "potDissenyarExpedientTipus";
	public static final String VARIABLE_SESSION_PERMISOS_GESTIO = "potGestionarExpedientTipus";
	public static final String VARIABLE_SESSION_PERMISOS_REASSIGNAR = "potReassignarExpedientTipus";
	
	private DissenyService dissenyService;
	private PermissionService permissionService;


	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getSession().getAttribute(VARIABLE_SESSION_PERMISOS_DISSENY) == null) {
			Entorn entorn = getEntornActiu(request);
			boolean permisosDisseny = false;
			boolean permisosGestio = false;
			boolean permisosReassignar = false;
			if (entorn != null) {
				List<ExpedientTipus> llistat = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
				for (ExpedientTipus expedientTipus: llistat) {
					if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
						permisosDisseny = true;
						break;
					}
					if (potGestionarExpedientTipus(entorn, expedientTipus)) {
						permisosGestio = true;
						break;
					}
				}	
				permissionService.filterAllowed(
						llistat,
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.REASSIGNMENT});
				permisosReassignar = llistat.size() > 0;
			}
			request.getSession().setAttribute(VARIABLE_SESSION_PERMISOS_DISSENY, new Boolean(permisosDisseny));
			request.getSession().setAttribute(VARIABLE_SESSION_PERMISOS_GESTIO, new Boolean(permisosGestio));
			request.getSession().setAttribute(VARIABLE_SESSION_PERMISOS_REASSIGNAR, new Boolean(permisosReassignar));
		}
		return true;
	}



	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}



	private Entorn getEntornActiu(HttpServletRequest request) {
		return (Entorn)request.getSession().getAttribute(EntornInterceptor.VARIABLE_SESSIO_ENTORN_ACTUAL);
	}
	private boolean potDissenyarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}
	private boolean potGestionarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.MANAGE}) != null;
	}
}
