package es.caib.helium.client.engine.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExpressionData {

    private String taskInstanceInstanceId;
    private String expressionLanguage;
    private String expression;
//    private String expectedClass;
    private List<VariableRest> valors;

}
