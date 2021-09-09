package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.IntegracioFeignClientConfig;
import es.caib.helium.client.integracio.IntegracioMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;


@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(name = IntegracioMsApiPath.NOM_SERVEI + ArxiuFeignClient.SERVEI, url = IntegracioMsApiPath.URL_LOCAL, configuration = IntegracioFeignClientConfig.class)
public interface ArxiuLocalFeignClient extends ArxiuFeignClient {

}
