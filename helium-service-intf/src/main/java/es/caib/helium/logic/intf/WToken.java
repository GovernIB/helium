package es.caib.helium.logic.intf;

import java.util.Date;

public interface WToken {

	String getId();

	String getName();

	String getFullName();

	String getNodeName();

	String getNodeClass();

	Date getStart();

	Date getEnd();

	boolean isAbleToReactivateParent();

	boolean isTerminationImplicit();

	boolean isSuspended();

	Date getNodeEnter();

	boolean isRoot();

	String getParentTokenName();

	String getParentTokenFullName();

	String getProcessInstanceId();

	Object getToken();
	
}