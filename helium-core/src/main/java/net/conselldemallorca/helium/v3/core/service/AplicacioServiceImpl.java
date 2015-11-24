/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.v3.core.repository.UsuariPreferenciesRepository;

/**
 * Implementació dels mètodes de AplicacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Autowired
	private UsuariPreferenciesRepository usuariPreferenciesRepository;

	@Autowired
	private UsuariActualHelper usuariActualHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	public UsuariPreferenciesDto getUsuariPreferencies() {
		String usuariActual = usuariActualHelper.getUsuariActual();
		logger.debug("Consulta de les preferències per a l'usuari actual ("
				+ "usuariActual=" + usuariActual + ")");
		return conversioTipusHelper.convertir(
				usuariPreferenciesRepository.findByCodi(usuariActual),
				UsuariPreferenciesDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

}
