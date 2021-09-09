package es.caib.helium.client.dada;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.dada.config.DadaFeignClientConfig;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(name = DadaMsApiPath.NOM_SERVEI, url = "${es.caib.helium.dada.url:" + DadaMsApiPath.URL_LOCAL + "}", configuration = DadaFeignClientConfig.class)
public interface DadaServiceLocalFeignClient extends DadaServiceFeignClient {
	
}
