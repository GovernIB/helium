package es.caib.helium.base.service.clients;

import es.caib.helium.camunda.model.Dada;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Profile(value = {"!spring-cloud & !compose"})
@Slf4j
@ConfigurationProperties(prefix = "es.caib.helium", ignoreUnknownFields = true)
@Component
public class DataServiceRestTemplateImpl implements HeliumDataService {

    private final RestTemplate restTemplate;

    private String heliumDataServiceHost;

    public void setHeliumDataServiceHost(String heliumDataServiceHost) {
        this.heliumDataServiceHost = heliumDataServiceHost;
    }

    public DataServiceRestTemplateImpl(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${es.caib.helium.dada-service-user}") String dadaUser,
            @Value("${es.caib.helium.dada-service-password}")String dadaPassword) {
        this.restTemplate = restTemplateBuilder
                .basicAuthentication(dadaUser, dadaPassword)
                .build();
    }

    @Override
    public Long getExpedientIdByProcessInstanceId(String processInstanceId) {
        log.debug("Cridant Data Service - ProcessInstanceId: " + processInstanceId);

        ResponseEntity<Dada> responseEntity = restTemplate
                .exchange(heliumDataServiceHost + HeliumDataServiceFeignClient.DADA_PATH, HttpMethod.GET,
                        null,
                        Dada.class,
                        processInstanceId,
                        "expedientId");

        if (responseEntity.getBody() != null) {
            return responseEntity.getBody().getExpedientId();
        }

        return null;
    }
}
