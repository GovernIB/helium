package es.caib.helium.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.zip.ZipInputStream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDefinitionDto implements WProcessDefinition {

    String deploymentId;
    String id;
    String key;
    String name;
    int version;
    String category;
    Object processDefinition;

    public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception {
        return null;
    }

    public Map<String, byte[]> getFiles() {
        return null;
    }
}
