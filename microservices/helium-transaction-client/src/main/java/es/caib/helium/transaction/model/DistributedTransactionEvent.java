package es.caib.helium.transaction.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class DistributedTransactionEvent<T> implements TransactionEvent<T> {

    String transactionId;
    String eventId;
    Integer timeout;
    boolean initiator;
    T result;


    @Override
    public Integer getTimeout() {
        if (this.timeout != null) {
            return this.timeout;
        }
        return TransactionEvent.super.getTimeout();
    }

}
