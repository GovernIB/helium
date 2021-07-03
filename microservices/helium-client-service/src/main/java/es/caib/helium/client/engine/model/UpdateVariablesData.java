package es.caib.helium.client.engine.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateVariablesData {

    private boolean deleteFirst;
    private List<VariableRest> variables;

}
