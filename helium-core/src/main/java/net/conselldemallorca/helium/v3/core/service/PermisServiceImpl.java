/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.logic.intf.dto.PermisRolDto;
import es.caib.helium.logic.intf.service.PermisService;
import es.caib.helium.persist.repository.PermisRepository;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;

/**
 * Implementació dels mètodes del servei ExpedientService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PermisServiceImpl implements PermisService {

	@Resource
	PermisRepository permisRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	@Override
	@Transactional(readOnly = true)
	public List<PermisRolDto> findAll() {
		return conversioTipusHelper.convertirList(
				permisRepository.findAll(), 
				PermisRolDto.class);
	}

}
