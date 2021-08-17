package es.caib.helium.client.expedient.proces;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.proces.model.ConsultaProcesDades;
import es.caib.helium.client.expedient.proces.model.ProcesDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface ProcesClientService {

	public PagedList<ProcesDto> findProcessAmbFiltrePaginatV1(
			ConsultaProcesDades consultaProcesDades);

	public PagedList<String> findProcessIdsAmbFiltrePaginatV1(
			ConsultaProcesDades consultaProcesDades);

	public void createProcesV1(ProcesDto procesDto);
	
	public void updateProcesV1(String procesId, ProcesDto procesDto);
	
	public void patchProcesV1(String procesId, JsonNode procesJson);
	
	public void deleteProcesV1(String procesId);
	
	public ProcesDto getProcesV1(String procesId);
	
	public List<ProcesDto> getLlistatProcessos(String procesId);

	/** Consulta únicament l'identificador de l'expedent per a una instància */
	public Long getProcesExpedientId(String processInstanceId);
}
