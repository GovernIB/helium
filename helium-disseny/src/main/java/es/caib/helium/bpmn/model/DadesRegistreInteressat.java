package es.caib.helium.bpmn.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Interessat o representant d'una anotació de registre feta des d'un handler.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DadesRegistreInteressat {

	/** Tipus d'interessat (nom de {@code RegistreInteressatTipusEnum}). */
	private String tipus;
	/** Tipus de document (nom de {@code RegistreInteressatDocumentTipusEnum}). */
	private String documentTipus;
	private String documentNum;
	private String email;
	private String telefon;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String pais;
	private String provincia;
	private String municipi;
	private String codiPostal;
	private String adresa;

}
