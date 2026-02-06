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
	private Integer activoLOPD;
	private Integer publicado;
	private Date fechaCaducidad;
	private Integer codigo;
	private String workflow;
	private Integer interno;
	private Link linkUnidadAdministrativaCompetente;
	private String terminoResolucion;
	private Integer codigoWF;
	private Rolsac2TipoProcedimiento tipoProcedimiento;
	private String estadoSIA;
	private String habilitadoFuncionario;
	private String lopdDerechos;
	private Integer esPdu;
	private String observaciones;
	private Date fechaActualizacion;
	private Integer habilitadoApoderado;
	private Link linkUnidadAdministrativaResponsable;
	private Date fechaPublicacion;
	private String lopdResponsable;
	private Integer uaCompetente;
	private Link linkLopdInfoAdicional;
	private String lopdFinalidad;
	private String objeto;
	private Rolsac2Silencio silencio;
	private Integer tramitElectronica;
	private String requisitos;
	private String incidenciasEmail;
	private String nombreProcedimientoWorkFlow;
	private String responsableEmail;
	private String responsableTelefono;
	private String responsable;
	private Integer codigoSIA;
	private Integer tipoVia;
	private Integer uaResponsable;
	private Integer tramitTelefonica;
	private Date fechaSIA;
	private Rolsac2Inicio lopdLegitimacion;
	private Integer tramitPresencial;
	private Link linkUnidadAdministrativaInstructora;
	private Integer comun;
	private Integer tieneTasa;
	private Rolsac2Contacto datosContacto;
}
