package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de ExpedientTipusUnitatOrganitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTipusUnitatOrganitzativaRepository extends JpaRepository<ExpedientTipusUnitatOrganitzativa, Long> {

	 ExpedientTipusUnitatOrganitzativa findByExpedientTipusIdAndUnitatOrganitzativaId(Long expedientTipusId, Long unitatOrganitzativaId);
	 
	 ExpedientTipusUnitatOrganitzativa findByExpedientTipusIdAndUnitatOrganitzativaCodi(Long expedientTipusId, String unitatOrganitzativaCodi);

	 List<ExpedientTipusUnitatOrganitzativa> findByExpedientTipusId(Long expedientTipusId);
	 
	 List<ExpedientTipusUnitatOrganitzativa> findByUnitatOrganitzativaId(Long unitatOrganitzativaId);
	 
	 List<ExpedientTipusUnitatOrganitzativa> findByExpedientTipusEntornId(Long entornId);
	 
	 /**
	  * Mètode per cercar els ExpedientTipusUnitatOrganitzativa que tinguin els tipus d'expedient indicats.
	  * @param expedientsTipusIds
	  * @return
	  */
    @Query(	 "from " 
			+"    ExpedientTipusUnitatOrganitzativa etuo " 
			+"where " 
			+	"(etuo.expedientTipus.id in (:expedientsTipusIds)) "
			)
    List<ExpedientTipusUnitatOrganitzativa> findByExpedientTipus(
			@Param("expedientsTipusIds") List<Long> expedientsTipusIds);
}
