package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.ArrayList;
import java.util.List;

public enum PortafirmesEstatEnum {
	BLOQUEJAT,
	PENDENT,	// El document s'ha enviat però encara no s'ha rebut al callback cap resposta
	SIGNAT,		// S'ha rebut petició al callback indicant que el document ha estat signat
	REBUTJAT,	// S'ha rebut petició al callback indicant que el document ha estat rebujat
	PROCESSAT,	// El document signat o rebujat s'ha processat correctament
	CANCELAT,	// El document s'ha esborrat de l'expedient
	ERROR,		// El document s'ha intentat processar i ha produit un error
	ESBORRAT;	// S'ha esborrat l'expedient al qual pertany el document
	
	public static List<PortafirmesEstatEnum> getPendents() {
		List<PortafirmesEstatEnum> estats = new ArrayList<PortafirmesEstatEnum>();
		estats.add(PortafirmesEstatEnum.PENDENT);
		estats.add(PortafirmesEstatEnum.SIGNAT);
		estats.add(PortafirmesEstatEnum.REBUTJAT);
		estats.add(PortafirmesEstatEnum.ERROR);
		return estats;
	}
}
