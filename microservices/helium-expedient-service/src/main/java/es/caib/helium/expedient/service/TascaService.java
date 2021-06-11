package es.caib.helium.expedient.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import es.caib.helium.expedient.model.ResponsableDto;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.ms.model.PagedList;

/** Servei per a la consulta i manteniment de la informació  a nivell de
 * tasques pel llistat de consultes de tasques.
 */
public interface TascaService {

    TascaDto createTasca(TascaDto expedient);

    TascaDto updateTasca(
            Long expedientId,
            TascaDto expedient);

    void delete(
            Long expedientId);

    TascaDto getById(
            Long expedientId);

    PagedList<TascaDto> listTasques(
    		Long entornId,
            Long expedientTipusId,
			String usuariAssignat,
			String nom,
			String titol,
			Long expedientId,
			String expedientTitol,
			String expedientNumero,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			boolean mostrarAssignadesUsuari,
			boolean mostrarAssignadesGrup,
			boolean nomesPendents,
            String filtreRsql,
            final Pageable pageable,
            final Sort sort);

	List<ResponsableDto> getResponsables(
			Long tascaId);

	List<ResponsableDto> setResponsables(
			Long tascaId, 
			List<String> responsables);

	void deleteResponsables(
			Long tascaId);
}
