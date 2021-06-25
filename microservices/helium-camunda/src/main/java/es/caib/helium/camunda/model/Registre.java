/**
 * 
 */
package es.caib.helium.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Classe per guardar un registre a dins jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@AllArgsConstructor
public class Registre implements Serializable {

	private String[] columnes;
	private Object[] valors;

	public Object getValor(int index) {
		return valors[index];
	}
	public Object getValor(String columna) {
		for (int i = 0; i < columnes.length; i++) {
			if (columnes[i].equals(columna)) {
				if (i < valors.length)
					return valors[i];
				else
					return null;
			}
		}
		return null;
	}

	private static final long serialVersionUID = 1L;

}
