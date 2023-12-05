package net.conselldemallorca.helium.v3.core.api.dto.procediment;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/** Classe per anar responent les línies d'informació en la consulta del progrés d'actualització de procediments. Permet tractar 
 * les actualitzacions en transaccions separades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProgresActualitzacioDto {

	/** Nivell del missatge d'informació.*/
	public enum NivellInfo {
		INFO,
		AVIS,
		ERROR
	}

	/** Línia d'informació que pot contenir un títo, text i llista de detalls. */
	@Getter @Setter
	public class ActualitzacioInfo {
		int index;
		NivellInfo tipus = NivellInfo.INFO;
		String titol;
		String text;
		List<String> linies = new ArrayList<String>();
		
		/** Constructor per defecte */
		public ActualitzacioInfo() {
			
		}
		
		public void addLinia(String linia) {
			linies.add(linia);
		}
	}


	
	/** Comptador de progrés de 0 a 100 per la barra de progrés. */
	Integer progres = 0;
	/** Total de número d'operacions. */
	Integer numOperacions = null;
	/** Número d'elements actualitzats per calcular el progrés. */
	Integer numElementsActualitzats = 0;
	/** Indica si el progrés ha finalitzat. */
	boolean finished = false;
	/** Indicasi s'ha produït algun error. */
	boolean error = false;
	/** Missatge d'error. */
	String errorMsg;
	/** Llistat d'avisos per pintar al final de progrés. */
	List<String> avisos = new ArrayList<String>();
	
	/** Comptadors de correctes, avisos i errors */
	int nExtingits = 0;
	int nNous = 0;
	int nCanvis = 0;
	int nAvisos = 0;
	int nErrors = 0;

	/** Línies d'informació. */
	int nInfo = 0;
	List<ActualitzacioInfo> info = new ArrayList<ActualitzacioInfo>();
	
	public void addInfo(String missatge) {
		this.addInfo(null, missatge, NivellInfo.INFO, null, false);
	}

	public void addInfo(String titol, String missatge, NivellInfo nivell, List<String> linies, boolean incrementar) {
		ActualitzacioInfo detall = new ActualitzacioInfo();
		detall.setTitol(titol);
		detall.setText(missatge);
		detall.setTipus(nivell);
		detall.setLinies(linies);
		this.addInfo(detall);
		if (incrementar) {
			this.incrementElementsActualitzats();
		}
	}

	public void addInfo(ActualitzacioInfo detall) {
		addInfo(detall, false);
	}
	
	public void addInfo(ActualitzacioInfo detall, boolean incrementar) {
		detall.setIndex(info.size());
		info.add(detall);
		nInfo = info.size();
		if (incrementar) {
			incrementElementsActualitzats();
		}
	}
	
	public void incrementElementsActualitzats() {
		if (numOperacions == null) {
			return;
		}
		this.numElementsActualitzats++;
		double auxprogres = (this.numElementsActualitzats.doubleValue()  / this.numOperacions.doubleValue()) * 100;
		this.setProgres((int) auxprogres);
	}

	/// Comptadors pel resum
	public void incExtingits() { nExtingits++;}
	public void incNous() { nNous++;}
	public void incCanvis() { nCanvis++; }
	public void incAvisos() { nAvisos++;}
	public void incErrors() { nErrors++;}
}
