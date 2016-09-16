/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * DTO amb informació d'una consulta per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private String valorsPredefinits;
	private String informeNom;
	private byte[] informeContingut;
	private boolean exportarActiu;
	private boolean ocultarActiu;
	private int ordre;
	private List<ConsultaCampExportacio> camps = new ArrayList<ConsultaCampExportacio>();



	public ConsultaExportacio(
			String codi,
			String nom) {
		this.codi = codi;
		this.nom = nom;
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
	public List<ConsultaCampExportacio> getCamps() {
		return camps;
	}
	public void setCamps(List<ConsultaCampExportacio> camps) {
		this.camps = camps;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}


	private static final long serialVersionUID = 1L;

}
