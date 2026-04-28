package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.persistence.repository.ExpedientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factoria per a la creació de les instàncies que implementen HeliumApi.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class HeliumApiFactory {

	private final ExpedientRepository expedientRepository;

	public HeliumApi createInstance(Long expedientId) {
		return new HeliumApiImpl(expedientId, expedientRepository);
	}

}
