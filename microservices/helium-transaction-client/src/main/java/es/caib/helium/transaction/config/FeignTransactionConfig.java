package es.caib.helium.transaction.config;

import es.caib.helium.transaction.helper.TransactionHelper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignTransactionConfig {

    @Bean
    public FeignClientInterceptor distributedTransactionRequestInterceptor() {
        return new FeignClientInterceptor();
    }

    public class FeignClientInterceptor implements RequestInterceptor {

        @Override
        public void apply(RequestTemplate requestTemplate) {

            if (TransactionHelper.getActualTransaction() != null) {
                log.info("FEIGN INTERCEPTOR: Afegint capçalera per transacció distribuïda [{}: {}], [{}: {}]",
                        TransactionHelper.TRANSACTION_HEADER,
                        TransactionHelper.getActualTransaction(),
                        TransactionHelper.TIMEOUT_HEADER,
                        TransactionHelper.getActualTimeout());
                requestTemplate.header(TransactionHelper.TRANSACTION_HEADER, TransactionHelper.getActualTransaction());
                requestTemplate.header(TransactionHelper.TIMEOUT_HEADER, TransactionHelper.getActualTimeout().toString());
            }
        }
    }

}