/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.exception.PluginException;
import net.conselldemallorca.helium.v3.core.api.service.ConfigService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.PersonaHelper;
import net.conselldemallorca.helium.v3.core.helper.UsuariActualCacheHelper;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.FestiuRepository;
import net.conselldemallorca.helium.v3.core.repository.ReassignacioRepository;
import net.conselldemallorca.helium.v3.core.repository.UsuariPreferenciesRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar la configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("configServiceV3")
public class ConfigServiceImpl implements ConfigService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private FestiuRepository festiuRepository;
	@Resource
	private ReassignacioRepository reassignacioRepository;
	@Resource
	private UsuariPreferenciesRepository usuariPreferenciesRepository;

	@Resource
	private UsuariActualCacheHelper usuariActualCacheHelper;
	@Resource
	private PersonaHelper personaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public List<EntornDto> findEntornsPermesosUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariActual = auth.getName();
		logger.debug("Cercant entorns permesos per a l'usuari actual (codi=" + usuariActual + ")");
		return usuariActualCacheHelper.findEntornsPermesosUsuariActual(auth.getName());
	}

	public void setEntornActual(EntornDto entorn) {
		Long entornId = (entorn != null) ? entorn.getId() : null;
		logger.debug("Configurant l'entorn actual (id=" + entornId + ")");
		EntornActual.setEntornId(entornId);
	}
	public EntornDto getEntornActual() {
		Long entornId = EntornActual.getEntornId();
		if (entornId == null)
			return null;
		return conversioTipusHelper.convertir(
				entornRepository.findOne(entornId),
				EntornDto.class);
	}

	public String getUsuariCodiActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			return null;
		else
			return auth.getName();
	}

	public PersonaDto getPersonaAmbCodi(String codi) throws PluginException {
		try {
			return personaHelper.findByCodi(codi);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

	public List<FestiuDto> findFestiusAll() {
		return conversioTipusHelper.convertirLlista(
				festiuRepository.findAll(),
				FestiuDto.class);
	}

	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
			String usuari) {
		return conversioTipusHelper.convertir(
				reassignacioRepository.findActivesByUsuariOrigenAndData(
						usuari,
						new Date()),
				ReassignacioDto.class);
	}

	public UsuariPreferenciesDto getPreferenciesUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariActual = auth.getName();
		logger.debug("Obtenint preferències per a l'usuari actual (codi=" + usuariActual + ")");
		return conversioTipusHelper.convertir(
				usuariPreferenciesRepository.findByCodi(usuariActual),
				UsuariPreferenciesDto.class);
	}

	public String getHeliumProperty(String propertyName) {
		return (String)GlobalProperties.getInstance().get(propertyName);
	}

	private static final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

}
