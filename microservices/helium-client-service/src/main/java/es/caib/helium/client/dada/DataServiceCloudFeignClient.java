package es.caib.helium.client.dada;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.dada.config.DadaFeignClientConfig;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = DadaMsApiPath.NOM_SERVEI, configuration = DadaFeignClientConfig.class)
public interface DataServiceCloudFeignClient extends DataServiceFeignClient {

}
