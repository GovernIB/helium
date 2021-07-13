package es.caib.helium.client.engine;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = EngineMsApiPath.NOM_SERVEI, configuration = EngineFeignClientConfig.class)
public interface EngineServiceCloudFeignClient {

}
