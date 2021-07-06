package es.caib.helium.client.integracio.notificacio.model;

import es.caib.helium.client.integracio.notificacio.enums.InteressatTipusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaNotib {
	
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String telefon;
	private String email;
	private InteressatTipusEnum tipus;
	private String codiDir3;
}
