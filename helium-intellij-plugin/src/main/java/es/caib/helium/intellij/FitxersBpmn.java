package es.caib.helium.intellij;

import java.text.Normalizer;
import java.util.Locale;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * Fitxers que s'obren amb l'editor visual de Helium: les definicions de procés BPMN 2.0 ({@code .bpmn} i
 * {@code .bpmn20.xml}, la convenció de Flowable).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
final class FitxersBpmn {

	private FitxersBpmn() {
	}

	static boolean esBpmn(VirtualFile file) {
		return file != null && !file.isDirectory() && esNomBpmn(file.getName());
	}

	static boolean esNomBpmn(String nom) {
		if (nom == null) {
			return false;
		}
		String minuscules = nom.toLowerCase(Locale.ROOT);
		return minuscules.endsWith(".bpmn") || minuscules.endsWith(".bpmn20.xml");
	}

	/**
	 * Nom del procés per a un fitxer nou: el que ha escrit l'usuari, sense directoris ni extensió
	 * ({@code "alta/Alta d'expedient.bpmn"} → {@code "Alta d'expedient"}).
	 */
	static String nomProces(String nomFitxer) {
		String nom = nomFitxer.substring(Math.max(nomFitxer.lastIndexOf('/'), nomFitxer.lastIndexOf('\\')) + 1).trim();
		String minuscules = nom.toLowerCase(Locale.ROOT);
		for (String extensio : new String[] {".bpmn20.xml", ".bpmn"}) {
			if (minuscules.endsWith(extensio)) {
				return nom.substring(0, nom.length() - extensio.length()).trim();
			}
		}
		return nom;
	}

	/**
	 * Identificador del procés (clau de la definició a Flowable) a partir del nom: ha de ser un nom XML (NCName), i
	 * només amb lletres ASCII perquè es fa servir a URLs i expressions. Els accents es treuen i la resta de caràcters
	 * passen a {@code _} ({@code "Alta d'expedient"} → {@code "Alta_d_expedient"}).
	 */
	static String idProces(String nomProces) {
		String id = Normalizer.normalize(nomProces, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.replaceAll("[^A-Za-z0-9_.-]+", "_")
				.replaceAll("^_+|_+$", "");
		if (id.isEmpty()) {
			return "Process_1";
		}
		// Un nom XML no pot començar per un dígit, un punt ni un guió
		return Character.isLetter(id.charAt(0)) ? id : "Process_" + id;
	}

}
