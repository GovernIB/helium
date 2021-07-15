package es.caib.helium.client.engine.processDefiniton;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-process-definition-client", name = EngineMsApiPath.NOM_SERVEI, configuration = ProcessDefinitionFeignClientConfig.class)
public interface ProcessDefinitionServiceCloudFeignClient extends ProcessDefinitionFeignClient {

}
