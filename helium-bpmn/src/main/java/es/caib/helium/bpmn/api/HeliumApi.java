package es.caib.helium.bpmn.api;

import es.caib.helium.bpmn.model.DocumentInfo;

public interface HeliumApi {

	Object getVariable(String codi);
	void setVariable(String codi, Object valor);
	<T> T getVariableDefaultValue(String codi, T defaultValue);

	DocumentInfo getDocument(String documentCodi);

	void expedientEstatModificar(String codi);

	void expedientComentariModificar(String comentari);



}
