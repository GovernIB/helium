/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Classe dto abstracte pels dto's del tipus d'expedient que poden ser heretables.
 * Serveix per informar les dades que facilitin més tard consultar si l'objecte és 
 * heretat o sobreescriu un objecte heretat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public abstract class HeretableDto implements Serializable {

	/** Indica si l'objecte és heretat del tipus d'expedient pare.*/
	protected boolean heretat = false;
	/** Indica si l'objecte sobreescriu una propietat del tipus d'expedient pare. */
	protected boolean sobreescriu = false;

	private static final long serialVersionUID = 1L;
}
