package es.caib.helium.persistence.repository;

import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.persistence.entity.ExpedientTasca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una tasca d'expedient que està emmagatzemat a dins la base
 * de dades.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTascaRepository extends JpaRepository<ExpedientTasca, Long> {
	public ExpedientTasca findByTaskId(String taskId);


	@Query(	" FROM ExpedientTasca et " +
			"	WHERE et.expedient.entorn.id = :entornId " +
			"	AND (:isResponsableNull = true OR ( " +
			"		et.assignee = :responsable OR " +
			"		(et.assignee is NULL AND :responsable in (SELECT c.userId FROM et.candidates c)) " +
			"	)) " +
			"	AND (:isTascaNull = true OR et.taskCode = :tasca) " +
			"	AND (:isTitolNull = true OR lower(et.name) LIKE lower('%' || :titol || '%')) " +
			"	AND (:isExpedientNull = true OR et.expedient.numeroDefault = :expedient) " +
			"	AND (:isExpedientTipusIdNull = true OR et.expedient.tipus.id = :expedientTipusId) " +
			"	AND (:isDataCreacioIniciNull = true OR et.startTime >= :dataCreacioInici) " +
			"	AND (:isDataCreacioFiNull = true OR et.startTime <= :dataCreacioFi) " +
			"	AND (:isDataLimitIniciNull = true OR et.dueDate >= :dataLimitInici) " +
			"	AND (:isDataLimitFiNull = true OR et.dueDate <= :dataLimitFi) " +
			"	AND ((:mostrarAssignadesUsuari = true AND :mostrarAssignadesGrup = true) " +
			"		OR (" +
			"		(:mostrarAssignadesUsuari = false OR (et.assignee IS NOT NULL)) " +
			"		AND (:mostrarAssignadesGrup = false OR (et.groupId IS NOT NULL AND et.assignee IS NULL)) " +
			"	)) " +
			"	AND (:administrador = true OR ( " +
			"		et.assignee IS NOT NULL " +
			"		OR et.groupId IS NOT NULL " +
			" ))")
	Page<ExpedientTasca> tascaFindByFiltrePaginat(
		@Param("entornId") Long entornId,
		@Param("responsable") String responsable,
		@Param("isResponsableNull") boolean isResponsableNull,
		@Param("tasca") String tasca,
		@Param("isTascaNull") boolean isTascaNull,
		@Param("titol") String titol,
		@Param("isTitolNull") boolean isTitolNull,
		@Param("expedient") String expedient,
		@Param("isExpedientNull") boolean isExpedientNull,
		@Param("expedientTipusId") Long expedientTipusId,
		@Param("isExpedientTipusIdNull") boolean isExpedientTipusIdNull,
		@Param("dataCreacioInici") Date dataCreacioInici,
		@Param("isDataCreacioIniciNull") Boolean isDataCreacioIniciNull,
		@Param("dataCreacioFi") Date dataCreacioFi,
		@Param("isDataCreacioFiNull") Boolean isDataCreacioFiNull,
		@Param("dataLimitInici") Date dataLimitInici,
		@Param("isDataLimitIniciNull") Boolean isDataLimitIniciNull,
		@Param("dataLimitFi") Date dataLimitFi,
		@Param("isDataLimitFiNull") Boolean isDataLimitFiNull,
		@Param("mostrarAssignadesUsuari") boolean mostrarAssignadesUsuari,
		@Param("mostrarAssignadesGrup") boolean mostrarAssignadesGrup,
		@Param("administrador") boolean administrador,
		Pageable pageable);
}
