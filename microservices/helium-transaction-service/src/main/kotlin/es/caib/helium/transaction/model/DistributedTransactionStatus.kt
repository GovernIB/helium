package es.caib.helium.transaction.model

enum class DistributedTransactionStatus {
    NEW,
    CONFIRMED,
    ROLLBACK,
    TO_ROLLBACK
}