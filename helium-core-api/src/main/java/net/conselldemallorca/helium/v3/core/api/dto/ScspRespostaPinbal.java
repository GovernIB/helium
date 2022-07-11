/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * Resposta a una petició Pinbal
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspRespostaPinbal {
	
	private String idPeticion;
	private ScspJustificant justificant;
	private EstatDto estatDto;
	private String estat;
	
	

	public String getIdPeticion() {
		return idPeticion;
	}

	public void setIdPeticion(String idPeticion) {
		this.idPeticion = idPeticion;
	}


	public void setEstat(EstatDto estat) {
		this.estat = estat.toString();
	}
	public void setEstat(String estat) {
		this.estat = estat.toString();
	}

	public ScspJustificant getJustificant() {
		return justificant;
	}

	public void setJustificant(ScspJustificant justificant) {
		this.justificant = justificant;
	}
	
	
	

	
}
