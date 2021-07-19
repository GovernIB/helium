package es.caib.helium.dada.repository;

import es.caib.helium.dada.exception.DadaException;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.Expedient;

import java.util.List;

public interface ExpedientRepositoryCustom {
	
	List<Expedient> findByFiltres(Consulta consulta);

	Long esborrarExpedientCascade(Long expedient) throws DadaException;

	Long esborrarExpedientsCascade(List<Long> expedients) throws DadaException;

	List<Expedient> getExpedientIdProcesPrincipalIdByExpedientIds(List<Long> ids);

	Expedient getExpedientIdProcesPrincipalIdByExpedientId(Long id) throws DadaException;
}
