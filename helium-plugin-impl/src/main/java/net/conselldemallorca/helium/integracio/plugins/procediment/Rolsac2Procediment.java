package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rolsac2Procediment {
	private Rolsac2Inicio iniciacion;
	private String tipo;
	private String estado;
	private String lopdDestinatario;
	private String destinatarios;
	private String lopdCabecera;
	private Boolean activoLOPD;
	private Integer uaInstructor;
	private Boolean publicado;
	private Date fechaCaducidad;
	private Integer codigo;
	private String workflow;
	private Boolean interno;
	private Link linkUnidadAdministrativaCompetente;
	private String terminoResolucion;
	private Integer codigoWF;
	private Rolsac2TipoProcedimiento tipoProcedimiento;
	private Boolean estadoSIA;
	private String habilitadoFuncionario;
	private String lopdDerechos;
	private Boolean esPdu;
	private String observaciones;
	private Date fechaActualizacion;
	private Boolean habilitadoApoderado;
	private Link linkUnidadAdministrativaResponsable;
	private Date fechaPublicacion;
	private String lopdResponsable;
	private Integer uaCompetente;
	private Link linkLopdInfoAdicional;
	private String lopdFinalidad;
	private String objeto;
	private Rolsac2Silencio silencio;
	private Boolean tramitElectronica;
	private String requisitos;
	private String incidenciasEmail;
	private String nombreProcedimientoWorkFlow;
	private String responsableEmail;
	private String responsableTelefono;
	private String responsable;
	private Boolean hateoasEnabled;
	private Integer codigoSIA;
	private Integer tipoVia;
	private Integer uaResponsable;
	private Boolean tramitTelefonica;
	private Date fechaSIA;
	private Rolsac2Inicio lopdLegitimacion;
	private Boolean tramitPresencial;
	private Link linkUnidadAdministrativaInstructora;
	private Boolean comun;
	private Boolean tieneTasa;
}
