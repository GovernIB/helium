package es.caib.helium.client.expedient.tasca;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface TascaClientService {

	public PagedList<TascaDto> findTasquesAmbFiltrePaginatV1(
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
            String filtre,
            final Pageable pageable,
            final Sort sort);
	
	public void createTascaV1(TascaDto tascaDto);
	
	public void updateTascaV1(Long tascaId, TascaDto tascaDto);
	
	public void patchTascaV1(Long tascaId, JsonNode tascaJson);
	
	public void deleteTascaV1(Long tascaId); 
	
	public TascaDto getTascaV1(Long tascaId); 
	
	public List<String> getResponsablesV1(Long tascaId);
	
	public void setResponsablesV1(Long tascaId, List<String> responsables);

	public void deleteResponsablesV1(Long tascaId);
}
