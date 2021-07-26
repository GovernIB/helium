package es.caib.helium.camunda.model.modeler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;
import java.io.IOException;

public abstract class JacksonAwareStringToTypeConverter<T> implements StringToTypeConverter<T> {
    protected ObjectMapper objectMapper;

    public abstract T convertQueryParameterToType(String paramString);

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected T mapToType(String value, Class<T> typeClass) {
        try {
            return (T)this.objectMapper.readValue(value, typeClass);
        } catch (JsonParseException e) {
            throw new InvalidRequestException(Response.Status.BAD_REQUEST, e, String.format("Cannot convert value %s to java type %s", new Object[] { value, typeClass
                    .getName() }));
        } catch (JsonMappingException e) {
            throw new InvalidRequestException(Response.Status.BAD_REQUEST, e, String.format("Cannot convert value %s to java type %s", new Object[] { value, typeClass
                    .getName() }));
        } catch (IOException e) {
            throw new InvalidRequestException(Response.Status.BAD_REQUEST, e, String.format("Cannot convert value %s to java type %s", new Object[] { value, typeClass
                    .getName() }));
        }
    }
}
