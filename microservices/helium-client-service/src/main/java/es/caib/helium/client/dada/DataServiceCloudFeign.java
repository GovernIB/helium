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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile(value = {"spring-cloud", "compose"})
public class DataServiceCloudFeign implements DataService {

    private final DataServiceCloudFeignClient dadaServiceFeignClient;
    
    @Override
    public PagedList<Expedient> consultaResultats(
			Integer entornId, 
			Integer expedientTipusId, 
			Integer page, 
			Integer size, 
			Consulta consulta) {
    	
    	return null;
    }
    
    @Override
	public List<Expedient> consultaResultatsLlistat(
			@RequestParam("entornId") Integer entornId,
			@RequestParam("expedientTipusId") Integer expedientTipusId, 
			@RequestBody Consulta consulta) { 
    	
    	return null;
    }

    @Override
    public Long getExpedientIdByProcessInstanceId(String processInstanceId) {

    	log.debug("Cridant Data Service - ProcessInstanceId: " + processInstanceId);
        var responseEntity = dadaServiceFeignClient.getExpedientIdByProcessInstanceId(processInstanceId);
        var expedientId = Objects.requireNonNull(responseEntity.getBody());
        return expedientId != null ? expedientId : null;
    }

    @Override
    public void crearExpedient(Expedient expedient) {
    	// TODO Auto-generated method stub
    	
    }

    @Override
    public void crearExpedients(List<Expedient> expedient) {
    	// TODO Auto-generated method stub
    	
    }

	@Override
	public Expedient getExpedient(Long expedientId) {
		// TODO Auto-generated method stub
		return null;
	}

}
