/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;

/**
 * Command per retornar un token donat el seu id
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class GetTokenByIdCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public GetTokenByIdCommand() {}

	public GetTokenByIdCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		return jbpmContext.getToken(id);
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	// methods for fluent programming
	public GetTokenByIdCommand id(long id) {
		setId(id);
	    return this;
	}

}
