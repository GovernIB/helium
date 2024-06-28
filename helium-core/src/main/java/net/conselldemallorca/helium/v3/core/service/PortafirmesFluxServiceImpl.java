/**
 *
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.PortafirmesFluxService;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;


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
	@Autowired
	ExpedientTipusRepository expedientTipusRepository;
	@Autowired
	DefinicioProcesRepository definicioProcesRepository;
	
	@Override
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			Long expedientTipusId,
			Long definicioProcesId,
			String usuariCodi,
			String urlReturn,
			boolean isPlantilla) throws SistemaExternException {
		logger.debug("(Iniciant flux de firma (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"isPlantilla=" + isPlantilla + ", " +
				"urlRedireccio=" + urlReturn + ")");
		
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		String descripcio = this.getDescripcioFiltre(expedientTipusId, definicioProcesId, usuariCodi);
		PortafirmesIniciFluxRespostaDto transaccioResponse = pluginHelper.portafirmesIniciarFluxDeFirma(
				idioma,
				isPlantilla,
				null,
				descripcio,
				false,
				urlReturn);
		
		return transaccioResponse;
	}
	
	/** Construeix el text per la descripció per poder trobar l'usuari per descripció. Si la definició de procés és global llavors
	 * en comptes del codi del tipus d'expedint posa el codi de la definició de procés. */
	private String getDescripcioFiltre(Long expedientTipusId, Long definicioProcesId, String usuari) {
		StringBuilder filtre = new StringBuilder();
		// entorn=|entornCodi|, tipus/definicio=|codi|
		if (definicioProcesId != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
			filtre.append("entorn=|").append(definicioProces.getEntorn().getCodi()).append("|");
			if (definicioProces.getExpedientTipus() != null) {
				filtre.append(", tipus=|").append(definicioProces.getExpedientTipus().getCodi()).append("|"); 
			} else {
				// Definició de procés global
				filtre.append(", definicio=|").append(definicioProces.getJbpmKey()).append("|");
			} 
		} else if (expedientTipusId != null) {
			ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
			filtre.append("entorn=|").append(expedientTipus.getEntorn().getCodi()).append("|");
			filtre.append(", tipus=|").append(expedientTipus.getCodi()).append("|");
		} else {
			filtre.append("entorn=||, tipus=||");
		}
		//, usuari=|usuari|
		filtre.append(", usuari=|");
		if (usuari != null) {
			filtre.append(usuari);
		}
		filtre.append("|");
		return filtre.toString();
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
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long expedientTipusId, Long definicioProcesId, String usuari) {
		logger.debug("Recuperant plantilles disponibles per l'usuari " + (usuari != null ? usuari : "aplicació"));
		String descripcioFiltre = getDescripcioFiltre(expedientTipusId, definicioProcesId, usuari);
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		List<PortafirmesFluxRespostaDto> resultat = pluginHelper.portafirmesRecuperarPlantillesDisponibles(descripcioFiltre, idioma, true);
		if (resultat!=null) {
			for (PortafirmesFluxRespostaDto aux: resultat) {
				aux.setFluxComu(usuari==null);
			}
		}
		return resultat;
	}
	
	@Override
	public boolean esborrarPlantilla(String plantillaFluxId) {
		logger.debug("Esborrant la plantilla amb id=" + plantillaFluxId);
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		return pluginHelper.portafirmesEsborrarPlantillaFirma(idioma, plantillaFluxId);
	}
	
	@Override
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId) {
		logger.debug("Consultant la url de la vista de l'estat del flux de firmees amb id=" + portafirmesId);
		String idioma = LocaleContextHolder.getLocale().getLanguage();
		return pluginHelper.portafirmesRecuperarUrlEstatFluxFirmes(portafirmesId, idioma);
	}


	@Override
	public List<PortafirmesCarrecDto> recuperarCarrecs() {
		logger.debug("Recuperant els càrrecs disponibles");
		return pluginHelper.portafirmesRecuperarCarrecs();
	}

	private static final Logger logger = LoggerFactory.getLogger(PortafirmesFluxServiceImpl.class);

}