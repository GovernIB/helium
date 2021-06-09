package es.caib.helium.integracio.domini.tramitacio;

import es.caib.helium.integracio.enums.tramitacio.ResultatProcesTipus;
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
