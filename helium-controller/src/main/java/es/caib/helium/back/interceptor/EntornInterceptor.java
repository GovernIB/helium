/**
 * 
 */
package es.caib.helium.back.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.UsuariPreferenciesDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.EntornService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import lombok.RequiredArgsConstructor;

/**
 * Interceptor per guardar a la sessió les dades de l'entorn
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class EntornInterceptor implements AsyncHandlerInterceptor {

	public static final String VARIABLE_REQUEST_CANVI_ENTORN = "entornCanviarAmbId";
	public static final String VARIABLE_REQUEST_CANVI_EXPTIP = "expedientTipusCanviarAmbId";

	private final EntornService entornService;
	private final AplicacioService aplicacioService;
	private final ExpedientService expedientService;
	private final ExpedientTipusService expedientTipusService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null && !isRequestResource(request)) {
			EntornDto entornSessio = (EntornDto) SessionHelper.getAttribute(
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
					Long entornId = Long.valueOf(canviEntorn);
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
													entornService.getEntornActualId(),
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
						if (entornActual != null && entornService.getEntornActualId() == null)
							entornService.setEntornActualId(entornActual.getId());
					}
				}
			}
			// Inicialitza la variable ThreadLocal de l'expedient que s'està iniciant
			expedientService.clearExpedientIniciant();
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
						SessionHelper.SessionManager sessionManager = SessionHelper.getSessionManager(request);
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

	@Override
	public void afterCompletion(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			Exception ex) {
		entornService.setEntornActualId(null);
		expedientService.clearExpedientIniciant();
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
		entornService.setEntornActualId(entorn.getId());
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
				tipusCrear.size() > 0 || tipusAltaCsv.size() > 0);
		
		
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
		return uri.contains(root + "/js/") ||
				uri.contains(root + "/css/") ||
				uri.contains(root + "/fonts/") ||
				uri.contains(root + "/extensions/") ||
				uri.contains(root + "/webjars/") ||
				uri.contains("/datatable") ||
				uri.contains("/error") ||
				uri.contains("/selection");
	}

}
