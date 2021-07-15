package es.caib.helium.client.domini.expedientTipus;

import es.caib.helium.client.domini.DominiMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(contextId = "domini-expedient-tipus-client",
        name = DominiMsApiPath.NOM_SERVEI, url = "${es.caib.helium.expedient.url:localhost:8082}",
        configuration = ExpedientTipusFeignClientConfig.class)
public interface ExpedientTipusServiceLocalFeignClient extends ExpedientTipusFeignClient {

}
