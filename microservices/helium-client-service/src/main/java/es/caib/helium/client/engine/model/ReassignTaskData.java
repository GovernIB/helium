package es.caib.helium.client.engine.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReassignTaskData {

    private String expression;
    private Long entornId;

}
