/**
 * 
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.ExcepcioLogDto;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.commons.dto.UsuariPreferenciesDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.logic.intf.service.AplicacioService;

/**
 * EJB per a AplicacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AplicacioServiceBean implements AplicacioService {

	@Autowired
	AplicacioService delegate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public UsuariPreferenciesDto getUsuariPreferencies() {
		return delegate.getUsuariPreferencies();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public PersonaDto findPersonaAmbCodi(String codi) {
		return delegate.findPersonaAmbCodi(codi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return delegate.findPersonaLikeNomSencer(text);
	}

	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<PersonaDto> findPersonaLikeCodiOrNomSencer(String text) throws SistemaExternException {
		return delegate.findPersonaLikeCodiOrNomSencer(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public PersonaDto findPersonaActual() throws NoTrobatException, SistemaExternException {
		return delegate.findPersonaActual();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public PersonaDto findPersonaCarrecAmbCodi(String codi) {
		return delegate.findPersonaCarrecAmbCodi(codi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public void excepcioSave(String peticio, String params, Throwable exception) {
		delegate.excepcioSave(peticio, params, exception);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return delegate.excepcioFindOne(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<ExcepcioLogDto> excepcioFindAll() {
		return delegate.excepcioFindAll();
	}

	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public void updateEntornActual(String entorn) throws NoTrobatException {
		delegate.updateEntornActual(entorn);
	}
}
