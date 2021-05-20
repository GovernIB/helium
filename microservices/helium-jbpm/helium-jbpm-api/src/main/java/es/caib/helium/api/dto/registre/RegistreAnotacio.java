/**
 * 
 */
package es.caib.helium.api.dto.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


/**
 * Classe que representa una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreAnotacio {

	private int numero;
	private Date data;
	private String identificador;
	private String entitatCodi;
	private String organ;
	private String organDescripcio;
	private String oficinaCodi;
	private String oficinaDescripcio;
	private String llibreCodi;
	private String llibreDescripcio;
	private String extracte;
	private String assumpteTipusCodi;
	private String assumpteTipusDescripcio;
	private String assumpteCodi;
	private String assumpteDescripcio;
	private String referencia;
	private String expedientNumero;
	private String idiomaCodi;
	private String idiomaDescripcio;
	private String transportTipusCodi;
	private String transportTipusDescripcio;
	private String transportNumero;
	private String usuariCodi;
	private String usuariNom;
	private String usuariContacte;
	private String aplicacioCodi;
	private String aplicacioVersio;
	private String documentacioFisicaCodi;
	private String documentacioFisicaDescripcio;
	private String observacions;
	private String exposa;
	private String solicita;
	private List<RegistreInteressat> interessats;
	private List<RegistreAnnex> annexos;

}
