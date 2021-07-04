package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.ReproDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.ValidacioException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

@Stateless
public class ReproService extends AbstractService<es.caib.helium.logic.intf.service.ReproService> implements es.caib.helium.logic.intf.service.ReproService {
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId, String tascaCodi) {
		return getDelegateService().findReprosByUsuariTipusExpedient(expedientTipusId, tascaCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto findById(Long id) {
		return getDelegateService().findById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto create(Long expedientTipusId, String nom, Map<String, Object> valors) {
		return getDelegateService().create(expedientTipusId, nom, valors);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String deleteById(Long id) {
		return getDelegateService().deleteById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String,Object> findValorsById(Long id) {
		return getDelegateService().findValorsById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto createTasca(Long expedientTipusId, Long tascaId, String nom, Map<String, Object> valors)
			throws NoTrobatException, ValidacioException {
		return getDelegateService().createTasca(expedientTipusId, tascaId, nom, valors);
	}
}
