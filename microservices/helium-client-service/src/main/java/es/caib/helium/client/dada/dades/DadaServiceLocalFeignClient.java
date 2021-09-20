package es.caib.helium.client.dada.dades;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.dada.dades.config.DadaClientConfig;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(name = DadaMsApiPath.NOM_SERVEI, url = "${es.caib.helium.dada.url:" + DadaMsApiPath.URL_LOCAL + "}", configuration = DadaClientConfig.class)
public interface DadaServiceLocalFeignClient extends DadaFeignClient {
	
}
