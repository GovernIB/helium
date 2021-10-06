package es.caib.helium.transaction.model;

public interface TransactionEvent<T> {

    public static Integer DEFAULT_TRANSACTION_TIMEOUT = 10; // segons

    public String getTransactionId();
    public boolean isInitiator();
    public String getEventId();
    public <T> T getResult();
    default public Integer getTimeout() {
        return DEFAULT_TRANSACTION_TIMEOUT;
    }
}
