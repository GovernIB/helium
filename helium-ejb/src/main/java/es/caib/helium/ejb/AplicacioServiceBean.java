/**
 *
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

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
public class AplicacioServiceBean extends AbstractServiceEjb<AplicacioService> implements AplicacioService {

	@Delegate
	AplicacioService delegateService;

	protected void setDelegateService(AplicacioService delegateService) {
		this.delegateService = delegateService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PermitAll
	public PersonaDto findPersonaAmbCodi(String codi) {
		return delegateService.findPersonaAmbCodi(codi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public UsuariPreferenciesDto getUsuariPreferencies() {
		return delegateService.getUsuariPreferencies();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return delegateService.findPersonaLikeNomSencer(text);
	}

	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<PersonaDto> findPersonaLikeCodiOrNomSencer(String text) throws SistemaExternException {
		return delegateService.findPersonaLikeCodiOrNomSencer(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public PersonaDto findPersonaActual() throws NoTrobatException, SistemaExternException {
		return delegateService.findPersonaActual();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public PersonaDto findPersonaCarrecAmbCodi(String codi) {
		return delegateService.findPersonaCarrecAmbCodi(codi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public void excepcioSave(String peticio, String params, Throwable exception) {
		delegateService.excepcioSave(peticio, params, exception);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return delegateService.excepcioFindOne(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<ExcepcioLogDto> excepcioFindAll() {
		return delegateService.excepcioFindAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public void updateEntornActual(String entorn) throws NoTrobatException {
		delegateService.updateEntornActual(entorn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({ "HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public void clearExpedient() {
		delegateService.clearExpedient();
	}

}
