/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.io.Serializable;

/**
 * Classe per guardar un registre a dins jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Registre implements Serializable {

	private String[] columnes;
	private Object[] valors;

	public Registre(
			String[] columnes,
			Object[] valors) {
		this.columnes = columnes;
		this.valors = valors;
	}

	public String[] getColumnes() {
		return columnes;
	}
	public void setColumnes(String[] columnes) {
		this.columnes = columnes;
	}
	public Object[] getValors() {
		return valors;
	}
	public void setValors(Object[] valors) {
		this.valors = valors;
	}

	public Object getValor(int index) {
		return valors[index];
	}
	public Object getValor(String columna) {
		for (int i = 0; i < columnes.length; i++) {
			if (columnes[i].equals(columna))
				return valors[i];
		}
		return null;
	}

	private static final long serialVersionUID = 1L;

}
