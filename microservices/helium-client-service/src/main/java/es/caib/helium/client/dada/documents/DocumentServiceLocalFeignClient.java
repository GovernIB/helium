package es.caib.helium.client.dada.documents;

import es.caib.helium.client.dada.documents.config.DocumentClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile(value = {"!spring-cloud & !compose"})
@FeignClient(name = DocumentMsApiPath.NOM_SERVEI, url = "${es.caib.helium.dada.url:" + DocumentMsApiPath.URL_LOCAL + "}", configuration = DocumentClientConfig.class)
public interface DocumentServiceLocalFeignClient extends DocumentFeignClient {
}
