package es.caib.helium.domini.config;

import es.caib.helium.domini.service.DominiWsServiceImpl;
import es.caib.helium.domini.ws.ConsultaDomini;
import es.caib.helium.domini.ws.ConsultaDominiResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;

/**
 * Classe de configuració per aaccés a serveis SOAP
 */
@Configuration
public class DominiWsConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//        marshaller.setContextPath("es.caib.helium.domini.ws");
//        marshaller.setPackagesToScan("es.caib.helium.domini.ws", "es.caib.helium.domini.model");
        marshaller.setClassesToBeBound(new Class[] {
                ConsultaDomini.class,
                ConsultaDominiResponse.class
        });
        return marshaller;
    }

    @Bean
    public DominiWsServiceImpl dominiWsServiceImpl(Jaxb2Marshaller marshaller) {
        DominiWsServiceImpl client = new DominiWsServiceImpl();
        client.setDefaultUri("http://localhost:8080/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
