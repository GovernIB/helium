/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.client.integracio.persones.model.Persona;
import es.caib.helium.logic.intf.dto.DocumentConversioDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.UsuariPreferenciesDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.util.GlobalProperties;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB per a AplicacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AplicacioService extends AbstractService<es.caib.helium.logic.intf.service.AplicacioService> implements es.caib.helium.logic.intf.service.AplicacioService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UsuariPreferenciesDto getUsuariPreferencies() {
		return getDelegateService().getUsuariPreferencies();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PersonaDto findPersonaAmbCodi(String codi) {
		return getDelegateService().findPersonaAmbCodi(codi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Persona> findPersonaLikeNomSencer(String text) {
		return getDelegateService().findPersonaLikeNomSencer(text);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<PersonaDto> findPersonaAll() throws SistemaExternException {
        return getDelegateService().findPersonaAll();
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PersonaDto findPersonaActual() throws NoTrobatException, SistemaExternException {
		return getDelegateService().findPersonaActual();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public GlobalProperties getGlobalProperties() {
		return getDelegateService().getGlobalProperties();
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public DocumentConversioDto convertFile(String arxiuNom, byte[] arxiuContingut, String extensioSortida) throws Exception {
        return getDelegateService().convertFile(arxiuNom, arxiuContingut, extensioSortida);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public String getArxiuMediaType(String nomFitxer) {
        return getDelegateService().getArxiuMediaType(nomFitxer);
    }
}
