/**
 * 
 */
package es.caib.helium.jbpm3.command;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.def.Action;

/**
 * Command per a retornar un node segons el seu identificador. Serveix per trobar
 * els nodes de tipus accio pels paràmetres de retroacció lligats a nodes action de
 * definicions de procés anteriors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetActionByIdCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = 4226595739537604600L;

	private long actionId;

	public GetActionByIdCommand(long actionId) {
		super();
		this.actionId = actionId;
	}

	public Action execute(JbpmContext jbpmContext) throws Exception {
		
		Query query = jbpmContext.getSession().createQuery(
				  " select a "
				  + " from org.jbpm.graph.def.Action a "
				  + "where a.id = :actionId");
		query.setLong("actionId", actionId);
		
		Action resposta = (Action) query.uniqueResult();
		
		return resposta;
	}

	public long getActionId() {
		return actionId;
	}
	public void setActionId(long id) {
		this.actionId = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + actionId;
	}

}
