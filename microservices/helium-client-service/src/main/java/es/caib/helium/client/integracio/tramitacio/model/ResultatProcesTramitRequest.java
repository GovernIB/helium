package es.caib.helium.client.integracio.tramitacio.model;

import es.caib.helium.client.integracio.tramitacio.enums.ResultatProcesTipus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultatProcesTramitRequest {
	
	protected String numeroEntrada;
	protected String clauAcces;
	protected ResultatProcesTipus resultatProces;
	protected String errorDescripcio;
}
