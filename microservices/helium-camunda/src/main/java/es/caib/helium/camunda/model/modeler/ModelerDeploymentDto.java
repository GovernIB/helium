package es.caib.helium.camunda.model.modeler;

import lombok.Data;
import org.camunda.bpm.engine.repository.Deployment;

import java.util.Date;

@Data
public class ModelerDeploymentDto extends LinkableDto {

    protected String id;
    protected String name;
    protected String source;
    protected Date deploymentTime;
    protected String tenantId;

    public static ModelerDeploymentDto fromDeployment(Deployment deployment) {
        ModelerDeploymentDto dto = new ModelerDeploymentDto();
        dto.id = deployment.getId();
        dto.name = deployment.getName();
        dto.source = deployment.getSource();
        dto.deploymentTime = deployment.getDeploymentTime();
        dto.tenantId = deployment.getTenantId();
        return dto;
    }
}
