package es.caib.helium.jbpm3.helper;

import es.caib.helium.api.dto.DominiRespostaFilaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DadesHelper {

    private static final String DADA_SERVICE_NAME = "helium-dada-service";
    private static final String DADA_API_PATH = "/api/v1/dades";

//    @Value("${es.caib.helium.jbpm.bridge.service.host}")
//    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ServiceDiscoveryHelper serviceDiscoveryHelper;

    public List<DominiRespostaFilaDto> dominiConsultar(
            String processInstanceId,
            String dominiCodi,
            String dominiId,
            Map<String, Object> parametres) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getDadesBridgeAddress() + "/{dominiId}/resultats")
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

    private String getDadesBridgeAddress() {
        return serviceDiscoveryHelper.getAddress(DADA_SERVICE_NAME) + DADA_API_PATH;
//        return bridgeAdress + "/dominis";
    }



}
