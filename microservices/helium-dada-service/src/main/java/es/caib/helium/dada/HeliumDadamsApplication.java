package es.caib.helium.dada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
// TODO: eliminar les exclusions i configurar la securització
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
//        , org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class, MongoDataAutoConfiguration.class
        }
)
public class HeliumDadamsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeliumDadamsApplication.class, args);
//        System.setProperty("oracle.jdbc.timezoneAsRegion", "false");
    }

}
