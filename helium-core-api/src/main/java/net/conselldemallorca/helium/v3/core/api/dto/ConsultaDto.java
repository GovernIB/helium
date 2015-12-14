/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Objecte de domini que representa una consulta d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaDto implements Serializable, Comparable<ConsultaDto> {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String valorsPredefinits;
	private String informeNom;
	private byte[] informeContingut;
	private boolean exportarActiu;
	private boolean ocultarActiu;
	private boolean generica;
	private int ordre;
	private ExpedientTipusDto expedientTipus;	

	private Set<ConsultaCampDto> camps = new HashSet<ConsultaCampDto>();
	private String formatExport;

	public ConsultaDto() {}
	public ConsultaDto(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}
	
	public Set<ConsultaCampDto> getCamps() {
		return this.camps;
	}
	public void setCamps(Set<ConsultaCampDto> camps) {
		this.camps= camps;
	}
	public void addCamp(ConsultaCampDto camp) {
		getCamps().add(camp);
	}
	public void removeCamp(ConsultaCampDto camp) {
		getCamps().remove(camp);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getValorsPredefinits() {
		return valorsPredefinits;
	}
	public void setValorsPredefinits(String valorsPredefinits) {
		this.valorsPredefinits = valorsPredefinits;
	}
	public String getInformeNom() {
		return informeNom;
	}
	public void setInformeNom(String informeNom) {
		this.informeNom = informeNom;
	}
	public byte[] getInformeContingut() {
		return informeContingut;
	}
	public void setInformeContingut(byte[] informeContingut) {
		this.informeContingut = informeContingut;
	}
	public boolean isExportarActiu() {
		return exportarActiu;
	}
	public void setExportarActiu(boolean exportarActiu) {
		this.exportarActiu = exportarActiu;
	}
	public boolean isOcultarActiu() {
		return ocultarActiu;
	}
	public void setOcultarActiu(boolean ocultarActiu) {
		this.ocultarActiu = ocultarActiu;
	}
	public boolean isGenerica() {
		return generica;
	}
	public void setGenerica(boolean generica) {
		this.generica = generica;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	public String getFormatExport() {
		return formatExport;
	}
	public void setFormatExport(String formatExport) {
		this.formatExport = formatExport;
	}

	@Override
	public int compareTo(ConsultaDto o) {
		return this.getNom().compareTo(o.getNom());
	}
	
	private static final long serialVersionUID = 1L;
}
