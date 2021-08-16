package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.DominiRespostaFilaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DominisHelper {

    private static final String DOMINI_SERVICE_NAME = "helium-domini-service";
    private static final String DOMINI_API_PATH = "/api/v1/dominis";

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ServiceDiscoveryHelper serviceDiscoveryHelper;

    public List<DominiRespostaFilaDto> dominiConsultar(
            String processInstanceId,
            String dominiCodi,
            String dominiId,
            Map<String, Object> parametres) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getDominisBridgeAddress() + "/{dominiId}/resultats")
                .queryParam("processInstanceId", processInstanceId)
                .queryParam("dominiCodi", dominiCodi);
        if (parametres != null) {
            for (Map.Entry<String, Object> entry : parametres.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
        }

        DominiRespostaFilaDto[] dominiRespostaFiles = restTemplate.getForObject(
                builder.buildAndExpand(dominiId).toUriString(),
                DominiRespostaFilaDto[].class);

        if (dominiRespostaFiles != null) {
            return Arrays.asList(dominiRespostaFiles);
        } else {
            return new ArrayList<DominiRespostaFilaDto>();
        }
    }

    private String getDominisBridgeAddress() {
        return serviceDiscoveryHelper.getAddress(DOMINI_SERVICE_NAME) + DOMINI_API_PATH;
//        return bridgeAdress + "/dominis";
    }



}
