package net.conselldemallorca.helium.jbpm3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Ignora les propietats JSon no declarades a les classes.
 * 
 */
@Configuration
public class JacksonMapperConfig {
	
    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter jacksonConvertor(){
        MappingJackson2HttpMessageConverter convertor= new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        convertor.setObjectMapper(mapper);
        return convertor;
    }
}
