/**
 * 
 */
package es.caib.helium.jbpm3.spring;

import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.orm.hibernate3.support.ClobStringType;


/**
 * Implementació de ClobStringType amb el LobHandlerConfigurat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JbpmClobStringType extends ClobStringType {

	public JbpmClobStringType() {
		super(new DefaultLobHandler(), null);
	}

}
