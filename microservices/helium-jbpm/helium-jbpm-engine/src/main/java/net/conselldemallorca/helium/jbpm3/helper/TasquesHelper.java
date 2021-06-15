package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.CampTascaDto;
import net.conselldemallorca.helium.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.api.dto.TascaDadaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TasquesHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;


    public List<CampTascaDto> findCampsPerTaskInstance(
            String processInstanceId,
            String processDefinitionId,
            String taskName) {
        CampTascaDto[] camps = restTemplate.getForObject(
                getTasquesBridgeAddress() + "/{processInstanceId}/task/{taskName}/camps?processDefinitionId={processDefinitionId}",
                CampTascaDto[].class,
                processInstanceId,
                taskName,
                processDefinitionId);

        if (camps != null) {
            return Arrays.asList(camps);
        } else {
            return new ArrayList<CampTascaDto>();
        }
    }

    public List<DocumentTascaDto> findDocumentsPerTaskInstance(
            String processInstanceId,
            String processDefinitionId,
            String taskName) {
        DocumentTascaDto[] documents = restTemplate.getForObject(
                getTasquesBridgeAddress() + "/{processInstanceId}/task/{taskName}/documents?processDefinitionId={processDefinitionId}",
                DocumentTascaDto[].class,
                processInstanceId,
                taskName,
                processDefinitionId);

        if (documents != null) {
            return Arrays.asList(documents);
        } else {
            return new ArrayList<DocumentTascaDto>();
        }
    }

    public String getDadaPerTaskInstance(
            String processInstanceId,
            String taskInstanceId,
            String varCodi) {
        return restTemplate.getForObject(
                getTasquesBridgeAddress() + "/{processInstanceId}/dades/{varCodi}?taskInstanceId={taskInstanceId}",
                String.class,
                processInstanceId,
                varCodi,
                taskInstanceId);
    }

    public CampTascaDto getCampTascaPerInstanciaTasca(
            String taskName,
            String processDefinitionId,
            String processInstanceId,
            String varCodi) {
        return restTemplate.getForObject(getTasquesBridgeAddress() + "/{processInstanceId}/campTasca/{varCodi}?taskName={taskName}&processDefinitionId={processDefinitionId}",
                CampTascaDto.class,
                processInstanceId,
                varCodi,
                taskName,
                processDefinitionId);
    }

//    public boolean isTascaEnSegonPla(Long taskId) {
//        return restTemplate.getForObject(
//                getTasquesBridgeAddress() + "/{taskId}/isSegonPla",
//                Boolean.class,
//                taskId);
//    }

    public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message) {
        restTemplate.postForLocation(
                getTasquesBridgeAddress() + "/{taskId}/missatge",
                message,
                taskId);
    }

    public void setErrorTascaSegonPla(Long taskId, String error) {
        restTemplate.postForLocation(
                getTasquesBridgeAddress() + "/{taskId}/error",
                error,
                taskId);
    }

    private String getTasquesBridgeAddress() {
        return bridgeAdress + "/tasques";
    }

}
