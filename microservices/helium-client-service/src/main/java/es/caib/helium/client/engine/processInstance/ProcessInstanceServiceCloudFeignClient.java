package es.caib.helium.client.engine.processInstance;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-process-instance-client", name = EngineMsApiPath.NOM_SERVEI, configuration = ProcessInstanceFeignClientConfig.class)
public interface ProcessInstanceServiceCloudFeignClient extends ProcessInstanceFeignClient {

}
