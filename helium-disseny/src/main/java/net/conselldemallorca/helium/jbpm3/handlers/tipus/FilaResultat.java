/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * Una fila del resultat d'una consulta al domini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FilaResultat implements Serializable {

	private List<ParellaCodiValor> columnes = new ArrayList<ParellaCodiValor>();



	public FilaResultat() {
	}

	public List<ParellaCodiValor> getColumnes() {
		return columnes;
	}
	public void setColumnes(List<ParellaCodiValor> columnes) {
		this.columnes = columnes;
	}

	public void addColumna(ParellaCodiValor columna) {
		getColumnes().add(columna);
	}
	public void deleteColumna(ParellaCodiValor columna) {
		getColumnes().remove(columna);
	}

	private static final long serialVersionUID = 1L;

}
