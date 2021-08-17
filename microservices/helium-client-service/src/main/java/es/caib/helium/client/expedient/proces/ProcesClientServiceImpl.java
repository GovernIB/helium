package es.caib.helium.client.expedient.proces;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.proces.model.ConsultaProcesDades;
import es.caib.helium.client.expedient.proces.model.ProcesDto;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProcesClientServiceImpl implements ProcesClientService {
	
	private final String MISSATGE_LOG = "Cridant Proces Service - Proces - ";
	
	private final ProcesFeignClient procesClient;

	@Override
	public PagedList<ProcesDto> findProcessAmbFiltrePaginatV1(ConsultaProcesDades consultaProcesDades) {
		
		log.debug(MISSATGE_LOG + " llista paginada de processos segons el filtre = " +  consultaProcesDades);
		var responseEntity = procesClient.findProcessAmbFiltrePaginatV1(consultaProcesDades);
		if (HttpStatus.NO_CONTENT.equals(responseEntity.getStatusCode()) ) {
			PagedList<ProcesDto> pagedList = PagedList.emptyPage();
			return pagedList;
		}
		var resultat = responseEntity.getBody();
		return resultat;
	}
	
	@Override
	public PagedList<String> findProcessIdsAmbFiltrePaginatV1(ConsultaProcesDades consultaProcesDades) {
		
		log.debug(MISSATGE_LOG + " llista paginada d'identificadors de processos segons el filtre " + consultaProcesDades);
		var responseEntity = procesClient.findProcessIdsAmbFiltrePaginatV1(consultaProcesDades);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public void createProcesV1(ProcesDto procesDto) {

		log.debug(MISSATGE_LOG + " creant proces " + procesDto.getId() + " per l'expedient " + procesDto.getExpedientId());
		procesClient.createProcesV1(procesDto);
	}

	@Override
	public void updateProcesV1(String procesId, ProcesDto procesDto) {

		log.debug(MISSATGE_LOG + " update proces " + procesDto.getId() + " de l'expedient " + procesDto.getExpedientId());
		procesClient.updateProcesV1(procesId, procesDto);
	}

	@Override
	public void patchProcesV1(String procesId, JsonNode procesJson) {

		log.debug(MISSATGE_LOG + " patch proces amb id " + procesId);
		procesClient.patchProcesV1(procesId, procesJson);
	}

	@Override
	public void deleteProcesV1(String procesId) {
		
		log.debug(MISSATGE_LOG + " delete proces amb id " + procesId);
		procesClient.deleteProcesV1(procesId);
	}

	@Override
	public ProcesDto getProcesV1(String procesId) {
		
		log.debug(MISSATGE_LOG + " obtinguent proces amb id " + procesId);
		var responseEntity = procesClient.getProcesV1(procesId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
	
	@Override
	public List<ProcesDto> getLlistatProcessos(String procesId) {
		log.debug(MISSATGE_LOG + " obtenint la llista de procesos pel proces amb id " + procesId);
		var responseEntity = procesClient.getLlistat(procesId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
		
	}

	@Override
	public Long getProcesExpedientId(String procesId) {
		log.debug(MISSATGE_LOG + " obtenint expedientId a partir de l'identificador del proces amb id " + procesId);
		var responseEntity = procesClient.getExpedientId(procesId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
}
