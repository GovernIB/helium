package es.caib.helium.client.domini.consultaTest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import es.caib.helium.client.domini.DominiMsApiPath;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(contextId = "domini-consulta-test-client",
        name = DominiMsApiPath.NOM_SERVEI,
        configuration = ConsultaTestFeignClientConfig.class)
public interface ConsultaTestServiceCloudFeignClient extends ConsultaTestFeignClient {

}
