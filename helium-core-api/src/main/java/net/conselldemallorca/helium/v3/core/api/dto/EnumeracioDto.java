/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Objecte de domini que representa una enumeració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String valors;

	private EntornDto entorn;
	private ExpedientTipusDto expedientTipus;

	private Set<CampDto> camps = new HashSet<CampDto>();
	private List<EnumeracioValorDto> enumeracioValors = new ArrayList<EnumeracioValorDto>();

	public EnumeracioDto() {}
	public EnumeracioDto(EntornDto entorn, String codi, String nom) {
		this.entorn = entorn;
		this.codi = codi;
		this.nom = nom;
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
	public String getValors() {
		return valors;
	}
	public void setValors(String valors) {
		this.valors = valors;
	}
	public EntornDto getEntorn() {
		return entorn;
	}
	public void setEntorn(EntornDto entorn) {
		this.entorn = entorn;
	}
	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	public Set<CampDto> getCamps() {
		return this.camps;
	}
	
	public void setCamps(Set<CampDto> camps) {
		this.camps = camps;
	}
	
	public void addCamp(CampDto camp) {
		getCamps().add(camp);
	}
	
	public void removeCamp(CampDto camp) {
		getCamps().remove(camp);
	}
	
	public List<ParellaCodiValorDto> getLlistaValors() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		if (valors != null) {
			String[] parelles = valors.split(",");
			for (int i = 0; i < parelles.length; i++) {
				String[] parts = parelles[i].split(":");
				if (parts.length == 2)
					resposta.add(new ParellaCodiValorDto(parts[0], parts[1]));
			}
		}
		return resposta;
	}

	public List<EnumeracioValorDto> getEnumeracioValors() {
		return this.enumeracioValors;
	}
	public void setEnumeracioValors(List<EnumeracioValorDto> enumeracioValors) {
		this.enumeracioValors = enumeracioValors;
	}
	public void addEnumeracioValors(EnumeracioValorDto enumeracioValors) {
		getEnumeracioValors().add(enumeracioValors);
	}
	public void removeEnumeracioValors(EnumeracioValorDto enumeracioValors) {
		getEnumeracioValors().remove(enumeracioValors);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((entorn == null) ? 0 : entorn.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnumeracioDto other = (EnumeracioDto) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (entorn == null) {
			if (other.entorn != null)
				return false;
		} else if (!entorn.equals(other.entorn))
			return false;
		return true;
	}

	private static final long serialVersionUID = -4869633305652583392L;

}
