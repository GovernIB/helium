package es.caib.helium.client.engine.timer;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "engine-timer-controller-client", name = EngineMsApiPath.NOM_SERVEI, configuration = TimerFeignClientConfig.class)
public interface TimerServiceCloudFeignClient extends TimerFeignClient {

}
