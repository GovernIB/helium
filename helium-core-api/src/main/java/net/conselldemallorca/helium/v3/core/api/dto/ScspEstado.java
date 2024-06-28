package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Resposta a una petició Pinbal, tant síncrona com asíncrona.
 * La covnersió de la resposta de la llibreria de pinbal a les classes de Helium es fa a PinbalPlugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspEstado {
	
	private String codigoEstado;
	private String codigoEstadoSecundario;
	private String literalError;
	private String literalErrorSec;
	private Integer tiempoEstimadoRespuesta;

	public String getCodigoEstado() {
		return this.codigoEstado;
	}

	public void setCodigoEstado(String codigoEstado) {
		this.codigoEstado = codigoEstado;
	}

	public String getCodigoEstadoSecundario() {
		return this.codigoEstadoSecundario;
	}

	public void setCodigoEstadoSecundario(String codigoEstadoSecundario) {
		this.codigoEstadoSecundario = codigoEstadoSecundario;
	}

	public String getLiteralError() {
		return this.literalError;
	}

	public void setLiteralError(String literalError) {
		this.literalError = literalError;
	}

	public String getLiteralErrorSec() {
		return this.literalErrorSec;
	}

	public void setLiteralErrorSec(String literalErrorSec) {
		this.literalErrorSec = literalErrorSec;
	}

	public Integer getTiempoEstimadoRespuesta() {
		return this.tiempoEstimadoRespuesta;
	}

	public void setTiempoEstimadoRespuesta(Integer tiempoEstimadoRespuesta) {
		this.tiempoEstimadoRespuesta = tiempoEstimadoRespuesta;
	}
}