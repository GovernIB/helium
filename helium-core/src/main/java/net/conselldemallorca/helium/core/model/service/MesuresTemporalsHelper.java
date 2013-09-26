/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.conselldemallorca.helium.core.model.dto.IntervalEventDto;
import net.conselldemallorca.helium.core.model.dto.MesuraTemporalDto;

import org.springframework.stereotype.Component;

/**
 * Helper per a mesurar intervals de temps i fer-ne estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MesuresTemporalsHelper {
	
	private static int mesures = 0;
	private static Boolean actiu = null;

	private Map<String, Map<Clau, Estadistiques>> intervalsEstadistiques = new HashMap<String, Map<Clau, Estadistiques>>();
	
	public MesuresTemporalsHelper(String sactiu, Integer imesures) {
		super();
		if (!"true".equalsIgnoreCase(sactiu))
			actiu = false;
		else 
			actiu = true;
		mesures = imesures;
	}

	public void mesuraIniciar(String nom, String familia) {
		mesuraIniciar(nom, familia, null, null, null);
	}
	public void mesuraIniciar(String nom, String familia, String tipusExpedient) {
		mesuraIniciar(nom, familia, tipusExpedient, null, null);
	}
	public void mesuraIniciar(String nom, String familia, String tipusExpedient, String tasca) {
		mesuraIniciar(nom, familia, tipusExpedient, tasca, null);
	}
	public void mesuraIniciar(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		if (actiu) {
			Clau clau = new Clau(nom, tipusExpedient, tasca, detall);
			if (!intervalsEstadistiques.containsKey(familia)) {
				intervalsEstadistiques.put(familia, new HashMap<Clau, Estadistiques>());
			}
			Map<Clau, Estadistiques> mEstadistiques = intervalsEstadistiques.get(familia);
			Estadistiques est = mEstadistiques.get(clau);
			if (est == null) {
				mEstadistiques.put(clau, new Estadistiques());
			} else {
				est.setInici(new Long(System.currentTimeMillis()));
			}
		}
	}
	
	public void mesuraCalcular(String nom, String familia) {
		mesuraCalcular(nom, familia, null, null, null);
	}
	public void mesuraCalcular(String nom, String familia, String tipusExpedient) {
		mesuraCalcular(nom, familia, tipusExpedient, null, null);
	}
	public void mesuraCalcular(String nom, String familia, String tipusExpedient, String tasca) {
		mesuraCalcular(nom, familia, tipusExpedient, tasca, null);
	}
	public void mesuraCalcular(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		if (actiu && intervalsEstadistiques.containsKey(familia)) {
			Clau clau = new Clau(nom, tipusExpedient, tasca, detall);
			Estadistiques est = intervalsEstadistiques.get(familia).get(clau);
			if (est != null)
				est.addNovaMesura();
		}
	}

	public void intervalNetejar(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		Clau clau = new Clau(nom, tipusExpedient, tasca, detall);
		if (intervalsEstadistiques.containsKey(familia)) {
			intervalsEstadistiques.get(familia).remove(clau);
		}
	}
	
	public List<MesuraTemporalDto> getEstadistiques(String familia) {
		Map<String, Map<Clau, Estadistiques>> estadistiquesFamilia = null;
		if ("".equals(familia)) {
			estadistiquesFamilia = intervalsEstadistiques;
		} else {
			estadistiquesFamilia = new HashMap<String, Map<Clau, Estadistiques>>();
			estadistiquesFamilia.put(familia, intervalsEstadistiques.get(familia));
		}
		
		List<MesuraTemporalDto> resposta = new ArrayList<MesuraTemporalDto>();
		
		SortedSet<String> families = new TreeSet<String>(estadistiquesFamilia.keySet());
		for (String family: families) {
			Map<Clau, Estadistiques> estadistiques = estadistiquesFamilia.get(family);
			if (estadistiques != null) {
				SortedSet<Clau> claus = new TreeSet<Clau>(estadistiques.keySet());
				for (Clau clau: claus) {
					Estadistiques estadistica = estadistiques.get(clau);
					if (estadistica != null && estadistica.getDarreraMesura() != null) {
						MesuraTemporalDto dto = new MesuraTemporalDto();
						dto.setClau(clau.getNom());
						dto.setTipusExpedient(clau.getTipusExpedient());
						dto.setTasca(clau.getTasca());
						dto.setDarrera(estadistica.getDarreraMesura());
						dto.setMitja(estadistica.getMitja());
						dto.setMinima(estadistica.getMinim());
						dto.setMaxima(estadistica.getMaxim());
						dto.setNumMesures(estadistica.getContador());
						LinkedList<IntervalEventDto> intervalEvents = new LinkedList<IntervalEventDto>();
						long iniMilis = 0;
						long fiMilis = 0;
						for (IntervalEvent event : estadistica.getEvents()) {
							intervalEvents.add(new IntervalEventDto(event.getDate(), event.getDuracio()));
							long milis =  event.getDate().getTime();
							if (iniMilis == 0 || iniMilis > milis) iniMilis = milis;
							if (fiMilis < milis) fiMilis = milis;
						}
						dto.setEvents(intervalEvents);
						if (dto.getNumMesures() > 1) {
							// Execucions per minut
							if (fiMilis - iniMilis == 0) {
								dto.setPeriode(estadistica.contador.doubleValue());
							} else {
								dto.setPeriode((dto.getNumMesures() * 60000) / (fiMilis - iniMilis));
							}
						}
						resposta.add(dto);
					}
				}
			}
		}
		return resposta;
	}
	
	public Set<String> getIntervalsFamilia() {
		return intervalsEstadistiques.keySet();
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

	protected class Clau implements Comparable<Clau> {
		private String nom;
		private String tipusExpedient;
		private String tasca;
		private String detall;
		
		public Clau(String nom, String tipusExpedient, String tasca, String detall) {
			super();
			this.nom = nom;
			this.tipusExpedient = tipusExpedient;
			this.tasca = tasca;
			this.detall = detall;
		}

		public String getNom() {
			return nom;
		}

		public void setNom(String nom) {
			this.nom = nom;
		}

		public String getTipusExpedient() {
			return tipusExpedient;
		}

		public void setTipusExpedient(String tipusExpedient) {
			this.tipusExpedient = tipusExpedient;
		}

		public String getTasca() {
			return tasca;
		}

		public void setTasca(String tasca) {
			this.tasca = tasca;
		}

		public String getDetall() {
			return detall;
		}

		public void setDetall(String detall) {
			this.detall = detall;
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((detall == null) ? 0 : detall.hashCode());
			result = prime * result + ((nom == null) ? 0 : nom.hashCode());
			result = prime * result + ((tasca == null) ? 0 : tasca.hashCode());
			result = prime
					* result
					+ ((tipusExpedient == null) ? 0 : tipusExpedient.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Clau other = (Clau) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (detall == null) {
				if (other.detall != null)
					return false;
			} else if (!detall.equals(other.detall))
				return false;
			if (nom == null) {
				if (other.nom != null)
					return false;
			} else if (!nom.equals(other.nom))
				return false;
			if (tasca == null) {
				if (other.tasca != null)
					return false;
			} else if (!tasca.equals(other.tasca))
				return false;
			if (tipusExpedient == null) {
				if (other.tipusExpedient != null)
					return false;
			} else if (!tipusExpedient.equals(other.tipusExpedient))
				return false;
			return true;
		}

		public int compareTo(Clau o) {
			int comp = this.nom.compareTo(o.nom);
			if (comp == 0) {
				comp = this.tipusExpedient.compareTo(o.tipusExpedient);
				if  (comp == 0) {
					comp = this.tasca.compareTo(o.tasca);
					return comp == 0 ? this.detall.compareTo(o.detall) : comp;
				}
			} 
			return comp;
		}

		private MesuresTemporalsHelper getOuterType() {
			return MesuresTemporalsHelper.this;
		}
	}
	
	protected class Estadistiques {
		private Long inici;
		private Long darreraMesura;
		private Long minim;
		private Long maxim;
		private Double mitja;
		private Long contador;
		private LinkedList<IntervalEvent> events;
		
		public Estadistiques() {
			super();
			this.inici = new Long(System.currentTimeMillis());
		}

		public void addNovaMesura() {
			Long diferencia = System.currentTimeMillis() - inici.longValue();
			darreraMesura = diferencia;
			if (minim == null || diferencia < minim)
				minim = diferencia;
			if (maxim == null || diferencia > maxim)
				maxim = diferencia;
			if (contador == null) {
				contador = 1L;
				events = new LinkedList<IntervalEvent>();
			} else {
				contador++;
				if (contador > mesures)
					events.removeFirst();
			}
			events.add(new IntervalEvent(new Date(), diferencia));
			if (mitja == null)
				mitja = new Double(diferencia);
			else
				mitja = new Double((mitja * contador + diferencia) / (contador + 1));
		}
		
		public Long getInici() {
			return inici;
		}

		public void setInici(Long inici) {
			this.inici = inici;
		}

		public Long getDarreraMesura() {
			return darreraMesura;
		}

		public void setDarreraMesura(Long darreraMesura) {
			this.darreraMesura = darreraMesura;
		}

		public Long getMinim() {
			return minim;
		}

		public void setMinim(Long minim) {
			this.minim = minim;
		}

		public Long getMaxim() {
			return maxim;
		}

		public void setMaxim(Long maxim) {
			this.maxim = maxim;
		}

		public Double getMitja() {
			return mitja;
		}

		public void setMitja(Double mitja) {
			this.mitja = mitja;
		}

		public Long getContador() {
			return contador;
		}

		public void setContador(Long contador) {
			this.contador = contador;
		}

		public LinkedList<IntervalEvent> getEvents() {
			return events;
		}

		public void setEvents(LinkedList<IntervalEvent> events) {
			this.events = events;
		}
	}
	
	public static boolean isActiu() {
		return actiu;
	}
}
