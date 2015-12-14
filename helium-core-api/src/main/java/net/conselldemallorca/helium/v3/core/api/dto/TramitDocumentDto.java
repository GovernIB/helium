/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.List;

/**
 * DTO amb informació d'un document d'un tràmit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitDocumentDto {

	protected String nom;
	protected String identificador;
	protected int instanciaNumero;

	protected boolean telematic;
	protected String telematicArxiuNom;
	protected String telematicArxiuExtensio;
	protected byte[] telematicArxiuContingut;
	protected String telematicReferenciaGestorDocumental;
	protected List<TramitDocumentSignaturaDto> telematicSignatures;
	protected long telematicReferenciaCodi;
	protected String telematicReferenciaClau;
	protected Boolean telematicEstructurat;

	protected boolean presencial;
	protected String presencialTipus;
	protected String presencialDocumentCompulsar;
	protected String presencialFotocopia;
	protected String presencialSignatura;

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public int getInstanciaNumero() {
		return instanciaNumero;
	}
	public void setInstanciaNumero(int instanciaNumero) {
		this.instanciaNumero = instanciaNumero;
	}
	public boolean isTelematic() {
		return telematic;
	}
	public void setTelematic(boolean telematic) {
		this.telematic = telematic;
	}
	public String getTelematicArxiuNom() {
		return telematicArxiuNom;
	}
	public void setTelematicArxiuNom(String telematicArxiuNom) {
		this.telematicArxiuNom = telematicArxiuNom;
	}
	public String getTelematicArxiuExtensio() {
		return telematicArxiuExtensio;
	}
	public void setTelematicArxiuExtensio(String telematicArxiuExtensio) {
		this.telematicArxiuExtensio = telematicArxiuExtensio;
	}
	public byte[] getTelematicArxiuContingut() {
		return telematicArxiuContingut;
	}
	public void setTelematicArxiuContingut(byte[] telematicArxiuContingut) {
		this.telematicArxiuContingut = telematicArxiuContingut;
	}
	public String getTelematicReferenciaGestorDocumental() {
		return telematicReferenciaGestorDocumental;
	}
	public void setTelematicReferenciaGestorDocumental(
			String telematicReferenciaGestorDocumental) {
		this.telematicReferenciaGestorDocumental = telematicReferenciaGestorDocumental;
	}
	public List<TramitDocumentSignaturaDto> getTelematicSignatures() {
		return telematicSignatures;
	}
	public void setTelematicSignatures(
			List<TramitDocumentSignaturaDto> telematicSignatures) {
		this.telematicSignatures = telematicSignatures;
	}
	public long getTelematicReferenciaCodi() {
		return telematicReferenciaCodi;
	}
	public void setTelematicReferenciaCodi(long telematicReferenciaCodi) {
		this.telematicReferenciaCodi = telematicReferenciaCodi;
	}
	public String getTelematicReferenciaClau() {
		return telematicReferenciaClau;
	}
	public void setTelematicReferenciaClau(String telematicReferenciaClau) {
		this.telematicReferenciaClau = telematicReferenciaClau;
	}
	public Boolean getTelematicEstructurat() {
		return telematicEstructurat;
	}
	public void setTelematicEstructurat(Boolean telematicEstructurat) {
		this.telematicEstructurat = telematicEstructurat;
	}
	public boolean isPresencial() {
		return presencial;
	}
	public void setPresencial(boolean presencial) {
		this.presencial = presencial;
	}
	public String getPresencialTipus() {
		return presencialTipus;
	}
	public void setPresencialTipus(String presencialTipus) {
		this.presencialTipus = presencialTipus;
	}
	public String getPresencialDocumentCompulsar() {
		return presencialDocumentCompulsar;
	}
	public void setPresencialDocumentCompulsar(String presencialDocumentCompulsar) {
		this.presencialDocumentCompulsar = presencialDocumentCompulsar;
	}
	public String getPresencialFotocopia() {
		return presencialFotocopia;
	}
	public void setPresencialFotocopia(String presencialFotocopia) {
		this.presencialFotocopia = presencialFotocopia;
	}
	public String getPresencialSignatura() {
		return presencialSignatura;
	}
	public void setPresencialSignatura(String presencialSignatura) {
		this.presencialSignatura = presencialSignatura;
	}

	public TramitDocumentSignaturaDto newDocumentSignatura(
			byte[] signatura,
			String format) {
		TramitDocumentSignaturaDto dto = new TramitDocumentSignaturaDto();
		dto.setSignatura(signatura);
		dto.setFormat(format);
		return dto;
	}

	public class TramitDocumentSignaturaDto {

		protected byte[] signatura;
		protected String format;

		public byte[] getSignatura() {
			return signatura;
		}
		public void setSignatura(byte[] signatura) {
			this.signatura = signatura;
		}
		public String getFormat() {
			return format;
		}
		public void setFormat(String format) {
			this.format = format;
		}

	}

}
