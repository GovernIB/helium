package es.caib.helium.logic.intf.dto.engine;


import java.util.Date;

import lombok.Data;

/** Classe per per representar la informació general d'una instància de procés dins d'un workflow engine.
 * 
 */
@Data
public class WProcessInstance {

	private String id;
	private String key;
	private String processDefinitionId;
	private String processDefinitionName;
	private String processDefinitionKey;
	private Integer processDefinitionVersion;
	private String parentProcessInstanceId;
	private String rootProcessInstanceId;
	private Date startTime;
	private String description;
	private boolean isSuspended;
}
