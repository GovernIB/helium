package es.caib.helium.disseny.engine;

public interface WNode {

	public enum WNodeType {
		Join,
		Fork,
		State
	};

	WNodeType getNodeType();

	Object getId();

	Object getLeavingTransition(String transicioOK);

}
