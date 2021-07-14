package es.caib.helium.client.engine.deploymentModeler;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-deployment-modeler-client", name = EngineMsApiPath.NOM_SERVEI, configuration = DeploymentModelerFeignClientConfig.class)
public interface DeploymentModelerServiceCloudFeignClient extends DeploymentModelerFeignClient {

}
