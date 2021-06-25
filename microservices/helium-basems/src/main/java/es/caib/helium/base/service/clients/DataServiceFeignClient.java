package es.caib.helium.base.service.clients;

import es.caib.helium.base.config.FeignClientConfig;
import es.caib.helium.base.model.Dada;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "helium-dada-service", configuration = FeignClientConfig.class)
public interface DataServiceFeignClient {

    public static final String DADA_PATH = "/api/v1/expedients/proces/{procesId}/dades/{codi}";

    @RequestMapping(method = RequestMethod.GET, value = DADA_PATH)
    ResponseEntity<Dada> getDadaByProcessInstanceId(@PathVariable String procesId, @PathVariable String codi);

}
