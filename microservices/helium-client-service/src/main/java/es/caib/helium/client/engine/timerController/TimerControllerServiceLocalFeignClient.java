package es.caib.helium.client.engine.timerController;

import es.caib.helium.client.engine.EngineMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(contextId = "engine-timer-controller-client", name = EngineMsApiPath.NOM_SERVEI, url = "${es.caib.helium.engine.url:localhost:8083}", configuration = TimerControllerFeignClientConfig.class)
public interface TimerControllerServiceLocalFeignClient extends TimerControllerFeignClient {

}
