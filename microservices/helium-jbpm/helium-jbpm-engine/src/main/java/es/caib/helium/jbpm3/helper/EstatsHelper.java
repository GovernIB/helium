package es.caib.helium.jbpm3.helper;

import es.caib.helium.api.dto.EnumeracioValorDto;
import es.caib.helium.api.dto.EstatDto;
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
public class EstatsHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;

    public EstatDto findEstatAmbEntornIExpedientTipusICodi(
            Long entornId,
            String expedientTipusCodi,
            String estatCodi) {
        return restTemplate.getForObject(
                getEstatsBridgeAddress() + "/{entornId}?expedientTipusCodi={expedientTipusCodi}&estatCodi={estatCodi}",
                EstatDto.class,
                entornId,
                expedientTipusCodi,
                estatCodi);
    }

    private String getEstatsBridgeAddress() {
        return bridgeAdress + "/estats";
    }

}
