package es.caib.helium.base.service.clients;

import es.caib.helium.camunda.model.Dada;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Profile(value = {"spring-cloud", "compose"})
public class DataServiceFeign implements HeliumDataService {

    private final HeliumDataServiceFeignClient heliumDataServiceFeignClient;


    @Override
    public Long getExpedientIdByProcessInstanceId(String processInstanceId) {

        log.debug("Cridant Data Service - ProcessInstanceId: " + processInstanceId);
        ResponseEntity<Dada> responseEntity = heliumDataServiceFeignClient.getDadaByProcessInstanceId(processInstanceId, "expedientId");
        Dada dada = Objects.requireNonNull(responseEntity.getBody());
        if (dada != null) {
            return dada.getExpedientId();
        }
        return null;
    }
}
