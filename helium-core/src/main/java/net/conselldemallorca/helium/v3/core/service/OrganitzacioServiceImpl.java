/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.v3.core.api.service.OrganitzacioService;
import net.conselldemallorca.helium.v3.core.repository.AreaJbpmIdRepository;

/**
 * Implementació del servei per a gestionar la informació de les taules JBPM_ID_*.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class OrganitzacioServiceImpl implements OrganitzacioService {

	@Autowired
	private AreaJbpmIdRepository areaJbpmIdRepository;

	@Override
	public List<String> findDistinctJbpmGroupNames() {
		logger.debug("Consulta de tots els grups de la taul JBPM_GROUP");
		return areaJbpmIdRepository.findDistinctJbpmGrupAll();
	}

	private static final Logger logger = LoggerFactory.getLogger(OrganitzacioServiceImpl.class);

}
