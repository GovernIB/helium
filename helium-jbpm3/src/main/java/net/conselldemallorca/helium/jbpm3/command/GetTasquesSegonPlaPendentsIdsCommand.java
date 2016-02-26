package net.conselldemallorca.helium.jbpm3.command;

import java.util.List;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

/**
 * Command per a trobar les ids de 
 * les tasques pendents d'executar en segon pla
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetTasquesSegonPlaPendentsIdsCommand extends AbstractBaseCommand
{

	private static final long serialVersionUID = -1908847549444051495L;

	public GetTasquesSegonPlaPendentsIdsCommand() {
		super();
 	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		StringBuilder taskQuerySb = new StringBuilder();
		
		taskQuerySb.append(
				"from " +
				"	 org.jbpm.taskmgmt.exe.TaskInstance ti " +
				"where " +
				"	 ti.marcadaFinalitzar is not null " +
				"and ti.iniciFinalitzacio is null " + 
				"and ti.errorFinalitzacio is null " + 
				"order by " + 
				"	 ti.marcadaFinalitzar asc");
		
//		LlistatIds resposta = new LlistatIds();
		
		StringBuilder selectSb = new StringBuilder("select distinct ti.id, ti.marcadaFinalitzar ");
		
		taskQuerySb.insert(0, selectSb);
		Query queryIds = jbpmContext.getSession().createQuery(taskQuerySb.toString());
		
		List<Object[]> resposta = (List<Object[]>)queryIds.list();
		return resposta;
	}

	
}
