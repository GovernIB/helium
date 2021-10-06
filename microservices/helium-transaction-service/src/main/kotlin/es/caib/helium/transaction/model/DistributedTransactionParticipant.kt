package es.caib.helium.transaction.model;

import java.io.Serializable

class DistributedTransactionParticipant(val eventId: String, var status: DistributedTransactionStatus): Serializable
