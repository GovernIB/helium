package es.caib.helium.transaction.config;

import es.caib.helium.transaction.interceptor.DistributedTransactionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebTransactionConfig implements WebMvcConfigurer {
    
    private final DistributedTransactionInterceptor distributedTransactionInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(distributedTransactionInterceptor);
    }
}
