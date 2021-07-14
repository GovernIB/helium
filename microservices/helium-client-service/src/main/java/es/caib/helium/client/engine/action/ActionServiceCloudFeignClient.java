package es.caib.helium.client.engine.action;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-action-client", name = EngineMsApiPath.NOM_SERVEI, configuration = ActionFeignClientConfig.class)
public interface ActionServiceCloudFeignClient extends ActionFeginClient {

}
