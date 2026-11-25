package es.caib.helium.persistence.repository;

import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.persistence.entity.ExpedientTasca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
			"	AND (et.completed IS NULL OR et.completed = false) " +
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

	/**
	 * Retorna, d'entre els expedients indicats, els que tenen alguna tasca activa (no completada, cancel·lada
	 * ni suspesa) del tipus demanat:
	 * <ul>
	 *   <li>personals: tasques assignades (a l'usuari, si {@code nomesMeves});</li>
	 *   <li>de grup: tasques sense assignar amb candidats o grup (amb l'usuari com a candidat, si
	 *       {@code nomesMeves}).</li>
	 * </ul>
	 * La llista d'expedients no pot tenir més de 1000 elements (límit de l'IN d'Oracle).
	 */
	@Query(	"SELECT DISTINCT et.expedient.id FROM ExpedientTasca et " +
			"	WHERE et.expedient.id IN (:expedientIds) " +
			"	AND (:mostrarAnulats = true OR et.expedient.anulat = false) " +
			"	AND (:nomesAlertes = false OR et.expedient.errorDesc IS NOT NULL) " +
			"	AND (et.completed IS NULL OR et.completed = false) " +
			"	AND (et.cancelled IS NULL OR et.cancelled = false) " +
			"	AND (et.suspended IS NULL OR et.suspended = false) " +
			"	AND ( " +
			"		(:personals = true AND et.assignee IS NOT NULL " +
			"			AND (:nomesMeves = false OR et.assignee = :usuari)) " +
			"		OR (:grup = true AND et.assignee IS NULL AND ( " +
			"			(:nomesMeves = false AND (et.groupId IS NOT NULL " +
			"				OR EXISTS (SELECT c.id FROM TascaCandidate c WHERE c.tasca = et))) " +
			"			OR EXISTS (SELECT c.id FROM TascaCandidate c WHERE c.tasca = et AND c.userId = :usuari))) " +
			"	)")
	List<Long> findExpedientIdsAmbTasquesActives(
		@Param("expedientIds") Collection<Long> expedientIds,
		@Param("usuari") String usuari,
		@Param("nomesMeves") boolean nomesMeves,
		@Param("personals") boolean personals,
		@Param("grup") boolean grup,
		@Param("mostrarAnulats") boolean mostrarAnulats,
		@Param("nomesAlertes") boolean nomesAlertes);

    void deleteByExpedientId(Long id);
}
