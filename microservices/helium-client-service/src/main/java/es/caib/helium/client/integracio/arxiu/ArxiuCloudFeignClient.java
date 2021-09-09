package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.IntegracioMsApiPath;
import es.caib.helium.client.integracio.arxiu.config.ArxiuClientConfig;
import es.caib.helium.client.integracio.notificacio.NotificacioFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = IntegracioMsApiPath.NOM_SERVEI + ArxiuFeignClient.SERVEI, configuration = ArxiuClientConfig.class)
public interface ArxiuCloudFeignClient extends NotificacioFeignClient {

}
