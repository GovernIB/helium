package es.caib.helium.client.expedient.tasca;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.tasca.model.ConsultaTascaDades;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface TascaClientService {

	public PagedList<TascaDto> findTasquesAmbFiltrePaginatV1(
			ConsultaTascaDades consultaTascaDades);

	public PagedList<String> findTasquesIdsAmbFiltrePaginatV1(
			ConsultaTascaDades consultaTascaDades);

	public void createTascaV1(TascaDto tascaDto);
	
	public void updateTascaV1(Long tascaId, TascaDto tascaDto);
	
	public void patchTascaV1(Long tascaId, JsonNode tascaJson);
	
	public void deleteTascaV1(Long tascaId); 
	
	public TascaDto getTascaV1(Long tascaId); 
	
	public List<String> getResponsablesV1(Long tascaId);
	
	public void setResponsablesV1(Long tascaId, List<String> responsables);

	public void deleteResponsablesV1(Long tascaId);
}
