package es.caib.helium.camunda.model.modeler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringListConverter extends JacksonAwareStringToTypeConverter<List<String>> {
    public List<String> convertQueryParameterToType(String value) {
        if (value == null || value.trim().isEmpty())
            return Collections.emptyList();
        return Arrays.asList(value.split(","));
    }
}
