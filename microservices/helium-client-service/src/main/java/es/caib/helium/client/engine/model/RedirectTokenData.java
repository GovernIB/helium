package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedirectTokenData {

    private String nodeName;
    private boolean cancelTasks;
    private boolean enterNodeIfTask;
    private boolean executeNode;

}
