package es.caib.helium.expedient.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import es.caib.helium.expedient.model.ProcesDto;
import es.caib.helium.ms.model.PagedList;

/** Servei per mantenir la informació de les instàncies de procés
 * i de l'arbre de processos.
 */
public interface ProcesService {

    ProcesDto createProces(ProcesDto expedient);

    ProcesDto updateProces(
            String procesId,
            ProcesDto proces);

    void delete(
    		String procesId);

    ProcesDto getById(
    		String procesId);
    
    PagedList<ProcesDto> listProcessos(
    		String processDefinitionId,
    		String procesArrelId,
            String filtreRsql,
            final Pageable pageable,
            final Sort sort);
}
