package es.caib.helium.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType

@SpringBootApplication
class HeliumTransactionsApplication {

    @Bean
    fun jacksonJmsMessageConverter(objectMapper: ObjectMapper): MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.setTargetType(MessageType.TEXT)
        converter.setTypeIdPropertyName("_type")
        converter.setObjectMapper(objectMapper)
        return converter
    }

//    @Bean
//    fun jmsTemplate(): JmsTemplate = JmsTemplate()
//    @Bean
//    fun topic(): Topic = jmsTemplate().connectionFactory.createConnection().createSession().createTopic("trx-events")
}

fun main(args: Array<String>) {
    runApplication<HeliumTransactionsApplication>(*args)
}
