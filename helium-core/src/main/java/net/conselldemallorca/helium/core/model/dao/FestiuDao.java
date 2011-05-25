/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Festiu;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Festiu
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class FestiuDao extends HibernateGenericDao<Festiu, Long> {

	private Date dataModificacioFestius;

	public FestiuDao() {
		super(Festiu.class);
	}

	public List<Festiu> findAmbAny(int any) {
		List<Festiu> resposta = new ArrayList<Festiu>();
		List<Festiu> festius = findAll();
		Calendar cal = Calendar.getInstance();
		for (Festiu festiu: festius) {
			cal.setTime(festiu.getData());
			if (cal.get(Calendar.YEAR) == any)
				resposta.add(festiu);
		}
		return resposta;
	}

	public Festiu findAmbData(Date data) {
		List<Festiu> festius = findByCriteria(Restrictions.eq("data", data));
		if (festius.size() > 0)
			return festius.get(0);
		return null;
	}

	public void modificacioFestius() {
		dataModificacioFestius = new Date();
	}
	public boolean isModificatFestius(Date date) {
		if (dataModificacioFestius == null) {
			dataModificacioFestius = new Date(0);
			return true;
		}
		return date.before(dataModificacioFestius);
	}

}
