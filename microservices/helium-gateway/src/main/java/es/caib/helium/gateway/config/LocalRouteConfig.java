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
                                .filters(f -> f.circuitBreaker(c -> c.setName("dominiCB")
                                        .setFallbackUri("forward:/domini-failover")
                                        .setRouteId("dom-failover")))
                                .uri("http://localhost:8081"))
                .route("helium-domini-failover-service",
                        r -> r.path("/api/domini-failover/**")
                                .uri("http://localhost:8181"))
                .build();
    }
}
