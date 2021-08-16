package es.caib.helium.camunda;

import org.camunda.bpm.spring.boot.starter.property.CamundaBpmProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"es.caib.helium.client"})
@EnableConfigurationProperties(CamundaBpmProperties.class)
//@EnableCamundaEventBus
@SpringBootApplication(
        exclude = {
                org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration.class
        },
        scanBasePackages = {
                "es.caib.helium.camunda",
                "es.caib.helium.client"
        }
)
public class HeliumCamundaApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(HeliumCamundaApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HeliumCamundaApplication.class);
    }

}
