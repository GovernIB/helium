package es.caib.helium.intellij;

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

}
