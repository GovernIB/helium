/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Document;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus document
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentDao extends HibernateGenericDao<Document, Long> {

	public DocumentDao() {
		super(Document.class);
	}

	@SuppressWarnings("unchecked")
	public List<Document> findAmbDefinicioProces(Long definicioProcesId) {
		return (List<Document>)getSession().
				createQuery(
						"from " +
						"    Document d " +
						"where " +
						"    d.definicioProces.id=?").
				setLong(0, definicioProcesId).
				list();
	}

	@SuppressWarnings("unchecked")
	public List<Document> findAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId) {
		return (List<Document>)getSession().
				createQuery(
						"from " +
						"    Document d " +
						"where " +
						"    d.definicioProces.id=? " +
						"order by " +
						"    d.codi asc").
				setLong(0, definicioProcesId).
				list();
	}

	@SuppressWarnings("unchecked")
	public Document findAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		List<Document> documents = (List<Document>)getSession().
				createQuery(
						"from " +
						"    Document d " +
						"where " +
						"    d.definicioProces.id=?" +
						"and d.codi=?").
				setLong(0, definicioProcesId).
				setString(1, codi).
				list();
		if (documents.size() > 0)
			return documents.get(0);
		return null;
	}

}
