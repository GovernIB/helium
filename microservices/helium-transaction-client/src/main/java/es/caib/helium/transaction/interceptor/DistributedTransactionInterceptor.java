package es.caib.helium.transaction.interceptor;

import es.caib.helium.transaction.helper.TransactionHelper;
import es.caib.helium.transaction.model.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class DistributedTransactionInterceptor implements AsyncHandlerInterceptor {

        public boolean preHandle(
                HttpServletRequest request,
                HttpServletResponse response,
                Object handler) throws ServletException {

            String transactionId = request.getHeader(TransactionHelper.TRANSACTION_HEADER);
            String stTimeout = request.getHeader(TransactionHelper.TIMEOUT_HEADER);

            if (transactionId != null && !transactionId.isBlank()) {
                log.info("DISTRIBUTED REQUEST INTERCEPTOR: Petició {} dins la transacció distribuida {}. Thread: {}",
                        request.getRequestURL(),
                        transactionId,
                        Thread.currentThread().getId());
                TransactionHelper.setActualTransaction(transactionId);
            }
            if (stTimeout != null && !stTimeout.isBlank()) {
                Integer timeout = TransactionEvent.DEFAULT_TRANSACTION_TIMEOUT;
                try {
                    timeout = Integer.valueOf(stTimeout);
                } catch (NumberFormatException nfe) {}
                TransactionHelper.setActualTimeout(timeout);
            }
            return true;
        }
    }