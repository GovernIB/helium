/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.UsuariPreferenciesDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.persist.repository.UsuariPreferenciesRepository;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;

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
	@Resource
	private PluginHelper pluginHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public UsuariPreferenciesDto getUsuariPreferencies() {
		String usuariActual = usuariActualHelper.getUsuariActual();
		logger.debug("Consulta de les preferències per a l'usuari actual ("
				+ "usuariActual=" + usuariActual + ")");
		return conversioTipusHelper.convertir(
				usuariPreferenciesRepository.findByCodi(usuariActual),
				UsuariPreferenciesDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonaDto findPersonaAmbCodi(String codi) {
		return pluginHelper.personaFindAmbCodi(codi);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonaDto findPersonaActual() {
		String usuariActual = usuariActualHelper.getUsuariActual();
		return pluginHelper.personaFindAmbCodi(usuariActual);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return pluginHelper.personaFindLikeNomSencer(text);
	}



	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

}
