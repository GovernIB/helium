package es.caib.helium.integracio.domini.notificacio;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.caib.helium.integracio.enums.notificacio.InteressatTipusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaNotib {
	
	private String nom;
	private String llinatge1;
	private String llinatge2;
	@JsonProperty("dni")
	private String nif;
	private String telefon;
	private String email;
	private InteressatTipusEnum tipus;
	private String codiDir3;
}
