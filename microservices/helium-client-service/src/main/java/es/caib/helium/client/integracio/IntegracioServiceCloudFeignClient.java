package es.caib.helium.client.integracio;

import es.caib.helium.client.integracio.arxiu.ArxiuFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = IntegracioMsApiPath.NOM_SERVEI, configuration = IntegracioFeignClientConfig.class)
public interface IntegracioServiceCloudFeignClient extends ArxiuFeignClient {

}
