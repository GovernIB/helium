package es.caib.helium.transaction.controller

import es.caib.helium.transaction.model.DistributedTransaction
import es.caib.helium.transaction.model.DistributedTransactionParticipant
import es.caib.helium.transaction.model.DistributedTransactionStatus
import es.caib.helium.transaction.repository.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController(val repository: TransactionRepository,
                            val template: JmsTemplate) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun add(@RequestBody transaction: DistributedTransaction): DistributedTransaction {
        logger.info("TRANSACTION SERVER: creant nova transacció...")
        val t = repository.save(transaction)
        logger.info("TRANSACTION SERVER: TransactionId: " + t.id)
        return t
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): DistributedTransaction? {
        logger.info("TRANSACTION SERVER: consultant transacció amb identificador: " + id)
        val t = repository.findById(id)
        if (t == null)
            logger.info("TRANSACTION SERVER: Transacció no existent")
        return t;
    }

    @GetMapping
    fun findAll(): List<DistributedTransaction> = repository.findAll()

    @PutMapping("/{id}/finish/{status}")
    fun finish(@PathVariable id: String, @PathVariable status: DistributedTransactionStatus) {
        val transaction: DistributedTransaction? = repository.findById(id)
        if (transaction != null) {
            transaction.status = status
            repository.update(transaction)
            template.convertAndSend("trx-events", DistributedTransaction(id, status))
        }
    }

    @PutMapping("/{id}/participants")
    fun addParticipant(@PathVariable id: String,
                       @RequestBody participant: DistributedTransactionParticipant) {
        logger.info("TRANSACTION SERVER: Afegint nou participant " + participant.eventId + " en transacció " + id)
        repository.findById(id)?.participants?.add(participant)
    }

    @PutMapping("/{id}/participants/{eventId}/status/{status}")
    fun updateParticipant(@PathVariable id: String,
                          @PathVariable eventId: String,
                          @PathVariable status: DistributedTransactionStatus) {
        logger.info("TRANSACTION SERVER: Actualitzant estat de la transacció " + id + ", event " + eventId + "[" + status + "]")
        val transaction: DistributedTransaction? = repository.findById(id)
        if (transaction != null) {
            val index = transaction.participants.indexOfFirst { it.eventId == eventId }
            if (index != -1) {
                transaction.participants[index].status = status
                repository.removeById(id)
                template.convertAndSend("trx-events", DistributedTransaction(id, status))
            }
        }
    }
}