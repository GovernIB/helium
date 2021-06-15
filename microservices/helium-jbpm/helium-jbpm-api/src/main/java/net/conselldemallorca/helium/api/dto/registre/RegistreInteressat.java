/**
 * 
 */
package net.conselldemallorca.helium.api.dto.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa un interessat d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreInteressat {

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
	private RegistreInteressat representant;

}
