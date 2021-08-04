package net.conselldemallorca.helium.back.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReassignTaskData {

    private String expression;
    private Long entornId;

}
