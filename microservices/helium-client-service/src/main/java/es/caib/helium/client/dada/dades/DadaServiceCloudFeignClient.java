package es.caib.helium.client.dada.dades;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.dada.dades.config.DadaClientConfig;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = DadaMsApiPath.NOM_SERVEI, configuration = DadaClientConfig.class)
public interface DadaServiceCloudFeignClient extends DadaFeignClient {

}
