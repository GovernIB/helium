/**
 * 
 */
package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Classe per guardar un registre a dins jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
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

	private static final long serialVersionUID = -5413051308564470967L;

}
