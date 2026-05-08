package es.caib.helium.persistence.repository;

import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.Recurs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Mètodes per a obtenir i modificar la informació de base de dades relativa a un recurs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RecursRepository extends JpaRepository<Recurs, Long> {

	Optional<Recurs> findByExpedientTipusAndDefinicioProcesAndNom(
		ExpedientTipus expedientTipus,
		DefinicioProces definicioProces,
		String nom);

	@Query(
		"SELECT r.contingut " +
		"FROM Recurs r " +
		"WHERE " +
		"    r.expedientTipus.id = :expedientTipusId " +
		"AND ((:definicioProcesId IS NULL AND r.definicioProces IS NULL) OR r.definicioProces.id = :definicioProcesId) " +
		"AND r.nom = :nom " +
		"AND (:isClass IS NULL OR r.classe = :isClass)")
	Optional<byte[]> findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
		Long expedientTipusId,
		Long definicioProcesId,
		String nom,
		Boolean isClass);

	List<Recurs> findByExpedientTipusIdAndHandler(Long expedientTipusId, boolean handler);

	@Query(
		"FROM Recurs r " +
		"WHERE " +
		"    r.expedientTipus.id = :expedientTipusId " +
		"AND r.definicioProces IS NULL " +
		"AND (:esNullFiltre = true OR LOWER(r.nom) LIKE LOWER('%'||:filtre||'%')) ")
	Page<Recurs> findByFiltrePaginat(
		@Param("expedientTipusId") Long expedientTipusId,
		@Param("esNullFiltre") boolean esNullFiltre,
		@Param("filtre") String filtre,
		Pageable pageable);

}
