package net.conselldemallorca.helium.v3.core.api.dto.comanda;

public enum FetEnum {
	EXP_TOT("Expedients totals", "Número d'expedients total"),
	EXP_OBE("Expedients oberts", "Número d'expedients oberts"),
	EXP_TAN("Expedients tancats", "Número d'expedients tancats"),
	EXP_ANUL("Expedients anul·lats", "Número d'expedients anul·lats"),
	EXP_NO_ANUL("Expedients no anul·lats", "Número d'expedients no anul·lats"),
	EXP_ARX("Expedients amb Arxiu", "Número d'expedients integrats amb Arxiu"),
	TAS_PEN("Tasques pendents", "Número de tasques pendents"),
	TAS_FIN("tasques finalitzades", "Número de tasques finalitzades"),
	AN_PEN("Anotacions pendents", "Número d'anotacions pendents"),
	AN_PROC("Anotacions processades", "Número d'anotacions processades"),
	CO_PINBAL("Consultes a PINBAL", "Número de consultes enviades a PINBAL"),
	CO_NOTIB("Consultes a NOTIB", "Número de notificacions i comunicacions realitzades a NOTIB"),
	CO_PORTAFIB("Peticions a PORTAFIB", "Nombre de peticions de signatura fetes a PORTAFIB");

	private String nom;
	private String descripcio;

	FetEnum(String nom, String descripcio) {
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
