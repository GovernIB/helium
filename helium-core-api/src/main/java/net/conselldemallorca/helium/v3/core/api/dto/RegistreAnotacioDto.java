/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.List;


/**
 * DTO amb informació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnotacioDto {

	private RegistreIdDto id;

	private String organCodi;
	private String oficinaCodi;
	private String entitatCodi;
	private String unitatAdministrativa;

	private String interessatNif;
	private String interessatNomAmbCognoms;
	private String interessatPaisCodi;
	private String interessatPaisNom;
	private String interessatProvinciaCodi;
	private String interessatProvinciaNom;
	private String interessatMunicipiCodi;
	private String interessatMunicipiNom;
	private boolean interessatAutenticat;

	private String representatNif;
	private String representatNomAmbCognoms;

	private String assumpteIdiomaCodi;
	private String assumpteTipus;
	private String assumpteExtracte;
	private String assumpteRegistreNumero;
	private String assumpteRegistreAny;

	private List<RegistreAnnexDto> annexos;



	public RegistreIdDto getId() {
		return id;
	}
	public void setId(RegistreIdDto id) {
		this.id = id;
	}
	public String getOrganCodi() {
		return organCodi;
	}
	public void setOrganCodi(String organCodi) {
		this.organCodi = organCodi;
	}
	public String getOficinaCodi() {
		return oficinaCodi;
	}
	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public String getInteressatNif() {
		return interessatNif;
	}
	public void setInteressatNif(String interessatNif) {
		this.interessatNif = interessatNif;
	}
	public String getInteressatNomAmbCognoms() {
		return interessatNomAmbCognoms;
	}
	public void setInteressatNomAmbCognoms(String interessatNomAmbCognoms) {
		this.interessatNomAmbCognoms = interessatNomAmbCognoms;
	}
	public String getInteressatPaisCodi() {
		return interessatPaisCodi;
	}
	public void setInteressatPaisCodi(String interessatPaisCodi) {
		this.interessatPaisCodi = interessatPaisCodi;
	}
	public String getInteressatPaisNom() {
		return interessatPaisNom;
	}
	public void setInteressatPaisNom(String interessatPaisNom) {
		this.interessatPaisNom = interessatPaisNom;
	}
	public String getInteressatProvinciaCodi() {
		return interessatProvinciaCodi;
	}
	public void setInteressatProvinciaCodi(String interessatProvinciaCodi) {
		this.interessatProvinciaCodi = interessatProvinciaCodi;
	}
	public String getInteressatProvinciaNom() {
		return interessatProvinciaNom;
	}
	public void setInteressatProvinciaNom(String interessatProvinciaNom) {
		this.interessatProvinciaNom = interessatProvinciaNom;
	}
	public String getInteressatMunicipiCodi() {
		return interessatMunicipiCodi;
	}
	public void setInteressatMunicipiCodi(String interessatMunicipiCodi) {
		this.interessatMunicipiCodi = interessatMunicipiCodi;
	}
	public String getInteressatMunicipiNom() {
		return interessatMunicipiNom;
	}
	public void setInteressatMunicipiNom(String interessatMunicipiNom) {
		this.interessatMunicipiNom = interessatMunicipiNom;
	}
	public boolean isInteressatAutenticat() {
		return interessatAutenticat;
	}
	public void setInteressatAutenticat(boolean interessatAutenticat) {
		this.interessatAutenticat = interessatAutenticat;
	}
	public String getRepresentatNif() {
		return representatNif;
	}
	public void setRepresentatNif(String representatNif) {
		this.representatNif = representatNif;
	}
	public String getRepresentatNomAmbCognoms() {
		return representatNomAmbCognoms;
	}
	public void setRepresentatNomAmbCognoms(String representatNomAmbCognoms) {
		this.representatNomAmbCognoms = representatNomAmbCognoms;
	}
	public String getAssumpteIdiomaCodi() {
		return assumpteIdiomaCodi;
	}
	public void setAssumpteIdiomaCodi(String assumpteIdiomaCodi) {
		this.assumpteIdiomaCodi = assumpteIdiomaCodi;
	}
	public String getAssumpteTipus() {
		return assumpteTipus;
	}
	public void setAssumpteTipus(String assumpteTipus) {
		this.assumpteTipus = assumpteTipus;
	}
	public String getAssumpteExtracte() {
		return assumpteExtracte;
	}
	public void setAssumpteExtracte(String assumpteExtracte) {
		this.assumpteExtracte = assumpteExtracte;
	}
	public String getAssumpteRegistreNumero() {
		return assumpteRegistreNumero;
	}
	public void setAssumpteRegistreNumero(String assumpteRegistreNumero) {
		this.assumpteRegistreNumero = assumpteRegistreNumero;
	}
	public String getAssumpteRegistreAny() {
		return assumpteRegistreAny;
	}
	public void setAssumpteRegistreAny(String assumpteRegistreAny) {
		this.assumpteRegistreAny = assumpteRegistreAny;
	}
	public List<RegistreAnnexDto> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<RegistreAnnexDto> annexos) {
		this.annexos = annexos;
	}

}
