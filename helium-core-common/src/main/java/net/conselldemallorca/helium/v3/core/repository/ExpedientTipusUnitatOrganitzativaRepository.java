package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment òrgan.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTipusUnitatOrganitzativaRepository extends JpaRepository<ExpedientTipusUnitatOrganitzativa, Long> {

	 ExpedientTipusUnitatOrganitzativa findByExpedientTipusIdAndUnitatOrganitzativaId(Long expedientTipusId, Long unitatOrganitzativaId);
	 
	 ExpedientTipusUnitatOrganitzativa findByExpedientTipusIdAndUnitatOrganitzativaCodi(Long expedientTipusId, String unitatOrganitzativaCodi);

	 List<ExpedientTipusUnitatOrganitzativa> findByExpedientTipusId(Long expedientTipusId);
	 
	 List<ExpedientTipusUnitatOrganitzativa> findByUnitatOrganitzativaId(Long unitatOrganitzativaId);
	 
	 List<ExpedientTipusUnitatOrganitzativa> findByExpedientTipusEntornId(Long entornId);
}
