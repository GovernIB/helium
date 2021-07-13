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
public class FindCarrecCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;

	public enum TipusConsulta {
		PERSONA_AMB_CARREC,
		PERSONA_AMB_GRUP,
		PERSONA_AMB_CARREC_I_GRUP,
		CARREC_PER_PERSONA_I_GRUP,
		FILTRE
	}
	private String filtre;
	private String personaCodi;
	private String grupCodi;
	private String carrecCodi;
	private TipusConsulta tipusConsulta;

	public FindCarrecCommand(TipusConsulta tipusConsulta, String filtre) {
		this.tipusConsulta = tipusConsulta;
		this.filtre = filtre;
	}

	public FindCarrecCommand(TipusConsulta tipusConsulta, String personaCodi, String grupCodi, String carrecCodi) {
		this.tipusConsulta = tipusConsulta;
		this.personaCodi = personaCodi;
		this.grupCodi = grupCodi;
		this.carrecCodi = carrecCodi;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Session session = jbpmContext.getSession();
		Query query;
		String queryString = "";
		switch (tipusConsulta) {
			case FILTRE:
				query = session.createQuery("select distinct m.role, m.group.name " +
						"from org.jbpm.identity.Membership m " +
						"where " +
						"    (:esNullFiltre = true or lower(m.role) like lower('%'||:filtre||'%') " +
						" 		or lower(m.group.name) like lower('%'||:filtre||'%'))" +
						"    and m.role is not null ");
				query.setParameter("esNullFiltre", (filtre == null || filtre.isEmpty()));
				query.setParameter("filtre", filtre);
				break;
			case PERSONA_AMB_GRUP:
				query = session.createQuery("select distinct m.user.name " +
						"from org.jbpm.identity.Membership m " +
						"where m.group.name = :grupCodi");
				query.setParameter("grupCodi", grupCodi);
				break;
			case PERSONA_AMB_CARREC:
				query = session.createQuery("select m.user.name " +
						"from org.jbpm.identity.Membership m " +
						"where m.role = :carrecCodi");
				query.setParameter("carrecCodi", carrecCodi);
				break;
			case PERSONA_AMB_CARREC_I_GRUP:
				query = session.createQuery("select m.user.name " +
						"from org.jbpm.identity.Membership m " +
						"where m.group.name = :grupCodi " +
						"  and m.role = :carrecCodi");
				query.setParameter("grupCodi", grupCodi);
				query.setParameter("carrecCodi", carrecCodi);
				break;
			case CARREC_PER_PERSONA_I_GRUP:
				query = session.createQuery("select m.role " +
						"from org.jbpm.identity.Membership m " +
						"where m.user.name = :personaCodi " +
						"  and m.group.name = :grupCodi");
				query.setParameter("personaCodi", personaCodi);
				query.setParameter("grupCodi", grupCodi);
				break;
		}

		return query.list();
	}

	public String getFiltre() {
		return filtre;
	}
	public void setFiltre(String filtre) {
		this.filtre = filtre;
	}
	public String getPersonaCodi() {
		return personaCodi;
	}
	public void setPersonaCodi(String personaCodi) {
		this.personaCodi = personaCodi;
	}
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public String getCarrecCodi() {
		return carrecCodi;
	}
	public void setCarrecCodi(String carrecCodi) {
		this.carrecCodi = carrecCodi;
	}
	public TipusConsulta getTipusConsulta() {
		return tipusConsulta;
	}
	public void setTipusConsulta(TipusConsulta tipusConsulta) {
		this.tipusConsulta = tipusConsulta;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "personaCodi=" + personaCodi +
				", grupCodi" + grupCodi +
				", carrecCodi" + carrecCodi +
				", filtre" + filtre;
	}

	//methods for fluent programming
	public FindCarrecCommand personaCodi(String personaCodi) {
		setPersonaCodi(personaCodi);
	    return this;
	}
	public FindCarrecCommand grupCodi(String grupCodi) {
		setGrupCodi(grupCodi);
		return this;
	}
	public FindCarrecCommand carrecCodi(String carrecCodi) {
		setCarrecCodi(carrecCodi);
		return this;
	}
	public FindCarrecCommand filtre(String filtre) {
		setFiltre(filtre);
		return this;
	}
}
