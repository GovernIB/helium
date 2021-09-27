/**
 * 
 */
package es.caib.helium.client.engine.model;

import es.caib.helium.client.integracio.portafirmes.model.DocumentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO amb informació d'un document d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTasca implements Serializable {

	private static final long serialVersionUID = -8552509655331736854L;

	private Long id;

	private boolean required;
	private boolean readOnly;
	private int order;

	private DocumentDto document;

	// Heretable
	protected boolean heretat = false;
	protected boolean sobreescriu = false;
	/** Quan es crea una relació entre un camp i la definició de procés pot ser que el camp sigui 
	 * del tipus d'expedient i la tasca sigui de la definició de procés del tipus expedient pare heretat. Aquest
	 * camp fa referència al tipus d'expedient que posseix la relació camp-tasca.
	 */
	private Long expedientTipusId;

}
