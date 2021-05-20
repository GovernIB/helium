/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO amb informació d'un termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class TerminiDto extends HeretableDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private boolean duradaPredefinida;
	private int anys;
	private int mesos;
	private Integer dies;
	private boolean laborable;
	private boolean manual = true;
	private Integer diesPrevisAvis;
	private boolean alertaPrevia;
	private boolean alertaFinal;
	private boolean alertaCompletat;
	
	public TerminiDto() {
		super();
		this.dies = 0;
	}
	public TerminiDto(String valor) {
		super();
		if (valor != null) {
			String[] dades = ((String)valor).split("/");
			if (dades.length >= 3) {
				this.anys = Integer.parseInt(dades[0]);
				this.mesos = Integer.parseInt(dades[1]);
				this.dies = Integer.parseInt(dades[2]);
			}
		}
	}
	
	public String getDurada() {
		return toString() + ((dies > 0) ? ((laborable) ? " laborables" : " naturals") : "");
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
		if (dies != null && dies > 0) {
			sb.append(dies);
			plural = dies > 1;
			sb.append((plural) ? " dies": " dia");
		}
		return sb.toString();
	}
	
	public String toSavinString() {
		if (isEmpty())
			return null;
		return anys + "/" + mesos + "/" + (dies == null ? 0 : dies);
	}
	public boolean isEmpty() {
		return (anys == 0 && mesos == 0 && dies == null);
	}

	private static final long serialVersionUID = -7870230958190962621L;
}
