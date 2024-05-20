package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

/**
 * Resposta a una petició Pinbal, tant síncrona com asíncrona.
 * La covnersió de la resposta de la llibreria de pinbal a les classes de Helium es fa a PinbalPlugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspRespostaPinbal {
	
	private String idPeticion;
	private ScspJustificant justificant;
	private Date dataProcessament;
	private EstatDto estatDto;
	private String estat;
	private PeticioPinbalEstatEnum estatAsincron;
	private String errorProcessament;

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
	public Date getDataProcessament() {
		return dataProcessament;
	}
	public void setDataProcessament(Date dataProcessament) {
		this.dataProcessament = dataProcessament;
	}
	public PeticioPinbalEstatEnum getEstatAsincron() {
		return estatAsincron;
	}
	public void setEstatAsincron(PeticioPinbalEstatEnum estatAsincron) {
		this.estatAsincron = estatAsincron;
	}
	public String getErrorProcessament() {
		return errorProcessament;
	}
	public void setErrorProcessament(String errorProcessament) {
		this.errorProcessament = errorProcessament;
	}
}