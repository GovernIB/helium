package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Resposta a una petició Pinbal, tant síncrona com asíncrona.
 * La covnersió de la resposta de la llibreria de pinbal a les classes de Helium es fa a PinbalPlugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspAtributos {
	
	private String idPeticion;
	private String numElementos;
	private String timeStamp;
	private String codigoCertificado;
	private ScspEstado estado;

	public String getIdPeticion() {
		return this.idPeticion;
	}

	public void setIdPeticion(String idPeticion) {
		this.idPeticion = idPeticion;
	}

	public String getNumElementos() {
		return this.numElementos;
	}

	public void setNumElementos(String numElementos) {
		this.numElementos = numElementos;
	}

	public String getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getCodigoCertificado() {
		return this.codigoCertificado;
	}

	public void setCodigoCertificado(String codigoCertificado) {
		this.codigoCertificado = codigoCertificado;
	}

	public ScspEstado getEstado() {
		return this.estado;
	}

	public void setEstado(ScspEstado estado) {
		this.estado = estado;
	}
}