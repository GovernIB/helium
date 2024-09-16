/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import edu.emory.mathcs.backport.java.util.Arrays;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultatAnnex;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultatAnnex.AnnexAccio;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.sistra.BackofficeSistra2Utils;
import es.caib.distribucio.backoffice.utils.sistra.BackofficeSistra2UtilsImpl;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Campo;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Elemento;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Formulario;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Valor;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;
import es.caib.distribucio.rest.client.integracio.domini.Annex;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.distribucio.rest.client.integracio.domini.Interessat;
import es.caib.distribucio.rest.client.integracio.domini.NtiEstadoElaboracion;
import es.caib.distribucio.rest.client.integracio.domini.NtiOrigen;
import es.caib.distribucio.rest.client.integracio.domini.NtiTipoDocumento;
import es.caib.plugins.arxiu.api.Document;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Alerta.AlertaPrioritat;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioInteressat;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra.TipusMapeig;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAnnexEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioMapeigResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuEstat;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaPerfilEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioInteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
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

	public static final String SISTRA2_CAMP_FORM_SIMPLE = "simple";
	public static final String SISTRA2_CAMP_FORM_COMPUESTO = "compuesto";
	public static final String SISTRA2_CAMP_FORM_MULTIVALUADO = "multivaluado";
	public static final String SISTRA2_CAMP_FORM_LISTA = "lista";
	
	@Resource
	private ExceptionHelper exceptionHelper;
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
	private CampRepository campRepository;
	
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private DissenyService dissenyService;
	@Autowired
	private ExpedientService expedientService;
	
	
	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	
	@Resource
	private PluginHelper pluginHelper;
	
	@Resource
	private PaginacioHelper paginacioHelper;
	
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;

	@Resource
	private AlertaHelper alertaHelper;
	
	@Resource
	private AnotacioHelper anotacioHelper;
	@Resource
	private MessageHelper messageHelper;
	
	@Resource
	private DocumentStoreRepository documentStoreRepository;


	/** Referència al client del WS de Distribució */
	private BackofficeIntegracioRestClient restClient = null;
	
	/** Referència pròpia per cridar mètodes de forma transaccional */
	private DistribucioHelper self;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void postContruct(){
        self = applicationContext.getBean(DistribucioHelper.class);
    }

		

	/** Mètode per obtenir la instància del client del WS de Distribucio.
	 * 
	 * @return
	 * @throws Exception
	 * 				Pot llençar excepcions de tipus IOException de comunicació o si no està configurat llença una excepció.
	 */
	private BackofficeIntegracioRestClient getClientRest() throws Exception {
		
		if (restClient == null) {
						
			String url_base = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.url");
			String usuari = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.username");
			String contrasenya = GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.password");
			
			if (url_base != null && usuari != null && contrasenya != null) {
				logger.trace(">>> Creant el client BackofficeIntegracioRestClient API REST");
				restClient = BackofficeIntegracioRestClientFactory.getRestClient(
						url_base, 
						usuari, 
						contrasenya);
			} else {
				throw new RuntimeException("Falta configurar les propietats pel client de Backoffice de DISTRIBUCIO net.conselldemallorca.helium.distribucio.backofficeIntegracio.*");
			}
		}
		return restClient;
	}

	/** Mètode per invocar al WS de Distribució i notificar un canvi d'estat amb observacions.
	 * 
	 * @param anotacioRegistreId
	 * @param estat
	 * @param observacions
	 * @throws SistemaExternException 
	 */
	public void canviEstat(
			es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId anotacioRegistreId,
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
			this.getClientRest().canviEstat(anotacioRegistreId, estat, observacions);

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
			throw new SistemaExternException(MonitorIntegracioHelper.INTCODI_DISTRIBUCIO, errorDescripcio, ex);
		}
	}

	/** Mètode per invocar al WS de Distribució per consultar la informació d'una anotació de registre a partir de la informació
	 * del seu identificador i clau.
	 * 
	 * @param idWs
	 * @return
	 */
	public AnotacioRegistreEntrada consulta(
			es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs)  throws SistemaExternException{
		AnotacioRegistreEntrada anotacioRegistreEntrada = null;

		String accioDescripcio = "Consulta de la informació de l'anotació de Distribució amb id de consulta \"" + (idWs != null ? idWs.getIndetificador() : "null") + "\"";
		IntegracioParametreDto[] parametres = new IntegracioParametreDto[] {
				new IntegracioParametreDto("idWs", (idWs != null ? idWs.getIndetificador() : "null"))
		};
		long t0 = System.currentTimeMillis();
		try {
			anotacioRegistreEntrada = this.getClientRest().consulta(idWs);

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
			throw new SistemaExternException(MonitorIntegracioHelper.INTCODI_DISTRIBUCIO, errorDescripcio, ex);
		}
		return anotacioRegistreEntrada;
	}
	
	/// Mètodes per operar amb la informació

	/** Mètode per guardar a Helium la informació d'una anotació de registre consultada a Distribucio.
	 *  
	 * @param idWs
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
				anotacioEntrada.getData(),
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
				origenData(anotacioEntrada.getOrigenData() != null ? anotacioEntrada.getOrigenData() : null).
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
	
	/** Mètode per actualitzar les dades referents als intents i error en les consultes
	 * 
	 * @param anotacioId
	 * @param consultaIntents
	 * @param consultaError
	 * @param consultaData
	 * @return
	 */
	@Transactional
	public Anotacio updateConsulta(	long anotacioId,
									int consultaIntents,
									String consultaError,
									Date consultaData) 
	{
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		anotacio.setConsultaIntents(consultaIntents);
		anotacio.setConsultaError(consultaError);
		anotacio.setConsultaData(consultaData);
		
		return anotacio;
	}

	/** Mètode per posar una anotació com a comunicada sense annexos ni interessats per a que es torni a processar.
	 * 
	 * @param anotacioId
	 * @param errorProcessament
	 * @return
	 */
	@Transactional
	public Anotacio resetConsulta(long anotacioId, String errorProcessament) 
	{
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		anotacio.setErrorProcessament(errorProcessament);
		anotacio.setConsultaIntents(0);
		anotacio.setEstat(AnotacioEstatEnumDto.COMUNICADA);

		// Esborra annexos y interessats per evitar duplicar-los quan es torni a consultar
		anotacioAnnexRepository.delete(anotacio.getAnnexos());
		anotacioInteressatRepository.delete(anotacio.getInteressats());
		anotacio.getAnnexos().clear();
		anotacio.getInteressats().clear();
		
		return anotacio;
	}

	/** Mètode per actualitzar l'estat a ERROR_PROCESSANT i la descripció de l'error de processament.
	 * 
	 * @param anotacioId
	 * @param errorProcessament
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Anotacio updateErrorProcessament(long anotacioId, String errorProcessament) {
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		anotacio.setErrorProcessament(errorProcessament);
		anotacio.setDataProcessament(new Date());
		anotacio.setEstat(AnotacioEstatEnumDto.ERROR_PROCESSANT);
		return anotacio;
	}

	/** Mètode per actualitzar l'error de processament per un annex en concret en una nova transacció.
	 * 
	 * @param annexId
	 * @param error
	 * @return
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW)
	public AnotacioAnnex updateErrorAnnex(long annexId, String error) {
		
		AnotacioAnnex anotacioAnnex = anotacioAnnexRepository.findOne(annexId);
		anotacioAnnex.setError(error);
		return anotacioAnnex;
	}
	
	/** Mètode per guardar a Helium la informació d'una anotació de registre consultada a Distribucio.
	 *  
	 * @param anotacioId
	 */
	@Transactional
	public Anotacio updateAnotacio(long anotacioId, AnotacioRegistreEntrada anotacioEntrada) {
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		
				// Actualitza l'anotació
		anotacio.setEstat(AnotacioEstatEnumDto.PENDENT);
		anotacio.setAssumpteCodiCodi(anotacioEntrada.getAssumpteTipusCodi());
		anotacio.setData(anotacioEntrada.getData());
		anotacio.setEntitatCodi(anotacioEntrada.getEntitatCodi());
		anotacio.setIdentificador(anotacioEntrada.getIdentificador());
		anotacio.setIdiomaCodi(anotacioEntrada.getIdiomaCodi());
		anotacio.setLlibreCodi(anotacioEntrada.getLlibreCodi());
		anotacio.setOficinaCodi(anotacioEntrada.getOficinaCodi());
		anotacio.setDestiCodi(anotacioEntrada.getDestiCodi());
		anotacio.setAplicacioCodi(anotacioEntrada.getAplicacioCodi());
		anotacio.setAplicacioVersio(anotacioEntrada.getAplicacioVersio());
		anotacio.setAssumpteCodiCodi(anotacioEntrada.getAssumpteCodiCodi());
		anotacio.setAssumpteCodiDescripcio(anotacioEntrada.getAssumpteCodiDescripcio());
		anotacio.setAssumpteTipusDescripcio(anotacioEntrada.getAssumpteTipusDescripcio());
		anotacio.setDocFisicaCodi(anotacioEntrada.getDocFisicaCodi());
		anotacio.setDocFisicaDescripcio(anotacioEntrada.getDocFisicaDescripcio());
		anotacio.setEntitatDescripcio(anotacioEntrada.getEntitatDescripcio());
		anotacio.setExpedientNumero(anotacioEntrada.getExpedientNumero());
		anotacio.setExposa(anotacioEntrada.getExposa());
		anotacio.setExtracte(anotacioEntrada.getExtracte());
		anotacio.setProcedimentCodi(anotacioEntrada.getProcedimentCodi());
		anotacio.setIdiomaDescripcio(anotacioEntrada.getIdomaDescripcio());
		anotacio.setLlibreDescripcio(anotacioEntrada.getLlibreDescripcio());
		anotacio.setObservacions(anotacioEntrada.getObservacions());
		anotacio.setOficinaDescripcio(anotacioEntrada.getOficinaDescripcio());
		anotacio.setOrigenData(anotacioEntrada.getOrigenData() != null ? anotacioEntrada.getOrigenData() : null);
		anotacio.setOrigenRegistreNumero(anotacioEntrada.getOrigenRegistreNumero());
		anotacio.setRefExterna(anotacioEntrada.getRefExterna());
		anotacio.setSolicita(anotacioEntrada.getSolicita());
		anotacio.setTransportNumero(anotacioEntrada.getTransportNumero());
		anotacio.setTransportTipusCodi(anotacioEntrada.getTransportTipusCodi());
		anotacio.setTransportTipusDescripcio(anotacioEntrada.getTransportTipusDescripcio());
		anotacio.setUsuariCodi(anotacioEntrada.getUsuariCodi());
		anotacio.setUsuariNom(anotacioEntrada.getUsuariNom());
		anotacio.setDestiDescripcio(anotacioEntrada.getDestiDescripcio());
				
		// Crea els interessats
		for (Interessat interessat: anotacioEntrada.getInteressats()) {
			anotacio.getInteressats().add(
					crearInteressatEntity(
							interessat,
							anotacio));
		}
		// Crea els annexos
		for (Annex annex: anotacioEntrada.getAnnexos()) {
			anotacio.getAnnexos().add(
					crearAnnexEntity(
							annex,
							anotacio));
		}	
		
		// Relaciona amb un tipus d'expedient i un expedient segons el codi de procediment de l'anotació
		this.updateRelacioExpedientTipus(anotacio);
		
		return anotacio;
	}

	/** Segons el codi de procediment de l'anotació troba el tipus d'expedient relacionat i segons el número d'expedient troba l'expedient al qual incorporar l'anotació.
	 * 
	 * @param anotacio
	 */
	@Transactional
	public void updateRelacioExpedientTipus(Anotacio anotacio) {
		ExpedientTipus expedientTipus = null;
		Expedient expedient = null;
		if (anotacio.getProcedimentCodi() != null) {

			// Cerca el tipus d'expedient per aquell codi de procediment
			List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findPerDistribuir(anotacio.getProcedimentCodi(), anotacio.getAssumpteCodiCodi());
			if (!expedientsTipus.isEmpty()) {
				expedientTipus = expedientsTipus.get(0);
				if (expedientsTipus.size() > 1) {
					StringBuilder expedientsTipusStr = new StringBuilder();
					for (ExpedientTipus et : expedientsTipus) {
						expedientsTipusStr.append(et.getCodi() + " ");
					}
					logger.warn("S'ha trobat més d'1 tipus d'expedient pel codi de procediment: " + anotacio.getProcedimentCodi() + " i assumpte: " + anotacio.getAssumpteCodiCodi() 
								+ ":[ " + expedientsTipusStr.toString() + "]. S'escull el primer " + expedientsTipus.get(0).getCodi() + " de l'entorn " + expedientsTipus.get(0).getEntorn().getCodi());
				}				
				// Cerca si hi ha cap expedient que coincideixi amb el número d'expedient
				if (anotacio.getExpedientNumero() != null) {
					expedient = expedientRepository.findByTipusAndNumero(expedientTipus, anotacio.getExpedientNumero());
				}
			}
		}
		anotacio.setExpedientTipus(expedientTipus);
		anotacio.setExpedient(expedient);
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
		 			documentTipus (interessat.getRepresentant().getDocumentTipus() != null ? interessat.getRepresentant().getDocumentTipus().name() : null).
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
				annex.getNtiFechaCaptura(),
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
				documentValid(annex.isDocumentValid()).
				documentError(annex.getDocumentError()).
				build();
		annexEntity.setFirmaContingut(annex.getFirmaContingut());
		annexEntity.setFirmaNom(annex.getFirmaNom());
		if (annex.getFirmaPerfil() != null)
			annexEntity.setFirmaPerfil(ArxiuFirmaPerfilEnumDto.valueOf(annex.getFirmaPerfil().toString()));
		annexEntity.setFirmaTamany(annex.getFirmaTamany());
		if (annex.getFirmaTipus() != null)
			annexEntity.setFirmaTipus(NtiTipoFirmaEnumDto.valueOf(annex.getFirmaTipus().toString()));
		
		if (annex.getEstat() != null) {
			switch(annex.getEstat()) {
			case DEFINITIU:
				annexEntity.setArxiuEstat(ArxiuEstat.DEFINITIU);
				break;
			case ESBORRANY:
				annexEntity.setArxiuEstat(ArxiuEstat.ESBORRANY);
				break;
			}
		}
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

	/** Guarda la informació de la petició a la taula d'anotacions amb l'estat de comunicada per a que la 
	 * tasca en segon pla TascaProgramadaService.comprovarAnotacionsPendents la processi.
	 * @param idWs
	 */
	@Transactional
	public void encuarAnotacio(es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs) {
		
		Date data = new Date();
		Anotacio anotacioEntity = Anotacio.getBuilder(
						idWs.getIndetificador(), 
						idWs.getClauAcces(), 
						data, // dataRecepcio 
						AnotacioEstatEnumDto.COMUNICADA, 
						null, // assumpteTipusCodi
						data, // data
						null, // entitatCodi
						idWs.getIndetificador(), 
						null, // idiomaCodi
						null, // llibreCodi
						null, // oficinaCodi
						null, // destiCodi
						null, // expedientTipus
						null) // expedient
				
				.extracte("(anotació comunicada pendent de consultar detalls)")
				.build();
		anotacioRepository.save(anotacioEntity);
	}

	
	/** Processa l'anotació consultada per crear un expedient si cal i incorporar la informació a l'expedient
	 * automàticament si cal i comunicar l'estat.
	 * 
	 * @param idWs
	 */
	@Transactional
	public void processarAnotacio(
			AnotacioRegistreId idWs,
			AnotacioRegistreEntrada anotacioDistribucio,
			Anotacio anotacio,
			BackofficeArxiuUtils backofficeUtils) throws Exception{

		// Comprova si ja hi ha una anotació processada per rebutjar possibles anotacions duplicades
		List<Anotacio> anotacions = anotacioRepository.findByDistribucioId(anotacio.getDistribucioId());
		if(anotacions!=null && !anotacions.isEmpty() && anotacions.size()>1) {
			boolean rebutjar=false;
			Expedient expedientAnotacioIdemNum = null;
			for(Anotacio anotacioIdemNumero: anotacions) {
				// expedient no anul·lat o anotació en estat pendent
				expedientAnotacioIdemNum = anotacioIdemNumero.getExpedient();
				if((expedientAnotacioIdemNum!=null && !expedientAnotacioIdemNum.isAnulat()) || AnotacioEstatEnumDto.PENDENT.equals(anotacioIdemNumero.getEstat()))
					rebutjar = true;	
			}
			if (rebutjar) {
				//vol dir q és un duplicat i s'ha de rebutjar
				String motiuRebuig = "L'anotació " + idWs.getIndetificador() + " es rebutja automàticament des d'Helium "
						+ "ja hi ha una anotació amb el mateix número en estat PROCESSADA/PENDENT rebuda el " + anotacio.getDataRecepcio() 
						+ (expedientAnotacioIdemNum!=null ? " per l'expedient " + expedientAnotacioIdemNum.getNumero() : "") ;
				self.rebutjar(anotacio, motiuRebuig);
				//Es comunica l'estat a Distribucio
				try {
					this.canviEstat(
							idWs, 
							es.caib.distribucio.rest.client.integracio.domini.Estat.REBUTJADA,
							motiuRebuig);
				} catch(Exception ed) {
					logger.error("Error comunicant el motiu de rebuig a Distribucio de la petició amb id : " + idWs.getIndetificador() + ": " + ed.getMessage(), ed);
				}
				return;
			}
		}
		
		// Comprova si l'anotació s'ha associat amb un tipus d'expedient amb processament automàtic
		ExpedientTipus expedientTipus = anotacio.getExpedientTipus();
		Expedient expedient = anotacio.getExpedient();
		if (expedientTipus != null 
				&& expedientTipus.isDistribucioProcesAuto()) {
			
			// Estableix l'entorn actual si no hi ha entorn al thread actual
			if (EntornActual.getEntornId() == null) {
				EntornActual.setEntornId(expedientTipus.getEntorn().getId());
			}
			
			boolean reprocessar = false;
			if (expedient == null) {
				Map<String, Object> variables = null;
				Map<String, DadesDocumentDto> documents = null;
				List<DadesDocumentDto> adjunts = null;
				AnotacioMapeigResultatDto resultatMapeig = null;
				
				if (expedientTipus.isDistribucioSistra()) {
					boolean ambContingut = expedient != null ? !expedient.isArxiuActiu() : !expedientTipus.isArxiuActiu(); 
					resultatMapeig = this.getMapeig(expedientTipus, anotacio, ambContingut);
					// Extreu documents i variables segons el mapeig sistra
					variables = resultatMapeig.getDades();
					documents = resultatMapeig.getDocuments();
					adjunts = resultatMapeig.getAdjunts();
				}
				// Crear l'expedient
				try {
					expedient = expedientHelper.iniciarNotNewTransaction( //expedientHelper.iniciar( //.iniciarNotNewTransaction(
							expedientTipus.getEntorn().getId(), //entornId
							null, //usuari, 
							expedientTipus.getId(), //expedientTipusId, 
							null, //definicioProcesId,
							null, //any, 
							expedientTipus.getDemanaNumero() ? anotacio.getIdentificador() : null, //numero, 
							anotacio.getDestiCodi(),  //unitatOrganitzativaCodi
							expedientTipus.getDemanaTitol() ? anotacio.getExtracte() : null, //titol, 
							anotacio.getIdentificador(), //registreNumero, 
							anotacio.getData(), //registreData, 
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
							adjunts,
							anotacio != null ? anotacio.getId() : null,
							null,
							true, // incorporar interessats
							backofficeUtils);
					anotacio.setExpedient(expedient);
				} catch (Throwable e) {
					String errorProcessament = "Error processant l'anotació " + idWs.getIndetificador() + ":" + e;
					// Crida fent referència al bean per crear una nova transacció
					String traçaCompleta = ExceptionUtils.getStackTrace(e);
					self.updateErrorProcessament(anotacio.getId(), errorProcessament.concat(traçaCompleta) );
					logger.error(errorProcessament, e);
					 //Es comunica l'estat a Distribucio
					try {
						this.canviEstat(
								idWs, 
								es.caib.distribucio.rest.client.integracio.domini.Estat.ERROR,
								errorProcessament);
					} catch(Exception ed) {
						logger.error("Error comunicant l'error de processament a Distribucio de la petició amb id : " + idWs.getIndetificador() + ": " + ed.getMessage(), ed);
					}	
					throw new Exception(errorProcessament + ": "
							+ ExceptionUtils.getRootCauseMessage(e), ExceptionUtils.getRootCause(e));
				}				
				if (resultatMapeig != null && resultatMapeig.isError()) {
					Alerta alerta = alertaHelper.crearAlerta(
							expedient.getEntorn(), 
							expedient, 
							new Date(), 
							null, 
							resultatMapeig.getMissatgeAlertaErrors());
					alerta.setPrioritat(AlertaPrioritat.ALTA);	
				}

			} else {
				// Incorporporar l'anotació a l'expedient
				try {
					anotacioHelper.incorporarReprocessarExpedient(
							anotacio,
							anotacio.getId(), 
							expedientTipus.getId(), 
							expedient.getId(),
							true,
							false,
							reprocessar,
							backofficeUtils);
				} catch (Exception e) {
					String traçaCompleta = ExceptionUtils.getStackTrace(e);
					String errorProcessament = "Error incorporant/reprocessant l'anotació " + idWs.getIndetificador() + " a l'expedient:" + traçaCompleta;
					this.canviEstatErrorAnotacio(errorProcessament, anotacio, idWs, e);
					throw new Exception(messageHelper.getMessage("error.proces.peticio") + ": "
							+ ExceptionUtils.getRootCauseMessage(e), ExceptionUtils.getRootCause(e));
				}
			}
			
			anotacio.setEstat(AnotacioEstatEnumDto.PROCESSADA);
			anotacio.setDataProcessament(new Date());

			// Canvi d'estat a processada
			// Notifica a Distribucio que s'ha rebut correctament
			try {
				this.canviEstat(
						idWs, 
						es.caib.distribucio.rest.client.integracio.domini.Estat.PROCESSADA,
						"Anotació incorporada a l'expedient d'Helium " + expedient.getIdentificadorLimitat());
			} catch(Exception e) {
				String errMsg = "Error comunicant l'estat de processada a Distribucio:" + e.getMessage();
				logger.warn(errMsg, e);				
			}
		} else if (expedientTipus==null){

			this.rebutjar(
					anotacio,
					"No hi ha cap tipus d'expedient per processar anotacions amb codi de procediment " + anotacio.getProcedimentCodi() + 
					" i assumpte " + (anotacio.getAssumpteCodiCodi()!=null ? anotacio.getAssumpteCodiCodi() : "(sense assumpte)") +", es rebutja automàticament amb data " 
					+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) +
					". Petició rebutjada a Helium.");				
		
		} else {
			
			try {
				this.canviEstat(
						idWs, 
						es.caib.distribucio.rest.client.integracio.domini.Estat.REBUDA,
						"Petició rebuda a Helium.");
			} catch(Exception e) {
				String errMsg = "Error comunicant l'estat de rebuda a Distribucio:" + e.getMessage();
				logger.warn(errMsg, e);				
			}			
		}
		logger.info("Rebuda correctament la petició d'anotació de registre amb id de Distribucio =" + (idWs != null ? idWs.getIndetificador() : ""));
	}
	
	public void canviEstatErrorAnotacio(String errorProcessament,Anotacio anotacio, AnotacioRegistreId idWs, Throwable e) {
		// Crida fent referència al bean per crear una nova transacció
		self.updateErrorProcessament(anotacio.getId(), errorProcessament);
		logger.error(errorProcessament, e);
		 //Es comunica l'estat a Distribucio
		try {
			this.canviEstat(
					idWs, 
					es.caib.distribucio.rest.client.integracio.domini.Estat.ERROR,
					errorProcessament);
		} catch(Exception ed) {
			logger.error("Error comunicant l'error de processament a Distribucio de la petició amb id : " + idWs.getIndetificador() + ": " + ed.getMessage(), ed);
		}
	}
	
	@Transactional
	public void rebutjar(Anotacio anotacio, String observacions ) {
		// Canvia l'estat del registre a la BBDD
		anotacio.setEstat(AnotacioEstatEnumDto.REBUTJADA);
		anotacio.setRebuigMotiu(observacions);
		anotacio.setDataProcessament(new Date());			
		// Notifica el nou estat a Distribucio
		try {
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setClauAcces(anotacio.getDistribucioClauAcces());
			anotacioRegistreId.setIndetificador(anotacio.getDistribucioId());
			anotacioRepository.save(anotacio);
			this.canviEstat(anotacioRegistreId,
							Estat.REBUTJADA,
							observacions);			
		} catch (Exception e) {
			String errMsg = "Error comunicant l'estat de rebutjada a Distribucio:" + e.getMessage();
			logger.warn(errMsg, e);
		}
	}
	
	/** Mètode per tornar a consultar i processar una anotació que estigui en estat d'error de processament.
	 * 
	 * @param anotacioId
	 * @throws Exception Llença excepció si l'anotació no està en estat d'error de processament o si hi ha error en la consulta.
	 */
	@Transactional
	public Anotacio reprocessarAnotacio(long anotacioId, BackofficeArxiuUtils backofficeUtils) throws Exception {
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);

		logger.debug("Rerocessant l'anotació " + anotacio.getIdentificador() + ".");

		// Comprova que està en error de processament o que està rebutjada o pendent i sense expedient relacionat
		if (!AnotacioEstatEnumDto.ERROR_PROCESSANT.equals(anotacio.getEstat())
				&& !( Arrays.asList(ArrayUtils.toArray(AnotacioEstatEnumDto.PENDENT, AnotacioEstatEnumDto.REBUTJADA)).contains(anotacio.getEstat())
						&& anotacio.getExpedient() == null) ) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot reprocessar perquè està en estat " + anotacio.getEstat() + (anotacio.getExpedient() != null ? " i té un expedient associat" : ""));
		}		
		
		// Consulta l'anotació
		AnotacioRegistreId idWs = new AnotacioRegistreId();
		idWs.setIndetificador(anotacio.getIdentificador());
		idWs.setClauAcces(anotacio.getDistribucioClauAcces());
		logger.debug("Consultant l'anotació " + idWs.getIndetificador() + " i clau " + idWs.getClauAcces());

		// Consulta la anotació a Distribucio
		AnotacioRegistreEntrada anotacioRegistreEntrada = null;
		try {
			anotacioRegistreEntrada = this.consulta(idWs);
		} catch(Exception e) {
			String errMsg = "Error consultant l'anotació " + idWs.getIndetificador() + " i clau " + idWs.getClauAcces() + ": " + e.getMessage();
			logger.error(errMsg, e);
			throw new Exception(errMsg, e);
		}			

		// Processa i comunica l'estat de processada 
		try {
			logger.debug("Rerocessant l'anotació " + idWs.getIndetificador() + ".");
			anotacio.setEstat(AnotacioEstatEnumDto.PENDENT);
			anotacio.setErrorProcessament(null);
			// Torna a consultar si està relacionat amb un tipus d'expedient i/o expedient
			this.updateRelacioExpedientTipus(anotacio);
			// Reprocessa l'anotació
			this.processarAnotacio(idWs, anotacioRegistreEntrada, anotacio, backofficeUtils);//aquí ja es comunica l'error i el canvi d'estat a Distribució
		} catch (Throwable e) {
			String errorProcessament = "Error processant l'anotació " + idWs.getIndetificador() + ":" + e;
			String traçaCompleta = ExceptionUtils.getStackTrace(e);
			anotacio.setErrorProcessament(errorProcessament.concat(traçaCompleta));
			anotacio.setEstat(AnotacioEstatEnumDto.ERROR_PROCESSANT);
			throw new Exception(errorProcessament + ": "
					+ ExceptionUtils.getRootCauseMessage(e), ExceptionUtils.getRootCause(e));
		}
		return anotacio;
	}

	/** Processa el mapeig amb Sistra2 de les variables, documents i adjunts. Retorna el resultat amb els
	 * mapejos i si hi ha hagut errors.
	 * 
	 * @param expedientTipus
	 * @param anotacio
	 * @param ambContingut Indica si pels documents i adjunts mapejats s'ha de recuperar el contingut.
	 * @return
	 */
	public AnotacioMapeigResultatDto getMapeig(ExpedientTipus expedientTipus, Anotacio anotacio, boolean ambContingut) {
		AnotacioMapeigResultatDto resultat = new AnotacioMapeigResultatDto();
		resultat.setAnotacioNumero(anotacio.getIdentificador());
		resultat.setDades(this.getDadesInicials(expedientTipus, anotacio, resultat.getErrorsDades()));
		resultat.setDocuments(this.getDocumentsInicials(expedientTipus, anotacio, ambContingut, resultat.getErrorsDocuments()));
		resultat.setAdjunts(this.getDocumentsAdjunts(expedientTipus, anotacio, ambContingut, resultat.getErrorsAdjunts()));
		return resultat;
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
	 * @throws Exception 
	 */
	public Map<String, Object> getDadesInicials(ExpedientTipus expedientTipus, Anotacio anotacio, Map<String, String> errors)  {
		
		logger.debug("Mapeig de documents SISTRA2-Helium per l'anotació " + anotacio.getIdentificador() + 
												" i el tipus d'expedient " + expedientTipus.getCodi() + " - " + expedientTipus.getNom() + 
												" de l'entorn " + expedientTipus.getEntorn().getCodi() + " - " + expedientTipus.getEntorn().getNom() + ": {");

		// Resposta<codi_helium, valor_helium>
		Map<String, Object> resposta = new HashMap<String, Object>();

		// Llista d'annexos tipus XML
		Map<String, AnotacioAnnex> annexos = new HashMap<String, AnotacioAnnex>();
		String identificador = null;
		for (AnotacioAnnex annex : anotacio.getAnnexos()) {
			if (annex.getNom().toLowerCase().endsWith(".xml")) { // Filtra només XML's
				identificador = FilenameUtils.removeExtension(annex.getNom());
				annexos.put(identificador, annex);
			}
		}
		Map<String, Camp> camps = new HashMap<String, Camp>();
		if (ExpedientTipusTipusEnumDto.FLOW.equals(expedientTipus.getTipus())) {
			// Obtenir una llista de variables inicials de la tasca
			for (CampTasca campTasca : getCampsStartTask(expedientTipus)) {
				camps.put(campTasca.getCamp().getCodi(), campTasca.getCamp());
			}
		} else {//En el cas de tipusExp per ESTATS es fa diferent
			List<Camp> campsList = campRepository.findByExpedientTipusOrderByCodiAsc(expedientTipus);
			for(Camp camp: campsList) {
				camps.put(camp.getCodi(), camp);
			}	
		}
		// Obtenir mapejos
		List<MapeigSistra> mapejosSistra = mapeigSistraRepository.findByFiltre(expedientTipus.getId(), TipusMapeig.Variable);

		// Llista de formularis <annex_titol, formulari>
		Map<String, Formulario> formularis = new HashMap<String, Formulario>();
		// Per cada mapeig Sistra
		for (MapeigSistra mapeig : mapejosSistra) {
			
			
			// Codi Sistra = ANNEX_TITOL.CAMP_CODI
			String[] codiSistra = mapeig.getCodiSistra().split("\\.");
			if (codiSistra.length >= 2) {
				String annexTitol = codiSistra[0];
				String campoId = codiSistra[1];
				Camp camp = camps.get(mapeig.getCodiHelium());
				
				if (annexos.containsKey(annexTitol) && camp != null) {
					logger.debug("{" + mapeig.getCodiHelium() + " (" + camp.getTipus() + ") - ");
					// Recupera la informació del formulari de l'annex
					Formulario formulari = formularis.get(annexTitol);
					if (formulari == null) {
						try {
							formulari = this.getFormulario(annexos.get(annexTitol));
						} catch (Exception e) {
							logger.error("Error obtenint el formulari \"" + annexTitol + "\" pel mapeig " + mapeig + " del tipus d'expedient " + expedientTipus.getCodi() + " i l'anotació " + anotacio.getIdentificador(), e);
							errors.put(mapeig.toString(), "Error obtenint el formulari: " + e.getMessage());
						}
						formularis.put(annexTitol, formulari);
					}
					if (formulari != null) {
						for (Campo campo : formulari.getCampos()) {
							if (campo.getId().equals(campoId)) {
								logger.debug(campo.getId() + "(" + campo.getTipo() + "): ");
								Object valorHelium;
								try {
									valorHelium = this.valorVariableHelium(campo, camp);
									resposta.put(mapeig.getCodiHelium(), valorHelium);
									logger.debug(valorHelium != null ? valorHelium.toString() : "");
								} catch (Exception ex) {
									logger.error("Error en el mapeig de variable SISTRA2 " + expedientTipus.getCodi() + " " + mapeig.getCodiHelium() + "-" + mapeig.getCodiSistra() + " de l'anotació " + anotacio.getIdentificador() + ": " + ex.getMessage(), ex);
									errors.put(mapeig.toString(), ex.getMessage());
								}
								break;
							}
						}
					}
				}
			}
			logger.debug("}");			
		}
		return resposta;
	}
	
	
	private Object valorVariableHelium(Campo campo, Camp camp) throws Exception {
		Object valorHelium = null;
		if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
			if (SISTRA2_CAMP_FORM_LISTA.equals(campo.getTipo())) {
				// Camp registre
				
				if (camp.isMultiple()) {
					Object[][] resposta = new Object[campo.getElementos() != null ? campo.getElementos().size() : 0][];
					for (int i = 0; i < resposta.length; i++) {
						Elemento elemento = campo.getElementos().get(i);
						Object[] filaResposta = new Object[camp.getRegistreMembres().size()];
						for (int j = 0; j < filaResposta.length && j < elemento.getCampo().size(); j++) {
							Camp campRegistre = camp.getRegistreMembres().get(j).getMembre();
							Campo campoElemento = elemento.getCampo().get(j);
							filaResposta[j] = valorPerHeliumSimple(getValorSistra(campoElemento, 0), campRegistre);
						}
						resposta[i] = filaResposta;
					}
					valorHelium = resposta;
				} else {
					Object[] resposta = new Object[camp.getRegistreMembres().size()];
					Elemento elemento = campo.getElementos().get(0);
					for (int i = 0; i < resposta.length && i < elemento.getCampo().size(); i++) {
						Camp campRegistre = camp.getRegistreMembres().get(i).getMembre();
						Campo campoElemento = elemento.getCampo().get(i);
						resposta[i] = valorPerHeliumSimple(getValorSistra(campoElemento, 0), campRegistre);
					}
					valorHelium = resposta;
				}
			} else {
				throw new Exception("No s'ha pogut mapejar el camp " + camp.getCodi() + " de tipus registre: el camp de SISTRA2 \"" + campo.getId() +
						"\" es de tipus " + campo.getTipo() + " i no es pot mapejar contra un registre");
			}
		} else {
			// Camp
			if (camp.isMultiple()) {
				// Multiple 
				Object[] valorsHelium = (Object[]) Array.newInstance(camp.getJavaClass(), campo.getValores().size());
				for (int i = 0; i < campo.getValores().size(); i++) {
					valorsHelium[i] = valorPerHeliumSimple(getValorSistra(campo, i), camp);
				}
				valorHelium = valorsHelium;
			} else {
				// Simple
				valorHelium = valorPerHeliumSimple(getValorSistra(campo, 0), camp);
			}
		}
		return valorHelium;
	}
	
	// Llegeix el valor depenent de l'índex i de si el camp és simple o compost.
	private String getValorSistra(Campo campo, int index) {
		String valor = null;
		if (campo != null 
				&& campo.getValores().size() > 0
				&& index < campo.getValores().size()) {
			if (SISTRA2_CAMP_FORM_COMPUESTO.equals(campo.getTipo())) {
				// Valor a l'atribut "codigo", per exemple: <CAMPO id="id" tipo="compuesto"><VALOR codigo="C1">C1 - Text</VALOR></CAMPO>
				valor = campo.getValores().get(index).getCodigo();
			} else if (SISTRA2_CAMP_FORM_MULTIVALUADO.equals(campo.getTipo())) {
				Valor vs2 = campo.getValores().get(index);
				valor = vs2.getCodigo() != null ? vs2.getCodigo() : vs2.getValue();				
			} else {
				// Valor de tipus "simple", per exemple <CAMPO id="id" tipo="simple"><VALOR>Text</VALOR></CAMPO>
				valor = campo.getValores().get(index) != null? 
						campo.getValores().get(index).getValue() 
						: null;
			}
		}
		return valor;
	}

	private Object valorPerHeliumSimple(String valor, Camp camp) {
		try {
			if (camp == null) {
			} else if (camp.getTipus().equals(TipusCamp.DATE)) {
				return new SimpleDateFormat("dd/MM/yyyy").parse(valor);
			} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
				return new Boolean(valor);
			} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
				Object preu = null;
				try {
					preu = new BigDecimal(valor);
				} catch(Exception e) {
					// No compleix amb el format "0.##", es prova amb el format #,##0.###
					DecimalFormat df = new DecimalFormat("#,##0.###");
					preu = new BigDecimal(df.parse(valor).doubleValue());
				}
				return preu;
			} else if (camp.getTipus().equals(TipusCamp.INTEGER)) {
				return new Long(valor);
			} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
				return new Double(valor);
			}
			return valor;
		} catch (Exception ex) {
			if (camp != null) {
				logger.error("Error en el mapeig de camp [codi=" + camp.getCodi() + 
						", etiqueta=" + camp.getEtiqueta() + ", tipus=" + camp.getTipus() +
						" pel valor " + valor + ":" + ex.getMessage());
			}
			return null;
		}
	}

	private Formulario getFormulario(AnotacioAnnex anotacioAnnex) throws Exception {
		Formulario formulario = null;
		if (anotacioAnnex != null) {
			try {	
				BackofficeSistra2Utils sistraUtils = new BackofficeSistra2UtilsImpl();
				byte[] contingut = null;
				if (anotacioAnnex.getContingut() != null) {
					contingut = anotacioAnnex.getContingut();
				} else if (anotacioAnnex.getUuid() != null){
					// Recupera el contingut de l'Arxiu
					es.caib.plugins.arxiu.api.Document document = pluginHelper.arxiuDocumentInfo(
							anotacioAnnex.getUuid(),
							null,
							true,
							anotacioAnnex.getFirmaTipus() != null);
					if (document != null && document.getContingut() != null)
						contingut = document.getContingut().getContingut();
				}
				if (contingut != null)
					formulario = sistraUtils.parseXmlFormulario(contingut);
				else 
					logger.error("No s'ha pogut consultar el contingut pel document SISTRA2 " + anotacioAnnex.getTitol() + " de l'anotacio " + anotacioAnnex.getAnotacio().getId() + " " + anotacioAnnex.getAnotacio().getIdentificador() + " perquè el contingut és null");
			} catch (Exception e) {
				String errMsg ="Error obtenint les dades del formulari per l'annex de Sistra2 " + anotacioAnnex.getTitol() + " de l'anotació " + anotacioAnnex.getAnotacio().getId() + " " + anotacioAnnex.getAnotacio().getIdentificador() + " " + anotacioAnnex.getAnotacio().getExtracte() + ": " + e.getMessage();
				logger.error(errMsg, e);
				throw new Exception(errMsg, e);
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

	public Map<String, DadesDocumentDto> getDocumentsInicials(ExpedientTipus expedientTipus, Anotacio anotacio, boolean ambContingut, Map<String, String> errors) {
		
		Map<String, DadesDocumentDto> resposta = new HashMap<String, DadesDocumentDto>();
		List<MapeigSistra> mapeigsSistra = mapeigSistraRepository.findByFiltre(expedientTipus.getId(), TipusMapeig.Document);
		if (mapeigsSistra.size() == 0)
			return resposta;

		List<DocumentDto> documents = getDocuments(expedientTipus);
		logger.debug("Mapeig de documents SISTRA2-Helium per l'anotació " + anotacio.getIdentificador() + 
												" i el tipus d'expedient " + expedientTipus.getCodi() + " - " + expedientTipus.getNom() + 
												" de l'entorn " + expedientTipus.getEntorn().getCodi() + " - " + expedientTipus.getEntorn().getNom() + ": {");
		for (MapeigSistra mapeig : mapeigsSistra){
			logger.debug("{" + mapeig.getCodiHelium() + " - ");
			DocumentDto docHelium = null;
			for (DocumentDto document : documents){
				if (document.getCodi().equalsIgnoreCase(mapeig.getCodiHelium())){
					docHelium = document;
					break;
				}
			}
			try {
				if (docHelium != null) {
					DadesDocumentDto document = documentSistra(anotacio, mapeig, docHelium, ambContingut, errors);
					if (document != null) {
						resposta.put(mapeig.getCodiHelium(), document);
						logger.debug(document.getCodi());
					}
				}
			} catch (Exception ex) {
				logger.error("Error en el mapeig de document SISTRA2 " + expedientTipus.getCodi() + " " + mapeig.getCodiHelium() + "-" + mapeig.getCodiSistra() + " de l'anotació " + anotacio.getIdentificador() + ": " + ex.getMessage(), ex);
				errors.put(mapeig.toString(), ex.getMessage());
			}
			logger.debug("}");
		}
		return resposta;		
	}
	
	/** Mètode per consultar el document a partir de la informació del mapeig del document o l'adjunt.
	 * 
	 * @param anotacio Anotació per la qual s'està fent el mapeig.
	 * @param mapeig Informació del mapeig del document o de l'adjunt.
	 * @param varHelium Codi de la variable Helium pel mapeig.
	 * @param errors Mapeig d'errors per tenir informació dels possibles errors.
	 * @param ambContingut Indica si recuperar el contingut o no del document.
	 * @return Retorna les dades del document.
	 * @throws Exception Error si no s'ha pogut recuperar el contingut ni crear un contingut buit.
	 */
	private DadesDocumentDto documentSistra(
			Anotacio anotacio,
			MapeigSistra mapeig,
			DocumentDto varHelium,
			boolean ambContingut,
			Map<String, String> errors) throws Exception {
		DadesDocumentDto resposta = null;
		String identificador = null;
		String varSistra = mapeig.getCodiSistra();
		// Recorre tots els documents annexos, si hi ha més d'un amb el mateix codi llavors es queda el que tingui extensió .pdf
		AnotacioAnnex document = null;
		for (AnotacioAnnex documentAnotacio: anotacio.getAnnexos()) {
			identificador = FilenameUtils.removeExtension(documentAnotacio.getNom());
			if (varSistra.equalsIgnoreCase(identificador)) {
				if (document == null
						|| documentAnotacio.getNom().toLowerCase().endsWith(".pdf")) {
					document = documentAnotacio;
				}
			}
		}
		if (document != null) {
			resposta = new DadesDocumentDto();
			resposta.setTitol(document.getTitol());
			if (varHelium != null) {
				resposta.setIdDocument(varHelium.getId());
				resposta.setCodi(varHelium.getCodi());
			}
			resposta.setData(anotacio.getData());
			resposta.setArxiuNom(document.getNom());
			resposta.setDocumentValid(document.isDocumentValid());
			resposta.setDocumentError(document.getDocumentError());
			resposta.setAnnexId(document.getId());
			
			resposta.setFirmaTipus(document.getFirmaTipus());

			if (ambContingut) {
				byte[] contingut = document.getContingut();
				Exception exception = null;
				if (ambContingut && document.getContingut() == null &&  document.getUuid() != null) {
					// Recupera el contingut de l'Arxiu
					Document documentArxiu = null;
					try {
						// Consulta la versió imprimible del document.
						documentArxiu = pluginHelper.arxiuDocumentInfo(document.getUuid(), null, true, true);
						contingut = documentArxiu.getContingut() != null? documentArxiu.getContingut().getContingut() : null;
					} catch (Exception e) {
						exception = e;
					}
					if (exception != null) {
						try {
							// Consulta la versió original.
							logger.error("Error consultant el document " + varSistra + " pel mapeig de l'anotació " + anotacio.getIdentificador() + ". Es procedeix a consultar el contingut original.", exception);
							documentArxiu = pluginHelper.arxiuDocumentOriginal(document.getUuid(), null);
						} catch (Exception e) {
							exception = e;
							logger.error("Error consultant l'original del document " + varSistra + " pel mapeig de l'anotació " + anotacio.getIdentificador(), e);
						}
					}
					contingut = documentArxiu != null && documentArxiu.getContingut() != null? 
									documentArxiu.getContingut().getContingut() 
									: null;
					if (contingut == null) {
						errors.put(varSistra, "No s'ha pogut obtenir el contingut imprimible ni original del document.");
						resposta = buildDocumentInvalid(anotacio, document, varHelium, mapeig, exception);
					} else {
						resposta.setArxiuContingut(contingut);
					}
				}
			} else {
				resposta.setUuid(document.getUuid());	
			}
		}
		return resposta;
	}
	
	/** Crea un PDF advertint de l'error i retorna un document invàlid per afegir a l'expeident. 
	 * @param mapeig 
	 * @throws Exception Excepció si no troba el contingut i no pot generar un contingut buit. */
	private DadesDocumentDto buildDocumentInvalid(
				Anotacio anotacio, 
				AnotacioAnnex annex, 
				DocumentDto varHelium, 
				MapeigSistra mapeig,
				Exception exception) throws Exception {

		DadesDocumentDto resposta = new DadesDocumentDto();
			resposta.setTitol(annex.getTitol());
			if (varHelium != null) {
				resposta.setIdDocument(varHelium.getId());
				resposta.setCodi(varHelium.getCodi());
			}
			resposta.setData(anotacio.getData());
			resposta.setArxiuNom(annex.getNom());
		resposta.setDocumentValid(false);
		
		String errMsg = "Error obtenint el document mapegat \"" + annex.getNom() + "\" amb títol \"" + annex.getTitol() + "\" de l'anotació " + anotacio.getIdentificador() + " pel mapeig " + mapeig.toString() + ".";
		resposta.setDocumentError(errMsg + ": " + exception.getClass() + ": " + exception.getMessage());
		
		try {
			// Crea un pdf i el guarda com un annex a l'expedient
			com.lowagie.text.Document document = new com.lowagie.text.Document();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, bos);        
	        document.open();
	        
	        Font fontTitol = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(255, 149, 35));
	        Paragraph p = new Paragraph("Avís d' HELIUM", fontTitol);
	        p.setExtraParagraphSpace(10f);
	        document.add(p);
	        document.add(new Paragraph(errMsg));
	        document.add(new Paragraph("Aquest document és invàlid. Podeu provar de substituir-lo per l'annex de l'anotació " + anotacio.getIdentificador() + " reprocessant el mapeig o editant aquest document amb les opcions de gestió de documents d'Helium."));
	        document.add(new Paragraph("Per a més informació consulteu el responsable del tipus d'expedient."));
	        document.add(new Paragraph("Data de la generació del document: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())));
	        document.add(new Paragraph("Detall de l'error en la consulta del document: "));
	        document.add(new Paragraph(exception.getClass() + ": " + exception.getMessage()));
	        document.add(new Paragraph(ExceptionUtils.getStackTrace(exception)));
			
	        document.close();
	        bos.close();
				
	        resposta.setArxiuContingut(bos.toByteArray());			
		} catch(Exception e) {
			logger.error("Error construïnt el document buit per l'error amb el document \"" + annex.getNom() + "\" de l'anotació " + anotacio.getIdentificador());
			throw new Exception("Hi ha hagut un error en el mapeig i no s'ha pogut generar el contingut buit: " + e.getMessage(), exception);
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

	public List<DadesDocumentDto> getDocumentsAdjunts(ExpedientTipus expedientTipus, Anotacio anotacio, boolean ambContingut, Map<String, String> errors) {

		logger.debug("Mapeig de documents adjunts SISTRA2-Helium per l'anotació " + anotacio.getIdentificador() + 
				" i el tipus d'expedient " + expedientTipus.getCodi() + " - " + expedientTipus.getNom() + 
				" de l'entorn " + expedientTipus.getEntorn().getCodi() + " - " + expedientTipus.getEntorn().getNom() + ": {");

		List<DadesDocumentDto> resposta = new ArrayList<DadesDocumentDto>();

		List<MapeigSistra> mapeigsSistra = mapeigSistraRepository.findByFiltre(expedientTipus.getId(), TipusMapeig.Adjunt);
	
		for (MapeigSistra mapeig : mapeigsSistra){
			logger.debug("{" + mapeig.getCodiSistra());
			if (MapeigSistra.TipusMapeig.Adjunt.equals(mapeig.getTipus())){
				try {
					DadesDocumentDto documentSistra = documentSistra(anotacio, mapeig, null, ambContingut, errors);
					if (documentSistra != null) {
						resposta.add(documentSistra);
					}
				} catch (Exception ex) {
					logger.error("Error en el mapeig d'adjunt SISTRA2 " + expedientTipus.getCodi() + " " + mapeig.getCodiSistra() + " de l'anotació " + anotacio.getIdentificador() + ": " + ex.getMessage(), ex);
					errors.put(mapeig.toString(), ex.getMessage());
				}
			}
			logger.debug("}");
		}		
		return resposta;
	}
	
	/** consulta la llista d'anotacions en estat comunicada i pendents de consultar que no han esgotat
	 * els reintents. 
	 * 
	 * @param maxReintents Indica el màxim de reintents que pot realitzar-se per l'anotació.
	 * @param maxAnotacions Indica el nombre màxim de resultats.
	 * @return
	 */
	public List<Anotacio> findPendentConsultar(int maxReintents, int maxAnotacions) {
		
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(maxAnotacions);
		paginacioParams.afegirOrdre("dataRecepcio", OrdreDireccioDto.ASCENDENT);
		
		Page<Anotacio> pagina = anotacioRepository.findAnotacionsPendentConsultarPaged(
				maxReintents, 
				paginacioHelper.toSpringDataPageable(paginacioParams));

		return pagina.getContent();

	}

	/** Incorpora l'annex a l'expedient segons el resultat. Si s'ha pogut moure
	 * llavors crea un nou document si no està mapejat com a Sistra.
	 * 
	 * {@inheritDoc}
	 * @param isSistra Si l'expedient no és sistra llavors crearà un annex per cada annex mogut.
	 * @param anotacio 
	 * @param expedient 
	 * @param annex 
	 * @param resultat 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void incorporarAnnex(boolean isSistra, Expedient expedient, Anotacio anotacio, AnotacioAnnex annex, ArxiuResultat resultat) {
		
		ArxiuResultatAnnex resultatAnnex = null;
		// El títol contindrà el número de l'anotació
		String adjuntTitol = anotacio.getIdentificador() + "/" + annex.getNom();
		logger.debug("Incorporant l'annex (annex=" + adjuntTitol + ") a l'expedient " + expedient.getIdentificador());
		try {
			byte[] contingut = null;
			String arxiuUuid = null;
			if (expedient.isArxiuActiu()) {
				resultatAnnex = resultat.getResultatAnnex(annex.getUuid());
				// Consulta si s'acaba de moure
				switch(resultatAnnex.getAccio()) {
				case ERROR:
					annex.setEstat(AnotacioAnnexEstatEnumDto.PENDENT);
					annex.setError(resultatAnnex.getErrorCodi() + " - " + resultatAnnex.getErrorMessage());
					break;
				case EXISTENT:
				case MOGUT:
					annex.setEstat(AnotacioAnnexEstatEnumDto.MOGUT);
					annex.setUuid(resultatAnnex.getIdentificadorAnnex());
					annex.setError(null);
					arxiuUuid = annex.getUuid();
				}
				if (arxiuUuid != null) {
					// Actualitza l'uuid a tots els documents que fan referència a l'annex que poden contenir l'uuid anterior
					for (DocumentStore ds : documentStoreRepository.findByAnnexId(annex.getId())) {
						ds.setArxiuUuid(arxiuUuid);
					}				
				}
			} else {
				Annex a = new Annex();
				a.setUuid(annex.getUuid());
				resultatAnnex = new ArxiuResultatAnnex();
				resultat.addResultatAnnex(a, resultatAnnex);
				// Recupera el contingut del document i crea un document a Helium
				contingut = annex.getContingut();
				if (contingut == null) {
					// Recupera el contingut de l'Arxiu
					Document documentArxiu = pluginHelper.arxiuDocumentInfo(annex.getUuid(), null, true, true);
					contingut = documentArxiu.getContingut() != null? documentArxiu.getContingut().getContingut() : null;
				}
				annex.setEstat(AnotacioAnnexEstatEnumDto.MOGUT);					
				// Posa el resultat per evitar error
				resultatAnnex.setAccio(AnnexAccio.MOGUT);
			}
			if (	!isSistra 
					&& AnotacioAnnexEstatEnumDto.MOGUT.equals(annex.getEstat())) {
				// Crea un document a Helium 
				Long documentStoreId = documentHelper.crearDocument(
						null, 
						expedient.getProcessInstanceId(), 
						null, 
						annex.getNtiFechaCaptura(), 
						true, 
						annex.getTitol(), 
						annex.getNom(), 
						contingut, 
						arxiuUuid,
						annex.getTipusMime(), 
						expedient.isArxiuActiu() && annex.getFirmaTipus() != null,  
						false, //firmaSeparada, 
						null, //firmaContingut, 
						annex.getNtiOrigen(), 
						annex.getNtiEstadoElaboracion(), 
						annex.getNtiTipoDocumental(), 
						annex.getNtiOrigen() != null ? annex.getNtiOrigen().toString() : null,
						annex.isDocumentValid(),
						annex.getDocumentError(),
						annex.getId(),
						null).getId();
				annex.setDocumentStoreId(documentStoreId);
			}
		} catch(Exception e) {
			annex.setError("Error incorporant l'annex a l'expedient: " + e.getMessage());
			annex.setEstat(AnotacioAnnexEstatEnumDto.PENDENT);
			if (resultatAnnex != null) {
				resultatAnnex.setAccio(AnnexAccio.ERROR);
				resultatAnnex.setErrorCodi(-1);
				resultatAnnex.setException(e);
				resultatAnnex.setErrorMessage("Error recuperant el contingut de l'annex: " + e.getMessage());
			}
		}
	}

	private List<Long> anotacionsPendentsEnProces = Collections.synchronizedList(new ArrayList<Long>());
	
	public boolean isProcessant(Long anotacioId) {
		return anotacionsPendentsEnProces.contains(anotacioId);
	}

	public void setProcessant(Long anotacioId, boolean processant) {
		if (processant) {
			if (!anotacionsPendentsEnProces.contains(anotacioId)) {
				anotacionsPendentsEnProces.add(anotacioId);
			}
		} else {
			anotacionsPendentsEnProces.remove(anotacioId);
		}
	}


	private static final Logger logger = LoggerFactory.getLogger(DistribucioHelper.class);
}
