/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.api.dto.PermisHeliumDto;
import net.conselldemallorca.helium.v3.core.api.service.PermisService;
import net.conselldemallorca.helium.v3.core.repository.PermisRepository;

/**
 * Implementació del servei per a gestionar permisos Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PermisServiceImpl implements PermisService {

	@Autowired
	private PermisRepository permisRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@Override
	public List<PermisHeliumDto> findAll() {
		logger.debug("Consulta de tots els permisos");
		return conversioTipusHelper.convertirList(
				permisRepository.findAll(), 
				PermisHeliumDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(PermisServiceImpl.class);

}
