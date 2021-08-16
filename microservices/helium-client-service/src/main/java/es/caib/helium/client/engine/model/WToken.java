package es.caib.helium.client.engine.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

@JsonDeserialize(as = ExecutionDto.class)
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

//	Object getToken();
	
}