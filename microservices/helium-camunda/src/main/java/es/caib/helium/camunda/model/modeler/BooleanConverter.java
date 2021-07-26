package es.caib.helium.camunda.model.modeler;

public class BooleanConverter extends JacksonAwareStringToTypeConverter<Boolean> {
    public Boolean convertQueryParameterToType(String value) {
        return mapToType(value, Boolean.class);
    }
}
