package net.conselldemallorca.helium.jbpm3.helper;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class AlertesHelper {

    @Value("${es.caib.helium.jbpm.bridge.service.host}")
    String bridgeAdress;

    @Autowired
    RestTemplate restTemplate;

    public void alertaCrear(
            Alerta alerta) {
        restTemplate.postForLocation(getAlertesBridgeAddress(), alerta);

    }

    public void alertaEsborrar(Long taskInstanceId) {
        restTemplate.delete(getAlertesBridgeAddress() + "/{taskInstanceId}", taskInstanceId);
    }

    private String getAlertesBridgeAddress() {
        return bridgeAdress + "/alertes";
//        return PropertiesHelper.getInstance().getProperty("es.caib.helium.jbpm.bridge.service.host");
    }


    @Data @Builder
    public static class Alerta {
        private Long entornId;
        private Long expedientId;
        private Date data;
        private String usuariCodi;
        private String text;
    }

}
