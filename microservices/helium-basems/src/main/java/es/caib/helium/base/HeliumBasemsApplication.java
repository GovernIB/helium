package es.caib.helium.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
// TODO: eliminar les exclusions i configurar la securització
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class}
)
public class HeliumBasemsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeliumBasemsApplication.class, args);
        System.setProperty("oracle.jdbc.timezoneAsRegion", "false");
    }

}
