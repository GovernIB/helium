/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.conselldemallorca.helium.core.common.ThreadLocalInfo;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;

/**
 * Interceptor per guardar a la sessió les dades de l'entorn
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntornInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_REQUEST_CANVI_ENTORN = "entornCanviarAmbId";
	public static final String VARIABLE_REQUEST_CANVI_EXPTIP = "expedientTipusCanviarAmbId";
	
	@Resource(name="entornServiceV3")
	private EntornService entornService;
	@Resource
	private AplicacioService aplicacioService;
	@Resource
	private ExpedientTipusService expedientTipusService;

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null && !isRequestResource(request)) {
			EntornDto entornSessio = (EntornDto)SessionHelper.getAttribute(
					request,
					SessionHelper.VARIABLE_ENTORN_ACTUAL_V3);
			EntornDto entornActual = null;
			String canviEntorn = request.getParameter(VARIABLE_REQUEST_CANVI_ENTORN);
			List<EntornDto> entorns = entornService.findActiusAmbPermisAcces();
			request.setAttribute("entorns", entorns);
			// Nova implementació
			if (entorns.size() == 0) {
				if (request.getServletPath().startsWith("/v3")) {
		            ModelAndView mav = new ModelAndView("v3/entornNoDisponible");
		            throw new ModelAndViewDefiningException(mav);
				}
			} else {
				if (canviEntorn != null) {
					Long entornId = new Long(canviEntorn);
					for (EntornDto entorn: entorns) {
						if (entorn.getId().longValue() == entornId.longValue()) {
							entornActual = entorn;
							setEntornActual(request, entornActual);
							break;
						}
					}
				} else {
					if (entornSessio == null) {
						UsuariPreferenciesDto prefs = aplicacioService.getUsuariPreferencies();
						if (prefs != null) {
							if (prefs.getDefaultEntornCodi() != null) {
								for (EntornDto entorn: entorns) {
									if (entorn.getCodi() != null && entorn.getCodi().equals(prefs.getDefaultEntornCodi())) {
										entornActual = entorn;
										setEntornActual(request, entornActual);
										break;
									}
								}
								if (entornActual == null) {
									entornActual = entorns.get(0);
									setEntornActual(request, entornActual);
								}
							} else {
								entornActual = entorns.get(0);
								setEntornActual(request, entornActual);
							}
							if (prefs.getExpedientTipusDefecteId() != null) {
								try {
									SessionHelper.setAttribute(
											request,
											SessionHelper.VARIABLE_EXPTIP_ACTUAL,
											expedientTipusService.findAmbIdPermisConsultar(
													EntornActual.getEntornId(),
													prefs.getExpedientTipusDefecteId()));
								} catch (Exception ignored) {
									// Si no el troba símplement no el selecciona
								}
							}
						} else {
							entornActual = entorns.get(0);
							setEntornActual(request, entornActual);
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
				}
			}
			// Inicialitza la variable ThreadLocal de l'expedient que s'està iniciant
			ThreadLocalInfo.setExpedient(null);
			if (entornActual != null) {
								
				// Refresca el tipus d'expedient actual
				@SuppressWarnings("unchecked")
				List<ExpedientTipusDto> accessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
						request,
						SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);
				String canviExpedientTipus = request.getParameter(VARIABLE_REQUEST_CANVI_EXPTIP);
				if (canviExpedientTipus != null) {
					if (canviExpedientTipus.length() > 0) {
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
						// Netejar selecció d'expedients
						SessionManager sessionManager = SessionHelper.getSessionManager(request);
						Set<Long> ids = sessionManager.getSeleccioConsultaGeneral();
						if (ids != null)
							ids.clear();
					} else {
						SessionHelper.removeAttribute(
								request,
								SessionHelper.VARIABLE_EXPTIP_ACTUAL);
					}
				}				
				// Consultas por tipo
				if (canviEntorn != null || canviExpedientTipus != null || SessionHelper.getAttribute(request, SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_AMB_CONSULTES_ACTIVES) == null) {
					accessibles = expedientTipusService.findAmbEntornPermisConsultar(
							entornActual.getId());
					SessionHelper.setAttribute(request, SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES, accessibles);
					List<ExpedientTipusDto> accessiblesConConsultasActivas = new ArrayList<ExpedientTipusDto>();
					for (ExpedientTipusDto expedientTipus: accessibles) {
						if (!expedientTipus.getConsultes().isEmpty()) {
							accessiblesConConsultasActivas.add(expedientTipus);
						}
					}
					SessionHelper.setAttribute(
							request,
							SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_AMB_CONSULTES_ACTIVES,
							accessiblesConConsultasActivas);
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
		ThreadLocalInfo.setExpedient(null);
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
		ent.setColorFons(entorn.getColorFons());
		ent.setColorLletra(entorn.getColorLletra());
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_ENTORN_ACTUAL,
				ent);
		// Emmagatzema els permisos per a l'entorn actual a la sessió de l'usuari
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PERMIS_ENTORN_DESIGN,
				entorn.isPermisDesign());
		SessionHelper.setAttribute(
				request, 
				SessionHelper.VARIABLE_PERMIS_ENTORN_ADMIN, 
				entorn.isPermisAdministration());
		// Guarda l'entorn actual
		EntornActual.setEntornId(entorn.getId());
		// Al canviar d'entorn hem de reconfigurar algunes variables de sessió
		List<ExpedientTipusDto> expedientsTipusAmbPermisDisseny = expedientTipusService.findAmbEntornPermisDissenyar(
				entorn.getId());
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PERMIS_EXPTIP_DISSENY,
				new Boolean(!expedientsTipusAmbPermisDisseny.isEmpty()));
		SessionHelper.setAttribute(
				request, 
				SessionHelper.VARIABLE_PERMIS_ANOTACIONS_PROCESSAR, 
				!expedientTipusService.findAmbEntornPermisAnotacio(entorn.getId()).isEmpty());
		SessionHelper.setAttribute(
				request, 
				SessionHelper.VARIABLE_PERMIS_SCRIPTS_EXECUTAR,
				!expedientTipusService.findAmbEntornPermisExecucioScript(entorn.getId()).isEmpty());
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_PREFERENCIES_USUARI,
				aplicacioService.getUsuariPreferencies());
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES,
				expedientTipusService.findAmbEntornPermisConsultar(
						entorn.getId()));
		
		// Actualitza si hi ha expedients per iniciar
		List<ExpedientTipusDto> tipusCrear = expedientTipusService.findAmbEntornPermisCrear(
				entorn.getId());
		
		List<ExpedientTipusDto> tipusAltaCsv = expedientTipusService.findAmbEntornPermisExecucioScript(
				entorn.getId());
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_HIHA_TRAMITS_INICIABLES,
				new Boolean(tipusCrear.size() > 0 || tipusAltaCsv.size() > 0));
		
		
		// Eliminam expedient actual
		SessionHelper.removeAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACTUAL);
		// Eliminam filtres de tasques i expedients
		SessionHelper.removeAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_GENERAL);
		SessionHelper.removeAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TASCA);
		SessionHelper.removeAttribute(
				request,
				SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS);
	}

	private boolean isRequestResource(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String root = request.getContextPath();
		if (
				uri.contains(root + "/img/") || 
				uri.contains(root + "/css/") || 
				uri.contains(root + "/js/") || 
				uri.contains("/datatable") || 
				uri.contains("/selection"))
			return true;
		return false;
	}

}
