/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaCompleteDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * EJB per a AdminService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AdminServiceBean implements AdminService {

	@Autowired
	AdminService delegate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public String getMetrics() {
		return delegate.getMetrics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void metricsEmailResponsables() {
		delegate.metricsEmailResponsables();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioDto> monitorIntegracioFindAll() {
		return delegate.monitorIntegracioFindAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracio(
			String integracioCodi) {
		return delegate.monitorIntegracioFindAccionsByIntegracio(integracioCodi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<DominiDto> monitorDominiFindByEntorn(
			Long entornId) {
		return delegate.monitorDominiFindByEntorn(entornId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public List<IntegracioAccioDto> monitorDominiFindAccionsByDomini(
			Long dominiId) {
		return delegate.monitorDominiFindAccionsByDomini(dominiId);
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
		delegate.monitorAddAccio(integracioCodi, descripcio, tipus, estat, tempsResposta, errorDescripcio, throwable, parametres);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByFamilia(
			String familia,
			boolean ambDetall) {
		return delegate.mesuraTemporalFindByFamilia(familia, ambDetall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByTipusExpedient() {
		return delegate.mesuraTemporalFindByTipusExpedient();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> mesuraTemporalFindByTasca() {
		return delegate.mesuraTemporalFindByTasca();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Set<String> mesuraTemporalFindFamiliesAll() {
		return delegate.mesuraTemporalFindFamiliesAll();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		delegate.mesuraTemporalIniciar(nom, familia, tipusExpedient, tasca, detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String nom, String familia, String tipusExpedient) {
		delegate.mesuraTemporalIniciar(nom, familia, tipusExpedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalIniciar(String clau, String familia) {
		delegate.mesuraTemporalIniciar(clau, familia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		delegate.mesuraTemporalCalcular(nom, familia, tipusExpedient, tasca, detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String nom, String familia, String tipusExpedient) {
		delegate.mesuraTemporalCalcular(nom, familia, tipusExpedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraTemporalCalcular(String clau, String familia) {
		delegate.mesuraTemporalCalcular(clau, familia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean mesuraTemporalIsActive() {
		return delegate.mesuraTemporalIsActive();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isStatisticActive() {
		return delegate.isStatisticActive();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> getHibernateStatistics(String familia, boolean exportar) {
		return delegate.getHibernateStatistics(familia, exportar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaCompleteDto> getTasquesCompletar() {
		return delegate.getTasquesCompletar();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updatePerfil(UsuariPreferenciesDto preferencies) {
		delegate.updatePerfil(preferencies);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updatePersona(PersonaDto persona) {
		delegate.updatePersona(persona);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ReassignacioDto> llistaReassignacions() {
		return delegate.llistaReassignacions();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void createReassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId) {
		delegate.createReassignacio(usuariOrigen, usuariDesti, dataInici, dataFi, dataCancelacio, tipusExpedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updateReassignacio(Long id, String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId) {
		delegate.updateReassignacio(id, usuariOrigen, usuariDesti, dataInici, dataFi, dataCancelacio, tipusExpedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteReassignacio(Long id) {
		delegate.deleteReassignacio(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto findReassignacioById(Long id) {
		return delegate.findReassignacioById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<IntegracioDto> monitorIntegracioFindAllEntronActual() {
		return delegate.monitorIntegracioFindAllEntronActual();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracioEntornActual(String integracioCodi) {
		return delegate.monitorIntegracioFindAccionsByIntegracioEntornActual(integracioCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaCompleteDto> getTasquesCompletarAdminEntorn() {
		return delegate.getTasquesCompletarAdminEntorn();
	}

}
