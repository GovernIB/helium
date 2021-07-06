package es.caib.helium.back.rest.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProcessStartData {

    private String actorId;
    private String processDefinitionId;
    private List<VariableRest> variables;

}
