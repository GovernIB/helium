package es.caib.helium.ejb;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.DominiDto;
import es.caib.helium.commons.dto.IntegracioAccioDto;
import es.caib.helium.commons.dto.IntegracioAccioEstatEnumDto;
import es.caib.helium.commons.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.commons.dto.IntegracioDto;
import es.caib.helium.commons.dto.IntegracioParametreDto;
import es.caib.helium.commons.dto.MesuraTemporalDto;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.commons.dto.ReassignacioDto;
import es.caib.helium.commons.dto.TascaCompleteDto;
import es.caib.helium.commons.dto.UsuariPreferenciesDto;
import es.caib.helium.logic.intf.service.AdminService;

/**
 * EJB per a AdminService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AdminServiceBean extends AbstractServiceEjb<AdminService> implements AdminService {

	@Delegate
	AdminService delegateService;

	protected void setDelegateService(AdminService delegateService) {
		this.delegateService = delegateService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public String getMetrics() {
		return delegateService.getMetrics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void metricsEmailResponsables() {
		delegateService.metricsEmailResponsables();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioDto> monitorIntegracioFindAll() {
		return delegateService.monitorIntegracioFindAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracio(
			String integracioCodi) {
		return delegateService.monitorIntegracioFindAccionsByIntegracio(integracioCodi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<DominiDto> monitorDominiFindByEntorn(
			Long entornId) {
		return delegateService.monitorDominiFindByEntorn(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioAccioDto> monitorDominiFindAccionsByDomini(
			Long dominiId) {
		return delegateService.monitorDominiFindAccionsByDomini(dominiId);
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
		delegateService.monitorAddAccio(integracioCodi, descripcio, tipus, estat, tempsResposta, errorDescripcio, throwable, parametres);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByFamilia(
			String familia,
			boolean ambDetall) {
		return delegateService.mesuraTemporalFindByFamilia(familia, ambDetall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByTipusExpedient() {
		return delegateService.mesuraTemporalFindByTipusExpedient();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByTasca() {
		return delegateService.mesuraTemporalFindByTasca();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Set<String> mesuraTemporalFindFamiliesAll() {
		return delegateService.mesuraTemporalFindFamiliesAll();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		delegateService.mesuraTemporalIniciar(nom, familia, tipusExpedient, tasca, detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String nom, String familia, String tipusExpedient) {
		delegateService.mesuraTemporalIniciar(nom, familia, tipusExpedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String clau, String familia) {
		delegateService.mesuraTemporalIniciar(clau, familia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		delegateService.mesuraTemporalCalcular(nom, familia, tipusExpedient, tasca, detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String nom, String familia, String tipusExpedient) {
		delegateService.mesuraTemporalCalcular(nom, familia, tipusExpedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String clau, String familia) {
		delegateService.mesuraTemporalCalcular(clau, familia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean mesuraTemporalIsActive() {
		return delegateService.mesuraTemporalIsActive();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isStatisticActive() {
		return delegateService.isStatisticActive();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> getHibernateStatistics(String familia, boolean exportar) {
		return delegateService.getHibernateStatistics(familia, exportar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaCompleteDto> getTasquesCompletar() {
		return delegateService.getTasquesCompletar();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updatePerfil(UsuariPreferenciesDto preferencies) {
		delegateService.updatePerfil(preferencies);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updatePersona(PersonaDto persona) {
		delegateService.updatePersona(persona);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ReassignacioDto> llistaReassignacions() {
		return delegateService.llistaReassignacions();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void createReassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId) {
		delegateService.createReassignacio(usuariOrigen, usuariDesti, dataInici, dataFi, dataCancelacio, tipusExpedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updateReassignacio(Long id, String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId) {
		delegateService.updateReassignacio(id, usuariOrigen, usuariDesti, dataInici, dataFi, dataCancelacio, tipusExpedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteReassignacio(Long id) {
		delegateService.deleteReassignacio(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto findReassignacioById(Long id) {
		return delegateService.findReassignacioById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<IntegracioDto> monitorIntegracioFindAllEntronActual() {
		return delegateService.monitorIntegracioFindAllEntronActual();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracioEntornsAdmin(String integracioCodi) {
		return delegateService.monitorIntegracioFindAccionsByIntegracioEntornsAdmin(integracioCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaCompleteDto> getTasquesCompletarAdminEntorn() {
		return delegateService.getTasquesCompletarAdminEntorn();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public Long canviarCodiUsusari(String codiActual, String codiNou) throws Exception {
		return delegateService.canviarCodiUsusari(codiActual, codiNou);
	}

	@Override
	public PersonaDto findPersonaByCodi(String usuariCodi) {
		// TODO Auto-generated method stub
		return null;
	}

}
