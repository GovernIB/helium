package es.caib.helium.client.expedient.expedient;

import es.caib.helium.client.expedient.ExpedientMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;


@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(contextId = "expedient-client", name = ExpedientMsApiPath.NOM_SERVEI, url = "${es.caib.helium.expedient.url:localhost:8085}", configuration = ExpedientFeignClientConfig.class)
public interface ExpedientServiceLocalFeignClient extends ExpedientFeignClient {

}
