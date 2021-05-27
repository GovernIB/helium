package es.caib.helium.integracio.domini.registre;

import es.caib.helium.integracio.enums.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.helium.integracio.enums.registre.RegistreInteressatTipusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistreAssentamentInteressat {

	private RegistreInteressatTipusEnum tipus;
	private RegistreInteressatDocumentTipusEnum documentTipus;
	private String documentNum;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String pais;
	private String provincia;
	private String municipi;
	private String adresa;
	private String codiPostal;
	private String email;
	private String telefon;
	private String emailHabilitat;
	private String canalPreferent;
	private String observacions;
	private RegistreAssentamentInteressat representant;
}
