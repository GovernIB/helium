package es.caib.helium.logic.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableFeignClients(basePackages = "es.caib.helium.client")
public class FeignClientConfig {

//
//    @Bean
//    public FeignClientInterceptor oauth2AuthRequestInterceptor() {
//        return new FeignClientInterceptor();
//    }
//
//    public class FeignClientInterceptor implements RequestInterceptor {
//
//        private static final String AUTHORIZATION_HEADER="Authorization";
//        private static final String TOKEN_TYPE = "Bearer";
//
//        @Override
//        public void apply(RequestTemplate requestTemplate) {
//            String token = KeycloakHelper.getTokenString();
//
//            if (token != null && !token.isEmpty()) {
//                requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, token));
//            }
//        }
//    }

    @Bean
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
            @Override
            public HttpMessageConverters getObject() throws BeansException {
                return new HttpMessageConverters(new RestTemplate().getMessageConverters());
            }
        }));
    }
}
