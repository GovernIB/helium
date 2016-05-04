package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.ReproDto;

public interface ReproService {
	public List<ReproDto> findReprosByUsuariEntornTipusExpedient(String usuari, Long entornId, Long expedientTipusId);
	public ReproDto findById(Long id);
	public void create(Long expedientTipusId, String nom, Map<String, Object> valors);
}
