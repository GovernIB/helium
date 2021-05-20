/**
 *
 */
package org.jbpm.graph.exe;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.jbpm.graph.def.Identifiable;

/**
 * Objecte de domini que representa un entorn de la instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcessInstanceEntorn implements Identifiable, Serializable {

	private long id;
	private String codi;
	private String nom;

	private static final long serialVersionUID = 1L;

}
