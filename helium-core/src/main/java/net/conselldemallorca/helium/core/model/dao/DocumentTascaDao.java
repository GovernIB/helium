/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus plantilla de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class DocumentTascaDao extends HibernateGenericDao<DocumentTasca, Long> {

	public DocumentTascaDao() {
		super(DocumentTasca.class);
	}

	public int getNextOrder(Long tascaId) {
		Object result = getSession().createQuery(
						"select " +
						"	 max(dt.order) " +
						"from " +
						"    DocumentTasca dt " +
						"where " +
						"    dt.tasca.id=?").
				setLong(0, tascaId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}

	public DocumentTasca getAmbOrdre(Long tascaId, int order) {
		return (DocumentTasca)getSession().createQuery(
				"from " +
				"    DocumentTasca dt " +
				"where " +
				"    dt.tasca.id=? " +
				"and dt.order=?").
		setLong(0, tascaId).
		setInteger(1, order).
		uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<DocumentTasca> findAmbTascaOrdenats(Long tascaId) {
		return getSession().createQuery(
				"from " +
				"    DocumentTasca dt " +
				"where " +
				"    dt.tasca.id=? " +
				"order by " +
				"    dt.order").
		setLong(0, tascaId).
		list();
	}

	public DocumentTasca findAmbDocumentTasca(Long documentId, Long tascaId) {
		return (DocumentTasca)getSession().createQuery(
				"from " +
				"    DocumentTasca dt " +
				"where " +
				"    dt.document.id=? " +
				"and dt.tasca.id=?").
		setLong(0, documentId).
		setLong(1, tascaId).
		uniqueResult();
	}

}
