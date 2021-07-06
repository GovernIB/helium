package es.caib.helium.client.monitor;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

import es.caib.helium.client.monitor.config.MonitorFeignClientConfig;

@Profile(value = {"spring-cloud", "compose"})
@FeignClient(name = MonitorMsApiPath.NOM_SERVEI, configuration = MonitorFeignClientConfig.class)
public interface MonitorServiceCloudFeignClient extends MonitorServiceFeignClient {

}
