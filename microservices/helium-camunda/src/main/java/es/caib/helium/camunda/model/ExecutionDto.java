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
public class ExecutionDto implements WToken {

    String id;
    String name;
    String fullName;
    String nodeName;
    String nodeClass;
    Date start;
    Date end;
    boolean ableToReactivateParent;
    boolean terminationImplicit;
    boolean suspended;
    Date nodeEnter;
    boolean root;
    String parentTokenName;
    String parentTokenFullName;
    String processInstanceId;

}
