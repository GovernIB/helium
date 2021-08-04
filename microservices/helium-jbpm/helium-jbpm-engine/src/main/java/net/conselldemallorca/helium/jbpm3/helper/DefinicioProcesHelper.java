package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.DefinicioProcesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DefinicioProcesHelper {

    @Autowired
    RestTemplate restTemplate;


    public Integer getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
            String jbpmKey,
            String processInstanceId) {
        return restTemplate.getForObject(
                getDefinicioProcesBridgeAddress() + "/{processInstanceId}/versio?jbpmKey={jbpmKey}",
                Integer.class,
                processInstanceId,
                jbpmKey);
    }

    public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId) {
        return restTemplate.getForObject(
                getDefinicioProcesBridgeAddress() + "/{processInstanceId}",
                DefinicioProcesDto.class,
                processInstanceId);
    }

    public Long getDefinicioProcesIdPerProcessInstanceId(String processInstanceId) {
        return restTemplate.getForObject(
                getDefinicioProcesBridgeAddress() + "/{processInstanceId}/id",
                Long.class,
                processInstanceId);
    }

    public Long getDefinicioProcesEntornAmbJbpmKeyIVersio(
            String jbpmKey,
            int version) {
        return restTemplate.getForObject(
                getDefinicioProcesBridgeAddress() + "/byJbpmKeyAndVersio/entornId?jbpmKey={jbpmKey}&version={version}",
                Long.class,
                jbpmKey,
                version);
    }

    public Long getDarreraVersioEntornAmbEntornIJbpmKey(
            Long entornId,
            String jbpmKey) {
        return restTemplate.getForObject(
                getDefinicioProcesBridgeAddress() + "/byEntornAndJbpmKey/entornId?entornId={entornId}&jbpmKey={jbpmKey}",
                Long.class,
                entornId,
                jbpmKey);
    }

    public void initializeDefinicionsProces() {
        restTemplate.postForLocation(getDefinicioProcesBridgeAddress() + "/initialize", "null");
    }

    public String getProcessDefinitionIdHeretadaAmbPid(String processInstanceId) {
        return restTemplate.getForObject(
                getDefinicioProcesBridgeAddress() + "/{processInstanceId}/byHerencia/id",
                String.class,
                processInstanceId);
    }


    private String getDefinicioProcesBridgeAddress() {
        return RestClientHelper.getBridgeAddress() + "/definicionsProces";
    }

}
