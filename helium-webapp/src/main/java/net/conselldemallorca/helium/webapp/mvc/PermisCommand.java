package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command pel manteniment de rols.
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
