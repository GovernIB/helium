package es.caib.helium.bpmn.engine;

public interface WTransitionLog {

	WNode getDestinationNode();

	Object getSourceNode();

	WToken getToken();

}
