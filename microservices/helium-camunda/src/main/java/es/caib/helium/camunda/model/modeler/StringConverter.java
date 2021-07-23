package es.caib.helium.camunda.model.modeler;

public class StringConverter extends JacksonAwareStringToTypeConverter<String> {
    public String convertQueryParameterToType(String value) {
        return value;
    }
}
