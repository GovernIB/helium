package es.caib.helium.client.engine.deployment;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-deployment-client", name = EngineMsApiPath.NOM_SERVEI, configuration = DeploymentFeignClientConfig.class)
public interface DeploymentServiceCloudFeignClient extends DeploymentFeignClient {

}
