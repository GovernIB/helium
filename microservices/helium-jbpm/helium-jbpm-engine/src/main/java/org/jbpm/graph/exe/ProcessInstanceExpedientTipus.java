/**
 *
 */
package org.jbpm.graph.exe;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.jbpm.graph.def.Identifiable;

/**
 * Objecte de domini que representa un tipus d'expedient de la instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcessInstanceExpedientTipus implements Identifiable, Serializable {

	private long id;
	private String codi;
	private String nom;
	private boolean teTitol;
	private boolean teNumero;

	private static final long serialVersionUID = 1L;
}
