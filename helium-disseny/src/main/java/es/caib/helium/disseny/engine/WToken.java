package es.caib.helium.disseny.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/** Classe per per representar la informació d'un token. En BPMN 2.0 i flowable l'equivalent al token és l'execució.
 *
 */
@Data
public class WToken {

	private String id;
	private String name;
	private Date start;
	private Date end;
	private Date nodeEnter;
	private String processInstanceId;
	private String processInstanceKey;
	boolean root;
	private String superRootTokenId;

	private WToken parent;
	private WToken superToken;
	private String processDefinitionId;
	private String processDefinitionKey;
	private String processDefinitionName;
	private Integer processDefinitionversion;

	private String nodeName;
	/** Per indicar si el token es troba en un node de tipus receiveTask per esperar un senyal. */
	private boolean receiveTask;

	private List<String> sortides = new ArrayList<>();


//	String getId();
//
//	String getName();
//
//	String getFullName();
//
//	String getNodeName();
//
//	String getNodeClass();
//
//	Date getStart();
//
//	Date getEnd();
//
//	boolean isAbleToReactivateParent();
//
//	boolean isTerminationImplicit();
//
//	boolean isSuspended();
//
//	Date getNodeEnter();
//
//	boolean isRoot();
//
//	String getParentTokenName();
//
//	String getParentTokenFullName();
//
//	String getProcessInstanceId();
//
//	WToken getToken();
//
//	WProcessInstance getProcessInstance();
//
//	WToken getParent();
//
//	WNode getNode();
}
