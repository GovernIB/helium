/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * EJB per a AdminService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AdminService extends AbstractService<es.caib.helium.logic.intf.service.AdminService> implements es.caib.helium.logic.intf.service.AdminService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public String getMetrics() {
		return getDelegateService().getMetrics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void metricsEmailResponsables() {
		getDelegateService().metricsEmailResponsables();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioDto> monitorIntegracioFindAll() {
		return getDelegateService().monitorIntegracioFindAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracio(
			String integracioCodi) {
		return getDelegateService().monitorIntegracioFindAccionsByIntegracio(integracioCodi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<DominiDto> monitorDominiFindByEntorn(
			Long entornId) {
		return getDelegateService().monitorDominiFindByEntorn(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioAccioDto> monitorDominiFindAccionsByDomini(
			Long dominiId) {
		return getDelegateService().monitorDominiFindAccionsByDomini(dominiId);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void monitorAddAccio(
			String integracioCodi,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			IntegracioAccioEstatEnumDto estat,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable,
			List<IntegracioParametreDto> parametres) {
		getDelegateService().monitorAddAccio(integracioCodi, descripcio, tipus, estat, tempsResposta, errorDescripcio, throwable, parametres);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByFamilia(
			String familia,
			boolean ambDetall) {
		return getDelegateService().mesuraTemporalFindByFamilia(familia, ambDetall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByTipusExpedient() {
		return getDelegateService().mesuraTemporalFindByTipusExpedient();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByTasca() {
		return getDelegateService().mesuraTemporalFindByTasca();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Set<String> mesuraTemporalFindFamiliesAll() {
		return getDelegateService().mesuraTemporalFindFamiliesAll();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		getDelegateService().mesuraTemporalIniciar(nom, familia, tipusExpedient, tasca, detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String nom, String familia, String tipusExpedient) {
		getDelegateService().mesuraTemporalIniciar(nom, familia, tipusExpedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String clau, String familia) {
		getDelegateService().mesuraTemporalIniciar(clau, familia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		getDelegateService().mesuraTemporalCalcular(nom, familia, tipusExpedient, tasca, detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String nom, String familia, String tipusExpedient) {
		getDelegateService().mesuraTemporalCalcular(nom, familia, tipusExpedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String clau, String familia) {
		getDelegateService().mesuraTemporalCalcular(clau, familia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean mesuraTemporalIsActive() {
		return getDelegateService().mesuraTemporalIsActive();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isStatisticActive() {
		return getDelegateService().isStatisticActive();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> getHibernateStatistics(String familia, boolean exportar) {
		return getDelegateService().getHibernateStatistics(familia, exportar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaCompleteDto> getTasquesCompletar() {
		return getDelegateService().getTasquesCompletar();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updatePerfil(UsuariPreferenciesDto preferencies) {
		getDelegateService().updatePerfil(preferencies);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updatePersona(PersonaDto persona) {
		getDelegateService().updatePersona(persona);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ReassignacioDto> llistaReassignacions() {
		return getDelegateService().llistaReassignacions();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void createReassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId) {
		getDelegateService().createReassignacio(usuariOrigen, usuariDesti, dataInici, dataFi, dataCancelacio, tipusExpedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updateReassignacio(Long id, String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId) {
		getDelegateService().updateReassignacio(id, usuariOrigen, usuariDesti, dataInici, dataFi, dataCancelacio, tipusExpedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteReassignacio(Long id) {
		getDelegateService().deleteReassignacio(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto findReassignacioById(Long id) {
		return getDelegateService().findReassignacioById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<IntegracioDto> monitorIntegracioFindAllEntronActual() {
		return getDelegateService().monitorIntegracioFindAllEntronActual();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracioEntornActual(String integracioCodi) {
		return getDelegateService().monitorIntegracioFindAccionsByIntegracioEntornActual(integracioCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaCompleteDto> getTasquesCompletarAdminEntorn() {
		return getDelegateService().getTasquesCompletarAdminEntorn();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void setIdiomaPref(String usuari, String idioma) {
		getDelegateService().setIdiomaPref(usuari, idioma);
	}

}
