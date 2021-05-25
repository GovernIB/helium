package es.caib.helium.sidecar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

@EnableSidecar
@SpringBootApplication
public class HeliumSidecarApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeliumSidecarApplication.class, args);
    }

}
