/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe que conté informació sobre la delegació d'una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DelegationInfo implements Serializable {

	private String sourceTaskId;
	private String targetTaskId;
	private Date start;
	private Date end;
	private String comment;
	private boolean supervised;

	private static final long serialVersionUID = 774909297938469787L;
}
