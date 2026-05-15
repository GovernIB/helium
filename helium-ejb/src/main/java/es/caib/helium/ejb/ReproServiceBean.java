package es.caib.helium.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.ReproDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.logic.intf.service.ReproService;

@Stateless
public class ReproServiceBean extends AbstractServiceEjb<ReproService> implements ReproService {

	@Delegate
	ReproService delegateService;

	protected void setDelegateService(ReproService delegateService) {
		this.delegateService = delegateService;
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId, String tascaCodi) {
		return delegateService.findReprosByUsuariTipusExpedient(expedientTipusId, tascaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto create(Long expedientTipusId, String nom, Map<String, Object> valors) {
		return delegateService.create(expedientTipusId, nom, valors);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String deleteById(Long id) {
		return delegateService.deleteById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String,Object> findValorsById(Long id) {
		return delegateService.findValorsById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto createTasca(Long expedientTipusId, Long tascaId, String nom, Map<String, Object> valors)
			throws NoTrobatException, ValidacioException {
		return delegateService.createTasca(expedientTipusId, tascaId, nom, valors);
	}

}
