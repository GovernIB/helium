package es.caib.helium.transaction.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType

//@Configuration
class JmsConfig {

//    @Bean
//    fun jacksonJmsMessageConverter(objectMapper: ObjectMapper): MessageConverter {
//        val converter = MappingJackson2MessageConverter()
//        converter.setTargetType(MessageType.TEXT)
//        converter.setTypeIdPropertyName("_type")
//        converter.setObjectMapper(objectMapper)
//        return converter
//    }
}