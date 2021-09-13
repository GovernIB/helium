package es.caib.helium.client.expedient.tasca;

import java.util.Date;
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
	
	public void updateTascaV1(String tascaId, TascaDto tascaDto);
	
	public void patchTascaV1(String tascaId, JsonNode tascaJson);
	
	public void deleteTascaV1(String tascaId); 
	
	public TascaDto getTascaV1(String tascaId); 
	
	public void setUsuariAssignat(String tascaId, String codiUsuari);

	public List<String> getResponsablesV1(String tascaId);
	
	public void setResponsablesV1(String tascaId, List<String> responsables);

	public void deleteResponsablesV1(String tascaId);
	
	public List<String> getGrupsV1(String tascaId);
	
	public void setGrupsV1(String tascaId, List<String> grups);

	public void deleteGrupsV1(String tascaId);

	public void setCancelada(String tascaId, boolean cancelada);

	public void setSuspesa(String tascaId, boolean suspesa);
	
	public void setErrorFinalitzacio(String tascaId, String errorFinalitzacio);

	public void marcarFinalitzar(String tascaId, Date marcadaFinalitzar);
}
