package es.caib.helium.integracio.service.persones;

import es.caib.helium.integracio.enums.persones.Sexe;

public class Utilitats {

	public static Sexe sexePerNom(String nom) {
		
		String[] parts = nom.trim().split(" ");
		String norm = parts[0];
		norm = norm.replaceAll("[àâ]", "a");
		norm = norm.replaceAll("[èéêë]", "e");
		norm = norm.replaceAll("[ïî]", "i");
		norm = norm.replaceAll("Ô", "o");
		norm = norm.replaceAll("[ûù]", "u");
		norm = norm.replaceAll("[ÀÂ]", "A");
		norm = norm.replaceAll("[ÈÉÊË]", "E");
		norm = norm.replaceAll("[ÏÎ]", "I");
		norm = norm.replaceAll("Ô", "O");
		norm = norm.replaceAll("[ÛÙ]", "U");
	
		if (norm.toLowerCase().endsWith("a")) {
			if (norm.equalsIgnoreCase("sebastia")) {
				return Sexe.SEXE_HOME;
			}
			return Sexe.SEXE_DONA;
		}
		return Sexe.SEXE_HOME;
	}
}
