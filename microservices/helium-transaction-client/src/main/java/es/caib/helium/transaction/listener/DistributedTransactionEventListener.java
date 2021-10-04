package es.caib.helium.transaction.listener;

import es.caib.helium.transaction.model.DistributedTransaction;
import es.caib.helium.transaction.service.EventBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributedTransactionEventListener {

    private final EventBus eventBus;

    @JmsListener(destination = "trx-events") //, containerFactory = "jmsContainerFactory")
    public void onMessage(DistributedTransaction transaction) {
        log.info("DISTRIBUTED EVENT LISTENER: Rebuda finalització de la transacció {} amb estat {}",
                transaction.getId(),
                transaction.getStatus());
        eventBus.sendTransaction(transaction);
    }

}
