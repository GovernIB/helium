/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus firma de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class FirmaTascaDao extends HibernateGenericDao<FirmaTasca, Long> {

	public FirmaTascaDao() {
		super(FirmaTasca.class);
	}

	public int getNextOrder(Long tascaId) {
		Object result = getSession().createQuery(
						"select " +
						"	 max(ft.order) " +
						"from " +
						"    FirmaTasca ft " +
						"where " +
						"    ft.tasca.id=?").
				setLong(0, tascaId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}

	public FirmaTasca getAmbOrdre(Long tascaId, int order) {
		return (FirmaTasca)getSession().createQuery(
				"from " +
				"    FirmaTasca ft " +
				"where " +
				"    ft.tasca.id=? " +
				"and ft.order=?").
		setLong(0, tascaId).
		setInteger(1, order).
		uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<FirmaTasca> findAmbTascaOrdenats(Long tascaId) {
		return getSession().createQuery(
				"from " +
				"    FirmaTasca ft " +
				"where " +
				"    ft.tasca.id=? " +
				"order by " +
				"    ft.order").
		setLong(0, tascaId).
		list();
	}

	public FirmaTasca findAmbDocumentTasca(Long documentId, Long tascaId) {
		return (FirmaTasca)getSession().createQuery(
				"from " +
				"    FirmaTasca ft " +
				"where " +
				"    ft.document.id=? " +
				"and ft.tasca.id=?").
		setLong(0, documentId).
		setLong(1, tascaId).
		uniqueResult();
	}

}
