/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

/**
 * Command que retorna tots els Timers associats a una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FindAreesCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private String usuariCodi;
	private boolean rols;



	public FindAreesCommand(String usuariCodi) {
		super();
		this.usuariCodi = usuariCodi;
		this.rols = false;
	}
	public FindAreesCommand(String usuariCodi, boolean rols) {
		super();
		this.usuariCodi = usuariCodi;
		this.rols = rols;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Session session = jbpmContext.getSession();
		String queryString = "select m.group.name " +
				"from org.jbpm.identity.Membership m " +
				"where m.user.name = :usuariCodi ";
		if (isRols()) {
			queryString += "and m.group.type = 'security-role'";
		}
		Query query = session.createQuery(queryString);
		query.setParameter("usuariCodi", usuariCodi);
		return query.list();
	}

	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public boolean isRols() {
		return rols;
	}
	public void setRols(boolean rols) {
		this.rols = rols;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "usuariCodi=" + usuariCodi + ", rols=" + rols;
	}

	//methods for fluent programming
	public FindAreesCommand usuariCodi(String usuariCodi) {
		setUsuariCodi(usuariCodi);
	    return this;
	}

	public FindAreesCommand rols(boolean rols) {
		setRols(rols);
		return this;
	}
}
