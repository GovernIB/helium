/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Objecte de domini que representa una validació de dades
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioDto {

	private Long id;
	private String nom;
	private String expressio;
	private String missatge;
	int ordre;
	private ExpedientTascaDto tasca;
	private CampDto camp;

	public ValidacioDto() {}
	public ValidacioDto(ExpedientTascaDto tasca, String expressio, String missatge) {
		this.expressio = expressio;
		this.missatge = missatge;
		this.tasca = tasca;
	}
	public ValidacioDto(CampDto camp, String expressio, String missatge) {
		this.expressio = expressio;
		this.missatge = missatge;
		this.camp = camp;
	}

	public ExpedientTascaDto getTasca() {
		return tasca;
	}
	public void setTasca(ExpedientTascaDto tasca) {
		this.tasca = tasca;
	}
	public CampDto getCamp() {
		return camp;
	}
	public void setCamp(CampDto camp) {
		this.camp = camp;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getExpressio() {
		return expressio;
	}
	public void setExpressio(String expressio) {
		this.expressio = expressio;
	}

	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}

	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((camp == null) ? 0 : camp.hashCode());
		result = prime * result
				+ ((expressio == null) ? 0 : expressio.hashCode());
		result = prime * result + ((tasca == null) ? 0 : tasca.hashCode());
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
		ValidacioDto other = (ValidacioDto) obj;
		if (camp == null) {
			if (other.camp != null)
				return false;
		} else if (!camp.equals(other.camp))
			return false;
		if (expressio == null) {
			if (other.expressio != null)
				return false;
		} else if (!expressio.equals(other.expressio))
			return false;
		if (tasca == null) {
			if (other.tasca != null)
				return false;
		} else if (!tasca.equals(other.tasca))
			return false;
		return true;
	}
}
