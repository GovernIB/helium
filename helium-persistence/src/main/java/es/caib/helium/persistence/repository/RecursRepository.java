package es.caib.helium.persistence.repository;

import es.caib.helium.persistence.entity.Recurs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Mètodes per a obtenir i modificar la informació de base de dades relativa a un recurs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RecursRepository extends JpaRepository<Recurs, Long> {

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

}
