/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.interceptor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.AlertaService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per guardar a la sessió les dades de l'entorn
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntornInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_REQUEST_CANVI_ENTORN = "entornCanviarAmbId";
	public static final String VARIABLE_REQUEST_CANVI_EXPTIP = "expedientTipusCanviarAmbId";
	public static final String VARIABLE_REQUEST_ALERTES_ACTIVES = "hiHaAlertesActives";
	public static final String VARIABLE_REQUEST_ALERTES_NOLLEGIDES = "hiHaAlertesNollegides";

	@Resource
	private AdminService adminService;
	@Resource(name="dissenyServiceV3")
	private DissenyService dissenyService;
	@Resource
	private PermissionService permissionService;

	private AlertaService alertaService;



	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			EntornDto entornSessio = (EntornDto)SessionHelper.getAttribute(
					request,
					SessionHelper.VARIABLE_ENTORN_ACTUAL_V3);
			EntornDto entornActual = null;
			String canviEntorn = request.getParameter(VARIABLE_REQUEST_CANVI_ENTORN);
			List<EntornDto> entorns = adminService.findEntornAmbPermisReadUsuariActual();
			request.setAttribute("entorns", entorns);
			// Si en el request existeix el paràmetre de selecció d'entorn
			// canvia l'entorn actual
			if (canviEntorn != null) {
				Long entornId = new Long(canviEntorn);
				for (EntornDto entorn: entorns) {
					if (entorn.getId().longValue() == entornId.longValue()) {
						entornActual = entorn;
						setEntornActual(request, entornActual);
						break;
					}
				}
			} else if (entornSessio == null) {
				if (entorns.size() == 1) {
					entornActual = entorns.get(0);
					setEntornActual(request, entornActual);
				} else {
					UsuariPreferenciesDto prefs = adminService.getPreferenciesUsuariActual();
					if (prefs != null && prefs.getDefaultEntornCodi() != null) {
						for (EntornDto entorn: entorns) {
							if (entorn.getCodi().equals(prefs.getDefaultEntornCodi())) {
								entornActual = entorn;
								setEntornActual(request, entornActual);
								break;
							}
						}
					}
				}
			} else {
				for (EntornDto entorn: entorns) {
					if (entorn.getCodi().equals(entornSessio.getCodi())) {
						entornActual = entorn;
						break;
					}
				}
				if (entornActual != null && EntornActual.getEntornId() == null)
					EntornActual.setEntornId(entornActual.getId());
			}
			// Inicialitza la variable ThreadLocal de l'expedient que s'està iniciant
			ExpedientIniciantDto.setExpedient(null);
			if (entornActual != null) {
				// Actualitza si hi ha expedients per iniciar
				List<ExpedientTipusDto> tipus = dissenyService.findExpedientTipusAmbPermisCrearUsuariActual(
						entornActual.getId());
				SessionHelper.setAttribute(
						request,
						SessionHelper.VARIABLE_HIHA_TRAMITS_INICIABLES,
						new Boolean(tipus.size() > 0));
				// Indica si hi ha alertes
				List<Alerta> alertes = alertaService.findActivesAmbEntornIUsuariAutenticat(
						entornActual.getId());
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
				// Refresca el tipus d'expedient actual
				String canviExpedientTipus = request.getParameter(VARIABLE_REQUEST_CANVI_EXPTIP);
				if (canviExpedientTipus != null) {
					if (canviExpedientTipus.length() > 0) {
						@SuppressWarnings("unchecked")
						List<ExpedientTipusDto> accessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
								request,
								SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);
						if (canviExpedientTipus != null) {
							Long expedientTipusId = new Long(canviExpedientTipus);
							for (ExpedientTipusDto expedientTipus: accessibles) {
								if (expedientTipus.getId().equals(expedientTipusId)) {
									SessionHelper.setAttribute(
											request,
											SessionHelper.VARIABLE_EXPTIP_ACTUAL,
											expedientTipus);
									break;
								}
							}
						}
					} else {
						SessionHelper.removeAttribute(
								request,
								SessionHelper.VARIABLE_EXPTIP_ACTUAL);
					}
				}
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
		ExpedientIniciantDto.setExpedient(null);
	}



	@Autowired
	public void setAlertaService(AlertaService alertaService) {
		this.alertaService = alertaService;
	}



	private void setEntornActual(
			HttpServletRequest request,
			EntornDto entorn) {
		// Emmagatzema l'entorn actual dins la sessió de l'usuari
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_ENTORN_ACTUAL_V3,
				entorn);
		Entorn ent = new Entorn();
		ent.setId(entorn.getId());
		ent.setCodi(entorn.getCodi());
		ent.setNom(entorn.getNom());
		ent.setActiu(true);
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_ENTORN_ACTUAL,
				ent);
		// Emmagatzema els permisos per a l'entorn actual a la sessió de l'usuari
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PERMIS_ENTORN_READ,
				permissionService.isGrantedAny(
						ent,
						Entorn.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.READ}));
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PERMIS_ENTORN_DESIGN,
				permissionService.isGrantedAny(
						ent,
						Entorn.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.DESIGN}));
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PERMIS_ENTORN_ORGANIZATION,
				permissionService.isGrantedAny(
						ent,
						Entorn.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.ORGANIZATION}));
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PERMIS_ENTORN_ADMINISTRATION,
				permissionService.isGrantedAny(
						ent,
						Entorn.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION}));
		// Guarda l'entorn actual
		EntornActual.setEntornId(entorn.getId());
		// Al canviar d'entorn hem de reconfigurar algunes variables de sessió
		List<ExpedientTipusDto> expedientsTipusAmbPermisDisseny = dissenyService.findExpedientTipusAmbPermisDissenyUsuariActual(
				entorn.getId());
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PERMIS_EXPTIP_DISSENY,
				new Boolean(!expedientsTipusAmbPermisDisseny.isEmpty()));
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PREFERENCIES_USUARI,
				adminService.getPreferenciesUsuariActual());
		List<ExpedientTipusDto> expedientsTipusAmbPermisGestio = dissenyService.findExpedientTipusAmbPermisGestioUsuariActual(
				entorn.getId());
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PERMIS_EXPTIP_GESTIO,
				new Boolean(!expedientsTipusAmbPermisGestio.isEmpty()));
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES,
				dissenyService.findExpedientTipusAmbPermisReadUsuariActual(
						entorn.getId()));
		SessionHelper.removeAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACTUAL);
	}

}
