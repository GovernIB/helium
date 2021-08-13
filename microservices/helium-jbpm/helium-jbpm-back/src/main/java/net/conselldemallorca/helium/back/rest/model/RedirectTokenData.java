package net.conselldemallorca.helium.back.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedirectTokenData {

    private String nodeName;
    private boolean cancelTasks;
    private boolean enterNodeIfTask;
    private boolean executeNode;

}
