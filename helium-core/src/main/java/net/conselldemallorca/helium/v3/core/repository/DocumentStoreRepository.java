/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un documentStore que està emmagatzemat a dins la 
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentStoreRepository extends JpaRepository<DocumentStore, Long> {

	public List<DocumentStore> findByProcessInstanceId(String processInstanceId);

	@Query("select d from " +
			"    Document d " +
			"where " +
			"    d.definicioProces.id=:definicioProcesId " +
			"and d.codi=:codi")
	public Document findAmbDefinicioProcesICodi(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("codi") String codi);

	public DocumentStore findById(Long documentStoreId);
	
	 List<DocumentStore> findByProcessInstanceId(Long processInstanceId);
}
