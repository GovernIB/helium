/**
 * 
 */
package es.caib.helium.logic.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.intf.dto.PermisRolDto;
import es.caib.helium.logic.intf.service.PermisService;
import es.caib.helium.persist.repository.PermisRepository;

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
