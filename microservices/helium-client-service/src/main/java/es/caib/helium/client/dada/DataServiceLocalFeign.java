package es.caib.helium.client.dada;

import java.util.List;
import java.util.Objects;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.dada.model.PagedList;
import es.caib.helium.client.dada.model.ValidList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile(value = {"!spring-cloud & !compose"})
public class DataServiceLocalFeign implements DataService {

	private final DataServiceLocalFeignClient dataServiceFeignClient;
	
	private final String missatgeLog = "Cridant Data Service - ";
	
	@Override
    public PagedList<Expedient> consultaResultats(
			Integer entornId, 
			Integer expedientTipusId, 
			Integer page, 
			Integer size, 
			Consulta consulta) {
    	
	 	log.debug(missatgeLog + " Consulta paginada - entornId: " + entornId 
	 			+ " - expedientTipusId: " + expedientTipusId + " page: " + page + " size: " + size 
	 			+ " consulta: " + consulta.toString());
	 	
	 	var responseEntity = dataServiceFeignClient.consultaResultatsPaginats(entornId, expedientTipusId, page, size, consulta);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
    }
 
	@Override
	public List<Expedient> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestBody Consulta consulta) { 
    	
		log.debug(missatgeLog + " Consulta paginada - entornId: " + entornId 
	 			+ " - expedientTipusId: " + expedientTipusId 
	 			+ " consulta: " + consulta.toString());
	 	
	 	var responseEntity = dataServiceFeignClient.consultaResultatsLlistat(entornId, expedientTipusId, consulta);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
    }

	@Override
	public Expedient getExpedient(Long expedientId) {
		
		log.debug(missatgeLog + "expedientId: " + expedientId);
		var responseEntity = dataServiceFeignClient.getExpedient(expedientId);
		var expedient = Objects.requireNonNull(responseEntity.getBody());
		return expedient;
	}

	@Override
	public void crearExpedient(Expedient expedient) {

		log.debug(missatgeLog + " creant expedient: " + expedient.toString());
		dataServiceFeignClient.createExpedient(expedient);
	}
	
	@Override
    public void crearExpedients(List<Expedient> expedients) {

		log.debug(missatgeLog + " creant expedients: " + expedients.toString());
		var valid = new ValidList<Expedient>();
		valid.setList(expedients);
		dataServiceFeignClient.createExpedients(valid);
    }
	
	@Override
    public Long getExpedientIdByProcessInstanceId(String processInstanceId) {
        log.debug(missatgeLog + "ProcessInstanceId: " + processInstanceId);
        var responseEntity = dataServiceFeignClient.getExpedientIdByProcessInstanceId(processInstanceId);
        return Objects.requireNonNull(responseEntity.getBody());
    }


}
