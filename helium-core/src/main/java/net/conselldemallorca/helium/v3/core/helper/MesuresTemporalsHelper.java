/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Helper per a mesurar intervals de temps i fer-ne estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MesuresTemporalsHelper {

	private Map<String, Long> intervalsInici = new HashMap<String, Long>();
	private Map<String, Long> intervalsDarreraMesura = new HashMap<String, Long>();
	private Map<String, Double> intervalsMitja = new HashMap<String, Double>();
	private Map<String, Integer> intervalsContador = new HashMap<String, Integer>();



	public void mesuraIniciar(String clau) {
		intervalsInici.put(
				clau,
				new Long(System.currentTimeMillis()));
	}
	public void mesuraCalcular(String clau) {
		Long inici = intervalsInici.get(clau);
		if (inici != null) {
			long diferencia = System.currentTimeMillis() - inici.longValue();
			intervalsDarreraMesura.put(clau, new Long(diferencia));
			Integer contador = intervalsContador.get(clau);
			if (contador == null)
				contador = new Integer(1);
			else
				contador = new Integer(contador.intValue() + 1);
			intervalsContador.put(clau, contador);
			Double mitja = intervalsMitja.get(clau);
			if (mitja == null)
				mitja = new Double(diferencia);
			else
				mitja = new Double((mitja * contador + diferencia) / (contador + 1));
			intervalsMitja.put(clau, mitja);
			intervalsInici.remove(clau);
		}
	}

	public void intervalNetejar(String clau) {
		intervalsInici.remove(clau);
		intervalsDarreraMesura.remove(clau);
		intervalsMitja.remove(clau);
		intervalsContador.remove(clau);
	}

	public Long getDarreraMesura(String clau) {
		return intervalsDarreraMesura.get(clau);
	}
	public Double getMitja(String clau) {
		return intervalsMitja.get(clau);
	}
	public int getNumMesures(String clau) {
		Integer numMesures = intervalsContador.get(clau);
		if (numMesures != null)
			return numMesures.intValue();
		else
			return 0;
	}

	public Set<String> getClausMesures() {
		return intervalsDarreraMesura.keySet();
	}

}
