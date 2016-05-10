package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.ReproDto;

public interface ReproService {
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId);
	public ReproDto findById(Long id);
	public ReproDto create(Long expedientTipusId, String nom, Map<String, Object> valors);
	public void deleteById(Long id);
}
