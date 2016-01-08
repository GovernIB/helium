/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Dao pels objectes de tipus firma de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaTascaRepository extends JpaRepository<FirmaTasca, Long> {
	@Query("select " +
			"	 max(ft.order) " +
			"from " +
			"    FirmaTasca ft " +
			"where " +
			"    ft.tasca.id=:tascaId")
	int getNextOrder(@Param("tascaId") Long tascaId);

	@Query("select ft from " +
			"    FirmaTasca ft " +
			"where " +
			"    ft.tasca.id=:tascaId " +
			"and ft.order=:order")
	FirmaTasca getAmbOrdre(@Param("tascaId") Long tascaId, @Param("order") int order);

	@Query("select ft from " +
			"    FirmaTasca ft " +
			"where " +
			"   ft.tasca.jbpmName=:jbpmName " +
			"	and ft.tasca.definicioProces.jbpmId=:jbpmId " +
			"order by " +
			"    ft.order")
	List<FirmaTasca> findAmbTascaOrdenats(
			@Param("jbpmName") String name,
			@Param("jbpmId") String jbpmId);

	@Query("select count(ft) from " +
			"    FirmaTasca ft " +
			"where " +
			"   ft.tasca.jbpmName=:jbpmName " +
			"	and ft.tasca.definicioProces.jbpmId=:jbpmId")
	Long countAmbTasca(
			@Param("jbpmName") String name,
			@Param("jbpmId") String jbpmId);

	@Query("select ft from " +
			"    FirmaTasca ft " +
			"where " +
			"    ft.document.id=:documentId " +
			"and ft.tasca.id=:tascaId")
	FirmaTasca findAmbDocumentTasca(@Param("documentId") Long documentId, @Param("tascaId") Long tascaId);
}
