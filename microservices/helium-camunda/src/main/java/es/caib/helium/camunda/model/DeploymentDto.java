package es.caib.helium.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentDto implements WDeployment {

    String id;
    String name;
    String tenantId;
    String category;
    List<? extends WProcessDefinition> processDefinitions;

}
