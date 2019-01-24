/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.notificacio;

import java.util.List;

/**
 * Informació d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Enviament {

	private Persona titular;
	private List<Persona> destinataris;
	private EntregaPostalTipus entregaPostalTipus;
	private EntregaPostalViaTipus  entregaPostalViaTipus;
	private String entregaPostalViaNom;
	private String entregaPostalNumeroCasa;
	private String entregaPostalNumeroQualificador;
	private String entregaPostalPuntKm;
	private String entregaPostalApartatCorreus;
	private String entregaPostalPortal;
	private String entregaPostalEscala;
	private String entregaPostalPlanta;
	private String entregaPostalPorta;
	private String entregaPostalBloc;
	private String entregaPostalComplement;
	private String entregaPostalCodiPostal;
	private String entregaPostalPoblacio;
	private String entregaPostalMunicipiCodi;
	private String entregaPostalProvinciaCodi;
	private String entregaPostalPaisCodi;
	private String entregaPostalLinea1;
	private String entregaPostalLinea2;
	private Integer entregaPostalCie;
	private String entregaPostalFormatSobre;
	private String entregaPostalFormatFulla;
	private boolean entregaDehObligat;
	private String entregaDehProcedimentCodi;

	public Persona getTitular() {
		return titular;
	}
	public void setTitular(Persona titular) {
		this.titular = titular;
	}
	public List<Persona> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<Persona> destinataris) {
		this.destinataris = destinataris;
	}
	public EntregaPostalTipus getEntregaPostalTipus() {
		return entregaPostalTipus;
	}
	public void setEntregaPostalTipus(EntregaPostalTipus entregaPostalTipus) {
		this.entregaPostalTipus = entregaPostalTipus;
	}
	public EntregaPostalViaTipus getEntregaPostalViaTipus() {
		return entregaPostalViaTipus;
	}
	public void setEntregaPostalViaTipus(EntregaPostalViaTipus entregaPostalViaTipus) {
		this.entregaPostalViaTipus = entregaPostalViaTipus;
	}
	public String getEntregaPostalViaNom() {
		return entregaPostalViaNom;
	}
	public void setEntregaPostalViaNom(String entregaPostalViaNom) {
		this.entregaPostalViaNom = entregaPostalViaNom;
	}
	public String getEntregaPostalNumeroCasa() {
		return entregaPostalNumeroCasa;
	}
	public void setEntregaPostalNumeroCasa(String entregaPostalNumeroCasa) {
		this.entregaPostalNumeroCasa = entregaPostalNumeroCasa;
	}
	public String getEntregaPostalNumeroQualificador() {
		return entregaPostalNumeroQualificador;
	}
	public void setEntregaPostalNumeroQualificador(String entregaPostalNumeroQualificador) {
		this.entregaPostalNumeroQualificador = entregaPostalNumeroQualificador;
	}
	public String getEntregaPostalPuntKm() {
		return entregaPostalPuntKm;
	}
	public void setEntregaPostalPuntKm(String entregaPostalPuntKm) {
		this.entregaPostalPuntKm = entregaPostalPuntKm;
	}
	public String getEntregaPostalApartatCorreus() {
		return entregaPostalApartatCorreus;
	}
	public void setEntregaPostalApartatCorreus(String entregaPostalApartatCorreus) {
		this.entregaPostalApartatCorreus = entregaPostalApartatCorreus;
	}
	public String getEntregaPostalPortal() {
		return entregaPostalPortal;
	}
	public void setEntregaPostalPortal(String entregaPostalPortal) {
		this.entregaPostalPortal = entregaPostalPortal;
	}
	public String getEntregaPostalEscala() {
		return entregaPostalEscala;
	}
	public void setEntregaPostalEscala(String entregaPostalEscala) {
		this.entregaPostalEscala = entregaPostalEscala;
	}
	public String getEntregaPostalPlanta() {
		return entregaPostalPlanta;
	}
	public void setEntregaPostalPlanta(String entregaPostalPlanta) {
		this.entregaPostalPlanta = entregaPostalPlanta;
	}
	public String getEntregaPostalPorta() {
		return entregaPostalPorta;
	}
	public void setEntregaPostalPorta(String entregaPostalPorta) {
		this.entregaPostalPorta = entregaPostalPorta;
	}
	public String getEntregaPostalBloc() {
		return entregaPostalBloc;
	}
	public void setEntregaPostalBloc(String entregaPostalBloc) {
		this.entregaPostalBloc = entregaPostalBloc;
	}
	public String getEntregaPostalComplement() {
		return entregaPostalComplement;
	}
	public void setEntregaPostalComplement(String entregaPostalComplement) {
		this.entregaPostalComplement = entregaPostalComplement;
	}
	public String getEntregaPostalCodiPostal() {
		return entregaPostalCodiPostal;
	}
	public void setEntregaPostalCodiPostal(String entregaPostalCodiPostal) {
		this.entregaPostalCodiPostal = entregaPostalCodiPostal;
	}
	public String getEntregaPostalPoblacio() {
		return entregaPostalPoblacio;
	}
	public void setEntregaPostalPoblacio(String entregaPostalPoblacio) {
		this.entregaPostalPoblacio = entregaPostalPoblacio;
	}
	public String getEntregaPostalMunicipiCodi() {
		return entregaPostalMunicipiCodi;
	}
	public void setEntregaPostalMunicipiCodi(String entregaPostalMunicipiCodi) {
		this.entregaPostalMunicipiCodi = entregaPostalMunicipiCodi;
	}
	public String getEntregaPostalProvinciaCodi() {
		return entregaPostalProvinciaCodi;
	}
	public void setEntregaPostalProvinciaCodi(String entregaPostalProvinciaCodi) {
		this.entregaPostalProvinciaCodi = entregaPostalProvinciaCodi;
	}
	public String getEntregaPostalPaisCodi() {
		return entregaPostalPaisCodi;
	}
	public void setEntregaPostalPaisCodi(String entregaPostalPaisCodi) {
		this.entregaPostalPaisCodi = entregaPostalPaisCodi;
	}
	public String getEntregaPostalLinea1() {
		return entregaPostalLinea1;
	}
	public void setEntregaPostalLinea1(String entregaPostalLinea1) {
		this.entregaPostalLinea1 = entregaPostalLinea1;
	}
	public String getEntregaPostalLinea2() {
		return entregaPostalLinea2;
	}
	public void setEntregaPostalLinea2(String entregaPostalLinea2) {
		this.entregaPostalLinea2 = entregaPostalLinea2;
	}
	public Integer getEntregaPostalCie() {
		return entregaPostalCie;
	}
	public void setEntregaPostalCie(Integer entregaPostalCie) {
		this.entregaPostalCie = entregaPostalCie;
	}
	public String getEntregaPostalFormatSobre() {
		return entregaPostalFormatSobre;
	}
	public void setEntregaPostalFormatSobre(String entregaPostalFormatSobre) {
		this.entregaPostalFormatSobre = entregaPostalFormatSobre;
	}
	public String getEntregaPostalFormatFulla() {
		return entregaPostalFormatFulla;
	}
	public void setEntregaPostalFormatFulla(String entregaPostalFormatFulla) {
		this.entregaPostalFormatFulla = entregaPostalFormatFulla;
	}
	public boolean isEntregaDehObligat() {
		return entregaDehObligat;
	}
	public void setEntregaDehObligat(boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}
	public String getEntregaDehProcedimentCodi() {
		return entregaDehProcedimentCodi;
	}
	public void setEntregaDehProcedimentCodi(String entregaDehProcedimentCodi) {
		this.entregaDehProcedimentCodi = entregaDehProcedimentCodi;
	}

}
