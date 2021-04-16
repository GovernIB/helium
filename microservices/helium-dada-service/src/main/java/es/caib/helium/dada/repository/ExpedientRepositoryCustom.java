package es.caib.helium.dada.repository;

import java.util.List;
import java.util.Map;

import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Filtre;
import es.caib.helium.dada.model.Ordre;

public interface ExpedientRepositoryCustom {
	
	List<Expedient> findByFiltres(Map<String, Filtre> filtres, Integer entornId, Integer expedientTipusId,
			List<Ordre> ordre, Integer page, Integer size);
}
