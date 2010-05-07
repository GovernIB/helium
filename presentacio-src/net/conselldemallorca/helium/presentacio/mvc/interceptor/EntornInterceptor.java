/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.UsuariPreferencies;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.EntornService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.model.service.PersonaService;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.util.EntornActual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per guardar a la sessió les dades de l'entorn
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class EntornInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_REQUEST_CANVI_ENTORN = "entornCanviarAmbId";
	public static final String VARIABLE_SESSIO_ENTORN_ACTUAL = "entornActual";
	public static final String VARIABLE_SESSIO_TRAMITS_PER_INICIAR = "hiHaTramitsPerIniciar";

	private EntornService entornService;
	private PersonaService personaService;
	private DissenyService dissenyService;
	private PermissionService permissionService;



	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			Entorn entornActual = (Entorn)request.getSession().getAttribute(VARIABLE_SESSIO_ENTORN_ACTUAL);
			// Si l'usuari només té un entorn el selecciona automàticament
			if (entornActual == null) {
				try {
					List<Entorn> entorns = entornService.findActius();
					permissionService.filterAllowed(
							entorns,
							Entorn.class,
							new Permission[] {
								ExtendedPermission.ADMINISTRATION,
								ExtendedPermission.READ});
					if (entorns.size() == 1) {
						entornActual = entorns.get(0);
					} else {
						UsuariPreferencies prefs = personaService.getUsuariPreferencies();
						if (prefs.getDefaultEntornCodi() != null) {
							Entorn entornDefecte = entornService.findAmbCodi(
									prefs.getDefaultEntornCodi());
							if (entornDefecte != null)
								entornActual = entornDefecte;
						}
					}
				} catch (Exception ex) {
					logger.error("Error cercant els entorns", ex);
				}
			}
			// Si en el request existeix el paràmetre de selecció d'entorn
			// configura l'entorn actual
			String canviEntorn = request.getParameter(VARIABLE_REQUEST_CANVI_ENTORN);
			if (canviEntorn != null) {
				entornActual = entornService.getById(new Long(canviEntorn));
			}
			// Verifica que es tenguin permisos per l'entorn actual
			if (entornActual != null) {
				entornActual = (Entorn)permissionService.filterAllowed(
						entornActual,
						Entorn.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.READ});
			}
			// Guarda l'entorn actual a la sessió i com a una variable ThreadLocal
			request.getSession().setAttribute(
					VARIABLE_SESSIO_ENTORN_ACTUAL,
					entornActual);
			if (entornActual != null)
				EntornActual.setEntornId(entornActual.getId());
			else
				EntornActual.setEntornId(null);
			// Actualitza si hi ha tràmits per iniciar
			if (entornActual != null) {
				List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entornActual.getId());
				permissionService.filterAllowed(
						tipus,
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.CREATE});
				request.getSession().setAttribute(
						VARIABLE_SESSIO_TRAMITS_PER_INICIAR,
						new Boolean(tipus.size() > 0));
			}
		}
		return true;
	}



	@Autowired
	public void setEntornService(EntornService entornService) {
		this.entornService = entornService;
	}
	@Autowired
	public void setPersonaService(PersonaService personaService) {
		this.personaService = personaService;
	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}



	private static final Log logger = LogFactory.getLog(EntornInterceptor.class);

}
