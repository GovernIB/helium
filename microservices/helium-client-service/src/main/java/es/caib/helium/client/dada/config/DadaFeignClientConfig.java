package es.caib.helium.client.dada.config;

import es.caib.helium.base.helper.KeycloakHelper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DadaFeignClientConfig {

    @Bean
    public FeignClientInterceptor oauth2AuthRequestInterceptor() {
        return new FeignClientInterceptor();
    }

    public class FeignClientInterceptor implements RequestInterceptor {

        private static final String AUTHORIZATION_HEADER="Authorization";
        private static final String TOKEN_TYPE = "Bearer";

        @Override
        public void apply(RequestTemplate requestTemplate) {
            String token = KeycloakHelper.getTokenString();

            if (token != null && !token.isEmpty()) {
                requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, token));
            }
        }
    }

}

