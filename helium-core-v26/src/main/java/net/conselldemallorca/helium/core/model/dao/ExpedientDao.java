/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService.FiltreAnulat;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientDao extends HibernateGenericDao<Expedient, Long> {

	public ExpedientDao() {
		super(Expedient.class);
	}

	public List<Expedient> findAmbEntorn(Long entornId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId));
	}
	public Expedient findAmbEntornIId(Long entornId, Long id) {
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("id", id),
				Restrictions.eq("entorn.id", entornId));
		if (expedients.size() > 0)
			return expedients.get(0);
		return null;
	}
	
	public List<Long> findListExpedients(Long entornId, String actorId) {
		return findListExpedients(entornId, actorId, null, null, null, null, false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> findListExpedients(Long entornId, String actorId, String expedient, String numeroExpedient, Long tipusExpedient, String sort, boolean asc) {		
		List<Long> resultat = new ArrayList<Long>();
			
		String hql = "select cast(ex.processInstanceId as long) "
				+ " from Expedient as ex "
				+ " where "
				+ " ex.entorn.id = :entornId ";
		
		if (tipusExpedient != null) {
			hql += "	and ex.tipus.id = :tipusExpedient ";
		}
		
		if (numeroExpedient != null && !"".equals(numeroExpedient)) {
			hql += "	and UPPER(case"
						+ " when (ex.numero is not null AND ex.titol is not null) then ('['||ex.numero||']') "
						+ " when (ex.numero is not null AND ex.titol is null) then ex.numero "
						+ " ELSE ex.numeroDefault END) like UPPER(:numeroExpedient) ";			
		}
		
		if (expedient != null && !"".equals(expedient)) {
			hql += "	and UPPER(case"
						+ " when (ex.numero is not null AND ex.titol is not null) then ('['||ex.numero||'] ' || ex.titol) "
						+ " when (ex.numero is not null AND ex.titol is null) then ex.numero "
						+ " when (ex.numero is null AND ex.titol is not null) then ex.titol "
						+ " ELSE ex.numeroDefault END) like UPPER(:expedient) ";			
		}
		
		if ("expedientTitol".equals(sort)) {
			hql += " order by (case"
						+ " when (ex.numero is not null AND ex.titol is not null) then ('['||ex.numero||'] ' || ex.titol) "
						+ " when (ex.numero is not null AND ex.titol is null) then ex.numero "
						+ " when (ex.numero is null AND ex.titol is not null) then ex.titol "
						+ " ELSE ex.numeroDefault END) " + (asc ? "asc" : "desc");
		} else if ("expedientTipusNom".equals(sort)) {
			hql += " order by ex.tipus.nom " + (asc ? "asc" : "desc");
		} 
		
		Query query = getSession().createQuery(hql);
		query.setLong("entornId", entornId);
		
		if (tipusExpedient != null) {
			query.setLong("tipusExpedient", tipusExpedient);
		}
		
		if (expedient != null && !"".equals(expedient)) {
			query.setString("expedient", "%"+expedient+"%");
		}
		
		if (numeroExpedient != null && !"".equals(numeroExpedient)) {
			query.setString("numeroExpedient", "%"+numeroExpedient+"%");
		}
		
		resultat = (List<Long>) query.list();
		
		return resultat;
	}
	
	public Expedient findAmbProcessInstanceId(String processInstanceId) {
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("processInstanceId", processInstanceId));
		if (expedients.size() > 0) {
			return expedients.get(0);
		} else {
			Expedient expedientIniciant = ExpedientIniciantDto.getExpedient();
			if (expedientIniciant != null && expedientIniciant.getProcessInstanceId().equals(processInstanceId))
				return expedientIniciant;
		}
		return null;
	}
	public int countAmbExpedientTipusId(Long expedientTipusId) {
		return getCountByCriteria(
				Restrictions.eq("tipus.id", expedientTipusId));
	}
	public Expedient findAmbEntornTipusITitol(
			Long entornId,
			Long expedientTipusId,
			String titol) {
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("tipus.id", expedientTipusId),
				Restrictions.eq("titol", titol));
		if (expedients.size() > 0)
			return expedients.get(0);
		return null;
	}
	public Expedient findAmbEntornTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero) {
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("tipus.id", expedientTipusId),
				Restrictions.eq("numero", numero));
		if (expedients.size() > 0)
			return expedients.get(0);
		return null;
	}
	
	public Expedient findAmbEntornTipusINumeroDefault(
			Long entornId,
			Long expedientTipusId,
			String numeroDefault) {
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("tipus.id", expedientTipusId),
				Restrictions.eq("numeroDefault", numeroDefault));
		if (expedients.size() > 0)
			return expedients.get(0);
		return null;
	}
	
	public List<Long> findPIExpedientsAmbEntornTipusINum(
			Long entornId,
			String numero) {
		List<Long> res = new ArrayList<Long>();
		
		List<Expedient> expedients = findByCriteria(
			Restrictions.eq("entorn.id", entornId),
			Restrictions.or(
					Restrictions.eq("numero", numero),
					Restrictions.eq("numeroDefault", numero)
			)
		);		
		
		for (Expedient expedient : expedients) {
			if ((!expedient.getTipus().getTeNumero() && numero.equals(expedient.getNumeroDefault())) || (expedient.getTipus().getTeNumero() && numero.equals(expedient.getNumero()))) {
				res.add(Long.valueOf(expedient.getProcessInstanceId()));
			}
		}
		
		return res;
	}

	public int countAmbEntornConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long[] expedientTipusIdPermesos,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			FiltreAnulat mostrarAnulats,
			String[] grupsUsuari) {
		return getCountByCriteria(getCriteriaForConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				expedientTipusIdPermesos,
				estatId,
				iniciat,
				finalitzat,
				geoPosX,
				geoPosY,
				geoReferencia,
				mostrarAnulats,
				grupsUsuari));
	}
	public List<Expedient> findAmbEntornConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long[] expedientTipusIdPermesos,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			FiltreAnulat mostrarAnulats,
			String[] grupsUsuari) {
		return findByCriteria(getCriteriaForConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				expedientTipusIdPermesos,
				estatId,
				iniciat,
				finalitzat,
				geoPosX,
				geoPosY,
				geoReferencia,
				mostrarAnulats,
				grupsUsuari));
	}
	public List<Expedient> findAmbEntornConsultaGeneralPagedAndOrdered(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long[] expedientTipusIdPermesos,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			FiltreAnulat mostrarAnulats,
			String[] grupsUsuari,
			int firstRow,
			int maxResults,
			String sort,
			boolean asc) {
		
		String hql = "select " 
		+ "case " 
				+ "	WHEN (ex.dataFi is null and es.id is null) then 'iniciat'" 
				+ " WHEN (ex.dataFi is null and es.id is not null) then es.nom "
				+ " ELSE 'finalizat' "
		+ "END as estatnom, ex "
		+ "from Expedient ex left join ex.estat as es "
		+ "where ex.entorn.id = :entornId";
		if (titol != null && titol.length() > 0)
			hql += " and lower(ex.titol) like lower(:titol)";
		if (numero != null && numero.length() > 0)
//			hql += " and (lower(ex.numero) like lower(:numero) OR lower(ex.numeroDefault) like lower(:numero))";
			hql += " and ((ex.tipus.teNumero = true and lower(ex.numero) like lower(:numero)) "
					+ "or (ex.tipus.teNumero = false and lower(ex.numeroDefault) like lower(:numero)))";
		if (dataInici1 != null && dataInici2 != null) {
			hql += " and ex.dataInici >= :dataInici1 and ex.dataInici <= :dataInici2";
		} else {
			if (dataInici1 != null) {
				hql += " and ex.dataInici >= :dataInici1";
			} else if (dataInici2 != null) {
				hql += " and ex.dataInici <= :dataInici2";
			}
		}
		if (expedientTipusId != null) {
			hql += " and ex.tipus.id = :expedientTipusId";
		}
		if (expedientTipusIdPermesos != null && expedientTipusIdPermesos.length > 0) {
			hql += " and ex.tipus.id in ( :expedientTipusIdPermesos )";
		}
		if (geoPosX != null) {
			hql += " and ex.geoPosX = :geoPosX";
		}
		if (geoPosY != null) {
			hql += " and ex.geoPosY = :geoPosY";
		}
		if (geoReferencia != null && geoReferencia.length() > 0) {
			hql += " and lower(ex.geoReferencia) like lower(:geoReferencia)";
		}
		if (mostrarAnulats == FiltreAnulat.ACTIUS) {
			hql += " and ex.anulat = false ";
		} else if (mostrarAnulats == FiltreAnulat.ANUL_LATS) {
			hql += " and ex.anulat = true ";
		}
		if (grupsUsuari != null && grupsUsuari.length > 0) {
			hql += " and (ex.tipus.restringirPerGrup = false or ex.grupCodi in (:grupsUsuari))";
		} else {
			hql += " and ex.tipus.restringirPerGrup = false ";
		}
		if (estatId != null && !finalitzat) {
			hql += " and ex.estat.id = :estatId";
		}
		if (iniciat && !finalitzat) {
			hql += " and ex.estat.id is null ";
			hql += " and ex.dataFi is null ";
		} else if (finalitzat && !iniciat) {
			hql += " and ex.dataFi is not null ";
		} else if (iniciat && finalitzat) {
			hql += " and ex.dataInici is null ";
		}
		
		hql += " order by ";
		String sorts[] = null;
		if ("identificador".equals(sort)) {
			sorts = new String[] {"ex.numero", "ex.numeroDefault", "ex.titol"};
		} else if ("estat.nom".equals(sort)) {
			sorts = new String[] {"1"};
		}  else if (sort == null) {
			sorts = new String[] {"ex.id"};
		} else {
			sorts = new String[] {"ex."+sort};
		}
		
		String orden = "";
		for (String st : sorts) {
			orden += "," + st + (asc ? " asc " : " desc ");
		}
		
		hql += orden.substring(1);

		Query query = getSession().createQuery(hql);
		query.setParameter("entornId", entornId);
		if (geoPosX != null) {
			query.setParameter("geoPosX", geoPosX);
		}
		if (geoPosY != null) {
			query.setParameter("geoPosY", geoPosY);
		}
		if (geoReferencia != null && geoReferencia.length() > 0) {
			query.setParameter("geoReferencia", "%"+geoReferencia+"%");
		}
		if (titol != null && titol.length() > 0) {
			query.setParameter("titol", "%"+titol+"%");
		}
		if (numero != null && numero.length() > 0) {
			query.setParameter("numero", "%"+numero+"%");
		}
		if (estatId != null && !finalitzat) {
			query.setParameter("estatId", estatId);
		}
		if (expedientTipusId != null) {
			query.setParameter("expedientTipusId", expedientTipusId);
		}
		if (expedientTipusIdPermesos != null && expedientTipusIdPermesos.length > 0) {
			query.setParameterList("expedientTipusIdPermesos", expedientTipusIdPermesos);
		}
		if (grupsUsuari != null && grupsUsuari.length > 0) {
			query.setParameterList("grupsUsuari", grupsUsuari);
		}
		if (dataInici1 != null) {
			query.setParameter("dataInici1", dataInici1);
		}
		if (dataInici2 != null) {
			query.setParameter("dataInici2", dataInici2);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.setMaxResults(maxResults).setFirstResult(firstRow).list();
		
		List<Expedient> listaExpedients = new ArrayList<Expedient>();
		for (Object[] obj : lista) {
			listaExpedients.add((Expedient) obj[1]);
		}
		
		return listaExpedients;
	}
	
	public List<Expedient> findAmbEntornLikeIdentificador(
			Long entornId,
			String text) {
		Criterion[] criteris = new Criterion[2];
		criteris[0] = Restrictions.ilike("titol", "%" + text + "%");
		criteris[1] = Restrictions.ilike("numero", "%" + text + "%");
		return findOrderedByCriteria(
				new String[] {"numero", "titol"},
				true,
				Restrictions.eq("entorn.id", entornId),
				Restrictions.or(
						Restrictions.ilike("titol", "%" + text + "%"),
						Restrictions.ilike("numero", "%" + text + "%")));
	}



	private Criteria getCriteriaForConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long[] expedientTipusIdPermesos,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			FiltreAnulat mostrarAnulats,
			String[] grupsUsuari) {
		Criteria crit = getSession().createCriteria(
				getPersistentClass());
		crit.createAlias("tipus", "tip");
		crit.add(Restrictions.eq("entorn.id", entornId));
		if (titol != null && titol.length() > 0)
			crit.add(Restrictions.ilike("titol", "%" + titol + "%"));
		if (numero != null && numero.length() > 0)
			crit.add(Restrictions.or(
					Restrictions.and(Restrictions.eq("tip.teNumero", true), Restrictions.ilike("numero", "%" + numero + "%")),
					Restrictions.and(Restrictions.eq("tip.teNumero", false), Restrictions.ilike("numeroDefault", "%" + numero + "%"))));
//			crit.add(Restrictions.ilike("numero", "%" + numero + "%"));
		if (dataInici1 != null && dataInici2 != null) {
			crit.add(Restrictions.between("dataInici", dataInici1, dataInici2));
		} else {
			if (dataInici1 != null)
				crit.add(Restrictions.ge("dataInici", dataInici1));
			else if (dataInici2 != null)
				crit.add(Restrictions.le("dataInici", dataInici2));
		}
		if (expedientTipusId != null)
			crit.add(Restrictions.eq("tipus.id", expedientTipusId));
		if (expedientTipusIdPermesos != null && expedientTipusIdPermesos.length > 0)
			crit.add(Restrictions.in("tipus.id", expedientTipusIdPermesos));
		if (estatId != null && !finalitzat)
			crit.add(Restrictions.eq("estat.id", estatId));
		if (iniciat && !finalitzat) {
			crit.add(Restrictions.isNull("estat.id"));
			crit.add(Restrictions.isNull("dataFi"));
		} else if (finalitzat && !iniciat) {
			crit.add(Restrictions.isNotNull("dataFi"));
		} else if (iniciat && finalitzat) {
			crit.add(Restrictions.isNull("dataInici"));
		}
		if (geoPosX != null)
			crit.add(Restrictions.eq("geoPosX", geoPosX));
		if (geoPosY != null)
			crit.add(Restrictions.eq("geoPosY", geoPosY));
		if (geoReferencia != null && geoReferencia.length() > 0)
			crit.add(Restrictions.ilike("geoReferencia", "%" + geoReferencia + "%"));
		if (mostrarAnulats == FiltreAnulat.ACTIUS) {
			crit.add(Restrictions.eq("anulat", false));
		} else if (mostrarAnulats == FiltreAnulat.ANUL_LATS) {
			crit.add(Restrictions.eq("anulat", true));
		}
		if (grupsUsuari != null && grupsUsuari.length > 0) {
			crit.add(Restrictions.or(
					Restrictions.eq("tip.restringirPerGrup", false),
					Restrictions.in("grupCodi", grupsUsuari)));
		} else {
			crit.add(Restrictions.eq("tip.restringirPerGrup", false));
		}
		return crit;
	}
}
