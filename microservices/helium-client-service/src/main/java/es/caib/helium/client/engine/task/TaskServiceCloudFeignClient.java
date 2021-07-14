package es.caib.helium.client.engine.task;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-task-client", name = EngineMsApiPath.NOM_SERVEI, configuration = TaskFeignClientConfig.class)
public interface TaskServiceCloudFeignClient extends TaskFeignClient {

}
