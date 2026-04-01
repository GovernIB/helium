/**
 * 
 */
package es.caib.helium.integracio.plugins.signatura;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre un certificat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DadesCertificat {

	private String tipusCertificat;
	private String subject;
	private String nomResponsable;
	private String primerLlinatgeResponsable;
	private String segonLlinatgeResponsable;
	private String nifResponsable;
	private String idEmisor;
	private String nifCif;
	private String email;
	private String dataNaixement;
	private String razonSocial;
	private String clasificacio;
	private String numeroSerie;
	private Date data;

	public void setNombreCompletoResponsable(String nombreCompletoResponsable) {
		String[] parts = nombreCompletoResponsable.split(" ");
		if (parts.length == 1) {
			this.nomResponsable = parts[0];
		} else if (parts.length == 2) {
			this.nomResponsable = parts[0];
			this.primerLlinatgeResponsable = parts[1];
		} else if (parts.length == 3) {
			this.nomResponsable = parts[0];
			this.primerLlinatgeResponsable = parts[1];
			this.segonLlinatgeResponsable = parts[2];
		} else if (parts.length > 3) {
			StringBuilder nombre = new StringBuilder();
			for (String part: parts) {
				if (part.length() > 0)
					nombre.append(part + " ");
			}
			this.nomResponsable = nombre.toString();
			this.primerLlinatgeResponsable = parts[parts.length - 2];
			this.segonLlinatgeResponsable = parts[parts.length - 1];
		}
	}
	public String getNombreCompletoResponsable() {
		StringBuilder sb = new StringBuilder();
		sb.append(getNomResponsable());
		if (getPrimerLlinatgeResponsable() != null) {
			sb.append(" ");
			sb.append(getPrimerLlinatgeResponsable());
			if (getSegonLlinatgeResponsable() != null) {
				sb.append(" ");
				sb.append(getSegonLlinatgeResponsable());
			}
		} else if (getSegonLlinatgeResponsable() != null) {
			sb.append(" ");
			sb.append(getSegonLlinatgeResponsable());
		}
		return sb.toString();
	}

	public void setApellidosResponsable(String apellidos) {
		String[] parts = apellidos.split(" ");
		if (parts.length == 1) {
			this.primerLlinatgeResponsable = parts[0];
		} else if (parts.length == 2) {
			this.primerLlinatgeResponsable = parts[0];
			this.segonLlinatgeResponsable = parts[1];
		} else if (parts.length > 2) {
			StringBuilder primer = new StringBuilder();
			for (String part: parts) {
				if (part.length() > 0)
					primer.append(part + " ");
			}
			this.primerLlinatgeResponsable = primer.toString();
			this.segonLlinatgeResponsable = parts[parts.length - 1];
		}
	}

}
