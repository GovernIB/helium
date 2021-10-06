package es.caib.helium.transaction.helper;

import es.caib.helium.transaction.model.DistributedTransaction;
import es.caib.helium.transaction.model.DistributedTransactionEvent;
import es.caib.helium.transaction.model.DistributedTransactionStatus;
import es.caib.helium.transaction.model.TransactionEvent;
import es.caib.helium.transaction.service.EventBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionHelper {

    @Value("${es.caib.helium.transaction.service.url:http://localhost:8093/api/v1/transactions}")
    private String transactionServiceUrl;

    public static final String TRANSACTION_HEADER = "X-Transaction-ID";
    public static final String TIMEOUT_HEADER = "X-Timeout";
    private static InheritableThreadLocal<String> actualTransactionThreadLocal = new InheritableThreadLocal<>();
    private static InheritableThreadLocal<Integer> actualTimeoutThreadLocal = new InheritableThreadLocal<>();
    private static InheritableThreadLocal<String> actualEventThreadLocal = new InheritableThreadLocal<>();
    private static EventBus bus;

    private final RestTemplate restTemplate;
    private final EventBus eventBus;
    private final ApplicationEventPublisher applicationEventPublisher;



    public static String getActualTransaction() {
        return actualTransactionThreadLocal.get();
    }
    public static void setActualTransaction(String transactionId) {
        actualTransactionThreadLocal.set(transactionId);
    }
    public static Integer getActualTimeout() {
        return actualTimeoutThreadLocal.get();
    }
    public static void setActualTimeout(Integer timeout) {
        actualTimeoutThreadLocal.set(timeout);
    }

    public static String getActualEvent() {
        return actualEventThreadLocal.get();
    }
    public static void setActualEvent(String eventId) {
        actualEventThreadLocal.set(eventId);
    }
    public static EventBus getBus() {
        return bus;
    }

    // Transaccions
    // =================================================================================================================
    public void startDistributedTransaction() {
        startDistributedTransaction(TransactionEvent.DEFAULT_TRANSACTION_TIMEOUT);
    }

    public void startDistributedTransaction(Integer timeout) {
        log.info("TRANSACTION HELPER: Iniciant transacció...");
        var transaction = restTemplate.postForObject(
                transactionServiceUrl,
                DistributedTransaction.builder()
                        .status(DistributedTransactionStatus.NEW)
                        .build(),
                DistributedTransaction.class);
        setActualTransaction(transaction.getId());
        setActualTimeout(timeout);
        log.info("TRANSACTION HELPER: > Transacció " + transaction.getId() + " inicialitzada.");

        applicationEventPublisher.publishEvent(
                DistributedTransactionEvent.builder()
                        .transactionId(transaction.getId())
                        .initiator(true)
                        .timeout(timeout)
                        .build());
        log.info("TRANSACTION HELPER: > Transacció " + transaction.getId() + " event iniciador publicat.");
    }


    // Events (participants)
    // =================================================================================================================

    public <T> T executeInDistributedTransaction(T executionResult) {
        log.info("TRANSACTION HELPER: Transacció: {}, Execució: {}. Recuperant resultat del bus", getActualTransaction(), getActualEvent());
        String transactionId = getActualTransaction();

        if (transactionId == null || transactionId.isBlank()) {
            log.info("TRANSACTION HELPER: No estam en una transacció. Es retorna el resultat de l'execució");
            return executionResult;
        }

        log.info("TRANSACTION HELPER: Estam en una transacció. Anam a cercar el resultat de l'execució al bus");
        val result = (T) eventBus.receiveEvent(
                getActualTransaction())       // eventId
                .getResult();
        log.info("TRANSACTION HELPER: > Transacció: {}, Execució: {}. Obtingut resultat", getActualTransaction(), getActualEvent());
        return result;
    }

    public <T> T getDistributedTransactionResult() {
        log.info("TRANSACTION HELPER: Transacció: {}, Execució: {}. Recuperant resultat del bus", getActualTransaction(), getActualEvent());
        val result = (T) eventBus.receiveEvent(getActualTransaction()).getResult();
        log.info("TRANSACTION HELPER: > Transacció: {}, Execució: {}. Obtingut resultat", getActualTransaction(), getActualEvent());
        return result;
    }

    @PostConstruct
    private void postConstruct() {
        bus = eventBus;
    }
}
