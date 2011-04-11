/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.model.hibernate.Alerta;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.UsuariPreferencies;
import net.conselldemallorca.helium.model.service.AlertaService;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.EntornService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.model.service.PersonaService;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.util.EntornActual;
import net.conselldemallorca.helium.util.ExpedientIniciant;

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
	public static final String VARIABLE_REQUEST_ALERTES_ACTIVES = "hiHaAlertesActives";
	public static final String VARIABLE_REQUEST_ALERTES_NOLLEGIDES = "hiHaAlertesNollegides";

	private EntornService entornService;
	private PersonaService personaService;
	private DissenyService dissenyService;
	private PermissionService permissionService;
	private AlertaService alertaService;



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
						if (prefs != null && prefs.getDefaultEntornCodi() != null) {
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
				request.getSession().removeAttribute(PermisosDissenyInterceptor.VARIABLE_SESSION_PERMISOS_DISSENY);
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
			// Inicialitza la variable ThreadLocal de l'expedient que s'està iniciant
			ExpedientIniciant.setExpedient(null);
			// Guarda l'entorn actual a la sessió i com a una variable ThreadLocal
			request.getSession().setAttribute(
					VARIABLE_SESSIO_ENTORN_ACTUAL,
					entornActual);
			if (entornActual != null)
				EntornActual.setEntornId(entornActual.getId());
			else
				EntornActual.setEntornId(null);
			if (entornActual != null) {
				// Actualitza si hi ha tràmits per iniciar
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
				// Indica si hi ha alertes
				List<Alerta> alertes = alertaService.findActivesAmbEntornIUsuariAutenticat(entornActual.getId());
				request.setAttribute(
						VARIABLE_REQUEST_ALERTES_ACTIVES,
						new Boolean(alertes != null && alertes.size() > 0));
				boolean totesLlegides = true;
				for (Alerta alerta: alertes) {
					if (!alerta.isLlegida()) {
						totesLlegides = false;
						break;
					}
				}
				request.setAttribute(
						VARIABLE_REQUEST_ALERTES_NOLLEGIDES,
						new Boolean(!totesLlegides));
			}
		}
		return true;
	}

	public void afterCompletion(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			Exception ex) {
		EntornActual.setEntornId(null);
		ExpedientIniciant.setExpedient(null);
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
	@Autowired
	public void setAlertaService(AlertaService alertaService) {
		this.alertaService = alertaService;
	}



	private static final Log logger = LogFactory.getLog(EntornInterceptor.class);

}
