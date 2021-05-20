package es.caib.helium.jbpm3.integracio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * Resultat d'una consulta paginada d'objectes jBPM.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@AllArgsConstructor
public class ResultatConsultaPaginadaJbpm<T> {

	private int count;
	private List<T> llista;

	public ResultatConsultaPaginadaJbpm(int count) {
		super();
		this.count = count;
		this.llista = new ArrayList<T>();
	}

}
