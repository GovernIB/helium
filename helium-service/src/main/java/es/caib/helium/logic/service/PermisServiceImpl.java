/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.intf.dto.PermisRolDto;
import es.caib.helium.logic.intf.service.PermisService;
import es.caib.helium.persist.repository.PermisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
	private ConversioTipusServiceHelper conversioTipusServiceHelper;

	@Override
	@Transactional(readOnly = true)
	public List<PermisRolDto> findAll() {
		return conversioTipusServiceHelper.convertirList(
				permisRepository.findAll(), 
				PermisRolDto.class);
	}

}
