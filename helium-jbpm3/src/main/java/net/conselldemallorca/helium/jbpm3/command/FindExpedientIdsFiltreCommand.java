/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

import net.conselldemallorca.helium.jbpm3.integracio.ResultatConsultaPaginadaJbpm;

/**
 * Command per a trobar els logs associats a una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FindExpedientIdsFiltreCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;

	private Long entornId;
	private String actorId;
	private Collection<Long> tipusIdPermesos;
	private String titol;
	private String numero;
	private Long tipusId;
	private Date dataIniciInici;
	private Date dataIniciFi;
	private Long estatId;
	private boolean nomesIniciats;
	private boolean nomesFinalitzats;
	private Double geoPosX;
	private Double geoPosY;
	private String geoReferencia;
	private boolean mostrarAnulats;
	private boolean mostrarNomesAnulats;
	private boolean nomesAlertes;
	private boolean nomesErrors;
	private boolean nomesTasquesPersonals;
	private boolean nomesTasquesGrup;
	private boolean nomesTasquesMeves;
	private int firstResult;
	private int maxResults;
	private String sort;
	private boolean asc;
	private boolean nomesCount;

	public FindExpedientIdsFiltreCommand(
			Long entornId,
			String actorId,
			Collection<Long> tipusIdPermesos,
			String titol,
			String numero,
			Long tipusId,
			Date dataIniciInici,
			Date dataIniciFi,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesIniciats,
			boolean nomesFinalitzats,
			boolean mostrarAnulats,
			boolean mostrarNomesAnulats,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesTasquesMeves,
			int firstResult,
			int maxResults,
			String sort,
			boolean asc,
			boolean nomesCount) {
		super();
		this.entornId = entornId;
		this.actorId = actorId;
		this.tipusIdPermesos = tipusIdPermesos;
		this.titol = titol;
		this.numero = numero;
		this.tipusId = tipusId;
		this.dataIniciInici = dataIniciInici;
		this.dataIniciFi = dataIniciFi;
		this.estatId = estatId;
		this.geoPosX = geoPosX;
		this.geoPosY = geoPosY;
		this.geoReferencia = geoReferencia;
		this.nomesIniciats = nomesIniciats;
		this.nomesFinalitzats = nomesFinalitzats;
		this.mostrarAnulats = mostrarAnulats;
		this.mostrarNomesAnulats = mostrarNomesAnulats;
		this.nomesAlertes = nomesAlertes;
		this.nomesErrors = nomesErrors;
		this.nomesTasquesPersonals = nomesTasquesPersonals;
		this.nomesTasquesGrup = nomesTasquesGrup;
		this.nomesTasquesMeves = nomesTasquesMeves;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
		this.sort = sort;
		this.asc = asc;
		this.nomesCount = nomesCount;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		if (tipusIdPermesos.isEmpty()) {
			return new ResultatConsultaPaginadaJbpm<Long>(0);
		}
		StringBuilder expedientQuerySb = new StringBuilder();
		expedientQuerySb.append(
				"from " +
				"    org.jbpm.graph.exe.ProcessInstanceExpedient pie " +
				"where " +
				"    pie.entorn.id = :entornId " +
				"and pie.tipus.id in (:tipusIdPermesos) ");
		if (titol != null && !titol.isEmpty()) {
			expedientQuerySb.append("and lower(pie.titol) like lower('%'||:titol||'%') ");
		}
		if (numero != null && !numero.isEmpty()) {
			expedientQuerySb.append("and lower(pie.numero) like lower('%'||:numero||'%') ");
		}
		if (tipusId != null) {
			expedientQuerySb.append("and pie.tipus.id = :tipusId ");
		}
		if (dataIniciInici != null) {
			expedientQuerySb.append("and pie.dataInici >= :dataIniciInici ");
		}
		if (dataIniciFi != null) {
			expedientQuerySb.append("and pie.dataInici <= :dataIniciFi ");
		}
		if (estatId != null) {
			expedientQuerySb.append("and pie.estatId = :estatId and pie.dataFi is null ");
		}
		if (geoPosX != null) {
			expedientQuerySb.append("and pie.geoPosX = :geoPosX ");
		}
		if (geoPosY != null) {
			expedientQuerySb.append("and pie.geoPosY = :geoPosY ");
		}
		if (geoReferencia != null) {
			expedientQuerySb.append("and pie.geoReferencia = :geoReferencia ");
		}
		if (nomesIniciats) {
			expedientQuerySb.append("and pie.dataFi is null and pie.estatId is null ");
		}
		if (nomesFinalitzats) {
			expedientQuerySb.append("and pie.dataFi is not null ");
		}
		boolean filtrarPerActorId = false;
		if (nomesTasquesPersonals || nomesTasquesGrup) {
			if (nomesTasquesMeves) {
				expedientQuerySb.append(
						"and exists (" + 
						"    from " +
						"        org.jbpm.taskmgmt.exe.TaskInstance ti left join ti.pooledActors pa " +
						"    where " +
						"        ti.processInstance.id = pie.processInstanceId " +
						"    and ti.isSuspended = false " +
						"    and ti.isOpen = true ");
				if (nomesTasquesPersonals) {
					expedientQuerySb.append("    and (ti.actorId is not null and ti.actorId = :actorId) ");
				} else if (nomesTasquesGrup) {
					expedientQuerySb.append("    and (ti.actorId is null and pa.actorId = :actorId) ");
				} else {
					expedientQuerySb.append("    and ((ti.actorId is not null and ti.actorId = :actorId) or (ti.actorId is null and pa.actorId = :actorId))) ");
				}
				expedientQuerySb.append(") ");
				filtrarPerActorId = true;
			} else {
				expedientQuerySb.append(
						"and exists (" + 
						"    from " +
						"        org.jbpm.taskmgmt.exe.TaskInstance as ti " +
						"    where " +
						"        ti.processInstance.id = pie.processInstanceId " +
						"    and ti.isSuspended = false " +
						"    and ti.isOpen = true ");
				if (nomesTasquesPersonals) {
					expedientQuerySb.append("    and ti.actorId is not null ");
				} else if (nomesTasquesGrup) {
					expedientQuerySb.append("    and (ti.actorId is null and exists elements(ti.pooledActors)) ");
				}
				expedientQuerySb.append(") ");
			}
		}
		expedientQuerySb.append("and (((:mostrarAnulats = true or pie.anulat = false) and :mostrarNomesAnulats = false) or (:mostrarNomesAnulats = true and pie.anulat = true)) ");
		if (nomesErrors) {
			expedientQuerySb.append("and (pie.errorDesc is not null or (pie.errorsIntegs is not null and pie.errorsIntegs > 0)) ");
		}
		if (nomesAlertes) {
			expedientQuerySb.append(
					"and exists (" + 
					"    from " +
					"        Alerta as al " +
					"    where " +
					"        al.entorn.id = pie.entorn.id " +
					"    and al.dataEliminacio is null " +
					"    and al.expedient.id = pie.id ) ");
		}
		Query queryCount = jbpmContext.getSession().createQuery(
				"select count(distinct pie.id) " + expedientQuerySb.toString());
		setQueryParams(
				queryCount,
				entornId,
				actorId,
				tipusIdPermesos,
				titol,
				numero,
				tipusId,
				dataIniciInici,
				dataIniciFi,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				mostrarAnulats,
				mostrarNomesAnulats,
				nomesAlertes,
				nomesErrors,
				filtrarPerActorId,
				0,
				-1);
		int count = ((Long)queryCount.uniqueResult()).intValue();
		if (!nomesCount) {
			List<String> sortColumns = new ArrayList<String>();
			if (sort != null) {
				/*sorts:
				titol
				numero
				identificador
				tipus
				dataInici
				dataFi,
				estat*/
				// Per defecte: dataCreacio desc
				expedientQuerySb.append("order by ");
				if ("titol".equals(sort)) {
					sortColumns.add("pie.titol");
				} else if ("numero".equals(sort)) {
					sortColumns.add("pie.numero");
				} else if ("identificador".equals(sort)) {
					sortColumns.add("pie.numero");
					sortColumns.add("pie.titol");
				} else if ("tipus".equals(sort)) {
					sortColumns.add("pie.tipus");
				} else if ("dataInici".equals(sort)) {
					sortColumns.add("pie.dataInici");
				} else if ("dataFi".equals(sort)) {
					sortColumns.add("pie.dataFi");
				} else if ("estat".equals(sort)) {
					sortColumns.add("case when pie.dataFi is null then 0 else 1 end");
					sortColumns.add("pie.estatId nulls first");
				}
				boolean sortFirst = true;
				for (String sortColumn: sortColumns) {
					if (!sortFirst)
						expedientQuerySb.append(", ");
					expedientQuerySb.append(sortColumn);
					expedientQuerySb.append((asc) ? " asc" : " desc");
					sortFirst = false;
				}
			}
			StringBuilder selectSb = new StringBuilder("select distinct pie.id");
			if (!sortColumns.isEmpty()) {
				for (String sortColumn: sortColumns) {
					selectSb.append(", ");
					selectSb.append(sortColumn);
				}
			}
			selectSb.append(" ");
			expedientQuerySb.insert(0, selectSb);
			Query queryIds = jbpmContext.getSession().createQuery(expedientQuerySb.toString());
			setQueryParams(
					queryIds,
					entornId,
					actorId,
					tipusIdPermesos,
					titol,
					numero,
					tipusId,
					dataIniciInici,
					dataIniciFi,
					estatId,
					geoPosX,
					geoPosY,
					geoReferencia,
					mostrarAnulats,
					mostrarNomesAnulats,
					nomesAlertes,
					nomesErrors,
					filtrarPerActorId,
					firstResult,
					maxResults);
			List<Object[]> resultat = (List<Object[]>)queryIds.list();
			if (!sortColumns.isEmpty()) {
				List<Long> ids = new ArrayList<Long>();
				for (Object[] fila: resultat) {
					ids.add((Long)fila[0]);
				}
				return new ResultatConsultaPaginadaJbpm<Long>(
						count,
						ids);
			} else {
				return new ResultatConsultaPaginadaJbpm<Long>(
						count,
						(List<Long>)queryIds.list());
			}
			
		} else {
			return new ResultatConsultaPaginadaJbpm<Long>(count);
		}
	}

	private void setQueryParams(
			Query query,
			Long entornId,
			String actorId,
			Collection<Long> tipusIdPermesos,
			String titol,
			String numero,
			Long tipusId,
			Date dataIniciInici,
			Date dataIniciFi,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean mostrarAnulats,
			boolean mostrarNomesAnulats,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean filtrarPerActorId,
			int firstResult,
			int maxResults) {
		query.setParameter("entornId", entornId);
		query.setParameterList("tipusIdPermesos", tipusIdPermesos);
		if (filtrarPerActorId) {
			query.setParameter("actorId", actorId);
		}
		if (titol != null && !titol.isEmpty()) {
			query.setParameter("titol", titol);
		}
		if (numero != null && !numero.isEmpty()) {
			query.setParameter("numero", numero);
		}
		if (tipusId != null) {
			query.setParameter("tipusId", tipusId);
		}
		if (dataIniciInici != null) {
			query.setParameter("dataIniciInici", dataIniciInici);
		}
		if (dataIniciFi != null) {
			query.setParameter("dataIniciFi", dataIniciFi);
		}
		if (estatId != null) {
			query.setParameter("estatId", estatId);
		}
		if (geoPosX != null) {
			query.setParameter("geoPosX", geoPosX);
		}
		if (geoPosY != null) {
			query.setParameter("geoPosY", geoPosY);
		}
		if (geoReferencia != null) {
			query.setParameter("geoReferencia", geoReferencia);
		}
		query.setParameter("mostrarAnulats", mostrarAnulats);
		query.setParameter("mostrarNomesAnulats", mostrarNomesAnulats);
//		if (nomesAlertes) {
//			query.setParameter("nomesAlertes", nomesAlertes);
//		}
		if (maxResults != -1) {
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
		}
	}

}
