package net.conselldemallorca.helium.back.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpressionData {

    private String taskInstanceInstanceId;
    private String expressionLanguage;
    private String expression;
    private String expectedClass;
    private List<VariableRest> valors;

}
