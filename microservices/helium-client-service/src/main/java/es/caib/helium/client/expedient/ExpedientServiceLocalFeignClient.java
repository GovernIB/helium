package es.caib.helium.client.expedient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.integracio.arxiu.ArxiuFeignClient;


@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(name = ExpedientMsApiPath.NOM_SERVEI, url = ExpedientMsApiPath.URL_LOCAL, configuration = ExpedientFeignClientConfig.class)
public interface ExpedientServiceLocalFeignClient extends ArxiuFeignClient {

}
