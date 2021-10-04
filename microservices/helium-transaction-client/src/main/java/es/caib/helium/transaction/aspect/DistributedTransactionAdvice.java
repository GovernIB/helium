package es.caib.helium.transaction.aspect;

import es.caib.helium.transaction.helper.TransactionHelper;
import es.caib.helium.transaction.model.DistributedTransaction;
import es.caib.helium.transaction.model.DistributedTransactionEvent;
import es.caib.helium.transaction.model.DistributedTransactionStatus;
import es.caib.helium.transaction.model.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class DistributedTransactionAdvice {

    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${es.caib.helium.transaction.service.url:http://localhost:8093/api/v1/transactions}")
    private String transactionServiceUrl;

    @Around("@annotation(DistributedTransaction)")
    public Object executeInDistributedTransaction(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("DISTRIBUTED ADVICE: Executant mètode dins transacció. Thread: {}", Thread.currentThread().getId());
        var transactionId = TransactionHelper.getActualTransaction();
        var timeout = TransactionHelper.getActualTimeout();
        if (timeout == null) {
            timeout = TransactionEvent.DEFAULT_TRANSACTION_TIMEOUT;
        }
        if (transactionId == null || transactionId.isBlank()) {
            log.error("DISTRIBUTED ADVICE: > No hi ha transacció activa. Finalitza l'execució.");
            // TODO: Si dóna excepció enviam un TO_ROLLBACK
            return joinPoint.proceed();
        } else {
            var eventId = UUID.randomUUID().toString();

            TransactionHelper.setActualEvent(eventId);
            log.info("DISTRIBUTED ADVICE: > Transacció sctiva: {}, Execució: {}. Executam el mètode", transactionId, eventId);

            var result = joinPoint.proceed();

            log.info("DISTRIBUTED ADVICE: > Transacció activa: {}, Execució: {}. Mètode executat", transactionId, eventId);

            applicationEventPublisher.publishEvent(DistributedTransactionEvent.builder()
                    .transactionId(transactionId)
                    .eventId(eventId)
                    .timeout(timeout)
                    .result(result)
                    .build());

            log.info("DISTRIBUTED ADVICE: > Transacció activa: {}, Execució: {}. Resultat plublicat al bus", transactionId, eventId);

            return null;
        }

    }

    @Before("@annotation(StartDistributedTransaction)")
    public void startDistributedTransaction(JoinPoint joinPoint) {
        log.info("DISTRIBUTED ADVICE: Iniciant transacció...");
        Integer timeout = DistributedTransactionEvent.DEFAULT_TRANSACTION_TIMEOUT;
        try {
            final String methodName = joinPoint.getSignature().getName();
            final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            if (method.getDeclaringClass().isInterface()) {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(methodName, method.getParameterTypes());
            }

            StartDistributedTransaction annotation = method.getAnnotation(StartDistributedTransaction.class);
            timeout = annotation.timeout();
        } catch (Exception ex) {}

        log.info("DISTRIBUTED ADVICE: > Informant al servidor de transaccions");
        var transaction = restTemplate.postForObject(
                transactionServiceUrl,
                es.caib.helium.transaction.model.DistributedTransaction.builder()
                        .status(DistributedTransactionStatus.NEW)
                        .build(),
                DistributedTransaction.class);
        TransactionHelper.setActualTransaction(transaction.getId());
        TransactionHelper.setActualTimeout(timeout);
        log.info("DISTRIBUTED ADVICE: > Transacció " + transaction.getId() + " inicialitzada.");

        applicationEventPublisher.publishEvent(
                DistributedTransactionEvent.builder()
                        .transactionId(transaction.getId())
                        .initiator(true)
                        .timeout(timeout)
                        .build());
        log.info("DISTRIBUTED ADVICE: > Transacció " + transaction.getId() + " event iniciador publicat.");
    }

}
