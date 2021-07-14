package es.caib.helium.client.engine.variableInstance;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-variable-instance-client", name = EngineMsApiPath.NOM_SERVEI, configuration = VariableInstanceFeignClientConfig.class)
public interface VariableInstanceServiceCloudFeignClient extends VariableInstanceFeignClient {

}
