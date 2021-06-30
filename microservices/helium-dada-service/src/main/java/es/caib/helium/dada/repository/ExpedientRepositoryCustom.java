package es.caib.helium.dada.repository;

import java.util.List;

import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.exception.DadaException;
import es.caib.helium.dada.model.Consulta;

public interface ExpedientRepositoryCustom {
	
	List<Expedient> findByFiltres(Consulta consulta);

	Long esborrarExpedientCascade(Long expedient) throws DadaException;

	Long esborrarExpedientsCascade(List<Long> expedients) throws DadaException;
}
