/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte de domini que representa la pertinença d'un camp a un altre
 * camp de tipus registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampRegistreDto implements Serializable {

	private Long id;
	private boolean obligatori = true;
	private boolean llistar = true;
	private int ordre;
	private CampDto registre;
	private CampDto membre;



	public CampRegistreDto() {}
	public CampRegistreDto(
			CampDto registre,
			CampDto membre,
			int ordre) {
		this.registre = registre;
		this.membre = membre;
		this.ordre = ordre;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}

	public boolean isLlistar() {
		return llistar;
	}
	public void setLlistar(boolean llistar) {
		this.llistar = llistar;
	}

	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public CampDto getRegistre() {
		return registre;
	}
	public void setRegistre(CampDto registre) {
		this.registre = registre;
	}

	public CampDto getMembre() {
		return membre;
	}
	public void setMembre(CampDto membre) {
		this.membre = membre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((membre == null) ? 0 : membre.hashCode());
		result = prime * result
				+ ((registre == null) ? 0 : registre.hashCode());
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
		CampRegistreDto other = (CampRegistreDto) obj;
		if (membre == null) {
			if (other.membre != null)
				return false;
		} else if (!membre.equals(other.membre))
			return false;
		if (registre == null) {
			if (other.registre != null)
				return false;
		} else if (!registre.equals(other.registre))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
