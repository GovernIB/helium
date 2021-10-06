package es.caib.helium.transaction.listener;

import es.caib.helium.transaction.exception.TransactionProcessingException;
import es.caib.helium.transaction.model.DistributedTransaction;
import es.caib.helium.transaction.model.DistributedTransactionStatus;
import es.caib.helium.transaction.model.TransactionEvent;
import es.caib.helium.transaction.service.EventBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionEventListener {

    private static final Integer SLEEP_TIME = 10;

    private final RestTemplate restTemplate;
    private final EventBus eventBus;

    @Value("${es.caib.helium.transaction.service.url:http://localhost:8093/api/v1/transactions}")
    private String transactionServiceUrl;


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleEvent(TransactionEvent event) throws Exception {
        log.info("TRANSACTION EVENT LISTENER: Execució {} espera la finalització de la transacció {}", event.getEventId(), event.getTransactionId());
        eventBus.sendEvent(event);
        DistributedTransaction transaction = null;
        Integer timeout_milliseconds = event.getTimeout() * 1000;

        if (event.isInitiator())
            return;

        for (int i = 0; i < timeout_milliseconds; i += 10) {
            transaction = eventBus.receiveTransaction(event.getTransactionId());
            if (transaction == null) {
                log.info("TRANSACTION EVENT LISTENER: > Continuam esperant el final de la transacció {}", event.getTransactionId());
                Thread.sleep(SLEEP_TIME);
            } else {
                break;
            }
        }
        log.info("TRANSACTION EVENT LISTENER: > Rebut final de la transacció {} per a l'event {} amb estat [{}]",
                event.getTransactionId(),
                event.getEventId(),
                transaction != null ? transaction.getStatus() : "TIMEOUT");
        if (transaction == null || transaction.getStatus() != DistributedTransactionStatus.CONFIRMED) {
            var msg = transaction != null ? transaction.getErrorMessage() : "Transaction TIMEOUT";
            throw new TransactionProcessingException(msg);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterRollback(TransactionEvent event) {
        log.info("TRANSACTION EVENT LISTENER: Rollback de l'execució {} de la transacció {}", event.getEventId(), event.getTransactionId());

        restTemplate.put(transactionServiceUrl + "/{transactionId}/finish/{status}",
                null,
                event.getTransactionId(),
                DistributedTransactionStatus.TO_ROLLBACK);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCompletion(TransactionEvent event) {
        log.info("TRANSACTION EVENT LISTENER: Commit de l'execució {} de la transacció {}", event.getEventId(), event.getTransactionId());

//        restTemplate.put(transactionServiceUrl + "/{transactionId}/participants/{eventId}/status/{status}",
//                null,
//                event.getTransactionId(),
//                event.getEventId(),
//                DistributedTransactionStatus.CONFIRMED);

        restTemplate.put(transactionServiceUrl + "/{transactionId}/finish/{status}",
                null,
                event.getTransactionId(),
                DistributedTransactionStatus.CONFIRMED);
    }

}
