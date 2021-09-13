package net.conselldemallorca.helium.jbpm3.helper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import net.conselldemallorca.helium.api.dto.ProcesDto;

@Component
public class ProcesHelper {

    @Autowired
    RestTemplate restTemplate;

	public void crear(ProcesDto proces) {
        restTemplate.postForLocation(getProcesBridgeAddress(), proces);
	}

	public void finalitzar(Long processInstanceId, Date end) {
        restTemplate.postForLocation(
        			getProcesBridgeAddress() + "/" + processInstanceId + "/finaltizar", 
        			end);
	}

    private String getProcesBridgeAddress() {
        return RestClientHelper.getBridgeAddress() + "/processos";
    }



}
