package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command pel manteniment de rols.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
public class PermisCommand {

	private String codi;
	private String descripcio;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
}
