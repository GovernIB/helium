package es.caib.helium.bpmn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Una fila del resultat d'una consulta de domini: les columnes són parelles codi-valor.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FilaResultat implements Serializable {

	private List<ParellaCodiValor> columnes = new ArrayList<ParellaCodiValor>();

	public List<ParellaCodiValor> getColumnes() {
		return columnes;
	}
	public void setColumnes(List<ParellaCodiValor> columnes) {
		this.columnes = columnes;
	}
	public void addColumna(ParellaCodiValor columna) {
		columnes.add(columna);
	}

	/** Valor de la columna amb aquest codi, o null si la fila no la té. */
	public Object getValor(String codi) {
		for (ParellaCodiValor columna : columnes) {
			if (columna.getCodi() != null && columna.getCodi().equals(codi)) {
				return columna.getValor();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return columnes.toString();
	}

	private static final long serialVersionUID = 1L;

}
