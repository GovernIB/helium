/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientConsultaCommand;
import net.conselldemallorca.helium.webapp.v3.command.TascaConsultaCommand;

/**
 * Helper per a gestionar les dades de la sessió d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SessionHelper {

	public static final String VARIABLE_ENTORN_ACTUAL = "entornActual";
	public static final String VARIABLE_ENTORN_ACTUAL_V3 = "entornActualV3";
	public static final String VARIABLE_EXPTIP_ACTUAL = "expedientTipusActual";
	public static final String VARIABLE_PREFERENCIES_USUARI = "preferenciesUsuari";
	public static final String VARIABLE_EXPTIP_ACCESSIBLES = "expedientTipusAccessibles";
	public static final String VARIABLE_EXPTIP_ACCESSIBLES_AMB_CONSULTES_ACTIVES = "expedientTipusAccessiblesAmbConsultesActives";
	public static final String VARIABLE_PERMIS_EXPTIP_DISSENY = "potDissenyarExpedientTipus";
	public static final String VARIABLE_PERMIS_EXPTIP_GESTIO = "potGestionarExpedientTipus";
	public static final String VARIABLE_PERMIS_EXPTIP_REASSIGNAR = "potReassignarExpedientTipus";
	public static final String VARIABLE_PERMIS_ENTORN_READ = "potLlegirEntorn";
	public static final String VARIABLE_PERMIS_ENTORN_DESIGN = "potDissenyarEntorn";
	public static final String VARIABLE_PERMIS_ENTORN_ORGANIZATION = "potOrganitzarEntorn";
	public static final String VARIABLE_PERMIS_ENTORN_ADMINISTRATION = "potAdministrarEntorn";
	public static final String VARIABLE_HIHA_TRAMITS_INICIABLES = "hiHaTramitsPerIniciar";

	public static final String VARIABLE_FILTRE_CONSULTA_GENERAL = "filtreConsultaGeneral";
	public static final String VARIABLE_FILTRE_CONSULTA_TASCA = "filtreConsultaTasca";
	public static final String VARIABLE_FILTRE_INFORME = "filtreInforme";
	public static final String VARIABLE_SELECCIO_CONSULTA_GENERAL = "seleccioConsultaGeneral";
	public static final String VARIABLE_SELECCIO_INFORME = "seleccioInforme";

	public static Object getAttribute(
			HttpServletRequest request,
			String name) {
		return request.getSession().getAttribute(
				name);
	}
	public static void setAttribute(
			HttpServletRequest request,
			String name,
			Object value) {
		request.getSession().setAttribute(
				name,
				value);
	}
	public static void removeAttribute(
			HttpServletRequest request,
			String name) {
		request.getSession().removeAttribute(
				name);
	}

	public static SessionManager getSessionManager(HttpServletRequest request) {
		return new SessionManager(request);
	}

	public static class SessionManager {

		private HttpServletRequest request;
		public SessionManager(HttpServletRequest request) {
			this.request = request;
		}

		public EntornDto getEntornActual() {
			return (EntornDto)getAttribute(
					request,
					VARIABLE_ENTORN_ACTUAL_V3);
		}
		public void setEntornActual(EntornDto entorn) {
			setAttribute(
					request,
					VARIABLE_ENTORN_ACTUAL_V3,
					entorn);
		}
		public ExpedientTipusDto getExpedientTipusActual() {
			return (ExpedientTipusDto)getAttribute(
					request,
					VARIABLE_EXPTIP_ACTUAL);
		}
		public void setExpedientTipusActual(ExpedientTipusDto expedientTipus) {
			setAttribute(
					request,
					VARIABLE_EXPTIP_ACTUAL,
					expedientTipus);
		}
		@SuppressWarnings("unchecked")
		public List<ExpedientTipusDto> getExpedientTipusAccessiblesAmbConsultesActives() {
			return (List<ExpedientTipusDto>)getAttribute(
					request,
					VARIABLE_EXPTIP_ACCESSIBLES_AMB_CONSULTES_ACTIVES);
		}
		public void setExpedientTipusAccessiblesAmbConsultesActives(List<ExpedientTipusDto> expedientsTipus) {
			setAttribute(
					request,
					VARIABLE_EXPTIP_ACCESSIBLES_AMB_CONSULTES_ACTIVES,
					expedientsTipus);
		}
		@SuppressWarnings("unchecked")
		public List<ExpedientTipusDto> getExpedientTipusAccessibles() {
			return (List<ExpedientTipusDto>)getAttribute(
					request,
					VARIABLE_EXPTIP_ACCESSIBLES);
		}
		public void setExpedientTipusAccessibles(List<ExpedientTipusDto> expedientsTipus) {
			setAttribute(
					request,
					VARIABLE_EXPTIP_ACCESSIBLES,
					expedientsTipus);
		}
		public Boolean getPotReassignarExpedientTipus() {
			return (Boolean)getAttribute(
					request,
					VARIABLE_PERMIS_EXPTIP_REASSIGNAR);
		}
		public void setPotReassignarExpedientTipus(Boolean potReassignarExpedientTipus) {
			setAttribute(
					request,
					VARIABLE_PERMIS_EXPTIP_REASSIGNAR,
					potReassignarExpedientTipus);
		}
		public Boolean getPotDissenyarExpedientTipus() {
			return (Boolean)getAttribute(
					request,
					VARIABLE_PERMIS_EXPTIP_DISSENY);
		}
		public void setPotDissenyarExpedientTipus(Boolean potDissenyarExpedientTipus) {
			setAttribute(
					request,
					VARIABLE_PERMIS_EXPTIP_DISSENY,
					potDissenyarExpedientTipus);
		}
		public Boolean getPotGestionarExpedientTipus() {
			return (Boolean)getAttribute(
					request,
					VARIABLE_PERMIS_EXPTIP_GESTIO);
		}
		public void setPotGestionarExpedientTipus(Boolean potGestionarExpedientTipus) {
			setAttribute(
					request,
					VARIABLE_PERMIS_EXPTIP_GESTIO,
					potGestionarExpedientTipus);
		}
		public Boolean getPotLlegirEntorn() {
			return (Boolean)getAttribute(
					request,
					VARIABLE_PERMIS_ENTORN_READ);
		}
		public void setPotLlegirEntorn(Boolean potLlegirEntorn) {
			setAttribute(
					request,
					VARIABLE_PERMIS_ENTORN_READ,
					potLlegirEntorn);
		}
		public Boolean getPotDissenyarEntorn() {
			return (Boolean)getAttribute(
					request,
					VARIABLE_PERMIS_ENTORN_DESIGN);
		}
		public void setPotDissenyarEntorn(Boolean potDissenyarEntorn) {
			setAttribute(
					request,
					VARIABLE_PERMIS_ENTORN_DESIGN,
					potDissenyarEntorn);
		}
		public Boolean getPotOrganitzarEntorn() {
			return (Boolean)getAttribute(
					request,
					VARIABLE_PERMIS_ENTORN_ORGANIZATION);
		}
		public void setPotOrganitzarEntorn(Boolean potOrganitzarEntorn) {
			setAttribute(
					request,
					VARIABLE_PERMIS_ENTORN_ORGANIZATION,
					potOrganitzarEntorn);
		}
		public Boolean getPotAdministrarEntorn() {
			return (Boolean)getAttribute(
					request,
					VARIABLE_PERMIS_ENTORN_ADMINISTRATION);
		}
		public void setPotAdministrarEntorn(Boolean potAdministrarEntorn) {
			setAttribute(
					request,
					VARIABLE_PERMIS_ENTORN_ADMINISTRATION,
					potAdministrarEntorn);
		}
		public Boolean getHiHaTramitsPerIniciar() {
			return (Boolean)getAttribute(
					request,
					VARIABLE_HIHA_TRAMITS_INICIABLES);
		}
		public void setHiHaTramitsPerIniciar(Boolean hiHaTramitsPerIniciar) {
			setAttribute(
					request,
					VARIABLE_HIHA_TRAMITS_INICIABLES,
					hiHaTramitsPerIniciar);
		}
		public ExpedientConsultaCommand getFiltreConsultaGeneral() {
			return (ExpedientConsultaCommand)getAttribute(
					request,
					VARIABLE_FILTRE_CONSULTA_GENERAL);
		}
		public void setFiltreConsultaGeneral(ExpedientConsultaCommand filtreConsultaGeneral) {
			setAttribute(
					request,
					VARIABLE_FILTRE_CONSULTA_GENERAL,
					filtreConsultaGeneral);
		}
		public TascaConsultaCommand getFiltreConsultaTasca() {
			return (TascaConsultaCommand)getAttribute(
					request,
					VARIABLE_FILTRE_CONSULTA_TASCA);
		}
		public void setFiltreConsultaTasca(TascaConsultaCommand filtreConsultaTasca) {
			setAttribute(
					request,
					VARIABLE_FILTRE_CONSULTA_TASCA,
					filtreConsultaTasca);
		}
		@SuppressWarnings("unchecked")
		public Set<Long> getSeleccioConsultaGeneral() {
			return (Set<Long>)getAttribute(
					request,
					VARIABLE_SELECCIO_CONSULTA_GENERAL);
		}
		public Object getFiltreInforme(Long expedientTipusId, Long consultaId) {
			return (Object)getAttribute(
					request,
					VARIABLE_FILTRE_INFORME + expedientTipusId + "-" + consultaId);
		}
		public void setFiltreInforme(Object filtreInforme, Long expedientTipusId, Long consultaId) {
			setAttribute(
					request,
					VARIABLE_FILTRE_INFORME + expedientTipusId + "-" + consultaId,
					filtreInforme);
		}
		public void removeFiltreInforme(Long expedientTipusId, Long consultaId) {
			removeAttribute(
					request,
					VARIABLE_FILTRE_INFORME + expedientTipusId + "-" + consultaId);
		}
		public void setSeleccioConsultaGeneral(Set<Long> seleccioConsultaGeneral) {
			setAttribute(
					request,
					VARIABLE_SELECCIO_CONSULTA_GENERAL,
					seleccioConsultaGeneral);
		}
		@SuppressWarnings("unchecked")
		public Set<Long> getSeleccioInforme() {
			return (Set<Long>)getAttribute(
					request,
					VARIABLE_SELECCIO_INFORME);
		}
		public void setSeleccioInforme(Set<Long> seleccioInforme) {
			setAttribute(
					request,
					VARIABLE_SELECCIO_INFORME,
					seleccioInforme);
		}
		public UsuariPreferenciesDto getPreferenciesUsuari() {
			return (UsuariPreferenciesDto)getAttribute(
					request,
					VARIABLE_PREFERENCIES_USUARI);
		}
		public void setPreferenciesUsuari(UsuariPreferenciesDto preferencies) {
			setAttribute(
					request,
					VARIABLE_PREFERENCIES_USUARI,
					preferencies);
		}
	}
}
