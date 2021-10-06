package es.caib.helium.transaction.service;

import es.caib.helium.transaction.exception.TransactionProcessingException;
import es.caib.helium.transaction.helper.TransactionHelper;
import es.caib.helium.transaction.model.DistributedTransaction;
import es.caib.helium.transaction.model.DistributedTransactionStatus;
import es.caib.helium.transaction.model.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventBus {

    private final RestTemplate restTemplate;

    @Value("${es.caib.helium.transaction.service.url:http://localhost:8093/api/v1/transactions}")
    private String transactionServiceUrl;

    private static Set<DistributedTransaction> transactions = new HashSet<>();
    private static Set<TransactionEvent> events = new HashSet<>();

    public void sendTransaction(DistributedTransaction transaction) {
        log.info("EVENT BUS: Afegint transacció {}...", transaction.getId());
        transactions.add(transaction);
    }

    @SneakyThrows
    public DistributedTransaction receiveTransaction(String transactionId) {
        log.info("EVENT BUS: Esperant el final de la transacció {}...", transactionId);

        DistributedTransaction transaction = null;
//        do {
        transaction = transactions.stream()
                .filter(t -> t.getId().equals(transactionId))
                .findFirst().orElse(null);
        if (transaction != null) {
            log.info(" > Rebut final de la transacció {}", transactionId);
            transactions.remove(transaction);
            return transaction;
        } else {
            log.info(" > Continuam esperant el final de la transacció {}", transactionId);
            Thread.sleep(10);
        }

//        } while(transaction == null);
        return null;
    }

    public void sendEvent(TransactionEvent event) {
        log.info("EVENT BUS: Afegint execució {}...", event.getEventId());
        events.add(event);
    }

    @SneakyThrows
    public TransactionEvent receiveEvent(String transactionId) {
        log.info("EVENT BUS: Esperant el final de la execució, en la transacció {}...", transactionId);
        TransactionEvent event = null;
        var timeout = TransactionHelper.getActualTimeout();
        if (timeout == null) {
            timeout = TransactionEvent.DEFAULT_TRANSACTION_TIMEOUT;
        }
        Integer timeout_milliseconds = timeout * 1000;

        do {
            log.info("EVENT BUS: Events encuats: ", events.stream().map(e -> e.getTransactionId()).collect(Collectors.joining(",")));
            event = events.stream()
                    .filter(e -> e.getTransactionId().equals(transactionId))
                    .findFirst().orElse(null);
            if (event != null) {
                log.info(" > Rebut final de la execució");
                events.remove(event);
                return event;
            } else {
                log.info(" > Continuam esperant el final de la execució");
                timeout_milliseconds -= 10;
                Thread.sleep(10);
            }

        } while(event == null && timeout_milliseconds > 0);

        log.info(" > Ens hem cansat d'esperar: TIMEOUT");
        restTemplate.put(transactionServiceUrl + "/{transactionId}/finish/{status}",
                null,
                event.getTransactionId(),
                DistributedTransactionStatus.TO_ROLLBACK);
        throw new TransactionProcessingException("Transaction TIMEOUT");

    }
}
