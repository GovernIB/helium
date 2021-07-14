package es.caib.helium.client.engine.execution;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(contextId = "engine-execution-client", name = EngineMsApiPath.NOM_SERVEI, url = "${es.caib.helium.engine.url:localhost:8083}", configuration = ExecutionFeignClientConfig.class)
public interface ExecutionServiceLocalFeignClient extends ExecutionFeignClient {

}
