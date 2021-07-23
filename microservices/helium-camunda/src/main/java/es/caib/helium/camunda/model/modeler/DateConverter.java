package es.caib.helium.camunda.model.modeler;

import javax.ws.rs.core.Response;
import java.util.Date;

public class DateConverter extends JacksonAwareStringToTypeConverter<Date> {
    public Date convertQueryParameterToType(String value) {
        if (value != null && (value.startsWith("\"") || value.endsWith("\"")))
            throw new InvalidRequestException(Response.Status.BAD_REQUEST,
                    String.format("Cannot convert value %s to java type %s because of double quotes", new Object[] { value, Date.class.getName() }));
        return mapToType("\"" + value + "\"", Date.class);
    }
}
