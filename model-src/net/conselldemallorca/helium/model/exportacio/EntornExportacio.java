/**
 * 
 */
package net.conselldemallorca.helium.model.exportacio;

import java.io.Serializable;
import java.util.List;

/**
 * DTO amb informació per exportar/importar dades d'un entorn
 * cap a/des de un altre Helium.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class EntornExportacio implements Serializable {

	private List<AreaTipusExportacio> areesTipus;
	private List<AreaExportacio> arees;
	private List<CarrecExportacio> carrecs;
	private List<DominiExportacio> dominis;
	private List<EnumeracioExportacio> enumeracions;
	private List<ExpedientTipusExportacio> expedientsTipus;



	public List<AreaTipusExportacio> getAreesTipus() {
		return areesTipus;
	}
	public void setAreesTipus(List<AreaTipusExportacio> areesTipus) {
		this.areesTipus = areesTipus;
	}
	public List<AreaExportacio> getArees() {
		return arees;
	}
	public void setArees(List<AreaExportacio> arees) {
		this.arees = arees;
	}
	public List<CarrecExportacio> getCarrecs() {
		return carrecs;
	}
	public void setCarrecs(List<CarrecExportacio> carrecs) {
		this.carrecs = carrecs;
	}
	public List<DominiExportacio> getDominis() {
		return dominis;
	}
	public void setDominis(List<DominiExportacio> dominis) {
		this.dominis = dominis;
	}
	public List<EnumeracioExportacio> getEnumeracions() {
		return enumeracions;
	}
	public void setEnumeracions(List<EnumeracioExportacio> enumeracions) {
		this.enumeracions = enumeracions;
	}
	public List<ExpedientTipusExportacio> getExpedientsTipus() {
		return expedientsTipus;
	}
	public void setExpedientsTipus(List<ExpedientTipusExportacio> expedientsTipus) {
		this.expedientsTipus = expedientsTipus;
	}



	private static final long serialVersionUID = 1L;

}
