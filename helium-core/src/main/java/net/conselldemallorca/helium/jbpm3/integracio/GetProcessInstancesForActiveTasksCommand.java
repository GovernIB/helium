/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.command.Command;

/**
 * Command per obtenir la llista de tasques personals
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetProcessInstancesForActiveTasksCommand extends AbstractGetObjectBaseCommand implements Command {

	private static final long serialVersionUID = -1908847549444051495L;
	private String actorId;
	private Long entornId;
	private boolean pooled;
	private String tasca;
	private String expedient;
	private Long tipusExpedient;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private Integer prioritat;
	private Date dataLimitInici;
	private Date dataLimitFi;

	public GetProcessInstancesForActiveTasksCommand() {}

	public GetProcessInstancesForActiveTasksCommand(String actorId, Long entornId, boolean pooled) {
		super();
		this.actorId = actorId;
		this.entornId = entornId;
		this.pooled = pooled;
	}

	public GetProcessInstancesForActiveTasksCommand(Long entornId, String actorId, String tasca, String expedient, Long tipusExpedient, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, boolean pooled) {
		super();
		this.entornId = entornId;
		this.actorId = actorId;
		this.tasca = tasca;
		this.expedient = expedient;
		this.tipusExpedient = tipusExpedient;
		this.dataCreacioInici = dataCreacioInici;
		this.dataCreacioFi = dataCreacioFi;
		this.prioritat = prioritat;
		this.dataLimitInici = dataLimitInici;
		this.dataLimitFi = dataLimitFi;
		this.pooled = pooled;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		setJbpmContext(jbpmContext);
		List<Long> resultado = new ArrayList<Long>();

		String filtradoExpediente = "select count(ex) "
				+ " from Expedient as ex "
				+ " where "
				+ " ex.entorn.id = :entornId "
				+ " AND ex.processInstanceId = ti.processInstance.id";
		
		if (tipusExpedient != null) {
			filtradoExpediente += "	and ex.tipus.id = :tipusExpedient ";
		}
		
		if (expedient != null && !"".equals(expedient)) {
			filtradoExpediente += "	and UPPER('['||(case when ex.numero is not null then ex.numero ELSE ex.numeroDefault END)||']'||ex.titol) like UPPER(:expedient)";			
		}
		
		String hqlPersonal =
				"select  " + 
				"	 ti.id," +
				"    ti.processInstance.superProcessToken.id, " + 
				"	 (" + filtradoExpediente + " ) " +
				"	 from " +
				"    org.jbpm.taskmgmt.exe.TaskInstance as ti " + 
				"	 join ti.processInstance pi" +
				"	 where " +
				"	 ti.actorId = :actorId " + 
				"	 and ti.isSuspended = false " +
				"	 and ti.isOpen = true";
		
		String hqlPooled =		
				"select  " + 
				"	 ti.id," +
				"    ti.processInstance.superProcessToken.id, " +  
				"	 (" + filtradoExpediente + " ) " +
				"	 from " +
				"    org.jbpm.taskmgmt.exe.TaskInstance as ti " + 
				"	 join ti.processInstance pi" +
				"	 join ti.pooledActors pooledActor " +
				"	 where " +
				"	 pooledActor.actorId = :actorId " +
				"	 and ti.actorId is null " + 
				"	 and ti.isSuspended = false " +
				"	 and ti.isOpen = true";
		
		String hql = pooled ? hqlPooled : hqlPersonal;
		
		if (dataCreacioInici != null) {
			hql += "	and ti.create >= :dataCreacioInici";
		}
		
		if (dataCreacioFi != null) {
			hql += "	and ti.create <= :dataCreacioFi";
		}
		
		if (dataLimitInici != null) {
			hql += "	and ti.dueDate >= :dataLimitInici";
		}
		
		if (dataLimitFi != null) {
			hql += "	and ti.dueDate <= :dataLimitFi";
		}
		
		if (prioritat != null) {
			hql += "	and ti.priority = :prioritat";
		}
		
		if (tasca != null && !"".equals(tasca)) {
			hql += "	and pi.processDefinition.id = (select (ta.definicioProces.jbpmId) from Tasca as ta where UPPER(ta.nom) like UPPER(:tasca) and ta.jbpmName = ti.name and ti.processInstance.processDefinition.id = ta.definicioProces.jbpmId) ";
		}
		
		Query query = jbpmContext.getSession().createQuery(hql);
		query.setString("actorId", actorId);
		query.setLong("entornId", entornId);
		
		if (dataCreacioInici != null) {
			query.setDate("dataCreacioInici", dataCreacioInici);
		}
		
		if (dataCreacioFi != null) {
			query.setDate("dataCreacioFi", dataCreacioFi);
		}
		
		if (dataLimitInici != null) {
			query.setDate("dataLimitInici", dataLimitInici);
		}
		
		if (dataLimitFi != null) {
			query.setDate("dataLimitFi", dataLimitFi);
		}
		
		if (prioritat != null) {
			query.setInteger("prioritat",3-prioritat);
		}
		
		if (tasca != null && !"".equals(tasca)) {
			query.setString("tasca","%"+tasca+"%");
		}
		
		if (expedient != null && !"".equals(expedient)) {
			query.setString("expedient", "%"+expedient+"%");
		}
		
		if (tipusExpedient != null) {
			query.setLong("tipusExpedient", tipusExpedient);
		}
		List<Object[]> resultIds = query.list();
		for (Object[] fila : resultIds) {
			if (fila[1] != null) {
				// Si tiene superProcessToken no es nodo final
				boolean fin = false;
				Long token = (Long) fila[1];
				while(!fin) {
					Query querySuperProcessToken = jbpmContext.getSession().createQuery(
						"select  " + 
						"	 ti.id," +
						"    pi.superProcessToken.id, " +  
						"	 (" + filtradoExpediente + " ) " +
						"	 from " + 
						"	 org.jbpm.taskmgmt.exe.TaskInstance as ti " +
						"	 join ti.processInstance pi" +
						"	 where " +
						"	 pi.id in (select tk.processInstance.id from org.jbpm.graph.exe.Token as tk where tk.id = ( :token ) ) ");
					querySuperProcessToken.setLong("entornId", entornId);
					querySuperProcessToken.setLong("token",token);
					
					if (expedient != null && !"".equals(expedient)) {
						querySuperProcessToken.setString("expedient", "%"+expedient+"%");
					}
					
					if (tipusExpedient != null) {
						querySuperProcessToken.setLong("tipusExpedient", tipusExpedient);
					}
					
					querySuperProcessToken.setMaxResults(1);
					
					Object[] res = (Object[]) querySuperProcessToken.uniqueResult();
					if (res[1] == null) {
						// Nodo final
						if ((Long) res[2] > 0) {
							// Cambiamos el número de expedientes de ese taskinstance
							fila[2] = res[2];
						}
						fin = true;
					} else {
						token = (Long) res[1];
					}
				}
			}
			if ((Long)fila[2] > 0 && !resultado.contains((Long) fila[0])) {
				// Si tiene expediente
				resultado.add((Long) fila[0]);
			}
		}

		return resultado;
	}

	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "actorId=" + actorId;
	}

	public GetProcessInstancesForActiveTasksCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}
}
