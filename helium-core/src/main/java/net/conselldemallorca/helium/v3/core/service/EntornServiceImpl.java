/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.helper.EntornHelper;
import net.conselldemallorca.helium.v3.core.helper.UsuariActualHelper;

/**
 * Implementació dels mètodes de EntornService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("entornServiceV3")
public class EntornServiceImpl implements EntornService {

	@Autowired
	private EntornHelper entornHelper;
	@Autowired
	private UsuariActualHelper usuariActualHelper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EntornDto> findAmbPermisAcces() {
		String usuariActual = usuariActualHelper.getUsuariActual();
		logger.debug("Consulta d'entorns amb accés per a l'usuari actual ("
				+ "usuariActual=" + usuariActual + ")");
		return usuariActualHelper.findEntornsPermesos(usuariActual);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EntornDto> findAll() {
		logger.debug("Consulta d'entorns del sistema)");
		return entornHelper.findAll();
	}

	private static final Logger logger = LoggerFactory.getLogger(EntornServiceImpl.class);

}
