package es.caib.helium.client.integracio.registre.model;

import es.caib.helium.client.integracio.registre.enums.RegistreInteressatDocumentTipusEnum;
import es.caib.helium.client.integracio.registre.enums.RegistreInteressatTipusEnum;
import lombok.Data;

@Data
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
