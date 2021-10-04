package net.conselldemallorca.helium.jbpm3.config;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import net.conselldemallorca.helium.api.util.ThreadLocalInfo;

/** Interceptor de crides rest per afegir el token d'autenticació en les peticions
 * REST. */
public class RestTemplateSecurityInterceptor implements ClientHttpRequestInterceptor  {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().set(AUTHORIZATION_HEADER, ThreadLocalInfo.getAuthorizationToken());
        return execution.execute(request, body);
    }
}
