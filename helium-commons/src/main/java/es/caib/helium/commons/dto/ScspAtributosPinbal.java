package es.caib.helium.commons.dto;


/**
 * Resposta a una petició Pinbal, tant síncrona com asíncrona.
 * La covnersió de la resposta de la llibreria de pinbal a les classes de Helium es fa a PinbalPlugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspAtributosPinbal {
	
	private String idPeticion;
	private String numElementos;
	private String timeStamp;
	private String codigoCertificado;
	private ScspEstadoPinbal estado;

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

	public ScspEstadoPinbal getEstado() {
		return this.estado;
	}

	public void setEstado(ScspEstadoPinbal estado) {
		this.estado = estado;
	}
}