package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.ReproDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;

public interface ReproService {
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId);
	public ReproDto findById(Long id) throws NoTrobatException, ValidacioException;
	public Map<String,Object> findValorsById(Long id) throws NoTrobatException;
	public ReproDto create(Long expedientTipusId, String nom, Map<String, Object> valors) throws NoTrobatException, ValidacioException;
	public String deleteById(Long id) throws NoTrobatException;
}
