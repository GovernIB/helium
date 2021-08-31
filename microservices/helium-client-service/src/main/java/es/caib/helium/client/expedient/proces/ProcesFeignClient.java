package es.caib.helium.client.expedient.proces;

import com.fasterxml.jackson.databind.JsonNode;
import es.caib.helium.client.expedient.proces.model.ConsultaProcesDades;
import es.caib.helium.client.expedient.proces.model.ProcesDto;
import es.caib.helium.client.model.PagedList;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface ProcesFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = ProcesApiPath.FIND_PROCESSOS_AMB_FILTRE_PAGINAT)
	public ResponseEntity<PagedList<ProcesDto>> findProcessAmbFiltrePaginatV1(
			@SpringQueryMap ConsultaProcesDades consultaProcesDades);

	@RequestMapping(method = RequestMethod.GET, value = ProcesApiPath.FIND_PROCESSOS_IDS_AMB_FILTRE_PAGINAT)
	public ResponseEntity<PagedList<String>> findProcessIdsAmbFiltrePaginatV1(
			@SpringQueryMap ConsultaProcesDades consultaProcesDades);

//	@CircuitBreaker(name = DadaMsApiPath.NOM_SERVEI, fallbackMethod = "createProcesFallback")
//	@Retry(name = DadaMsApiPath.NOM_SERVEI)
	@RequestMapping(method = RequestMethod.POST, value = ProcesApiPath.CREATE_PROCES)
	public ResponseEntity<Void> createProcesV1(@RequestBody ProcesDto procesDto);
	
	@RequestMapping(method = RequestMethod.PUT, value = ProcesApiPath.PUT_PROCES)
	public ResponseEntity<Void> updateProcesV1(
            @PathVariable("procesId") String procesId,
            @RequestBody ProcesDto procesDto);
	
	@RequestMapping(method = RequestMethod.PATCH, value = ProcesApiPath.PATCH_PROCES)
	public ResponseEntity<Void> patchProcesV1(
          @PathVariable("procesId") String procesId,
          @RequestBody JsonNode procesJson);
	
	@RequestMapping(method = RequestMethod.DELETE, value = ProcesApiPath.DELETE_PROCES)
    public ResponseEntity<Void> deleteProcesV1(
            @PathVariable("procesId") String procesId);
	
	@RequestMapping(method = RequestMethod.GET, value = ProcesApiPath.GET_PROCES)
	public ResponseEntity<ProcesDto> getProcesV1(
            @PathVariable("procesId") String procesId);
	
	@RequestMapping(method = RequestMethod.GET, value = ProcesApiPath.GET_PROCES_LLISTAT)
	public ResponseEntity<List<ProcesDto>> getLlistat(
            @PathVariable("procesId") String procesId);

	@RequestMapping(method = RequestMethod.GET, value = ProcesApiPath.GET_PROCES_EXPEDIENT_ID)
	public ResponseEntity<Long> getExpedientId(
            @PathVariable("procesId") String procesId);


//	default ResponseEntity<Void> createProcesFallback(ProcesDto procesDto) {
//		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//	}
}
