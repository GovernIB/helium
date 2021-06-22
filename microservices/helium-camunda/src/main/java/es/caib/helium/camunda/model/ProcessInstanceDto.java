package es.caib.helium.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceDto implements WProcessInstance {

    String id;
    String processDefinitionId;
    String processDefinitionName;
    String parentProcessInstanceId;
    Date startTime;
    Date endTime;
    String description;
    Long expedientId;
//    Object processDefinition;

//    public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception {
//        return null;
//    }
//
//    public Map<String, byte[]> getFiles() {
//        return null;
//    }
}
