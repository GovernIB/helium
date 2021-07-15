package es.caib.helium.client.engine.areaCarrec;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-area-carrec-client", name = EngineMsApiPath.NOM_SERVEI, configuration = AreaCarrecFeignClientConfig.class)
public interface AreaCarrecServiceCloudFeignClient extends AreaCarrecFeignClient {

}
