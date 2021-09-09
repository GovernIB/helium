package es.caib.helium.expedient.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.ms.model.PagedList;

/** Servei per a la consulta i manteniment de la informació  a nivell de
 * tasques pel llistat de consultes de tasques.
 */
public interface TascaService {

    TascaDto createTasca(TascaDto expedient);

    TascaDto updateTasca(
            String tascaId,
            TascaDto tasca);

    void delete(
    		String tascaId);

    TascaDto getById(
    		String tascaId);

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

	List<String> getResponsables(
			String tascaId);

	void setResponsables(
			String tascaId, 
			List<String> responsables);

	void deleteResponsables(
			String tascaId);
	
	List<String> getGrups(
			String tascaId);

	void setGrups(
			String tascaId, 
			List<String> responsables);

	void deleteGrups(
			String tascaId);
	
}
