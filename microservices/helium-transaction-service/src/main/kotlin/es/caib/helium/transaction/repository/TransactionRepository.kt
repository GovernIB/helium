package es.caib.helium.transaction.repository

import es.caib.helium.transaction.model.DistributedTransaction
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class TransactionRepository {

    val transactions = mutableListOf<DistributedTransaction>()

    fun findById(id: String): DistributedTransaction? {
        return transactions.singleOrNull { it.id == id }
    }

    fun findAll(): List<DistributedTransaction> {
        return transactions
    }

    fun save(transaction: DistributedTransaction): DistributedTransaction {
        transaction.id = UUID.randomUUID().toString()
        transactions.add(transaction)
        return transaction
    }

    fun update(transaction: DistributedTransaction): DistributedTransaction {
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index >= 0) {
            transactions[index] = transaction
        }
        return transaction
    }

    fun removeById(id: String): Boolean {
        return transactions.removeIf { it.id == id }
    }
}