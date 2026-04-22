package es.caib.helium.logic.intf.dto.engine;

import lombok.Data;

/** Classe per per representar la informació general d'una definició de procés definida dins d'un workflow engine.
 * 
 */
@Data
public class WProcessDefinition {

    private String deploymentId;
    private String id;
    private String key;
    private String name;
    private int version;
    private String category;
}
