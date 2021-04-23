package es.caib.helium.dada.repository;

import java.util.List;

import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Consulta;

public interface ExpedientRepositoryCustom {
	
	List<Expedient> findByFiltres(Consulta consulta);

	void esborrarExpedientCascade(Long expedient);

	void esborrarExpedientsCascade(List<Long> expedients);
}
