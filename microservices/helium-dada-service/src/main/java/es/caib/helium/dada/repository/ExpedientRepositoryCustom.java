package es.caib.helium.dada.repository;

import java.util.List;
import java.util.Optional;

import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Consulta;

public interface ExpedientRepositoryCustom {
	
	List<Expedient> findByFiltres(Consulta consulta);

	Long esborrarExpedientCascade(Long expedient);

	Long esborrarExpedientsCascade(List<Long> expedients);
}
