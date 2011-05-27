/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.DocumentStore.DocumentFont;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus document
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class DocumentStoreDao extends HibernateGenericDao<DocumentStore, Long> {

	public DocumentStoreDao() {
		super(DocumentStore.class);
	}

	public Long create(
			String processInstanceId,
			String jbpmVariable,
			Date dataDocument,
			DocumentFont documentFont,
			String arxiuNom,
			byte[] arxiuContingut,
			boolean adjunt,
			String adjuntTitol) {
		DocumentStore ds = new DocumentStore(
				documentFont,
				processInstanceId,
				jbpmVariable,
				new Date(),
				dataDocument,
				arxiuNom);
		ds.setAdjunt(adjunt);
		if (adjunt)
			ds.setAdjuntTitol(adjuntTitol);
		if (arxiuContingut != null)
			ds.setArxiuContingut(arxiuContingut);
		getHibernateTemplate().saveOrUpdate(ds);
		return ds.getId();
	}

	public void update(
			Long id,
			Date dataDocument,
			String arxiuNom,
			byte[] arxiuContingut,
			String adjuntTitol) {
		DocumentStore ds = this.getById(id, false);
		ds.setDataDocument(dataDocument);
		ds.setDataModificacio(new Date());
		if (ds.isAdjunt())
			ds.setAdjuntTitol(adjuntTitol);
		ds.setArxiuNom(arxiuNom);
		if (arxiuContingut != null) {
			ds.setArxiuContingut(arxiuContingut);
		}
	}

	public void updateReferenciaFont(
			Long id,
			String referenciaFont) {
		DocumentStore ds = this.getById(id, false);
		ds.setReferenciaFont(referenciaFont);
	}

	public void updateRegistreEntrada(
			Long id,
			Date data,
			String numero,
			String organCodi,
			String oficinaCodi,
			String oficinaNom) {
		DocumentStore ds = this.getById(id, false);
		ds.setRegistreData(data);
		ds.setRegistreNumero(numero);
		ds.setRegistreOficinaCodi(oficinaCodi);
		ds.setRegistreOficinaNom(oficinaNom);
		ds.setRegistreEntrada(true);
	}
	public void updateRegistreSortida(
			Long id,
			Date data,
			String numero,
			String organCodi,
			String oficinaCodi,
			String oficinaNom) {
		DocumentStore ds = this.getById(id, false);
		ds.setRegistreData(data);
		ds.setRegistreNumero(numero);
		ds.setRegistreOficinaCodi(oficinaCodi);
		ds.setRegistreOficinaNom(oficinaNom);
		ds.setRegistreEntrada(false);
	}

	@SuppressWarnings("unchecked")
	public List<DocumentStore> findAmbProcessInstanceId(String processInstanceId) {
		return getHibernateTemplate().find(
				"from " +
				"    DocumentStore ds " +
				"where " +
				"    ds.processInstanceId=? " +
				"order by " +
				"    ds.dataDocument",
				processInstanceId);
	}

}
