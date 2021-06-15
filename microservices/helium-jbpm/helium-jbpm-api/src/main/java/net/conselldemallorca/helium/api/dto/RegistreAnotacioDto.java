/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * DTO amb informació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreAnotacioDto {

	private RegistreIdDto id;

	private String organCodi;
	private String oficinaCodi;
	private String entitatCodi;
	private String unitatAdministrativa;

	private String interessatNif;
	private String interessatNomAmbCognoms;
	private String interessatNom;
	private String interessatCognom1;
	private String interessatCognom2;
	private String interessatPaisCodi;
	private String interessatPaisNom;
	private String interessatProvinciaCodi;
	private String interessatProvinciaNom;
	private String interessatMunicipiCodi;
	private String interessatMunicipiNom;
	private boolean interessatAutenticat;
	private String interessatEmail;
	private String interessatMobil;

	private String representatNif;
	private String representatNomAmbCognoms;
	private String representatNom;
	private String representatCognom1;
	private String representatCognom2;

	private String assumpteIdiomaCodi;
	private String assumpteTipus;
	private String assumpteExtracte;
	private String assumpteRegistreNumero;
	private String assumpteRegistreAny;

	private List<RegistreAnnexDto> annexos;

}
