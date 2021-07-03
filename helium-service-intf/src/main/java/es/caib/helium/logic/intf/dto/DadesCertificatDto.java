/**
 * 
 */
package es.caib.helium.logic.intf.dto;

/**
 * Informació sobre un certificat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesCertificatDto {

	private String tipoCertificado;
	private String subject;
	private String nombreResponsable;
	private String primerApellidoResponsable;
	private String segundoApellidoResponsable;
	private String nifResponsable;
	private String idEmisor;
	private String nifCif;
	private String email;
	private String fechaNacimiento;
	private String razonSocial;
	private String clasificacion;
	private String numeroSerie;



	public String getTipoCertificado() {
		return tipoCertificado;
	}
	public void setTipoCertificado(String tipoCertificado) {
		this.tipoCertificado = tipoCertificado;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getNombreResponsable() {
		return nombreResponsable;
	}
	public void setNombreResponsable(String nombreResponsable) {
		this.nombreResponsable = nombreResponsable;
	}
	public String getPrimerApellidoResponsable() {
		return primerApellidoResponsable;
	}
	public void setPrimerApellidoResponsable(String primerApellidoResponsable) {
		this.primerApellidoResponsable = primerApellidoResponsable;
	}
	public String getSegundoApellidoResponsable() {
		return segundoApellidoResponsable;
	}
	public void setSegundoApellidoResponsable(String segundoApellidoResponsable) {
		this.segundoApellidoResponsable = segundoApellidoResponsable;
	}
	public String getNifResponsable() {
		return nifResponsable;
	}
	public void setNifResponsable(String nifResponsable) {
		this.nifResponsable = nifResponsable;
	}
	public String getIdEmisor() {
		return idEmisor;
	}
	public void setIdEmisor(String idEmisor) {
		this.idEmisor = idEmisor;
	}
	public String getNifCif() {
		return nifCif;
	}
	public void setNifCif(String nifCif) {
		this.nifCif = nifCif;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}
	public String getNumeroSerie() {
		return numeroSerie;
	}
	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}



	public void setNombreCompletoResponsable(String nombreCompletoResponsable) {
		String[] parts = nombreCompletoResponsable.split(" ");
		if (parts.length == 1) {
			this.nombreResponsable = parts[0];
		} else if (parts.length == 2) {
			this.nombreResponsable = parts[0];
			this.primerApellidoResponsable = parts[1];
		} else if (parts.length == 3) {
			this.nombreResponsable = parts[0];
			this.primerApellidoResponsable = parts[1];
			this.segundoApellidoResponsable = parts[2];
		} else if (parts.length > 3) {
			StringBuilder nombre = new StringBuilder();
			for (String part: parts) {
				if (part.length() > 0)
					nombre.append(part + " ");
			}
			this.nombreResponsable = nombre.toString();
			this.primerApellidoResponsable = parts[parts.length - 2];
			this.segundoApellidoResponsable = parts[parts.length - 1];
		}
	}
	public String getNombreCompletoResponsable() {
		StringBuilder sb = new StringBuilder();
		sb.append(getNombreResponsable());
		if (getPrimerApellidoResponsable() != null) {
			sb.append(" ");
			sb.append(getPrimerApellidoResponsable());
			if (getSegundoApellidoResponsable() != null) {
				sb.append(" ");
				sb.append(getSegundoApellidoResponsable());
			}
		} else if (getSegundoApellidoResponsable() != null) {
			sb.append(" ");
			sb.append(getSegundoApellidoResponsable());
		}
		return sb.toString();
	}

	public void setApellidosResponsable(String apellidos) {
		String[] parts = apellidos.split(" ");
		if (parts.length == 1) {
			this.primerApellidoResponsable = parts[0];
		} else if (parts.length == 2) {
			this.primerApellidoResponsable = parts[0];
			this.segundoApellidoResponsable = parts[1];
		} else if (parts.length > 2) {
			StringBuilder primer = new StringBuilder();
			for (String part: parts) {
				if (part.length() > 0)
					primer.append(part + " ");
			}
			this.primerApellidoResponsable = primer.toString();
			this.segundoApellidoResponsable = parts[parts.length - 1];
		}
	}

}
