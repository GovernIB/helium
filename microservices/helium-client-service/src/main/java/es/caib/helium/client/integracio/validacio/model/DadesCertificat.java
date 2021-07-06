package es.caib.helium.client.integracio.validacio.model;

import lombok.Data;

@Data
public class DadesCertificat {
	
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
}
