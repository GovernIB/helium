package es.caib.helium.logic.flowable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;

/**
 * Handler de prova que no depèn de cap motor de workflow. Guarda el que ha rebut a cada execució.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HandlerProva implements HeliumBpmnHandler {

	static final List<HandlerProva> EXECUCIONS = Collections.synchronizedList(new ArrayList<>());

	String text;
	String valorVariable;
	Integer numero;
	HeliumApi heliumApi;

	@Override
	public void execute(HeliumApi heliumApi) {
		this.heliumApi = heliumApi;
		EXECUCIONS.add(this);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setValorVariable(String valorVariable) {
		this.valorVariable = valorVariable;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

}
