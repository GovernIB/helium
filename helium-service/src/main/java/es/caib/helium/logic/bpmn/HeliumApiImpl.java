package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.repository.ExpedientRepository;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityNotFoundException;

/**
 * Implementació de HeliumApi per a passar com a argument a l'execució de handlers BPMN.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public class HeliumApiImpl implements HeliumApi {

	private final Long expedientId;
	private final ExpedientRepository expedientRepository;

	@Override
	public void expedientComentariModificar(String comentari) {
		Expedient expedient = expedientRepository.findById(expedientId).
			orElseThrow(() -> new EntityNotFoundException("Expedient (id=" + expedientId + ")"));
		expedient.setComentari(comentari);
	}

}
