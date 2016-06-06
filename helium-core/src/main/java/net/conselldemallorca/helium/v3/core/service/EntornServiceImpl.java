/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementació dels mètodes de EntornService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("entornServiceV3")
public class EntornServiceImpl implements EntornService {

	@Resource
	private EntornRepository entornRepository;

	@Autowired
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EntornDto> findActiusAmbPermisAcces() {
		String usuariActual = usuariActualHelper.getUsuariActual();
		logger.debug("Consulta d'entorns amb accés per a l'usuari actual ("
				+ "usuariActual=" + usuariActual + ")");
		return usuariActualHelper.findEntornsActiusPermesos(usuariActual);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EntornDto> findActiusAll() {
		logger.debug("Consulta dels entorns actius");
		return conversioTipusHelper.convertirList(
				entornRepository.findByActiuTrue(),
				EntornDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(EntornServiceImpl.class);

}
