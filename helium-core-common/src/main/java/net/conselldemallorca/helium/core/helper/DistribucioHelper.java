/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.ws.backofficeintegracio.Annex;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.BackofficeIntegracio;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
import es.caib.distribucio.ws.backofficeintegracio.Interessat;
import es.caib.distribucio.ws.backofficeintegracio.NtiEstadoElaboracion;
import es.caib.distribucio.ws.backofficeintegracio.NtiOrigen;
import es.caib.distribucio.ws.backofficeintegracio.NtiTipoDocumento;
import es.caib.distribucio.ws.client.BackofficeIntegracioWsClientFactory;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioInteressat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioInteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.ws.backoffice.distribucio.AnotacioRegistreId;

/**
 * Mètodes comuns per cridar WebService de Distribucio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DistribucioHelper {

	@Autowired
	AnotacioRepository anotacioRepository;
	@Autowired
	AnotacioAnnexRepository anotacioAnnexRepository;
	@Autowired
	AnotacioInteressatRepository anotacioInteressatRepository;
	@Autowired
	ExpedientTipusRepository expedientTipusRepository;
	@Autowired
	ExpedientRepository expedientRepository;
	
	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;


	/// Invocació al WS
	
	/** Referència al client del WS de Distribució */
	private BackofficeIntegracio wsClient = null;
	
	/** Mètode per obtenir la instància del client del WS de Distribucio.
	 * 
	 * @return
	 * @throws Exception
	 * 				Pot llençar excepcions de tipus IOException de comunicació o si no està configurat llença una excepció.
	 */
	private BackofficeIntegracio getBackofficeIntegracioServicePort() throws Exception {
		
		if (wsClient == null) {
			String url = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.url");
			String usuari = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.username");
			String contrasenya = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.password");

			if (url == null || "".equals(url.trim()))
				throw new Exception("No s'ha trobat la configuració per accedir al WS del backoffice de Distribucio (net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.url");
			wsClient = BackofficeIntegracioWsClientFactory.getWsClient(
					url,
					usuari,
					contrasenya);
		}
		return wsClient;
	}

	/** Mètode per invocar al WS de Distribució i notificar un canvi d'estat amb observacions.
	 * 
	 * @param anotacioRegistreId
	 * @param estat
	 * @param observacions
	 * @throws SistemaExternException 
	 */
	public void canviEstat(
			es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId anotacioRegistreId,
			Estat estat, 
			String observacions) throws SistemaExternException {
		String accioDescripcio = "Canvi d'estat de l'anotació de Distribució amb id de consulta \"" + (anotacioRegistreId != null ? anotacioRegistreId.getIndetificador() : "null") + "\" a " + estat;
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto("anotacioRegistreId", (anotacioRegistreId != null ? anotacioRegistreId.getIndetificador() : "null")),
				new IntegracioParametreDto("estat", estat),
				new IntegracioParametreDto("observacions", observacions)
		};
		long t0 = System.currentTimeMillis();
		try {
			this.getBackofficeIntegracioServicePort().canviEstat(anotacioRegistreId, estat, observacions);

			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut notificar l'estat a Distribució: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw new SistemaExternException(errorDescripcio, ex);
		}
	}

	/** Mètode per invocar al WS de Distribució per consultar la informació d'una anotació de registre a partir de la informació
	 * del seu identificador i clau.
	 * 
	 * @param idWs
	 * @return
	 */
	public AnotacioRegistreEntrada consulta(
			es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId idWs)  throws SistemaExternException{
		AnotacioRegistreEntrada anotacioRegistreEntrada = null;

		String accioDescripcio = "Consulta de la informació de l'anotació de Distribució amb id de consulta \"" + (idWs != null ? idWs.getIndetificador() : "null") + "\"";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto("idWs", (idWs != null ? idWs.getIndetificador() : "null"))
		};
		long t0 = System.currentTimeMillis();
		try {
			anotacioRegistreEntrada = this.getBackofficeIntegracioServicePort().consulta(idWs);

			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					parametres);
		} catch (Exception ex) {
			String errorDescripcio = "No s'ha pogut consultar la informació d'una anotació de Distribució: " + ex.getMessage();
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex,
					parametres);
			throw new SistemaExternException(errorDescripcio, ex);
		}
		return anotacioRegistreEntrada;
	}
	
	/// Mètodes per operar amb la informació

	/** Mètode per guardar a Helium la informació d'una anotació de registre consultada a Distribucio.
	 *  
	 * @param anotacio
	 */
	@Transactional
	public Anotacio guardarAnotacio(AnotacioRegistreId id, AnotacioRegistreEntrada anotacioEntrada) {
		
		ExpedientTipus expedientTipus = null;
		Expedient expedient = null;
		if (anotacioEntrada.getProcedimentCodi() != null) {

			// Cerca el tipus d'expedient per aquell codi de procediment
			List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findPerDistribuir(anotacioEntrada.getProcedimentCodi(), anotacioEntrada.getAssumpteCodiCodi());
			if (!expedientsTipus.isEmpty()) {
				expedientTipus = expedientsTipus.get(0);
				
				// Cerca si hi ha cap expedient que coincideixi amb el número d'expedient
				if (anotacioEntrada.getExpedientNumero() != null) {
					expedient = expedientRepository.findByTipusAndNumero(expedientTipus, anotacioEntrada.getExpedientNumero());
				}
			}
		}
		// Crea l'anotació
		Anotacio anotacioEntity = Anotacio.getBuilder(
				id.getIndetificador(),
				id.getClauAcces(),
				new Date(),
				AnotacioEstatEnumDto.PENDENT,
				anotacioEntrada.getAssumpteTipusCodi(),
				anotacioEntrada.getData().toGregorianCalendar().getTime(),
				anotacioEntrada.getEntitatCodi(),
				anotacioEntrada.getIdentificador(),
				anotacioEntrada.getIdiomaCodi(),
				anotacioEntrada.getLlibreCodi(),
				anotacioEntrada.getOficinaCodi(),
				anotacioEntrada.getDestiCodi(),
				expedientTipus,
				expedient).
				aplicacioCodi(anotacioEntrada.getAplicacioCodi()).
				aplicacioVersio(anotacioEntrada.getAplicacioVersio()).
				assumpteCodiCodi(anotacioEntrada.getAssumpteCodiCodi()).
				assumpteCodiDescripcio(anotacioEntrada.getAssumpteCodiDescripcio()).
				assumpteTipusDescripcio(anotacioEntrada.getAssumpteTipusDescripcio()).
				docFisicaCodi(anotacioEntrada.getDocFisicaCodi()).
				docFisicaDescripcio(anotacioEntrada.getDocFisicaDescripcio()).
				entitatDescripcio(anotacioEntrada.getEntitatDescripcio()).
				expedientNumero(anotacioEntrada.getExpedientNumero()).
				exposa(anotacioEntrada.getExposa()).
	 			extracte(anotacioEntrada.getExtracte()).
	 			procedimentCodi(anotacioEntrada.getProcedimentCodi()).
				idiomaDescripcio(anotacioEntrada.getIdomaDescripcio()).
				llibreDescripcio(anotacioEntrada.getLlibreDescripcio()).
				observacions(anotacioEntrada.getObservacions()).
				oficinaDescripcio(anotacioEntrada.getOficinaDescripcio()).
				origenData(anotacioEntrada.getOrigenData() != null ? anotacioEntrada.getOrigenData().toGregorianCalendar().getTime() : null).
				origenRegistreNumero(anotacioEntrada.getOrigenRegistreNumero()).
				refExterna(anotacioEntrada.getRefExterna()).
				solicita(anotacioEntrada.getSolicita()).
				transportNumero(anotacioEntrada.getTransportNumero()).
				transportTipusCodi(anotacioEntrada.getTransportTipusCodi()).
				transportTipusDescripcio(anotacioEntrada.getTransportTipusDescripcio()).
				usuariCodi(anotacioEntrada.getUsuariCodi()).
				usuariNom(anotacioEntrada.getUsuariNom()).
				destiDescripcio(anotacioEntrada.getDestiDescripcio()).
				build();
		anotacioRepository.save(anotacioEntity);
				
		// Crea els interessats
		for (Interessat interessat: anotacioEntrada.getInteressats()) {
			anotacioEntity.getInteressats().add(
					crearInteressatEntity(
							interessat,
							anotacioEntity));
		}
		// Crea els annexos
		for (Annex annex: anotacioEntrada.getAnnexos()) {
			anotacioEntity.getAnnexos().add(
					crearAnnexEntity(
							annex,
							anotacioEntity));
		}	
		
		return anotacioEntity;
	}

	private AnotacioInteressat crearInteressatEntity(
			Interessat interessat,
			Anotacio anotacio) {
		
		AnotacioInteressat representantEntity = null;
		if (interessat.getRepresentant() != null) {
			representantEntity = AnotacioInteressat.getBuilder(	
		 			interessat.getRepresentant().getTipus().name()).
		 			adresa (interessat.getRepresentant().getAdresa()).
		 			canal (interessat.getRepresentant().getCanal()).
		 			cp (interessat.getRepresentant().getCp()).
		 			documentNumero (interessat.getRepresentant().getDocumentNumero()).
		 			documentTipus (interessat.getRepresentant().getDocumentTipus().name()).
		 			email (interessat.getRepresentant().getEmail()).
		 			llinatge1 (interessat.getRepresentant().getLlinatge1()).
		 			llinatge2 (interessat.getRepresentant().getLlinatge2()).
		 			municipiCodi (interessat.getRepresentant().getMunicipiCodi()).
		 			nom (interessat.getRepresentant().getNom()).
		 			observacions (interessat.getRepresentant().getObservacions()).
		 			paisCodi (interessat.getRepresentant().getPaisCodi()).
		 			provinciaCodi (interessat.getRepresentant().getProvinciaCodi()).
		 			raoSocial (interessat.getRepresentant().getRaoSocial()).
		 			telefon (interessat.getRepresentant().getTelefon()).
		 			pais (interessat.getRepresentant().getPais()).
		 			provincia (interessat.getRepresentant().getProvincia()).
		 			municipi (interessat.getRepresentant().getMunicipi()).
		 			organCodi(interessat.getRepresentant().getOrganCodi()).
					build();
		}
		AnotacioInteressat interessatEntity = AnotacioInteressat.getBuilder(	
		 			interessat.getTipus().name()).
		 			adresa (interessat.getAdresa()).
		 			canal (interessat.getCanal()).
		 			cp (interessat.getCp()).
		 			documentNumero (interessat.getDocumentNumero()).
		 			documentTipus (interessat.getDocumentTipus() != null ? interessat.getDocumentTipus().name() : null).
		 			email (interessat.getEmail()).
		 			llinatge1 (interessat.getLlinatge1()).
		 			llinatge2 (interessat.getLlinatge2()).
		 			municipiCodi (interessat.getMunicipiCodi()).
		 			nom (interessat.getNom()).
		 			observacions (interessat.getObservacions()).
		 			paisCodi (interessat.getPaisCodi()).
		 			provinciaCodi (interessat.getProvinciaCodi()).
		 			pais (interessat.getPais()).
		 			provincia (interessat.getProvincia()).
		 			municipi (interessat.getMunicipi()).		 			
		 			raoSocial (interessat.getRaoSocial()).
		 			telefon (interessat.getTelefon()).
		 			organCodi(interessat.getOrganCodi()).
		 			representant(representantEntity).
		 			anotacio(anotacio).
					build();
		return anotacioInteressatRepository.save(interessatEntity);
	}

	private AnotacioAnnex crearAnnexEntity(Annex annex, Anotacio anotacio) {
		
		AnotacioAnnex annexEntity = AnotacioAnnex.getBuilder(
				annex.getNom(),
				annex.getNtiFechaCaptura().toGregorianCalendar().getTime(),
				toNtiOrigenEnumDto(annex.getNtiOrigen()),
				toNtiTipoDocumentalEnumDto(annex.getNtiTipoDocumental()),
				annex.getSicresTipoDocumento() != null? annex.getSicresTipoDocumento().toString() : null,
				annex.getTitol(),
				anotacio,
				toNtiEstadoElaboracionEnumDto(annex.getNtiEstadoElaboracion())).
				contingut(annex.getContingut()).
				firmaContingut(annex.getFirmaContingut()).
				observacions(annex.getObservacions()).
				sicresValidezDocumento(annex.getSicresValidezDocumento() != null? annex.getSicresValidezDocumento().toString() : null).
				tipusMime(annex.getTipusMime()).
				uuid(annex.getUuid()).
				firmaNom(annex.getFirmaNom()).
				build();
		return anotacioAnnexRepository.save(annexEntity);	
	}

	public static NtiTipoDocumentalEnumDto toNtiTipoDocumentalEnumDto(NtiTipoDocumento ntiTipoDocumental) {
		NtiTipoDocumentalEnumDto ntiTipoDocumentalEnumDto = null;
		if (ntiTipoDocumental != null) {
			ntiTipoDocumentalEnumDto = NtiTipoDocumentalEnumDto.valueOf(ntiTipoDocumental.toString());
		}
		return ntiTipoDocumentalEnumDto;
	}

	public static NtiEstadoElaboracionEnumDto toNtiEstadoElaboracionEnumDto(NtiEstadoElaboracion ntiEstadoElaboracion) {
		NtiEstadoElaboracionEnumDto ntiEstadoElaboracionEnumDto = null;
		if (ntiEstadoElaboracion != null) {
			switch(ntiEstadoElaboracion) {
			case ALTRES:
				ntiEstadoElaboracionEnumDto = NtiEstadoElaboracionEnumDto.ALTRES;
				break;
			case COPIA_ELECT_AUTENTICA_CANVI_FORMAT:
				ntiEstadoElaboracionEnumDto = NtiEstadoElaboracionEnumDto.COPIA_CF;
				break;
			case COPIA_ELECT_AUTENTICA_PAPER:
				ntiEstadoElaboracionEnumDto = NtiEstadoElaboracionEnumDto.COPIA_DP;
				break;
			case COPIA_ELECT_AUTENTICA_PARCIAL:
				ntiEstadoElaboracionEnumDto = NtiEstadoElaboracionEnumDto.COPIA_PR;
				break;
			case ORIGINAL:
				ntiEstadoElaboracionEnumDto = NtiEstadoElaboracionEnumDto.ORIGINAL;
				break;
			default:
				break;			
			}
		}
		return ntiEstadoElaboracionEnumDto;
	}

	public static NtiOrigenEnumDto toNtiOrigenEnumDto(NtiOrigen ntiOrigen) {
		NtiOrigenEnumDto ntiOrigenEnumDto = null;
		if (ntiOrigen != null) {
			switch(ntiOrigen) {
			case ADMINISTRACIO:
				ntiOrigenEnumDto = NtiOrigenEnumDto.ADMINISTRACIO;
				break;
			case CIUTADA:
				ntiOrigenEnumDto = NtiOrigenEnumDto.CIUTADA;
				break;
			default:
				break;
			}
		}
		return ntiOrigenEnumDto;
	}
}