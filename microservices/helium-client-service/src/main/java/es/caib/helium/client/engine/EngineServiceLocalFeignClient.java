package es.caib.helium.client.engine;

import es.caib.helium.client.engine.action.ActionFeginClient;
import es.caib.helium.client.engine.deployment.DeploymentFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(name = EngineMsApiPath.NOM_SERVEI, url = "${es.caib.helium.engine.url:localhost:8083}", configuration = EngineFeignClientConfig.class)
public interface EngineServiceLocalFeignClient extends DeploymentFeignClient, ActionFeginClient {

}
