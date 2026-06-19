package es.caib.helium.back.interceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.helper.SessionHelper.SessionManager;
import es.caib.helium.back.helper.UsuariActualHelper;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.UsuariPreferenciesDto;
import es.caib.helium.commons.utils.EntornActual;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.EntornService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;

/**
 * Interceptor per guardar a la sessió les dades de l'entorn
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntornInterceptor implements HandlerInterceptor {

	public static final String VARIABLE_REQUEST_CANVI_ENTORN = "entornCanviarAmbId";
	public static final String VARIABLE_REQUEST_CANVI_EXPTIP = "expedientTipusCanviarAmbId";

	public static final String VARIABLE_REQUEST_ALERTES_NOLLEGIDES = "hiHaAlertesNollegides";

	@Autowired
	private EntornService entornService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {

		if(request.getServletPath().startsWith("/entorn"))
				return true;

		if (request.getUserPrincipal() != null && !isRequestResource(request)) {
			EntornDto entornSessio = (EntornDto)SessionHelper.getAttribute(
					request,
					SessionHelper.VARIABLE_ENTORN_ACTUAL_V3);
			EntornDto entornActual = null;
			String canviEntorn = request.getParameter(VARIABLE_REQUEST_CANVI_ENTORN);
			List<EntornDto> entorns = entornService.findActiusAmbPermisAcces();
			request.setAttribute("entorns", entorns);
			// Nova implementació
			if (entorns.size() == 0 
					&& !UsuariActualHelper.isAdministrador()) {
				if (request.getServletPath().startsWith("")) {
		            ModelAndView mav = new ModelAndView("entornNoDisponible");
		            throw new ModelAndViewDefiningException(mav);
				}
			} else {
				if (canviEntorn != null) {
					Long entornId = Long.valueOf(canviEntorn);
					for (EntornDto entorn: entorns) {
						if (entorn.getId().longValue() == entornId.longValue()) {
							entornActual = entorn;
							setEntornActual(request, entornActual);
							aplicacioService.updateEntornActual(entornActual.getCodi());
							break;
						}
					}
				} else {
					if (entornSessio == null) {
						UsuariPreferenciesDto prefs = aplicacioService.getUsuariPreferencies();
						if (prefs != null) {
							Date now = new Date();
							// si existeix entorn actual i el valor es va modificar fa menys de 8 hores
							if(prefs.getCurrentEntornCodi() != null &&
							   prefs.getCurrentEntornData() != null &&
							   TimeUnit.HOURS.convert(now.getTime() - prefs.getCurrentEntornData().getTime(), TimeUnit.MILLISECONDS) < 8
							   ) {
								for (EntornDto entorn: entorns) {
									if (entorn.getCodi() != null && entorn.getCodi().equals(prefs.getCurrentEntornCodi())) {
										entornActual = entorn;
										setEntornActual(request, entornActual);
										break;
									}
								}
								if (entornActual == null) {
									entornActual = entorns.get(0);
									setEntornActual(request, entornActual);
								}
							} else if (prefs.getDefaultEntornCodi() != null) {
								for (EntornDto entorn: entorns) {
									if (entorn.getCodi() != null && entorn.getCodi().equals(prefs.getDefaultEntornCodi())) {
										entornActual = entorn;
										setEntornActual(request, entornActual);
										break;
									}
								}
								if (entornActual == null && !entorns.isEmpty()) {
									entornActual = entorns.get(0);
									setEntornActual(request, entornActual);
								}
							} else if ( !entorns.isEmpty()) {
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
						} else if (!entorns.isEmpty()) {
							entornActual = entorns.get(0);
							setEntornActual(request, entornActual);
						}

						// Actualitzam l'entorn actual a base de dades
						if(entornActual != null)
							aplicacioService.updateEntornActual(entornActual.getCodi());
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
			aplicacioService.clearExpedient();
			if (entornActual != null) {

				// Indica si hi ha alertes no llegides
				// ELIMINAR DE LA INTERFÍCIE 26
				if (!request.getRequestURI().contains("")) {
//					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					int alertesNoLlegides = 0; //alertaService.countActivesAmbEntornIUsuari(entornActual.getId(), auth.getName(), AlertaService.ALERTAS_NO_LLEGIDES);
					request.setAttribute(VARIABLE_REQUEST_ALERTES_NOLLEGIDES, alertesNoLlegides > 0);
				}
				/////////////////////////////////

				// Refresca el tipus d'expedient actual
				@SuppressWarnings("unchecked")
				List<ExpedientTipusDto> accessibles = (List<ExpedientTipusDto>)SessionHelper.getAttribute(
						request,
						SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES);
				String canviExpedientTipus = request.getParameter(VARIABLE_REQUEST_CANVI_EXPTIP);
				if (canviExpedientTipus != null) {
					if (canviExpedientTipus.length() > 0) {
						Long expedientTipusId = Long.valueOf(canviExpedientTipus);
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
		aplicacioService.clearExpedient();
	}



	private void setEntornActual(
			HttpServletRequest request,
			EntornDto entorn) {
		// Emmagatzema l'entorn actual dins la sessió de l'usuari
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_ENTORN_ACTUAL_V3,
				entorn);
		EntornDto ent = new EntornDto();
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
				!expedientsTipusAmbPermisDisseny.isEmpty());
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
				expedientTipusService.findAmbEntornPermisConsultar(entorn.getId()));
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ADMIN,
				expedientTipusService.findAmbEntornPermisAdmin(entorn.getId()));
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_EXPTIP_ACCESSIBLES_ANOTACIONS,
				expedientTipusService.findAmbEntornPermisAnotacio(entorn.getId()));

		// Actualitza si hi ha expedients per iniciar
		List<ExpedientTipusDto> tipusCrear = expedientTipusService.findAmbEntornPermisCrear(
				entorn.getId());

		List<ExpedientTipusDto> tipusAltaCsv = expedientTipusService.findAmbEntornPermisExecucioScript(
				entorn.getId());
		SessionHelper.setAttribute(
				request,
				SessionHelper.VARIABLE_HIHA_TRAMITS_INICIABLES,
				(tipusCrear.size() > 0 || tipusAltaCsv.size() > 0));


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
				uri.contains(root + "/webjars/") ||
				uri.contains("/datatable") ||
				uri.contains("/selection"))
			return true;
		return false;
	}

}

