package es.caib.helium.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.domini.DominiFeignClientConfig;
import es.caib.helium.client.domini.DominiMsApiPath;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(name = DominiMsApiPath.NOM_SERVEI, url = DominiMsApiPath.URL_LOCAL, configuration = DominiFeignClientConfig.class)
public interface DominiServiceLocalFeignClient {

}
