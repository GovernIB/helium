package es.caib.helium.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!spring-cloud && !compose")
@Configuration
public class LocalRouteConfig {

    @Bean
    public RouteLocator localRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("helium-domini-service",
                        r -> r.path("/api/v1/dominis*", "/api/v1/dominis/*")
                                .uri("http://localhost:8081"))
                .build();
    }
}
