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
public class FindGrupCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private String filtre;

	public FindGrupCommand(String filtre) {
		super();
		this.filtre = filtre;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Session session = jbpmContext.getSession();
		Query query = session.createQuery("select g.name " +
				"from org.jbpm.identity.Group g " +
				"where g.type = 'organisation' " +
				"    and (:esNullFiltre = true or lower(g.name) like lower('%'||:filtre||'%')) ");
		query.setParameter("esNullFiltre", (filtre == null || filtre.isEmpty()));
		query.setParameter("filtre", filtre);
		return query.list();
	}

	public String getFiltre() {
		return filtre;
	}
	public void setFiltre(String filtre) {
		this.filtre = filtre;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "filtre=" + filtre;
	}

	//methods for fluent programming
	public FindGrupCommand usuariCodi(String filtre) {
		setFiltre(filtre);
	    return this;
	}
}
