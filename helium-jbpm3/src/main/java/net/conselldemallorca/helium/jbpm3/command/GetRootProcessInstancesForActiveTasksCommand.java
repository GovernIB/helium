/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper.MostrarTasquesDto;
import net.conselldemallorca.helium.jbpm3.integracio.LlistatIds;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.command.Command;

/**
 * Command per obtenir la llista de tasques personals
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetRootProcessInstancesForActiveTasksCommand extends AbstractGetObjectBaseCommand implements Command {

	private static final long serialVersionUID = -1908847549444051495L;
	private String actorId;
	private boolean tasquesUsuari = false;
	private List<Long> idsPIExpedients;
	private String tascaSel; 
	private String titol;
	private Date dataCreacioInici; 
	private Date dataCreacioFi;
	private Integer prioritat;
	private Date dataLimitInici;
	private Date dataLimitFi;
	private boolean mostrarTasquesTots = false;
	private Boolean mostrarTasquesNomesGroup = false;
	private Boolean mostrarTasquesNomesPersonals = false;
	private int firstRow;
	private int maxResults;
	private String sort;
	private boolean asc;
	private boolean nomesActives = true;

	public GetRootProcessInstancesForActiveTasksCommand() {}
	
	public GetRootProcessInstancesForActiveTasksCommand(String actorId, List<Long> idsPIExpedients) {
		super();
		this.actorId = actorId;
		this.idsPIExpedients = idsPIExpedients;
	}

	public GetRootProcessInstancesForActiveTasksCommand(String actorId, List<Long> idsPIExpedients, Boolean mostrarTasquesNomesGroup) {
		super();
		this.actorId = actorId;
		this.idsPIExpedients = idsPIExpedients;
		this.mostrarTasquesNomesPersonals = !mostrarTasquesNomesGroup;
		this.mostrarTasquesNomesGroup = mostrarTasquesNomesGroup;
	}

	public GetRootProcessInstancesForActiveTasksCommand(String actorId, String titol, String tascaSel, List<Long> idsPIExpedients, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, String sort, boolean asc,Boolean mostrarTasquesNomesGroup) {
		super();
		this.actorId = actorId;
		this.idsPIExpedients = idsPIExpedients;
		this.titol = titol; 
		this.tascaSel = tascaSel; 
		this.dataCreacioInici = dataCreacioInici; 
		this.dataCreacioFi = dataCreacioFi;
		this.prioritat = prioritat;
		this.dataLimitInici = dataLimitInici;
		this.dataLimitFi = dataLimitFi;
		this.mostrarTasquesNomesPersonals = !mostrarTasquesNomesGroup;
		this.mostrarTasquesNomesGroup = mostrarTasquesNomesGroup;
		this.sort = sort;
		this.asc = asc;
	}

	public GetRootProcessInstancesForActiveTasksCommand(String responsable, String titol, String tascaSel, List<Long> idsExpedients, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, List<OrdreDto> ordres, boolean mostrarTasquesPersonals, boolean mostrarTasquesGrup, boolean tasquesUsuari) {
		super();
		this.actorId = responsable;
		this.idsPIExpedients = idsExpedients;
		this.titol = titol == null ? null : titol.trim(); 
		this.tascaSel = tascaSel; 
		this.dataCreacioInici = dataCreacioInici; 
		this.dataCreacioFi = dataCreacioFi;
		this.prioritat = prioritat;
		this.dataLimitInici = dataLimitInici;
		this.dataLimitFi = dataLimitFi;
		this.mostrarTasquesNomesPersonals = mostrarTasquesPersonals;
		this.mostrarTasquesNomesGroup = mostrarTasquesGrup;
		this.mostrarTasquesTots = (mostrarTasquesPersonals && mostrarTasquesGrup) || (tasquesUsuari && !mostrarTasquesPersonals && !mostrarTasquesGrup);
		for (OrdreDto or : ordres) {
			this.asc = or.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
			this.sort = or.getCamp();
			break;
		}
	}

	public GetRootProcessInstancesForActiveTasksCommand(String actorId, String titol, String tascaSel, List<Long> idsPIExpedients, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, String sort, boolean asc, MostrarTasquesDto mostrarTasques) {
		super();
		this.actorId = actorId;
		this.idsPIExpedients = idsPIExpedients;
		this.titol = titol; 
		this.tascaSel = tascaSel; 
		this.dataCreacioInici = dataCreacioInici; 
		this.dataCreacioFi = dataCreacioFi;
		this.prioritat = prioritat;
		this.dataLimitInici = dataLimitInici;
		this.dataLimitFi = dataLimitFi;
		this.mostrarTasquesTots = mostrarTasques == MostrarTasquesDto.MOSTRAR_TASQUES_TOTS;
		this.mostrarTasquesNomesGroup = mostrarTasques == MostrarTasquesDto.MOSTRAR_TASQUES_NOMES_GROUPS;
		this.mostrarTasquesNomesPersonals = mostrarTasques == MostrarTasquesDto.MOSTRAR_TASQUES_NOMES_PERSONALS;
		this.sort = sort;
		this.asc = asc;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		
		setJbpmContext(jbpmContext);
		
		String hqlNoUserLlistatTasques =
		    "select  " + 
		    "    ti.processInstance.id, " +
		    "    ti.processInstance.superProcessToken.id, " +
		    "    ti.id," +
		    " 	 ti.name" +
		    "  from " +
		    "    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
		    "  where " +
		    "  ti.isSuspended = false and ti.isOpen = true " +
		    ((nomesActives) ? "and ti.isSuspended = false and ti.isOpen = true " : " ") +
			((!mostrarTasquesTots && mostrarTasquesNomesGroup) ? "and ti.actorId is null " : " ") +
			((!mostrarTasquesTots && mostrarTasquesNomesPersonals) ? "and ti.actorId is not null " : " ");
		
		String hqlPersonal =
		    "select  " + 
		    "    ti.processInstance.id, " +
		    "    ti.processInstance.superProcessToken.id, " +
		    "    ti.id," +
		    " 	 ti.name" +
		    "  from " +
		    "    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
		    "  where " +
		    "    ti.actorId = :actorId " + 
		    ((nomesActives) ? "and ti.isSuspended = false and ti.isOpen = true" : "");
		  
		String hqlPooled =  
		    "select  " + 
		    "    ti.processInstance.id, " +
		    "    ti.processInstance.superProcessToken.id, " +
		    "    ti.id," +
		    " 	 ti.name" +
		    "  from " +
		    "    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
		    "  join ti.pooledActors pooledActor " +
		    "  where " +
		    "  pooledActor.actorId = :actorId " +
		    "and ti.actorId is null " + 
		    ((nomesActives) ? "and ti.isSuspended = false and ti.isOpen = true" : "");
		  
		String hql = "";
		
		if (dataCreacioInici != null) {
			hql += " and ti.create >= :dataCreacioInici";
		}

		if (dataCreacioFi != null) {
			hql += " and ti.create <= :dataCreacioFi";
		}

		if (dataLimitInici != null) {
			hql += " and ti.dueDate >= :dataLimitInici";
		}

		if (dataLimitFi != null) {
			hql += " and ti.dueDate <= :dataLimitFi";
		}

		if (prioritat != null) {
			hql += " and ti.priority = :prioritat";
		}

		if (tascaSel != null && !"".equals(tascaSel)) {
			hql += " and ti.task.name = :tascaSel";
		}
		
		if (titol != null && !"".equals(titol)) {
			hql += " and upper(ti.description) like '%@#@TITOL@#@%'||:titol||'%@#@ENTORNID@#@%' ";
		}		
		
		if ("dataCreacio".equals(sort)) {
			hql += " order by ti.create " + (asc ? "asc" : "desc");
		} else if ("prioritat".equals(sort)) {
			hql += " order by ti.priority " + (asc ? "asc" : "desc");
		} else if ("dataLimit".equals(sort)) {
			hql += " order by ti.dueDate " + (asc ? "asc" : "desc");
		} else if ("titol".equals(sort)) {
			hql += " order by ti.name " + (asc ? "asc" : "desc");
		} 
		
		List<Object[]> llistaActorId = new ArrayList<Object[]>();
		
		if (actorId == null || "".equals(actorId)) {
			Query query = jbpmContext.getSession().createQuery(hqlNoUserLlistatTasques + hql);
			
			if (dataCreacioInici != null) 
				query.setDate("dataCreacioInici", dataCreacioInici);
			
			if (dataCreacioFi != null) 
				query.setDate("dataCreacioFi", dataCreacioFi);
			
			if (dataLimitInici != null) 
				query.setDate("dataLimitInici", dataLimitInici);
			
			if (dataLimitFi != null) 
				query.setDate("dataLimitFi", dataLimitFi);
			
			if (prioritat != null) 
				query.setInteger("prioritat",3-prioritat);
			
			if (tascaSel != null && !"".equals(tascaSel))
				query.setString("tascaSel", tascaSel);
			
			if (titol != null && !"".equals(titol))
				query.setString("titol", titol.toUpperCase());
			
			if (mostrarTasquesTots || mostrarTasquesNomesPersonals || mostrarTasquesNomesGroup) {
				llistaActorId.addAll(query.list());		
			}
		} else {
			Query queryPersonal = jbpmContext.getSession().createQuery(hqlPersonal + hql);
			queryPersonal.setString("actorId", actorId);
		
			Query queryPooled = jbpmContext.getSession().createQuery(hqlPooled + hql);
			queryPooled.setString("actorId", actorId);
		
			if (dataCreacioInici != null) {
				queryPersonal.setDate("dataCreacioInici", dataCreacioInici);
				queryPooled.setDate("dataCreacioInici", dataCreacioInici);
			}
			
			if (dataCreacioFi != null) {
				queryPersonal.setDate("dataCreacioFi", dataCreacioFi);
				queryPooled.setDate("dataCreacioFi", dataCreacioFi);
			}
			
			if (dataLimitInici != null) {
				queryPersonal.setDate("dataLimitInici", dataLimitInici);
				queryPooled.setDate("dataLimitInici", dataLimitInici);
			}
			
			if (dataLimitFi != null) {
				queryPersonal.setDate("dataLimitFi", dataLimitFi);
				queryPooled.setDate("dataLimitFi", dataLimitFi);
			}
			
			if (prioritat != null) {
				queryPersonal.setInteger("prioritat",3-prioritat);
				queryPooled.setInteger("prioritat",3-prioritat);
			}

			if (tascaSel != null && !"".equals(tascaSel)) {
				queryPersonal.setString("tascaSel", tascaSel);
				queryPooled.setString("tascaSel", tascaSel);
			}
			
			if (titol != null && !"".equals(titol)) {
				queryPersonal.setString("titol", titol.toUpperCase());
				queryPooled.setString("titol", titol.toUpperCase());
			}
			
			if (mostrarTasquesTots || mostrarTasquesNomesPersonals) {
				llistaActorId.addAll(queryPersonal.list());
			}
			
			if (mostrarTasquesTots || mostrarTasquesNomesGroup) {
				llistaActorId.addAll(queryPooled.list());
			}
		}
		
		Set<Long> superProcessTokenIds = new HashSet<Long>();
		do {
			superProcessTokenIds.clear();
			for (Object[] reg: llistaActorId) {
				if (reg[1] != null)
					superProcessTokenIds.add((Long)reg[1]);
			}
			if (superProcessTokenIds.size() > 0) {
				Query queryProcessInstancesPare = jbpmContext.getSession().createQuery(
						"select " +
						"    t.processInstance.id, " +
						"    t.processInstance.superProcessToken.id, " +
						"    t.id " +
						"from " +
						"    org.jbpm.graph.exe.Token as t " +
						"where " +
						"    t.id in (:superProcessTokenIds)");
				queryProcessInstancesPare.setParameterList(
						"superProcessTokenIds",
						superProcessTokenIds);
				
				queryProcessInstancesPare = translateIn(superProcessTokenIds, queryProcessInstancesPare, "superProcessTokenIds", "t.id");
				
				List<Object[]> llistaProcessInstancesPare = queryProcessInstancesPare.list();
				for (Object[] regAct: llistaActorId) {
					if (regAct[1] != null) {
						for (Object[] regSup: llistaProcessInstancesPare) {
							if (regSup[2].equals(regAct[1])) {
								regAct[0] = regSup[0];	
								regAct[1] = regSup[1];	
								break;
							}
						}
					}
				}
			}
		} while (superProcessTokenIds.size() > 0);
		
		List<Long> listadoTask = new ArrayList<Long>();
		
		// Ordenamos la lista en el caso de que sea por expedientes
	    if ("expedientTitol".equals(sort) || "expedientIdentificador".equals(sort) || "expedientTipusNom".equals(sort)) {
	    	for (Long id : idsPIExpedients) {
		    	for (Object[] fila : llistaActorId) {
		    		if (id.equals(fila[0]) && !listadoTask.contains(fila[2])) {
		    			listadoTask.add((Long) fila[2]);
		    		}
		    	}	
	    	}			
		} else {
	    	for (Object[] fila : llistaActorId) {
	    		if (idsPIExpedients.contains(fila[0]) && !listadoTask.contains(fila[2])) {
	    			listadoTask.add((Long) fila[2]);
	    		}
	    	}			
		}
		
	    LlistatIds listado = new LlistatIds();
	    
	    if (getFirstRow() > listadoTask.size()) {
	    	setFirstRow(0);
	    }
	    
		maxResults = (maxResults > listadoTask.size()) ? listadoTask.size() : maxResults;
		int limit = (maxResults > 0)? getFirstRow()+maxResults : listadoTask.size();
		limit = (limit > listadoTask.size()) ? listadoTask.size() : limit;
	    listado.setCount(listadoTask.size());
	    listado.setIds(listadoTask.subList(getFirstRow(), limit));

	    return listado;
	}


	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public int getFirstRow() {
		return firstRow;
	}
	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}
	public boolean isTasquesUsuari() {
		return tasquesUsuari;
	}
	public void setTasquesUsuari(boolean tasquesUsuari) {
		this.tasquesUsuari = tasquesUsuari;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public boolean isNomesActives() {
		return nomesActives;
	}
	public void setNomesActives(boolean nomesActives) {
		this.nomesActives = nomesActives;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "actorId=" + actorId;
	}

	public GetRootProcessInstancesForActiveTasksCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}

	private Query translateIn(final Collection<?> values, final Query query, final String param, final String namedParam) {
		final int size = values.size();
		final int mod = (size % 1000);
		final int numberOfIn = (size / 1000) + (mod == 0 ? 0 : 1);
		final List<String> params = new ArrayList<String>();

		final String regex = "([\\w\\.]+)\\s+(not\\s+)?in\\s*\\(\\s*:" + param + "\\s*\\)";

		final StringBuilder in = new StringBuilder(" $2 (");

		for (int i = 0; i < numberOfIn; i++) {
			final StringBuilder newNameParam = new StringBuilder();
			newNameParam.append(param);
			newNameParam.append(i);

			in.append("$1 in (:");
			in.append(newNameParam.toString());
			in.append(")");

			params.add(newNameParam.toString());

			if (i + 1 < numberOfIn) {
				in.append(" or ");
			}
		}

		in.append(") ");

		final String newQueryString = query.getQueryString().replaceAll(regex, in.toString());

		final Query newQuery = getJbpmContext().getSession().createQuery(newQueryString);
		
		final List<Object> copy = new ArrayList<Object>(values);

		for (int count = 0, from = 0, to = (size>1000) ? 1000 : size; count < numberOfIn; count++) {
			final String nameParam = params.get(count);
			newQuery.setParameterList(nameParam, copy.subList(from, to));

			from = to;

			if (count + 2 < numberOfIn) {
				to += 1000;
			}
			else if (count + 2 == numberOfIn) {
				to += (mod == 0) ? 1000 : mod;
			}
		}

		return newQuery;
	}
}
