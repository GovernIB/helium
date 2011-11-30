/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Camps relatius als expedients indexats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MesurarTemps {

	public static Map<String, Long> instants;



	public static void reiniciarInstant(String clau) {
		getInstants().put(
				clau,
				new Long(System.currentTimeMillis()));
	}
	public static long obtenirDiferencia(String clau) {
		Long instant = getInstants().get(clau);
		if (instant == null)
			return 0;
		return System.currentTimeMillis() - instant.longValue();
	}
	public static void imprimirInstantStdout(String clau) {
		imprimirInstantStdout(clau, null);
	}
	public static void imprimirInstantStdout(String clau, String text) {
		String perAfegir = "";
		if (text != null)
			perAfegir += " " + text + " ";
		System.out.println("---> [" + clau + "]" + perAfegir + ": " + obtenirDiferencia(clau));
	}
	public static void imprimirInstantStdoutIReiniciar(String clau) {
		imprimirInstantStdout(clau, null);
		reiniciarInstant(clau);
	}
	public static void imprimirInstantStdoutIReiniciar(String clau, String text) {
		imprimirInstantStdout(clau, text);
		reiniciarInstant(clau);
	}



	private static Map<String, Long> getInstants() {
		if (instants == null)
			instants = new HashMap<String, Long>();
		return instants;
	}

}
