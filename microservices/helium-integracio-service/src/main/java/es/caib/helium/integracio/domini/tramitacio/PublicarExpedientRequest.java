package es.caib.helium.integracio.domini.tramitacio;

import java.util.List;

import lombok.Data;

@Data
public class PublicarExpedientRequest {

	//TODO @VALIDS
	protected String expedientIdentificador;
	protected String expedientClau;
	protected long unitatAdministrativa;
	protected String idioma;
	protected String descripcio;
	protected boolean autenticat;
	protected String representantNif;
	protected String representatNif;
	protected String representatNom;
	protected String representatApe1;
	protected String representatApe2;
	protected String tramitNumero;
	protected boolean avisosHabilitat;
	protected String avisosSMS;
	protected String avisosEmail;
	protected List<Event> events;
	protected String codiProcediment;
}
