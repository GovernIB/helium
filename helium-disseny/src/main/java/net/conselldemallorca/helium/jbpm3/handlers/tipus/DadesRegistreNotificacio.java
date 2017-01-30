/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;



/**
 * Classe amb informació sobre un seient registral de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesRegistreNotificacio implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String numero;
	private Date data;

	private String organCodi;
	private String oficinaCodi;
	private boolean interessatAutenticat;
	private String interessatEntitatCodi;
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
	private String interessatEmail;
	private String interessatMobil;
	
	private String representatNif;
	private String representatNomAmbCognoms;
	private String representatNom;
	private String representatCognom1;
	private String representatCognom2;
	private String expedientIdentificador;
	private String expedientClau;
	private String expedientUnitatAdministrativa;
	private String anotacioIdiomaCodi;
	private String anotacioTipusAssumpte;
	private String anotacioAssumpte;
	private boolean notificacioJustificantRecepcio;
	private String notificacioAvisTitol;
	private String notificacioAvisText;
	private String notificacioAvisTextSms;
	private String notificacioOficiTitol;
	private String notificacioOficiText;
	private String notificacioSubsanacioTramitIdentificador;
	private int notificacioSubsanacioTramitVersio;
	private String notificacioSubsanacioTramitDescripcio;
	private Map<String, String> notificacioSubsanacioParametres;
	private boolean notificacioCrearExpedient;



	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
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
	public boolean isInteressatAutenticat() {
		return interessatAutenticat;
	}
	public void setInteressatAutenticat(boolean interessatAutenticat) {
		this.interessatAutenticat = interessatAutenticat;
	}
	public String getInteressatEntitatCodi() {
		return interessatEntitatCodi;
	}
	public void setInteressatEntitatCodi(String interessatEntitatCodi) {
		this.interessatEntitatCodi = interessatEntitatCodi;
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
	public String getInteressatNom() {
		return interessatNom;
	}
	public void setInteressatNom(String interessatNom) {
		this.interessatNom = interessatNom;
	}
	public String getInteressatCognom1() {
		return interessatCognom1;
	}
	public void setInteressatCognom1(String interessatCognom1) {
		this.interessatCognom1 = interessatCognom1;
	}
	public String getInteressatCognom2() {
		return interessatCognom2;
	}
	public void setInteressatCognom2(String interessatCognom2) {
		this.interessatCognom2 = interessatCognom2;
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
	public String getRepresentatNom() {
		return representatNom;
	}
	public void setRepresentatNom(String representatNom) {
		this.representatNom = representatNom;
	}
	public String getRepresentatCognom1() {
		return representatCognom1;
	}
	public void setRepresentatCognom1(String representatCognom1) {
		this.representatCognom1 = representatCognom1;
	}
	public String getRepresentatCognom2() {
		return representatCognom2;
	}
	public void setRepresentatCognom2(String representatCognom2) {
		this.representatCognom2 = representatCognom2;
	}
	public String getExpedientIdentificador() {
		return expedientIdentificador;
	}
	public void setExpedientIdentificador(String expedientIdentificador) {
		this.expedientIdentificador = expedientIdentificador;
	}
	public String getExpedientClau() {
		return expedientClau;
	}
	public void setExpedientClau(String expedientClau) {
		this.expedientClau = expedientClau;
	}
	public String getExpedientUnitatAdministrativa() {
		return expedientUnitatAdministrativa;
	}
	public void setExpedientUnitatAdministrativa(String expedientUnitatAdministrativa) {
		this.expedientUnitatAdministrativa = expedientUnitatAdministrativa;
	}
	public String getAnotacioIdiomaCodi() {
		return anotacioIdiomaCodi;
	}
	public void setAnotacioIdiomaCodi(String anotacioIdiomaCodi) {
		this.anotacioIdiomaCodi = anotacioIdiomaCodi;
	}
	public String getAnotacioTipusAssumpte() {
		return anotacioTipusAssumpte;
	}
	public void setAnotacioTipusAssumpte(String anotacioTipusAssumpte) {
		this.anotacioTipusAssumpte = anotacioTipusAssumpte;
	}
	public String getAnotacioAssumpte() {
		return anotacioAssumpte;
	}
	public void setAnotacioAssumpte(String anotacioAssumpte) {
		this.anotacioAssumpte = anotacioAssumpte;
	}
	public boolean isNotificacioJustificantRecepcio() {
		return notificacioJustificantRecepcio;
	}
	public void setNotificacioJustificantRecepcio(
			boolean notificacioJustificantRecepcio) {
		this.notificacioJustificantRecepcio = notificacioJustificantRecepcio;
	}
	public String getNotificacioAvisTitol() {
		return notificacioAvisTitol;
	}
	public void setNotificacioAvisTitol(String notificacioAvisTitol) {
		this.notificacioAvisTitol = notificacioAvisTitol;
	}
	public String getNotificacioAvisText() {
		return notificacioAvisText;
	}
	public void setNotificacioAvisText(String notificacioAvisText) {
		this.notificacioAvisText = notificacioAvisText;
	}
	public String getNotificacioAvisTextSms() {
		return notificacioAvisTextSms;
	}
	public void setNotificacioAvisTextSms(String notificacioAvisTextSms) {
		this.notificacioAvisTextSms = notificacioAvisTextSms;
	}
	public String getNotificacioOficiTitol() {
		return notificacioOficiTitol;
	}
	public void setNotificacioOficiTitol(String notificacioOficiTitol) {
		this.notificacioOficiTitol = notificacioOficiTitol;
	}
	public String getNotificacioOficiText() {
		return notificacioOficiText;
	}
	public void setNotificacioOficiText(String notificacioOficiText) {
		this.notificacioOficiText = notificacioOficiText;
	}
	public String getNotificacioSubsanacioTramitIdentificador() {
		return notificacioSubsanacioTramitIdentificador;
	}
	public void setNotificacioSubsanacioTramitIdentificador(
			String notificacioSubsanacioTramitIdentificador) {
		this.notificacioSubsanacioTramitIdentificador = notificacioSubsanacioTramitIdentificador;
	}
	public int getNotificacioSubsanacioTramitVersio() {
		return notificacioSubsanacioTramitVersio;
	}
	public void setNotificacioSubsanacioTramitVersio(
			int notificacioSubsanacioTramitVersio) {
		this.notificacioSubsanacioTramitVersio = notificacioSubsanacioTramitVersio;
	}
	public String getNotificacioSubsanacioTramitDescripcio() {
		return notificacioSubsanacioTramitDescripcio;
	}
	public void setNotificacioSubsanacioTramitDescripcio(
			String notificacioSubsanacioTramitDescripcio) {
		this.notificacioSubsanacioTramitDescripcio = notificacioSubsanacioTramitDescripcio;
	}
	public Map<String, String> getNotificacioSubsanacioParametres() {
		return notificacioSubsanacioParametres;
	}
	public void setNotificacioSubsanacioParametres(
			Map<String, String> notificacioSubsanacioParametres) {
		this.notificacioSubsanacioParametres = notificacioSubsanacioParametres;
	}
	public boolean isNotificacioCrearExpedient() {
		return notificacioCrearExpedient;
	}
	public void setNotificacioCrearExpedient(boolean notificacioCrearExpedient) {
		this.notificacioCrearExpedient = notificacioCrearExpedient;
	}
	public String getInteressatEmail() {
		return interessatEmail;
	}
	public void setInteressatEmail(String interessatEmail) {
		this.interessatEmail = interessatEmail;
	}
	public String getInteressatMobil() {
		return interessatMobil;
	}
	public void setInteressatMobil(String interessatMobil) {
		this.interessatMobil = interessatMobil;
	}

}
