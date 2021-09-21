package es.caib.helium.expedient.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.ms.model.PagedList;

/** Servei per a la consulta i manteniment de la informació a nivell d'expedients
 * 
 *
 */
public interface ExpedientService {

    void importarExpedients(List<Expedient> expedients) throws Exception ;

    ExpedientDto createExpedient(ExpedientDto expedient);

    ExpedientDto updateExpedient(
            Long expedientId,
            ExpedientDto expedient);

    void delete(
            Long expedientId);

    ExpedientDto getById(
            Long expedientId);

    PagedList<ExpedientDto> listExpedients(
    		String usuariCodi,
    		List<String> grups,
    		Long entornId,
            Long expedientTipusId,
            Collection<Long> tipusIdPermesos,
            String titol,
            String numero,
            Date dataInici1,
            Date dataInici2,
            Date dataFi1,
            Date dataFi2,
            boolean nomesIniciats,
            boolean nomesFinalitzats,
            Long estatId,
            boolean nomesTasquesPersonals,
            boolean nomesTasquesMeves,
            boolean nomesTasquesGrup,
            boolean nomesAlertes,
            boolean nomesErrors,
            boolean mostrarAnulats,
            boolean mostrarNomesAnulats,
            String filtreRsql,
            final Pageable pageable,
            final Sort sort);

    /** Consulta els diferents usuaris assignats a les tasques d'un expedient
     * 
     * @param expedientId
     * @return
     */
	List<String> getParticipants(Long expedientId);
}
