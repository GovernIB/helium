package es.caib.helium.back.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedirectTokenData {

    private String nodeName;
    private boolean cancelTasks;
    private boolean enterNodeIfTask;
    private boolean executeNode;

}
