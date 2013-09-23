/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.springframework.stereotype.Component;

/**
 * Helper per a mesurar intervals de temps i fer-ne estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MesuresTemporalsHelper {
	
	private static final int MESURES = 100;
	@SuppressWarnings("unused")
	private static Boolean actiu = null;

	private Map<String, String> intervalsFamilia = new HashMap<String, String>();
	private Map<String, Long> intervalsInici = new HashMap<String, Long>();
	private Map<String, Long> intervalsDarreraMesura = new HashMap<String, Long>();
	private Map<String, Long> intervalsMinim = new HashMap<String, Long>();
	private Map<String, Long> intervalsMaxim = new HashMap<String, Long>();
	private Map<String, Double> intervalsMitja = new HashMap<String, Double>();
	private Map<String, Integer> intervalsContador = new HashMap<String, Integer>();
	private Map<String, LinkedList<IntervalEvent>> intervalEvents = new HashMap<String, LinkedList<IntervalEvent>>();

	public void mesuraIniciar(String clau, String familia) {
		if (actiu == null) {
			String sactiu = GlobalProperties.getInstance().getProperty("app.mesura.temps.actiu");
			if (!"true".equalsIgnoreCase(sactiu))
				actiu = false;
			else 
				actiu = true;
		}
		if (actiu) {
			intervalsInici.put(
					clau,
					new Long(System.currentTimeMillis()));
			intervalsFamilia.put(clau, familia);
		}
	}
	
	public void mesuraCalcular(String clau) {
		if (actiu) {
			Long inici = intervalsInici.get(clau);
			LinkedList<IntervalEvent> events;
			if (inici != null) {
				Long diferencia = System.currentTimeMillis() - inici.longValue();
				intervalsDarreraMesura.put(clau, diferencia);
				if (intervalsMinim.get(clau) == null || diferencia < intervalsMinim.get(clau))
					intervalsMinim.put(clau, diferencia);
				if (intervalsMaxim.get(clau) == null || diferencia > intervalsMaxim.get(clau))
					intervalsMaxim.put(clau, diferencia);
				Integer contador = intervalsContador.get(clau);
				if (contador == null) {
					contador = new Integer(1);
					events = new LinkedList<IntervalEvent>();
				} else {
					contador = new Integer(contador.intValue() + 1);
					events = intervalEvents.get(clau);
					if (contador > MESURES)
						events.removeFirst();
				}
				intervalsContador.put(clau, contador);
				events.add(new IntervalEvent(new Date(), diferencia));
				intervalEvents.put(clau, events);
				Double mitja = intervalsMitja.get(clau);
				if (mitja == null)
					mitja = new Double(diferencia);
				else
					mitja = new Double((mitja * contador + diferencia) / (contador + 1));
				intervalsMitja.put(clau, mitja);
				intervalsInici.remove(clau);
			}
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

	public Long getMinim(String clau) {
		return intervalsMinim.get(clau);
	}
	public Long getMaxim(String clau) {
		return intervalsMaxim.get(clau);
	}
	public List<String> getClausMesures(String familia) {
		List<String> claus = new ArrayList<String>(intervalsDarreraMesura.keySet());
		Collections.sort(claus);
		
		if (familia != null && !"".equals(familia)) {
			List<String> keys = new ArrayList<String>();
			for (String clau: claus) {
				String familiaClau = intervalsFamilia.get(clau);
				if (familia.equals(familiaClau)) {
					keys.add(clau);
				}
			}
			return keys;
		} else {
			return claus;
		}
	}
	public Set<String> getIntervalsFamilia() {
		Set<String> families = new HashSet<String>();
		for (String familia: intervalsFamilia.values()) {
			families.add(familia);
		}
		return families;
	}
	
	public LinkedList<IntervalEvent> getIntervalEvents(String clau) {
		return intervalEvents.get(clau);
	}

	public class IntervalEvent {
		private Date date;
		private Long duracio;
		
		public IntervalEvent(Date date, Long duracio) {
			super();
			this.date = date;
			this.duracio = duracio;
		}
		
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public Long getDuracio() {
			return duracio;
		}
		public void setDuracio(Long duracio) {
			this.duracio = duracio;
		}
	}

	public static boolean isActiu() {
		return actiu;
	}
}
