/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;

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
		//System.out.println(">>> Consulta expedient: " + entornId + ", " + expedientTipusId + ", " + numero);
		if (expedients.size() > 0) {
			//System.out.println(">>> Resultats trobats: " + expedients.size());
			return expedients.get(0);
		}
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
			boolean mostrarAnulats) {
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
				mostrarAnulats));
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
			boolean mostrarAnulats) {
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
				mostrarAnulats));
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
			boolean mostrarAnulats,
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
						mostrarAnulats));
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



	private Criterion[] getCriteriaForConsultaGeneral(
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
			boolean mostrarAnulats) {
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("entorn.id", entornId));
		if (titol != null && titol.length() > 0)
			crit.add(Restrictions.ilike("titol", "%" + titol + "%"));
		if (numero != null && numero.length() > 0)
			crit.add(Restrictions.eq("numero", numero));
		if (dataInici1 != null && dataInici2 != null)
			crit.add(Restrictions.between("dataInici", dataInici1, dataInici2));
		if (expedientTipusId != null)
			crit.add(Restrictions.eq("tipus.id", expedientTipusId));
		if (expedientTipusIdPermesos != null && expedientTipusIdPermesos.length > 0)
			crit.add(Restrictions.in("tipus.id", expedientTipusIdPermesos));
		if (estatId != null && !finalitzat)
			crit.add(Restrictions.eq("estat.id", estatId));
		if (iniciat)
			crit.add(Restrictions.isNull("estat.id"));
		if (finalitzat)
			crit.add(Restrictions.isNotNull("dataFi"));
		if (geoPosX != null)
			crit.add(Restrictions.eq("geoPosX", geoPosX));
		if (geoPosY != null)
			crit.add(Restrictions.eq("geoPosY", geoPosY));
		if (geoReferencia != null && geoReferencia.length() > 0)
			crit.add(Restrictions.ilike("geoReferencia", "%" + geoReferencia + "%"));
		if (!mostrarAnulats) {
			crit.add(Restrictions.eq("anulat", false));
		}
		return crit.toArray(new Criterion[crit.size()]);
	}

}
