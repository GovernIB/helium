/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto.procediment;

import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;

/**
 * Informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentDto implements Comparable<ProcedimentDto>{

	private Long id;
	private String codi;
	private String nom;
	private String codiSia;
	private ProcedimentEstatEnumDto estat;
	private UnitatOrganitzativaDto unitatOrganitzativa;

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
	public String getCodiSia() {
		return codiSia;
	}
	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}
	public ProcedimentEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(ProcedimentEstatEnumDto estat) {
		this.estat = estat;
	}
	public UnitatOrganitzativaDto getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public void setUnitatOrganitzativa(UnitatOrganitzativaDto unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	public String getCodiNom() {
		return codiSia + " - " + nom;
	}
	public String getCodiNomEstat() {
		return codiSia + " - " + nom + " => " + estat;
	}
	
	@Override
	public int compareTo(ProcedimentDto o) {
		int ret;
		try {
			ret = ((Long.valueOf(codiSia)).compareTo(Long.valueOf(o.getCodiSia())));
		} catch( Exception e) {
			ret = codiSia.compareTo(o.getCodiSia());
		}
		return ret;
	}

}
