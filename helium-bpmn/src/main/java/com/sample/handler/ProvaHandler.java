package com.sample.handler;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.HeliumActionHandler;
import lombok.Setter;

@Setter
public class ProvaHandler implements HeliumActionHandler {

	private String variable1;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		System.out.println("Executant handler de prova");
		System.out.println("\tvariable1: " + variable1);
		System.out.println("Fi de l'execució del handler de prova");
	}

}
