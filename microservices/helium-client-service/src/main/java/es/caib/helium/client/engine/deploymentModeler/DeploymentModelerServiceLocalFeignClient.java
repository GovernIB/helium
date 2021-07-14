package es.caib.helium.client.engine.deploymentModeler;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(contextId = "engine-deployment-modeler-client", name = EngineMsApiPath.NOM_SERVEI, url = "${es.caib.helium.engine.url:localhost:8083}", configuration = DeploymentModelerFeignClientConfig.class)
public interface DeploymentModelerServiceLocalFeignClient extends DeploymentModelerFeignClient {

}
