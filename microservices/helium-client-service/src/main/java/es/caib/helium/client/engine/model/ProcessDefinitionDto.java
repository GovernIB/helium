package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

//    Object processDefinition;

//    public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception {
//        return null;
//    }
//
//    public Map<String, byte[]> getFiles() {
//        return null;
//    }
}
