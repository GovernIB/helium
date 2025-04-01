/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExceptionHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.UsuariPreferencies;
import net.conselldemallorca.helium.v3.core.api.dto.ExcepcioLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.repository.UsuariPreferenciesRepository;

/**
 * Implementació dels mètodes de AplicacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Autowired private UsuariPreferenciesRepository usuariPreferenciesRepository;
	@Autowired private UsuariActualHelper usuariActualHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Resource  private PluginHelper pluginHelper;
	@Resource  private ExceptionHelper exceptionHelper;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonaDto findPersonaCarrecAmbCodi(String codi) {
		
		logger.debug("Obtenint usuari/càrrec amb codi (codi=" + codi + ")");
		PersonaDto persona = null;
		if (codi.matches("CARREC\\[(.*)\\]")) {
			String codiCarrec = codi.substring(codi.indexOf('[') + 1, codi.indexOf(']'));
			PortafirmesCarrecDto carrec = pluginHelper.portafirmesRecuperarCarrec(codiCarrec);
			if (carrec != null) {
				persona = new PersonaDto();
				persona.setCodi(codi);
				persona.setNom(carrec.getCarrecName() + (carrec.getUsuariPersonaNom() != null ? " - " + carrec.getUsuariPersonaNom() : "") );
				persona.setDni(carrec.getUsuariPersonaNif());
			} else {
				throw new NoTrobatException(PersonaDto.class, codi);
			}
		} else {
			persona = pluginHelper.personaFindAmbCodi(codi);			
		}
		return persona;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PersonaDto> findPersonaLikeCodiOrNomSencer(String text) throws SistemaExternException {
		return pluginHelper.personaFindLikeCodiOrNomSencer(text);
	}

	@Override
	public void excepcioSave(String peticio, String params, Throwable exception) {
		exceptionHelper.addExcepcio(peticio, params, exception);
	}

	@Override
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return exceptionHelper.findAll().get(index.intValue());
	}

	@Override
	public List<ExcepcioLogDto> excepcioFindAll() {
		return exceptionHelper.findAll();
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

	@Override
	@Transactional
	public void updateEntronActual(String entorn) throws NoTrobatException {
		String usuariActual = usuariActualHelper.getUsuariActual();
		logger.debug("Modificant entorn actual per a l'usuari actual ("
				+ "usuariActual=" + usuariActual + ", entorn=" + entorn + ")");
		UsuariPreferencies pref = usuariPreferenciesRepository.findByCodi(usuariActual);
		pref.setCurrentEntornCodi(entorn);
		pref.setCurrentEntornData(new Date());
		usuariPreferenciesRepository.save(pref);
	}
}
