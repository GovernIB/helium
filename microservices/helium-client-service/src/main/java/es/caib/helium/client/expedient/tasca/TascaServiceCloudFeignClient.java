package es.caib.helium.client.expedient.tasca;

import es.caib.helium.client.expedient.ExpedientMsApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "tasca-client", name = ExpedientMsApiPath.NOM_SERVEI, configuration = TascaFeignClientConfig.class)
public interface TascaServiceCloudFeignClient extends TascaFeignClient {

}
