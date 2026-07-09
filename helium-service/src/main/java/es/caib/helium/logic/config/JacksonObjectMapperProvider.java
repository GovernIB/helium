package es.caib.helium.logic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {
	private final ObjectMapper defaultObjectMapper;

	public JacksonObjectMapperProvider() {
		defaultObjectMapper = new ObjectMapper();

		// 1. Register the Java 8 Date/Time module
		defaultObjectMapper.registerModule(new JavaTimeModule());

		// Optional: Prevent dates from turning into arrays of numbers
		defaultObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return defaultObjectMapper;
	}
}
