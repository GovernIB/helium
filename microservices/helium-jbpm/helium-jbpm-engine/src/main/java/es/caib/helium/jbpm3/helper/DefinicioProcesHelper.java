package es.caib.helium.jbpm3.helper;

import es.caib.helium.api.dto.ArxiuDto;
import es.caib.helium.api.dto.DefinicioProcesDto;
import es.caib.helium.api.dto.DocumentDissenyDto;
import es.caib.helium.api.dto.DocumentDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class DefinicioProcesHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

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
        return bridgeAdress + "/definicionsProces";
    }

}
