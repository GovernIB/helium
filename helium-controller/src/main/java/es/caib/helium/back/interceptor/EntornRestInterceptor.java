/**
 * 
 */
package es.caib.helium.back.interceptor;

import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.EntornService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Interceptor per guardar a la sessió les dades de l'entorn
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class EntornRestInterceptor implements AsyncHandlerInterceptor {

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
		if (request.getUserPrincipal() != null) {
			String entornRest = request.getHeader("R-entorn");
			if (entornRest != null) {
				List<EntornDto> entorns = entornService.findActiusAmbPermisAcces();
				if (entorns != null ) {
					Long entornId = Long.valueOf(entornRest);
					for (EntornDto entorn : entorns) {
						if (entorn.getId().longValue() == entornId.longValue()) {
							setEntornActual(request, entorn);
							break;
						}
					}
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
//		entornService.setEntornActualId(null);
//		expedientService.clearExpedientIniciant();
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

}
