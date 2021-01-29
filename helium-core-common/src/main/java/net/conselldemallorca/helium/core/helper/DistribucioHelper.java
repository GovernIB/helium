/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.backoffice.utils.sistra.BackofficeSistra2Utils;
import es.caib.distribucio.backoffice.utils.sistra.BackofficeSistra2UtilsImpl;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Campo;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Formulario;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Valor;
import es.caib.distribucio.ws.backofficeintegracio.Annex;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
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
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra.TipusMapeig;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaPerfilEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioInteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.MapeigSistraRepository;

/**
 * Mètodes comuns per cridar WebService de Distribucio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DistribucioHelper {

	@Autowired
	private AnotacioRepository anotacioRepository;
	@Autowired
	private AnotacioAnnexRepository anotacioAnnexRepository;
	@Autowired
	private AnotacioInteressatRepository anotacioInteressatRepository;
	@Autowired
	private ExpedientTipusRepository expedientTipusRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private CampTascaRepository campTascaRepository;
	@Autowired
	private MapeigSistraRepository mapeigSistraRepository;
	
	
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private AnotacioService anotacioService;
	@Autowired
	private DissenyService dissenyService;
	@Autowired
	private ExpedientService expedientService;
	
	
	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	
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
	public Anotacio guardarAnotacio(AnotacioRegistreId idWs, AnotacioRegistreEntrada anotacioEntrada) {
		
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
				idWs.getIndetificador(),
				idWs.getClauAcces(),
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
				build();
		annexEntity.setFirmaContingut(annex.getFirmaContingut());
		annexEntity.setFirmaNom(annex.getFirmaNom());
		if (annex.getFirmaPerfil() != null)
			annexEntity.setFirmaPerfil(ArxiuFirmaPerfilEnumDto.valueOf(annex.getFirmaPerfil().toString()));
		annexEntity.setFirmaTamany(annex.getFirmaTamany());
		if (annex.getFirmaTipus() != null)
			annexEntity.setFirmaTipus(NtiTipoFirmaEnumDto.valueOf(annex.getFirmaTipus().toString()));
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

	/** Processa la petició dins d'una mateixa transacció per consultar l'anotació, guardar-la, processar-la
	 * automàticament si cal i comunicar l'esta.
	 * 
	 * @param idWs
	 */
	@Transactional
	public void processarAnotacio(AnotacioRegistreId idWs) throws Exception{

		// si la petició no està a BBDD consulta la petició i la guarda a BBDD
		AnotacioRegistreEntrada anotacioRegistreEntrada = this.consulta(idWs);

		// Guarda l'anotació
		Anotacio anotacioCreada = this.guardarAnotacio(idWs, anotacioRegistreEntrada);

		// Comprova si l'anotació s'ha associat amb un tipus d'expedient amb processament automàtic
		ExpedientTipus expedientTipus = anotacioCreada.getExpedientTipus();
		if (expedientTipus != null 
				&& expedientTipus.isDistribucioProcesAuto()) {
			Map<String, Object> variables = null;
			Map<String, DadesDocumentDto> documents = null;
			List<DadesDocumentDto> adjunts = null;
			
			if (expedientTipus.isDistribucioSistra()) {
				// Extreu documents i variables segons el mapeig sistra
				variables = this.getDadesInicials(expedientTipus, anotacioCreada);
				documents = this.getDocumentsInicials(expedientTipus, anotacioCreada);
				adjunts = this.getDocumentsAdjunts(expedientTipus, anotacioCreada);
			}
			// Crear l'expedient
			Expedient expedient = expedientHelper.iniciar(
					expedientTipus.getEntorn().getId(), //entornId
					null, //usuari, 
					expedientTipus.getId(), //expedientTipusId, 
					null, //definicioProcesId,
					null, //any, 
					null, //numero, 
					null, //titol, 
					null, //registreNumero, 
					null, //registreData, 
					null, //unitatAdministrativa, 
					null, //idioma, 
					false, //autenticat, 
					null, //tramitadorNif, 
					null, //tramitadorNom, 
					null, //interessatNif, 
					null, //interessatNom, 
					null, //representantNif, 
					null, //representantNom, 
					false, //avisosHabilitats, 
					null, //avisosEmail, 
					null, //avisosMobil, 
					false, //notificacioTelematicaHabilitada, 
					variables, //variables, 
					null, //transitionName, 
					IniciadorTipusDto.INTERN, //IniciadorTipus 
					null, //iniciadorCodi, 
					null, //responsableCodi, 
					documents, //documents, 
					adjunts); //adjunts);
			
			// Incorporporar l'anotació a l'expedient
			anotacioService.incorporarExpedient(
					anotacioCreada.getId(), 
					expedientTipus.getId(), 
					expedient.getId(),
					true,
					false);
			
			// Canvi d'estat a processada
			// Notifica a Distribucio que s'ha rebut correctament
			this.canviEstat(
					idWs, 
					es.caib.distribucio.ws.backofficeintegracio.Estat.PROCESSADA,
					"Petició processada a Helium.");
		} else {
			// Notifica a Distribucio que s'ha rebut correctament
			this.canviEstat(
					idWs, 
					es.caib.distribucio.ws.backofficeintegracio.Estat.REBUDA,
					"Petició rebuda a Helium.");
			
		}
		logger.info("Rebuda correctament la petició d'anotació de registre amb id de Distribucio =" + idWs);
	}

	/** S'accepten documents xml tècnics de Sistra de formularis. Per cada document tècnic, si està mapejat
	 * i és un XML que concorda amb l' XSD del formulari llavors es mapegen les dades de la forma ANNEX_TITOL.CAMPO_ID amb
	 * la llibreria d'utilitats de Distribucio per fitxers tècnics de SISTRA. Les variables poden ser simples
	 * o múltiples.
	 * <ul><li> Annexos <--> Mapeig <--> Variables tasca inicial</li>
	 * <li>[titol, annex] <--> [Mapeig] <--> [codi_helium, camp_tasca]</li></ul>
	 * @param expedientTipus
	 * @param anotacio
	 * @return
	 */
	private Map<String, Object> getDadesInicials(ExpedientTipus expedientTipus, Anotacio anotacio) {
		
		// Resposta<codi_helium, valor_helium>
		Map<String, Object> resposta = new HashMap<String, Object>();

		// Llista d'annexos tipus XML
		Map<String, AnotacioAnnex> annexos = new HashMap<String, AnotacioAnnex>();
		for (AnotacioAnnex annex : anotacio.getAnnexos()) {
			if (annex.getNom().toLowerCase().endsWith(".xml")) // Filtra només XML's
				annexos.put(annex.getTitol(), annex);
		}
		// Obtenir una llista de variables inicials de la tasca
		Map<String, CampTasca> campsTasca = new HashMap<String, CampTasca>();
		for (CampTasca camp : getCampsStartTask(expedientTipus)) {
			campsTasca.put(camp.getCamp().getCodi(), camp);
		}
		// Obtenir mapejos
		List<MapeigSistra> mapejosSistra = mapeigSistraRepository.findByFiltre(expedientTipus.getId(), TipusMapeig.Variable);
		if (mapejosSistra.size() == 0)
			return null;

		// Llista de formularis <annex_titol, formulari>
		Map<String, Formulario> formularis = new HashMap<String, Formulario>();
		// Per cada mapeig Sistra
		for (MapeigSistra mapeig : mapejosSistra) {
			// Codi Sistra = ANNEX_TITOL.CAMP_CODI
			String[] codiSistra = mapeig.getCodiSistra().split(".");
			if (codiSistra.length >= 2) {
				String annexTitol = codiSistra[0];
				String campoId = codiSistra[1];
				CampTasca campTasca = campsTasca.get(mapeig.getCodiHelium());
				if (annexos.containsKey(annexTitol) && campTasca != null) {
					// Recupera la informació del formulari de l'annex
					Formulario formulari = formularis.get(annexTitol);
					if (formulari == null) {
						formulari = this.getFormulario(annexos.get(annexTitol));
						formularis.put(annexTitol, formulari);
					}
					if (formulari != null) {
						for (Campo campo : formulari.getCampos()) {
							if (campo.getId().equals(campoId)) {
								Object valorHelium = this.valorVariableHelium(campo, campTasca);
								resposta.put(mapeig.getCodiHelium(), valorHelium);
								break;
							}
						}
					}
				}
			}
		}		
		return resposta;
	}
	
	
	private Object valorVariableHelium(Campo campo, CampTasca campTasca) {
		Object valorHelium = null;
		Camp camp = campTasca.getCamp();
		if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
			// Camp registre
			Object[] dadesRegistre = new Object[camp.getRegistreMembres().size()];
			for (int i = 0; i < camp.getRegistreMembres().size(); i++) {
				CampRegistre campRegistre = camp.getRegistreMembres().get(i);
				for (Valor valor : campo.getValores()) {
					if (campRegistre.getMembre().getCodi().equals(valor.getCodigo())) {
						dadesRegistre[i] = valorPerHeliumSimple(valor.getValue(), campRegistre.getMembre());
					}
				}
			}
			if (camp.isMultiple()) {
				Object[] valorsHelium = new Object[1];
				valorsHelium[0] = dadesRegistre;
				valorHelium = valorsHelium;
			} else {
				valorHelium = dadesRegistre;
			}
		} else {
			// Camp
			if (camp.isMultiple()) {
				// Multiple
				Object[] valorsHelium = new Object[campo.getValores().size()];
				for (int i = 0; i < campo.getValores().size(); i++) {
					valorsHelium[i] = valorPerHeliumSimple(campo.getValores().get(i).getValue(), camp);
				}
				valorHelium = valorsHelium;
			} else {
				// Simple
				String valorSistra = campo.getValores().size() > 0 ? campo.getValores().get(0).getValue() : null;
				valorHelium = valorPerHeliumSimple(valorSistra, camp);
			}
		}
		return valorHelium;
	}
	
	private Object valorPerHeliumSimple(String valor, Camp camp) {
		try {
			if (camp == null) {
			} else if (camp.getTipus().equals(TipusCamp.DATE)) {
				return new SimpleDateFormat("dd/MM/yyyy").parse(valor);
			} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
				return new Boolean(valor);
			} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
				return new BigDecimal(valor);
			} else if (camp.getTipus().equals(TipusCamp.INTEGER)) {
				return new Long(valor);
			} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
				return new Double(valor);
			}
			return valor;
		} catch (Exception ex) {
			return null;
		}
	}

	private Formulario getFormulario(AnotacioAnnex anotacioAnnex) {
		Formulario formulario = null;
		if (anotacioAnnex != null) {
			try {	
				BackofficeSistra2Utils sistraUtils = new BackofficeSistra2UtilsImpl();
				formulario = sistraUtils.parseXmlFormulario(anotacioAnnex.getContingut());
			} catch (Exception e) {
				logger.error("Error obtenint les dades del formulari per l'annex de Sistra2 " + anotacioAnnex.getTitol() + " de l'anotació " + anotacioAnnex.getId() + " " + anotacioAnnex.getAnotacio().getIdentificador() + " " + anotacioAnnex.getAnotacio().getExtracte());
			}
		}
		return formulario;
	}

	private List<CampTasca> getCampsStartTask(ExpedientTipus expedientTipus) {
		ExpedientTascaDto startTask = expedientService.getStartTask(
				expedientTipus.getEntorn().getId(),
				expedientTipus.getId(),
				null,
				null);
		if (startTask != null) {
			return campTascaRepository.findAmbTascaIdOrdenats(startTask.getTascaId(), expedientTipus.getId());
		} else {
			return new ArrayList<CampTasca>();
		}
	}

	private Map<String, DadesDocumentDto> getDocumentsInicials(ExpedientTipus expedientTipus, Anotacio anotacio) {
		
		List<MapeigSistra> mapeigsSistra = mapeigSistraRepository.findByFiltre(expedientTipus.getId(), TipusMapeig.Document);
		if (mapeigsSistra.size() == 0)
			return null;
		boolean trobat = false;
		Map<String, DadesDocumentDto> resposta = new HashMap<String, DadesDocumentDto>();
		List<DocumentDto> documents = getDocuments(expedientTipus);
		
		for (MapeigSistra mapeig : mapeigsSistra){
			trobat = true;
			DocumentDto docHelium = null;
			for (DocumentDto document : documents){
				if (document.getCodi().equalsIgnoreCase(mapeig.getCodiHelium())){
					docHelium = document;
					break;
				}
			}
			try {
				if (docHelium != null) {
					DadesDocumentDto document = documentSistra(anotacio, mapeig.getCodiSistra(), docHelium);
					if (document != null) {
						resposta.put(mapeig.getCodiHelium(), document);
					}
				}
			} catch (Exception ex) {
				logger.error("Error llegint dades del document de SISTRA", ex);
			}
		}
		if (trobat)
			return resposta;
		else
			return null;
		
	}
	
	private DadesDocumentDto documentSistra(
			Anotacio anotacio,
			String varSistra,
			DocumentDto varHelium) throws Exception {
		DadesDocumentDto resposta = null;
		for (AnotacioAnnex document: anotacio.getAnnexos()) {
			if (varSistra.equalsIgnoreCase(document.getTitol())) {
				resposta = new DadesDocumentDto();
				resposta.setIdDocument(varHelium.getId());
				resposta.setCodi(varHelium.getCodi());
				resposta.setData(anotacio.getData());
				resposta.setArxiuNom(document.getNom());
				resposta.setArxiuContingut(document.getContingut());
				break;
			}
		}
		return resposta;
	}
	private List<DocumentDto> getDocuments(ExpedientTipus expedientTipus) {
		DefinicioProcesDto definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipus.getId());
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
				expedientTipus.getId(), 
				definicioProces != null ? definicioProces.getId() : null, 
				true);
		return documents;
	}

	private List<DadesDocumentDto> getDocumentsAdjunts(ExpedientTipus expedientTipus, Anotacio anotacio) {

		List<MapeigSistra> mapeigsSistra = mapeigSistraRepository.findByFiltre(expedientTipus.getId(), TipusMapeig.Adjunt);
		if (mapeigsSistra.size() == 0)
			return null;
		
		boolean trobat = false;
		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();

		for (MapeigSistra mapeig : mapeigsSistra){
			if (MapeigSistra.TipusMapeig.Adjunt.equals(mapeig.getTipus())){
				trobat = true;
				try {
					resposta.addAll(documentsSistraAdjunts(anotacio, mapeig.getCodiHelium()));
				} catch (Exception ex) {
					logger.error("Error llegint dades del document de SISTRA", ex);
				}
			}
		}
		
		if (trobat)
			return resposta;
		else
			return null;
	}
	
	private List<DadesDocumentDto> documentsSistraAdjunts(Anotacio anotacio, String codiHelium) {
		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();
		for (AnotacioAnnex document: anotacio.getAnnexos()) {
			if (document.getTitol().equalsIgnoreCase(codiHelium)) {
				DadesDocumentDto docResposta = new DadesDocumentDto();
				docResposta.setTitol(document.getTitol());
				docResposta.setData(anotacio.getData());
				docResposta.setArxiuNom(document.getNom());
				docResposta.setArxiuContingut(document.getContingut());
				resposta.add(docResposta);
			}
		}
		return resposta;
	}


	private static final Logger logger = LoggerFactory.getLogger(DistribucioHelper.class);
}
