package es.caib.helium.client.integracio;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = IntegracioMsApiPath.NOM_SERVEI, configuration = IntegracioFeignClientConfig.class)
public interface IntegracioServiceCloudFeignClient {

}
