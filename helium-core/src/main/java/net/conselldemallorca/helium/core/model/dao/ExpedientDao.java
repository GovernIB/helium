/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.service.ExpedientService.FiltreAnulat;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
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
	
	public List<Long> findListExpedients(Long entornId, String actorId) {
		return findListExpedients(entornId, actorId, null, null, null, false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> findListExpedients(Long entornId, String actorId, String expedient, Long tipusExpedient, String sort, boolean asc) {		
		List<Long> resultat = new ArrayList<Long>();
			
		String hql = "select cast(ex.processInstanceId as long) "
				+ " from Expedient as ex "
				+ " where "
				+ " ex.entorn.id = :entornId ";
		
		if (tipusExpedient != null) {
			hql += "	and ex.tipus.id = :tipusExpedient ";
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
		
		resultat = (List<Long>) query.list();
		
		return resultat;
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
	// TODO: revisar aquestes dues consultes, ja que haurien de tenir un resultat únic!!
	public Expedient findAmbEntornTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero) {
//		Query query = getSession().createQuery(
//				"from " +
//				"    Expedient e " +
//				"where " +
//				"    e.entorn.id=? " +
//				"and e.tipus.id=? " +
//				"and e.numero=? ").
//				setLong(0, entornId).
//				setLong(1, expedientTipusId).
//				setString(2, numero);
//		return (Expedient)query.uniqueResult();
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
//		Query query = getSession().createQuery(
//				"from " +
//				"    Expedient e " +
//				"where " +
//				"    e.entorn.id=? " +
//				"and e.tipus.id=? " +
//				"and e.numeroDefault=? ").
//				setLong(0, entornId).
//				setLong(1, expedientTipusId).
//				setString(2, numeroDefault);
//		return (Expedient)query.uniqueResult();
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("tipus.id", expedientTipusId),
				Restrictions.eq("numeroDefault", numeroDefault));
		if (expedients.size() > 0)
			return expedients.get(0);
		return null;
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
		String sorts[] = null;
		if ("identificador".equals(sort)) {
			sorts = new String[] {
					"numero",
					"titol"};
		} else if (sort == null) {
			sorts = new String[] {"id"};
		} else {
			sorts = new String[] {sort};
		}
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sorts,
				asc,
				getCriteriaForConsultaGeneral(
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
			crit.add(Restrictions.like("numero", "%" + numero + "%"));
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
