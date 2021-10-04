package es.caib.helium.dada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(
//        scanBasePackages = {
//                "es.caib.helium.dada",
//                "es.caib.helium.transaction"
//        }
)
public class HeliumDadamsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeliumDadamsApplication.class, args);
    }


//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
//        return restTemplateBuilder.build();
//    }
}
