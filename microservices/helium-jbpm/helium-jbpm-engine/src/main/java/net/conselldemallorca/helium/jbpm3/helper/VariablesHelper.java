package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.CampTipusIgnored;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class VariablesHelper {

    @Autowired
    RestTemplate restTemplate;


    public CampTipusIgnored getCampAndIgnored(
            String processDefinitionId,
            Long expedientId,
            String varCodi) {
        return restTemplate.getForObject(
          getVariablesBridgeAddress() + "/{varCodi}/campAndIgnored?processDefinitionId={processDefinitionId}&expedientId={expedientId}",
                CampTipusIgnored.class,
                varCodi,
                processDefinitionId,
                expedientId
        );
    }


    private String getVariablesBridgeAddress() {
        return RestClientHelper.getBridgeAddress() + "/variables";
    }

}
