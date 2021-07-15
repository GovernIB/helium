package es.caib.helium.logic.intf.dto;

import java.util.ArrayList;
import java.util.List;


/**
 * Resultat d'una consulta paginada d'objectes jBPM.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResultatConsultaPaginada<T> {

	private long count;
	private List<T> llista;

	public ResultatConsultaPaginada(int count) {
		super();
		this.count = count;
		this.llista = new ArrayList<T>();
	}
	public ResultatConsultaPaginada(long count, List<T> llista) {
		super();
		this.count = count;
		this.llista = llista;
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public List<T> getLlista() {
		return llista;
	}
	public void setLlista(List<T> llista) {
		this.llista = llista;
	}

}
