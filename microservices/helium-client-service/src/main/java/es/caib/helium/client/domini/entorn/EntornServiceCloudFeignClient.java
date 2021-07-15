package es.caib.helium.client.domini.entorn;

import es.caib.helium.client.domini.DominiMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "domini-entorn-client",
        name = DominiMsApiPath.NOM_SERVEI,
        configuration = EntornFeignClientConfig.class)
public interface EntornServiceCloudFeignClient extends EntornFeignClient {

}
