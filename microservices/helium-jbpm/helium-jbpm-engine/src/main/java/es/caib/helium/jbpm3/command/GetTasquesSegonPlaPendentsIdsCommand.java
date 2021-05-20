package es.caib.helium.jbpm3.command;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

import java.util.List;

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
	public List<Object[]> execute(JbpmContext jbpmContext) throws Exception {
		StringBuilder taskQuerySb = new StringBuilder();
		
		taskQuerySb.append(
				"from " +
				"	 org.jbpm.taskmgmt.exe.TaskInstance ti " +
				"where " +
				"	 ti.marcadaFinalitzar is not null " +
				"and ti.isSuspended = false " +
				"and ti.isOpen = true " +
				"order by " + 
				"	 ti.marcadaFinalitzar asc");
		
		StringBuilder selectSb = new StringBuilder("select distinct ti.id, ti.marcadaFinalitzar, ti.iniciFinalitzacio, ti.errorFinalitzacio ");
		
		taskQuerySb.insert(0, selectSb);
		Query queryIds = jbpmContext.getSession().createQuery(taskQuerySb.toString());
		
		List<Object[]> resposta = (List<Object[]>)queryIds.list();
		return resposta;
	}

	
}
