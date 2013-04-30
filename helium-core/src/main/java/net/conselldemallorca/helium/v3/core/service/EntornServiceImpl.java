/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.helper.UsuariActualCacheHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar terminis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("entornServiceV3")
public class EntornServiceImpl implements EntornService {

	@Resource
	private UsuariActualCacheHelper usuariActualCacheHelper;


	
	public List<EntornDto> findPermesosUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta d'expedients permesos per a l'usuari actual (codi=" + auth.getName() + ")");
		return usuariActualCacheHelper.findEntornsPermesosUsuariActual(auth.getName());
	}



	private static final Logger logger = LoggerFactory.getLogger(EntornServiceImpl.class);

}
