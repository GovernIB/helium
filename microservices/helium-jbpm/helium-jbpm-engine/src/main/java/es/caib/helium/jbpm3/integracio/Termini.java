/**
 * 
 */
package es.caib.helium.jbpm3.integracio;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Classe per guardar un termini a dins jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class Termini implements Serializable {

	private int anys = 0;
	private int mesos = 0;
	private int dies = 0;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		boolean plural = false;
		if (anys > 0) {
			sb.append(anys);
			plural = anys > 1;
			sb.append((plural) ? " anys": " any");
			if (mesos > 0 && dies > 0)
				sb.append(", ");
			else if (mesos > 0 || dies > 0)
				sb.append(" i ");
		}
		if (mesos > 0) {
			sb.append(mesos);
			plural = mesos > 1;
			sb.append((plural) ? " mesos": " mes");
			if (dies > 0)
				sb.append(" i ");
		}
		if (dies > 0) {
			sb.append(dies);
			plural = dies > 1;
			sb.append((plural) ? " dies": " dia");
		}
		return sb.toString();
	}
	
	public static Termini valueFromString(String valorTermini) {
		Termini termini = new Termini();
		try {
			String[] parts = valorTermini.split("/");
			if (parts.length == 3) {
				termini.setAnys(new Integer(parts[0]));
				termini.setMesos(new Integer(parts[1]));
				termini.setDies(new Integer(parts[2]));
			}
		} catch (Exception ex) {}
		return termini;
	}
	
	public static String valueFromTermini(Termini valorTermini) {
		return valorTermini.getAnys() + "/" + valorTermini.getMesos() + "/" + valorTermini.getDies();
	}

	private static final long serialVersionUID = 774909297938469787L;

}
