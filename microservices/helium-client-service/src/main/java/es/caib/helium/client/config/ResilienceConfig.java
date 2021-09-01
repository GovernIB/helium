package es.caib.helium.client.config;

public class ResilienceConfig {

//    @Bean
//    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
//        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
//                .timeoutDuration(Duration.ofSeconds(10))
//                .build();
//        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
//                .failureRateThreshold(50)
//                .waitDurationInOpenState(Duration.ofMillis(1000))
//                .slidingWindowSize(2)
//                .build();
//
//        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
//                .timeLimiterConfig(timeLimiterConfig)
//                .circuitBreakerConfig(circuitBreakerConfig)
//                .build());
//    }
//
//    @Bean
//    public Customizer<Resilience4JCircuitBreakerFactory> specificCustomConfiguration() {
//
//        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
//                .timeoutDuration(Duration.ofSeconds(10))
//                .build();
//        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
//                .failureRateThreshold(50)
//                .waitDurationInOpenState(Duration.ofMillis(1000))
//                .slidingWindowSize(2)
//                .build();
//
//        return factory -> factory.configure(
//                builder -> builder
//                        .circuitBreakerConfig(circuitBreakerConfig)
//                        .timeLimiterConfig(timeLimiterConfig)
//                        .build(),
//                DadaMsApiPath.NOM_SERVEI);
//    }

}
