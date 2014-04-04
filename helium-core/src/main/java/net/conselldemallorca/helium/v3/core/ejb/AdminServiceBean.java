/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaCompleteDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AdminServiceBean implements AdminService {
	@Autowired
	AdminService delegate;

	/**
	 * Retorna una llista d'entorns per als quals l'usuari actual te permis de lectura.
	 * 
	 * @return El llistat d'entorns.
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornDto> findEntornAmbPermisReadUsuariActual() {
		return delegate.findEntornAmbPermisReadUsuariActual();
	}

	/**
	 * Retorna les preferències de l'usuari actual.
	 * 
	 * @return Les preferències.
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UsuariPreferenciesDto getPreferenciesUsuariActual() {
		return delegate.getPreferenciesUsuariActual();
	}

	/**
	 * Obté les mesures temporals per a evaluar el rendiment de l'aplicació al efectuar determinades tasques.
	 * 
	 * @return El llistat de mesures.
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> findMesuresTemporals(String familia, boolean ambDetall) {
		return delegate.findMesuresTemporals(familia, ambDetall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> findMesuresTemporalsTipusExpedient() {
		return delegate.findMesuresTemporalsTipusExpedient();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<MesuraTemporalDto> findMesuresTemporalsTasca() {
		return delegate.findMesuresTemporalsTasca();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Set<String> findFamiliesMesuresTemporals() {
		return delegate.findFamiliesMesuresTemporals();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object getMesuresTemporalsHelper() {
		return delegate.getMesuresTemporalsHelper();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraIniciar(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		delegate.mesuraIniciar(nom, familia, tipusExpedient, tasca, detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraIniciar(String nom, String familia, String tipusExpedient) {
		delegate.mesuraIniciar(nom, familia, tipusExpedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraIniciar(String clau, String familia) {
		delegate.mesuraIniciar(clau, familia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraCalcular(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		delegate.mesuraCalcular(nom, familia, tipusExpedient, tasca, detall);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraCalcular(String nom, String familia, String tipusExpedient) {
		delegate.mesuraCalcular(nom, familia, tipusExpedient);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraCalcular(String clau, String familia) {
		delegate.mesuraCalcular(clau, familia);
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
}
