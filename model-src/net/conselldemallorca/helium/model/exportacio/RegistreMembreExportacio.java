/**
 * 
 */
package net.conselldemallorca.helium.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'una validació per exportar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class RegistreMembreExportacio implements Serializable {

	private String codi;
	private boolean obligatori;
	private boolean llistar;
	int ordre;



	public RegistreMembreExportacio(String codi, boolean obligatori, boolean llistar, int ordre) {
		this.codi = codi;
		this.obligatori = obligatori;
		this.llistar = llistar;
		this.ordre = ordre;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}
	public boolean isLlistar() {
		return llistar;
	}
	public void setLlistar(boolean llistar) {
		this.llistar = llistar;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}



	private static final long serialVersionUID = 1L;

}
