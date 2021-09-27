package es.caib.helium.client.integracio.persones;

import es.caib.helium.client.integracio.IntegracioMsApiPath;
import es.caib.helium.client.integracio.persones.config.PersonaClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = IntegracioMsApiPath.NOM_SERVEI + PersonaFeignClient.SERVEI, configuration = PersonaClientConfig.class)
public interface PersonaCloudFeignClient extends PersonaFeignClient {

}
