package es.caib.helium.disseny.engine;

import java.util.Date;

public interface WDelegationInfo {

	void setSourceTaskId(String id);

	void setTargetTaskId(String id);

	void setStart(Date date);

	void setComment(String comentari);

	void setSupervised(boolean supervisada);

	String getSourceTaskId();

	boolean isSupervised();

}
