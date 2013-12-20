/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.v3.core.api.dto.IntervalEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaCompleteDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static Long inici = null;
	private static Long fi = null;

	private Map<String, Map<Clau, Estadistiques>> intervalsEstadistiques = new HashMap<String, Map<Clau, Estadistiques>>();
	private Map<String, TascaComplete> tasquesComplete = new HashMap<String, TascaComplete>();
	
	public MesuresTemporalsHelper(String sactiu, Integer imesures) {
		super();
		if (!"true".equalsIgnoreCase(sactiu))
			actiu = false;
		else 
			actiu = true;
		mesures = imesures;
		inici = new Long(System.currentTimeMillis());
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
		try {
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
		} catch (Exception e) {
			logger.error("No s'ha pogut iniciar la mesura de temps: " + nom, e);
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
		try {
			if (actiu && intervalsEstadistiques.containsKey(familia)) {
				Clau clau = new Clau(nom, tipusExpedient, tasca, detall);
				Estadistiques est = intervalsEstadistiques.get(familia).get(clau);
				if (est != null)
					est.addNovaMesura();
			}
		} catch (Exception e) {
			logger.error("No s'ha pogut calcular la mesura de temps: " + nom, e);
		}
	}

	public void intervalNetejar(String nom, String familia, String tipusExpedient, String tasca, String detall) {
		Clau clau = new Clau(nom, tipusExpedient, tasca, detall);
		if (intervalsEstadistiques.containsKey(familia)) {
			intervalsEstadistiques.get(familia).remove(clau);
		}
	}
	
	public void tascaCompletarIniciar(Expedient exp, String tascaId, String tascaNom) {
		tasquesComplete.put(tascaId, new TascaComplete(
				tascaId, 
				exp.getId(), 
				exp.getTipus().getNom(), 
				exp.getIdentificador(), 
				tascaNom, 
				new Date()));
	}
	
	public void tascaCompletarFinalitzar(String tascaId) {
		tasquesComplete.remove(tascaId);
	}
	
	public void tascaCompletarNetejar() {
		tasquesComplete.clear();
	}
	
	public List<MesuraTemporalDto> getEstadistiques(String familia, boolean ambDetall) {
		fi = System.currentTimeMillis();
		Map<String, Map<Clau, Estadistiques>> estadistiquesFamilia = null;
		if (familia == null || "".equals(familia)) {
			estadistiquesFamilia = intervalsEstadistiques;
		} else {
			estadistiquesFamilia = new HashMap<String, Map<Clau, Estadistiques>>();
			estadistiquesFamilia.put(familia, intervalsEstadistiques.get(familia));
		}
		
		List<MesuraTemporalDto> resposta = new ArrayList<MesuraTemporalDto>();
		Long temps = fi - inici;
		
//		SortedSet<String> families = new TreeSet<String>(estadistiquesFamilia.keySet());
//		for (String family: families) {
		for (String family: estadistiquesFamilia.keySet()) {
			Map<Clau, Estadistiques> estadistiques = estadistiquesFamilia.get(family);
			if (estadistiques != null) {
//				SortedSet<Clau> claus = new TreeSet<Clau>(estadistiques.keySet());
//				for (Clau clau: claus) {
				for (Clau clau: estadistiques.keySet()) {
					if (ambDetall || clau.getDetall() == null) {
						Estadistiques estadistica = estadistiques.get(clau);
						if (estadistica != null && estadistica.getDarreraMesura() != null) {
							MesuraTemporalDto dto = new MesuraTemporalDto();
							dto.setClau(clau.getNom());
							dto.setTipusExpedient(clau.getTipusExpedient());
							dto.setTasca(clau.getTasca());
							dto.setDetall(clau.getDetall());
							dto.setDarrera(estadistica.getDarreraMesura());
							dto.setMitja(estadistica.getMitja());
							dto.setMinima(estadistica.getMinim());
							dto.setMaxima(estadistica.getMaxim());
							dto.setNumMesures(estadistica.getContador());
							LinkedList<IntervalEventDto> intervalEvents = new LinkedList<IntervalEventDto>();
							for (IntervalEvent event : estadistica.getEvents()) {
								if (event != null && event.getDate() != null && event.getDuracio() != null) {
									intervalEvents.add(new IntervalEventDto(event.getDate(), event.getDuracio()));
								} else {
									logger.error("ERROR ESTADISTIQUES: Mesura " + clau.getNom() + " amb events nulls.");
								}
							}
							dto.setEvents(intervalEvents);
							// Execucions per minut
							dto.setPeriode((dto.getNumMesures() * 60000.0) / temps);
							resposta.add(dto);
						}
					}
				}
			}
		}
		Collections.sort(resposta);
		return resposta;
	}
	
	public List<MesuraTemporalDto> getEstadistiquesTipusExpedient() {
		List<MesuraTemporalDto> estadistiques = findMesures(true);
		Collections.sort(estadistiques, new Comparator<MesuraTemporalDto>() {
			public int compare(MesuraTemporalDto o1, MesuraTemporalDto o2) {
				int comp = compareStr(o1.getTipusExpedient(), o2.getTipusExpedient()); 
				if (comp == 0) {
					comp = compareStr(o1.getClau(), o2.getClau());
					if  (comp == 0) {
						if (o1.getDetall() == null || o2.getDetall() == null)
							return compareStr(o2.getDetall(), o1.getDetall());
						else 
							return compareStr(o1.getDetall(), o2.getDetall());
					}
				} 
				return comp;
			}
		});

		Long temps = fi - inici;
		List<MesuraTemporalDto> resposta = new ArrayList<MesuraTemporalDto>();
		MesuraTemporalDto ultima = null;
		for (MesuraTemporalDto estadistica: estadistiques) {
			if (ultima != null && ultima.getNomTE().equals(estadistica.getNomTE())) {
				ultima.setMaxima(Math.max(ultima.getMaxima(), estadistica.getMaxima()));
				ultima.setMinima(Math.min(ultima.getMinima(), estadistica.getMinima()));
				ultima.setMitja(((ultima.getMitja() * ultima.getNumMesures()) + (estadistica.getMitja() * estadistica.getNumMesures())) / (ultima.getNumMesures() + estadistica.getNumMesures()));
				ultima.setNumMesures(ultima.getNumMesures() + estadistica.getNumMesures());
				ultima.setPeriode(((ultima.getNumMesures() + estadistica.getNumMesures()) * 60000.0) / temps);
			} else {
				resposta.add(estadistica);
				ultima = estadistica;
			}
		}
		
		return resposta;
	}

	public List<MesuraTemporalDto> getEstadistiquesTasca() {
		List<MesuraTemporalDto> estadistiques = findMesures(false);
		Collections.sort(estadistiques, new Comparator<MesuraTemporalDto>() {
			public int compare(MesuraTemporalDto o1, MesuraTemporalDto o2) {
				int comp = compareStr(o1.getTipusExpedient(), o2.getTipusExpedient()); 
				if (comp == 0) {
					comp = compareStr(o1.getTasca(), o2.getTasca());
				}
				if (comp == 0) {
					comp = compareStr(o1.getClau(), o2.getClau());
					if  (comp == 0) {
						if (o1.getDetall() == null || o2.getDetall() == null)
							return compareStr(o2.getDetall(), o1.getDetall());
						else 
							return compareStr(o1.getDetall(), o2.getDetall());
					}
				} 
				return comp;
			}
		});
		return estadistiques;
	}
	
	private List<MesuraTemporalDto> findMesures(boolean ambTipus) {
		fi = System.currentTimeMillis();
		List<MesuraTemporalDto> resposta = new ArrayList<MesuraTemporalDto>();
		Long temps = fi - inici;
		boolean ambTasca = !ambTipus;
		
		for (String family: intervalsEstadistiques.keySet()) {
			Map<Clau, Estadistiques> estadistiques = intervalsEstadistiques.get(family);
			for (Clau clau: estadistiques.keySet()) {
				if ((ambTipus &&  clau.getTipusExpedient() != null) || (ambTasca && clau.getTasca() != null)) {
					Estadistiques estadistica = estadistiques.get(clau);
					if (estadistica != null && estadistica.getDarreraMesura() != null) {
						MesuraTemporalDto dto = new MesuraTemporalDto();
						dto.setClau(clau.getNom());
						dto.setTipusExpedient(clau.getTipusExpedient());
						dto.setTasca(clau.getTasca());
						dto.setDetall(clau.getDetall());
						dto.setDarrera(estadistica.getDarreraMesura());
						dto.setMitja(estadistica.getMitja());
						dto.setMinima(estadistica.getMinim());
						dto.setMaxima(estadistica.getMaxim());
						dto.setNumMesures(estadistica.getContador());
						// Execucions per minut
						dto.setPeriode((dto.getNumMesures() * 60000.0) / temps);
						resposta.add(dto);
					}
				}
			}
		}
		return resposta;
	}
	
	public List<TascaCompleteDto> getTasquesCompletar() {
		fi = System.currentTimeMillis();
		List<TascaCompleteDto> resposta = new ArrayList<TascaCompleteDto>();
		
		for (TascaComplete tasca: tasquesComplete.values()) {
			Double temps = Long.valueOf(fi - tasca.getInici().getTime()).doubleValue() / 1000.0;
			TascaCompleteDto dto = new TascaCompleteDto();
			dto.setExpedient(tasca.getExpedient());
			dto.setExpedientId(tasca.getExpedientId());
			dto.setInici(tasca.getInici());
			dto.setTasca(tasca.getTasca());
			dto.setTascaId(tasca.getTascaId());
			dto.setTempsExecucio(temps);
			dto.setTipusExpedient(tasca.getTipusExpedient());
			resposta.add(dto);
		}
		return resposta;
	}
	
	public static Long getTemps() {
		if (fi == null) 
			fi = System.currentTimeMillis(); 
		return fi - inici;
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
			int comp = compareStr(this.nom, o.nom);
			if (comp == 0) {
				comp = compareStr(this.tipusExpedient, o.tipusExpedient);
				if  (comp == 0) {
					comp = compareStr(this.tasca, o.tasca);
					return comp == 0 ? compareStr(this.detall, o.detall) : comp;
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
			this.contador = 0L;
		}

		public void addNovaMesura() {
			Long diferencia = System.currentTimeMillis() - inici.longValue();
			darreraMesura = diferencia;
			if (minim == null || diferencia < minim)
				minim = diferencia;
			if (maxim == null || diferencia > maxim)
				maxim = diferencia;
			if (contador == 0L) {
				contador++;
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
				mitja = new Double((mitja * (contador - 1) + diferencia) / contador);
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
	
	private int compareStr(String str1, String str2) {
	    return (str1 == null ? (str2 == null ? 0 : 1) : (str2 == null ? -1 : str1.compareTo(str2)));
	}
	
	protected class TascaComplete {
		String tascaId;
		Long expedientId;
		String tipusExpedient;
		String expedient;
		String tasca;
		Date inici;
		
		public TascaComplete(String tascaId, Long expedientId, String tipusExpedient,
				String expedient, String tasca, Date inici) {
			super();
			this.tascaId = tascaId;
			this.expedientId = expedientId;
			this.tipusExpedient = tipusExpedient;
			this.expedient = expedient;
			this.tasca = tasca;
			this.inici = inici;
		}

		public String getTascaId() {
			return tascaId;
		}

		public void setTascaId(String tascaId) {
			this.tascaId = tascaId;
		}

		public Long getExpedientId() {
			return expedientId;
		}

		public void setExpedientId(Long expedientId) {
			this.expedientId = expedientId;
		}

		public String getTipusExpedient() {
			return tipusExpedient;
		}

		public void setTipusExpedient(String tipusExpedient) {
			this.tipusExpedient = tipusExpedient;
		}

		public String getExpedient() {
			return expedient;
		}

		public void setExpedient(String expedient) {
			this.expedient = expedient;
		}

		public String getTasca() {
			return tasca;
		}

		public void setTasca(String tasca) {
			this.tasca = tasca;
		}

		public Date getInici() {
			return inici;
		}

		public void setInici(Date inici) {
			this.inici = inici;
		}
		
	}
	
	private static final Log logger = LogFactory.getLog(MesuresTemporalsHelper.class);
	
}
