package net.conselldemallorca.helium.v3.core.api.dto.comanda;

public enum DimEnum {
	
	UOR("Unitat organitzativa", "Codi de la unitat organitzativa al qual pertany l'expedient"),
	TIP("Tipus d'expedient", "Codi del tipus d'expedient"),
	ENT("Entorn", "Codi de l'entorn al qual pertany l'expedient");
	
	private String nom;
	private String descripcio;

	DimEnum(String nom, String descripcio) {
		this.nom = nom;
		this.descripcio = descripcio;
	}

	public String getNom() {
		return nom;
	}

	public String getDescripcio() {
		return descripcio;
	}
}
