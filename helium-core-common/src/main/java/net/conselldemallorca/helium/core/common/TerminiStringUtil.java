/**
 * 
 */
package net.conselldemallorca.helium.core.common;

import java.io.Serializable;

/**
 * Classe per guardar un termini a dins jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TerminiStringUtil implements Serializable {

	private int anys;
	private int mesos;
	private int dies;


	public TerminiStringUtil() {
		super();
		this.anys = this.mesos = this.dies = 0;
	}
	public TerminiStringUtil(int anys, int mesos, int dies) {
		super();
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
	}

	public int getAnys() {
		return anys;
	}
	public void setAnys(int anys) {
		this.anys = anys;
	}
	public int getMesos() {
		return mesos;
	}
	public void setMesos(int mesos) {
		this.mesos = mesos;
	}
	public int getDies() {
		return dies;
	}
	public void setDies(int dies) {
		this.dies = dies;
	}

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
	
	public static TerminiStringUtil valueFromString(String valorTermini) {
		String[] parts = valorTermini.split("/");
		try {
			return new TerminiStringUtil(
					new Integer(parts[0]),
					new Integer(parts[1]),
					new Integer(parts[2]));
		} catch (NumberFormatException ex) {
			return new TerminiStringUtil();
		}
	}
	
	public static String valueFromTermini(TerminiStringUtil valorTermini) {
		return valorTermini.getAnys() + "/" + valorTermini.getMesos() + "/" + valorTermini.getDies();
	}

	private static final long serialVersionUID = 774909297938469787L;

}
