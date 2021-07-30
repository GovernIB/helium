/**
 * 
 */
package es.caib.helium.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe que conté informació sobre la delegació d'una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelegationInfo implements Serializable {

	private String sourceTaskId;
	private String targetTaskId;
	private Date start;
	private Date end;
	private String comment;
	private boolean supervised;
	private String usuariDelegador;
	private String usuariDelegat;

	private static final long serialVersionUID = 774909297938469787L;

}
