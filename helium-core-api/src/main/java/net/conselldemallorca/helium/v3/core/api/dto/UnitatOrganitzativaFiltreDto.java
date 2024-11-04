/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per al filtre d'anotacions de distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatOrganitzativaFiltreDto {

	private String codi;
	private String denominacio;
	private String cif;
	
	private String codiUnitatSuperior;
	private UnitatOrganitzativaEstatEnumDto estat;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getDenominacio() {
		return denominacio;
	}

	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}

	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
	}

	public UnitatOrganitzativaEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(UnitatOrganitzativaEstatEnumDto estat) {
		this.estat = estat;
	}

	
}
