package es.caib.helium.client.engine.taskVariable;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-task-variable-client", name = EngineMsApiPath.NOM_SERVEI, configuration = TaskVariableFeignClientConfig.class)
public interface TaskVariableServiceCloudFeignClient extends TaskVariableFeignClient {

}
