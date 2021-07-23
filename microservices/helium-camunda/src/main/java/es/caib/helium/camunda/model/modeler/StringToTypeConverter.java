package es.caib.helium.camunda.model.modeler;

public interface StringToTypeConverter<T> {
    T convertQueryParameterToType(String paramString);
}
