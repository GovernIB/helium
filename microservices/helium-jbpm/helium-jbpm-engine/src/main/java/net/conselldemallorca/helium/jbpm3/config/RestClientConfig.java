package net.conselldemallorca.helium.jbpm3.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import lombok.Setter;

/** Configuració del client REST amb un interceptor per afegir el token
 * d'autenticació.
 */
@Configuration
public class RestClientConfig {

	@Setter
	@Value("${es.caib.helium-jbpm.rest.readTimeout?10000}")
	private String stReadTimeout;
	@Setter
	@Value("${es.caib.helium-jbpm.rest.connectionTimeout?300000}")
	private String stConnectionTimeout;

	@Bean
	public RestTemplate restTemplate() {

		int readTimeout = 10000;
		int connectionTimeout = 300000;

		try {
			readTimeout = Integer.parseInt(stReadTimeout);
		} catch (NumberFormatException nfe) {
		}
		try {
			connectionTimeout = Integer.parseInt(stConnectionTimeout);
		} catch (NumberFormatException nfe) {
		}

		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectTimeout(connectionTimeout);
		httpRequestFactory.setReadTimeout(readTimeout);

		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		if (CollectionUtils.isEmpty(interceptors)) {
			interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		}
		interceptors.add(new RestTemplateSecurityInterceptor());
		restTemplate.setInterceptors(interceptors);
		return restTemplate;
	}
}