package es.caib.helium.jbpm3.helper;

import es.caib.helium.api.dto.EnumeracioValorDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class EnumeracionsHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;

    public void enumeracioSetValor(
            Enumeracio alerta) {
        restTemplate.postForLocation(getEnumetacionsBridgeAddress() + "/{enumeracioCodi}/enumeracio", alerta, alerta.getEnumeracioCodi());
    }

    public List<EnumeracioValorDto> enumeracioConsultar(String processInstanceId, String enumeracioCodi) {
        EnumeracioValorDto[] enumeracioValors = restTemplate.getForObject(
                getEnumetacionsBridgeAddress() + "/{enumeracioCodi}?processInstanceId={processInstanceId}",
                EnumeracioValorDto[].class,
                enumeracioCodi,
                processInstanceId);
        if (enumeracioValors != null) {
            return Arrays.asList(enumeracioValors);
        } else {
            return new ArrayList<EnumeracioValorDto>();
        }
    }

    private String getEnumetacionsBridgeAddress() {
        return bridgeAdress + "/enumeracions";
    }


    @Data @Builder
    public static class Enumeracio {
        private String processInstanceId;
        private String enumeracioCodi;
        private String codi;
        private String valor;
    }

}
