/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Classe dto abstracte pels dto's del tipus d'expedient que poden ser heretables.
 * Serveix per informar les dades que facilitin més tard consultar si l'objecte és 
 * heretat o sobreescriu un objecte heretat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class HeretableDto implements Serializable {

	/** Indica si l'objecte és heretat del tipus d'expedient pare.*/
	protected boolean heretat = false;
	/** Indica si l'objecte sobreescriu una propietat del tipus d'expedient pare. */
	protected boolean sobreescriu = false;

	public boolean isHeretat() {
		return heretat;
	}
	public void setHeretat(boolean heretat) {
		this.heretat = heretat;
	}
	public boolean isSobreescriu() {
		return sobreescriu;
	}
	public void setSobreescriu(boolean sobreescriu) {
		this.sobreescriu = sobreescriu;
	}
	
	private static final long serialVersionUID = 8338184447461193307L;
}
