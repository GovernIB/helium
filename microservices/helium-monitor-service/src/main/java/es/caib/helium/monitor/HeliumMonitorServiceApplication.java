package es.caib.helium.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class}
)
public class HeliumMonitorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeliumMonitorServiceApplication.class, args);
    }

}
