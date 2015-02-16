package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;

public interface PluginService {
	PersonaDto findPersonaAmbCodi(String responsableDefecteCodi);
}
