package es.caib.helium.transaction.model

import java.io.Serializable

data class DistributedTransaction( var id: String? = null,
                                   var status: DistributedTransactionStatus,
                                   val participants: MutableList<DistributedTransactionParticipant> = mutableListOf()): Serializable