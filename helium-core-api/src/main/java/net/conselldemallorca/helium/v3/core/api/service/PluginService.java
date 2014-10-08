package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;

public interface PluginService {
	PersonaDto findPersonaAmbCodi(String responsableDefecteCodi);

	List<PersonaDto> findPersonaLikeNomSencer(String text);

	boolean isPersonesPluginJdbc();
}
