/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.UsuariPreferenciesDto;
import es.caib.helium.logic.intf.service.AplicacioService;

/**
 * EJB per a AplicacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AplicacioServiceBean implements AplicacioService {

	@Autowired
	AplicacioService delegate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UsuariPreferenciesDto getUsuariPreferencies() {
		return delegate.getUsuariPreferencies();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PersonaDto findPersonaAmbCodi(String codi) {
		return delegate.findPersonaAmbCodi(codi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return delegate.findPersonaLikeNomSencer(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PersonaDto findPersonaActual() throws NoTrobatException, SistemaExternException {
		return delegate.findPersonaActual();
	}

}
