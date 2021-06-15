package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.DominiRespostaFilaDto;
import lombok.Data;
import lombok.Getter;
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
//    private static final String DADA_API_PATH = "/api/v1/dades";
    private static final String DADA_API_PATH = "/api/v1/expedients"; // TODO: expedients???? --> dades

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

    @Data
    public class Dada {

        private String id;
        private String codi;
        private Tipus tipus;
        private boolean multiple;
        private List<Valor> valor;

        private Long expedientId;
        private Long procesId;
    }

    @Getter
    public enum Tipus {

        Long("Long"),
        String("String"),
        Date("Date"),
        Float("Float"),
        Termini("Termini"),
        Preu("Preu"),
        Integer("Integer"),
        Boolean("Boolean"),
        Registre("Registre");

        private String valor;

        private Tipus(String valor) {
            this.valor = valor;
        }
    }

    public abstract class Valor {}

    @Data
    public class ValorSimple extends Valor {
        private String valor;
        private String valorText;
    }

    @Data
    public class ValorRegistre extends Valor {
        private List<Dada> camps;
    }

}
