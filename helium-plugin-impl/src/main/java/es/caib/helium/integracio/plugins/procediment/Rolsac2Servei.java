package es.caib.helium.integracio.plugins.procediment;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2Servei {
	private String tipo;
	private String estado;
	private String lopdDestinatario;
	private Link linkLopdInfoAdicional;
	private String objeto;
	private String lopdFinalidad;
	private Boolean tramitElectronica;
	private String nombre;
	private String destinatarios;
	private String requisitos;
	private String lopdCabecera;
	private String nombreProcedimientoWorkFlow;
	private Integer uaInstructor;
	private Boolean activoLOPD;
	private Boolean publicado;
	private String responsableEmail;
	private Date fechaCaducidad;
	private String responsableTelefono;
	private Integer codigo;
	private String workflow;
	private Integer codigoSIA;
	private Boolean interno;
	private Integer uaResponsable;
	private String terminoResolucion;
	private Boolean tramitTelefonica;
	private String codigoWF;
	private Date fechaSIA;
	private String estadoSIA;
	private String habilitadoFuncionario;
	private Rolsac2Inicio lopdLegitimacion;
	private String lopdDerechos;
	private String observaciones;
	private Date fechaActualizacion;
	private Boolean habilitadoApoderado;
	private Link linkUnidadAdministrativaResponsable;
	private Boolean tramitPresencial;
	private Date fechaPublicacion;
	private Integer comun;
	private Link linkUnidadAdministrativaInstructora;
	private Boolean tieneTasa;
	private String lopdResponsable;
	private Object link_plantillaSel;
	private Link link_tipoTramitacion;
	private Rolsac2DadesContacte datosContacto;
}
