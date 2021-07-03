/**
 * 
 */
package es.caib.helium.logic.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Camps relatius als expedients indexats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MesurarTemps {

	public static Map<String, Long> instants;

	public static Map<String, Double> mitjaValors;
	public static Map<String, Integer> mitjaContadors;



	public static void diferenciaReiniciar(String clau) {
		getInstants().put(
				clau,
				new Long(System.currentTimeMillis()));
	}
	public static long diferenciaCalcular(String clau) {
		Long instant = getInstants().get(clau);
		if (instant == null)
			return 0;
		return System.currentTimeMillis() - instant.longValue();
	}
	public static void diferenciaImprimirStdout(String clau) {
		diferenciaImprimirStdout(clau, null);
	}
	public static void diferenciaImprimirStdout(String clau, String text) {
		/*String perAfegir = "";
		if (text != null)
			perAfegir += " " + text + " ";
		logger.debug("---> [" + clau + "]" + perAfegir + ": " + diferenciaCalcular(clau) + "ms");*/
	}
	public static void diferenciaImprimirStdoutIReiniciar(String clau) {
		diferenciaImprimirStdout(clau, null);
		diferenciaReiniciar(clau);
	}
	public static void diferenciaImprimirStdoutIReiniciar(String clau, String text) {
		diferenciaImprimirStdout(clau, text);
		diferenciaReiniciar(clau);
	}

	public static void mitjaReiniciar(String clau) {
		getMitjaValors().put(
				clau,
				new Double(0));
		getMitjaContadors().put(
				clau,
				new Integer(0));
	}
	public static void mitjaCalcular(String clauMitja, String clauDiferencia) {
		double mitjaAntiga = 0;
		if (getMitjaValors().get(clauMitja) != null)
			mitjaAntiga = getMitjaValors().get(clauMitja).doubleValue();
		int contador = 0;
		if (getMitjaContadors().get(clauMitja) != null)
			contador = getMitjaContadors().get(clauMitja).intValue();
		long diferencia = diferenciaCalcular(clauDiferencia);
		diferenciaReiniciar(clauDiferencia);
		double mitja = (mitjaAntiga * contador + diferencia) / (contador + 1);
		getMitjaValors().put(
				clauMitja,
				new Double(mitja));
		getMitjaContadors().put(
				clauMitja,
				new Integer(contador + 1));
	}
	public static void mitjaImprimirStdout(String clau) {
		mitjaImprimirStdout(clau, null);
	}
	public static void mitjaImprimirStdout(String clau, String text) {
		String perAfegir = "";
		if (text != null)
			perAfegir += " " + text;
		logger.debug("---> [" + clau + "]" + perAfegir + ": " + getMitjaValors().get(clau) + "ms (n=" + getMitjaContadors().get(clau) + ")");
	}



	private static Map<String, Long> getInstants() {
		if (instants == null)
			instants = new HashMap<String, Long>();
		return instants;
	}
	private static Map<String, Double> getMitjaValors() {
		if (mitjaValors == null)
			mitjaValors = new HashMap<String, Double>();
		return mitjaValors;
	}
	private static Map<String, Integer> getMitjaContadors() {
		if (mitjaContadors == null)
			mitjaContadors = new HashMap<String, Integer>();
		return mitjaContadors;
	}

	private static final Log logger = LogFactory.getLog(MesurarTemps.class);
}
