/**
 *
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.PortafirmesFluxService;


/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PortafirmesFluxServiceImpl implements PortafirmesFluxService {

	@Autowired
	PluginHelper pluginHelper;
	@Autowired
	AplicacioService aplicacioService;
	
	@Override
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn,
			boolean isPlantilla) throws SistemaExternException {
		logger.debug("(Iniciant flux de firma (" +
				"urlRedireccio=" + urlReturn + ")");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariCodi =auth.getName();
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		PortafirmesIniciFluxRespostaDto transaccioResponse = pluginHelper.portafirmesIniciarFluxDeFirma(
				idioma,
				isPlantilla,
				null,
				"user=" + usuariCodi,
				true,
				urlReturn);
		
		return transaccioResponse;
	}
	
	@Override
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String idTransaccio) {
		logger.debug("(Recuperant flux de firma (" + 
				"idTransaccio=" + idTransaccio +")");
		return pluginHelper.portafirmesRecuperarFluxDeFirma(idTransaccio);
	}
	
	@Override
	public void tancarTransaccio(String idTransaccio) {
		logger.debug("(Tancant flux de firma (" + 
				"idTransaccio=" + idTransaccio +")");
		pluginHelper.portafirmesTancarFluxDeFirma(idTransaccio);
	}

	@Override
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String plantillaFluxId) {
		logger.debug("Recuperant detall flux de firma (" +
				"plantillaFluxId=" + plantillaFluxId +")");
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		return pluginHelper.portafirmesRecuperarInfoFluxDeFirma(
				plantillaFluxId, 
				idioma);
	}
	
	@Override
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId) {
		logger.debug("Recuperant url visualització plantilla (" +
				"plantillaId=" + plantillaFluxId +")");
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		return pluginHelper.portafirmesRecuperarUrlPlantilla(
				plantillaFluxId, 
				idioma,
				null,
				false);
	}

	@Override
	public String recuperarUrlEdicioPlantilla(
			String plantillaFluxId,
			String returnUrl) {
		logger.debug("Recuperant url edició plantilla (" +
				"plantillaId=" + plantillaFluxId +")");
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		return pluginHelper.portafirmesRecuperarUrlPlantilla(
				plantillaFluxId, 
				idioma,
				returnUrl,
				true);
	}
	
	@Override
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(boolean filtrar) {
		logger.debug("Recuperant plantilles disponibles per l'usuari aplicació");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariCodi =auth.getName();
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		return pluginHelper.portafirmesRecuperarPlantillesDisponibles(usuariCodi, idioma, filtrar);
	}
	
	@Override
	public boolean esborrarPlantilla(String plantillaFluxId) {
		logger.debug("Esborrant la plantilla amb id=" + plantillaFluxId);
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		return pluginHelper.portafirmesEsborrarPlantillaFirma(idioma, plantillaFluxId);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PortafirmesFluxServiceImpl.class);


}