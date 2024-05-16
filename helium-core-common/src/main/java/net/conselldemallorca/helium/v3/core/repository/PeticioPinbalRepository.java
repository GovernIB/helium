package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;

public interface PeticioPinbalRepository extends JpaRepository<PeticioPinbal, Long> {
	public PeticioPinbal findByPinbalId(String pinbalId);
	public List<PeticioPinbal> findByEntornIdAndTipusIdAndExpedientId(Long entornId, Long tipusId, Long expedientId);
}