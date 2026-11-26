package es.caib.helium.bpmn.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TerminiInfo {

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
	private Date dataInici;
	private Date dataFi;
	private Date dataAturada;
	private Date dataCancelacio;

	public TerminiInfo(String valor) {
		super();
		if (valor != null) {
			String[] dades = valor.split("/");
			if (dades.length >= 3) {
				this.anys = Integer.parseInt(dades[0]);
				this.mesos = Integer.parseInt(dades[1]);
				this.dies = Integer.parseInt(dades[2]);
			}
		}
	}

	public TerminiInfo(int anys, int mesos, int dies) {
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
	}

	public static TerminiInfo valueFromString(String valorTermini) {
		TerminiInfo termini = new TerminiInfo();
		try {
			String[] parts = valorTermini.split("/");
			if (parts.length == 3) {
				termini.setAnys(Integer.parseInt(parts[0]));
				termini.setMesos(Integer.parseInt(parts[1]));
				termini.setDies(Integer.parseInt(parts[2]));
			}
		} catch (Exception ex) {}
		return termini;
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

}
