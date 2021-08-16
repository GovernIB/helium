package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.api.dto.InteressatDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InteressatsHelper {

    @Autowired
    RestTemplate restTemplate;

    public void crear(InteressatDto interessat) {
        restTemplate.postForLocation(getInteressatsBridgeAddress(), interessat);
    }

    public void modificar(InteressatDto interessat) {
        restTemplate.put(getInteressatsBridgeAddress(), interessat);
    }

    public void eliminar(String interessatCodi, Long expedientId) {
        restTemplate.delete(getInteressatsBridgeAddress() + "/{interessatCodi}/expedient/{expedientId}",
                interessatCodi,
                expedientId);
    }

    private String getInteressatsBridgeAddress() {
        return RestClientHelper.getBridgeAddress() + "/interessats";
    }

}
