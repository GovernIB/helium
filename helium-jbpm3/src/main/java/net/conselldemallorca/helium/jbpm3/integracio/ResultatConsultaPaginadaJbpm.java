package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.ArrayList;
import java.util.List;


/**
 * Resultat d'una consulta paginada d'objectes jBPM.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResultatConsultaPaginadaJbpm<T> {

	private int count;
	private List<T> llista;

	public ResultatConsultaPaginadaJbpm(int count) {
		super();
		this.count = count;
		this.llista = new ArrayList<T>();
	}
	public ResultatConsultaPaginadaJbpm(int count, List<T> llista) {
		super();
		this.count = count;
		this.llista = llista;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<T> getLlista() {
		return llista;
	}
	public void setLlista(List<T> llista) {
		this.llista = llista;
	}

}
