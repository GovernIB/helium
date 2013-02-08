/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.HashSet;
import java.util.Set;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Command representa una consulta d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaCommand {

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

	private Entorn entorn;
	private ExpedientTipus expedientTipus;

	private Set<ConsultaCamp> camps = new HashSet<ConsultaCamp>();

	private Set<Consulta> subConsultes = new HashSet<Consulta>();
	private Set<Consulta> superConsultes = new HashSet<Consulta>();
	private String formatExport;
	
	
	public ConsultaCommand() {
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
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	public Set<ConsultaCamp> getCamps() {
		return camps;
	}
	public void setCamps(Set<ConsultaCamp> camps) {
		this.camps = camps;
	}
	public Set<Consulta> getSubConsultes() {
		return subConsultes;
	}
	public void setSubConsultes(Set<Consulta> subConsultes) {
		this.subConsultes = subConsultes;
	}
	public Set<Consulta> getSuperConsultes() {
		return superConsultes;
	}
	public void setSuperConsultes(Set<Consulta> superConsultes) {
		this.superConsultes = superConsultes;
	}
	public String getFormatExport() {
		return formatExport;
	}
	public void setFormatExport(String formatExport) {
		this.formatExport = formatExport;
	}


	


}
