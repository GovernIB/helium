/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un document que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

	List<Document> findByDefinicioProces(DefinicioProces definicioProces);

	Document findByDefinicioProcesAndCodi(
			DefinicioProces definicioProces,
			String codi);

	@Query("select d from " +
			"    Document d " +
			"where " +
			"    d.definicioProces.id=:id")
	List<Document> findAmbDefinicioProces(@Param("id") Long id);

	@Query(" select d from " +
			"    Document d " +
			"where " +
			"    d.definicioProces.id=:definicioProcesId " +
			"and d.codi=:codi")
	List<Document> findAmbDefinicioProcesICodi(@Param("definicioProcesId") Long definicioProcesId, @Param("codi") String codi);
}
