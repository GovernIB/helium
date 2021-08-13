package es.caib.helium.client.expedient.proces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.expedient.ExpedientMsApiPath;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "proces-client", name = ExpedientMsApiPath.NOM_SERVEI, configuration = ProcesFeignClientConfig.class)
public interface ProcesServiceCloudFeignClient extends ProcesFeignClient {

}
