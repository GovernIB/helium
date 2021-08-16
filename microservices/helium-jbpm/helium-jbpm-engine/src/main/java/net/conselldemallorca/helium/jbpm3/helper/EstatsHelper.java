package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.EstatDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EstatsHelper {

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
        return RestClientHelper.getBridgeAddress() + "/estats";
    }

}
