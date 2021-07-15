package es.caib.helium.client.domini.domini;

import es.caib.helium.client.domini.DominiMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "domini-domini-client",
        name = DominiMsApiPath.NOM_SERVEI,
        configuration = DominiFeignClientConfig.class)
public interface DominiServiceCloudFeignClient extends DominiFeignClient {

}
