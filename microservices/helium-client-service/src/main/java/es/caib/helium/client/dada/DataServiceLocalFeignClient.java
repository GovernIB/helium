package es.caib.helium.client.dada;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.dada.config.FeignClientConfig;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(name = "helium-dada-service", url = "localhost:8083", configuration = FeignClientConfig.class)
public interface DataServiceLocalFeignClient extends DataServiceFeignClientMethods {
	
}
