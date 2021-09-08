package es.caib.helium.client.integracio.notificacio;

import es.caib.helium.client.integracio.IntegracioMsApiPath;
import es.caib.helium.client.integracio.notificacio.config.NotificacioClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = IntegracioMsApiPath.NOM_SERVEI + NotificacioFeignClient.SERVEI, configuration = NotificacioClientConfig.class)
public interface NotificacioCloudFeignClient extends NotificacioFeignClient {

}
