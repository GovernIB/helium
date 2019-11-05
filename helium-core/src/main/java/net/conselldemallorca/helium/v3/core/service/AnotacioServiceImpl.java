/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Arrays;
import es.caib.distribucio.backoffice.utils.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.ArxiuResultatAnnex;
import es.caib.distribucio.backoffice.utils.BackofficeUtils;
import es.caib.distribucio.backoffice.utils.BackofficeUtilsImpl;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
import es.caib.plugins.arxiu.api.Document;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DistribucioHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioInteressat;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAnnexEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;

/**
 * Implementació del servei per a gestionar anotacions de distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AnotacioServiceImpl implements AnotacioService, ArxiuPluginListener {

	@Resource
	private EntornHelper entornHelper;
	@Autowired
	private DistribucioHelper distribucioHelper;
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private AnotacioRepository anotacioRepository;
	@Resource
	private AnotacioAnnexRepository anotacioAnnexRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private InteressatRepository interessatRepository;	
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Resource
	private PluginHelper pluginHelper;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(
			Long entornId,
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les anotacions per datatable (" +
				"anotacioFiltreDto=" + filtreDto + ")");

		// Llista d'expedients tipus amb permís de relacionar
		List<Long> expedientTipusIdsPermesos = null;
		// Pot veure:
		// - Totes les anotacions si és administrador d'Helium
		// - Les anotacions dels tipus d'expedient amb permís de relacionar en el cas de no ser-ho
		// - Les anotacions d'un expedient en el cas de passar-ho com a filtre
		// Si el filtre especifica l'id de l'expedient des de la pipella d'anotacions de l'expedient
		if (filtreDto.getExpedientId() != null) {
			// Comprova que pugui llegir l'expedient pel cas de la pipella d'anotacions de l'expedient
			expedientHelper.getExpedientComprovantPermisos(filtreDto.getExpedientId(), true, false, false, false);
		} else {
			// Comporova que sigui administrador o recupera els tipus permesos per la vista d'anotacions
			if (!usuariActualHelper.isAdministrador()) {
				expedientTipusIdsPermesos = expedientTipusHelper.findIdsAmbPermisos(
						entornHelper.getEntorn(entornId),
						new Permission[] {
								ExtendedPermission.RELATE
						});
				if (expedientTipusIdsPermesos.isEmpty())
					expedientTipusIdsPermesos.add(0L);
			}
		}
		
		Date dataFinal = null;
		if (filtreDto.getDataFinal() != null) {
			// Corregeix la data final per arribar a les 00:00:00h del dia següent.
			Calendar c = new GregorianCalendar();
			c.setTime(filtreDto.getDataFinal());
			c.add(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			dataFinal = c.getTime();
		}
		Page<Anotacio> page = anotacioRepository.findAmbFiltrePaginat(
				filtreDto.getCodiProcediment() == null || filtreDto.getCodiProcediment().isEmpty(),
				filtreDto.getCodiProcediment(),
				filtreDto.getCodiAssumpte() == null || filtreDto.getCodiAssumpte().isEmpty(),
				filtreDto.getCodiAssumpte(),
				filtreDto.getNumeroExpedient() == null || filtreDto.getNumeroExpedient().isEmpty(),
				filtreDto.getNumeroExpedient(),
				filtreDto.getNumero() == null || filtreDto.getNumero().isEmpty(),
				filtreDto.getNumero(),
				filtreDto.getExtracte() == null || filtreDto.getExtracte().isEmpty(),
				filtreDto.getExtracte(),
				filtreDto.getDataInicial() == null,
				filtreDto.getDataInicial(),
				dataFinal == null,
				dataFinal,
				filtreDto.getEstat() == null,
				filtreDto.getEstat(),
				filtreDto.getExpedientTipusId() == null,
				filtreDto.getExpedientTipusId(),
				filtreDto.getExpedientId() == null,
				filtreDto.getExpedientId(),
				expedientTipusIdsPermesos == null,
				expedientTipusIdsPermesos == null? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesos,
				paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams));
		
		PaginaDto<AnotacioListDto> pagina = paginacioHelper.toPaginaDto(page, AnotacioListDto.class);

		return pagina;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public AnotacioDto findAmbId(Long id) {
		logger.debug(
				"Consultant l'anotació amb id (" +
				"id=" + id +  ")");
		Anotacio anotacio = anotacioRepository.findOne(id);
		
		// Comprovar permís de lectura
		this.comprovaPermisLectura(anotacio);
		
		if (anotacio == null) {
			throw new NoTrobatException(Anotacio.class, id);
		}
		AnotacioDto dto = conversioTipusHelper.convertir(
				anotacio,
				AnotacioDto.class);
		return dto;	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void rebutjar(Long anotacioId, String observacions) {
		logger.debug(
				"Rebutjant petició d'anotació de registre (" +
				"anotacioId=" + anotacioId + ", " +
				"observacions=" + observacions + ")");
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que no està processada ni rebutjada
		if (! AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot rebutjar perquè està en estat " + anotacio.getEstat());
		}

		// Canvia l'estat del registre a la BBDD
		anotacio.setEstat(AnotacioEstatEnumDto.REBUTJADA);
		anotacio.setRebuigMotiu(observacions);
		anotacio.setDataProcessament(new Date());
		
		// Notifica el nou estat a Distribucio
		try {
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setClauAcces(anotacio.getDistribucioClauAcces());
			anotacioRegistreId.setIndetificador(anotacio.getDistribucioId());

			distribucioHelper.canviEstat(
					anotacioRegistreId,
					Estat.REBUTJADA,
					observacions);
		} catch (Exception e) {
			String errMsg = "Error comunicant l'estat de rebutjada a Distribucio:" + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AnotacioDto updateExpedient(Long anotacioId, Long expedientTipusId, Long expedientId) {
		logger.debug(
				"Actualitzant la petició d'anotació de registre (" +
				"anotacioId=" + anotacioId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"expedientId=" + expedientId + ")");
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que no està processada ni rebutjada
		if (! AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot actualitzar perquè està en estat " + anotacio.getEstat());
		}
		// Actualitza l'expedient tipus
		ExpedientTipus expedientTipus = null;
		Expedient expedient = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
			if (expedientId != null)
				expedient = expedientRepository.findOne(expedientId);
		}
		anotacio.setExpedientTipus(expedientTipus);
		anotacio.setExpedient(expedient);

		return conversioTipusHelper.convertir(anotacio, AnotacioDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AnotacioDto incorporarExpedient(
			Long anotacioId, 
			Long expedientTipusId, 
			Long expedientId, 
			boolean associarInteressats) {
		logger.debug(
				"Incorporant la petició d'anotació de registre a un expedient(" +
				"anotacioId=" + anotacioId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"expedientId=" + expedientId + ")");
				
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que està pendent
		if (! AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot actualitzar perquè està en estat " + anotacio.getEstat());
		}

		// Recupera la informació del tipus d'expedient i l'expedient
		ExpedientTipus expedientTipus = null;
		Expedient expedient = null;
		expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		expedient = expedientRepository.findOne(expedientId);
		if (expedientTipus == null || expedient == null)
			throw new RuntimeException("No es pot incorporar l'anotació " + 
						anotacioId + " a l'expedient " + expedientId + " i tipus d'expedient " + 
						expedientTipusId + " perquè no s'ha pogut recuperar la informació associada.");
		anotacio.setExpedientTipus(expedientTipus);
		anotacio.setExpedient(expedient);
		
		// Si l'expedient està integrat amb l'arxiu s'utlitzarà la llibreria d'utilitats de backoffice de Distribució per moure tots els annexos i incorporar
		// la informació dels interessats.
		ArxiuResultat resultat = null;
		if (expedient.isArxiuActiu()) {
			// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació de l'anotació directament a l'expedient dins l'Arxiu
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			BackofficeUtils backofficeUtils = new BackofficeUtilsImpl(pluginHelper.getArxiuPlugin());
			// Posarà els annexos en la carpeta de l'anotació
			backofficeUtils.setCarpeta(anotacio.getIdentificador());
			// S'enregistraran els events al monitor d'integració
			backofficeUtils.setArxiuPluginListener(this);
			// Prepara la consulta a Distribució
			es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId idWs = new AnotacioRegistreId();
			idWs.setClauAcces(anotacio.getDistribucioClauAcces());
			idWs.setIndetificador(anotacio.getDistribucioId());
			try {
				AnotacioRegistreEntrada anotacioRegistreEntrada = distribucioHelper.consulta(idWs);
				resultat = backofficeUtils.crearExpedientAmbAnotacioRegistre(expedientArxiu, anotacioRegistreEntrada);
			} catch(Exception e) {
				String errMsg = "Error consultant l'anotació de registre \"" + anotacio.getIdentificador() + "\" de Distribució:_" + e.getMessage();
				logger.error(errMsg, e);
				throw new SistemaExternException(errMsg, e);
			}
		}
		
		// Associa tots els annexos de l'anotació com annexos de l'expedient
		AnotacioAnnexEstatEnumDto estat;
		DocumentStore documentStore;
		for ( AnotacioAnnex annex : anotacio.getAnnexos() ) {
			
			// Incorpora cada annex de forma separada per evitar excepcions i continuar amb els altres
			this.incorporarAnnex(expedient, anotacio, annex, resultat);						
		}
		
		if (associarInteressats) {
			// Incorpora els interessats a l'expedient
			Interessat interessatEntity;
			for(AnotacioInteressat interessat : anotacio.getInteressats()) {
				// Comprova si ja existeix
				interessatEntity = interessatRepository.findByCodiAndExpedient(
						interessat.getDocumentNumero(), expedient);
				if (interessatEntity == null) {
					// Crea el nou interessat
					logger.debug("Creant l'interessat (interessat=" + interessat + ") a l'expedient " + expedient.getIdentificador());
					interessatEntity = new Interessat(
							interessat.getId(),
							interessat.getDocumentNumero(), // Codi
							interessat.getNom() != null? interessat.getNom() : interessat.getRaoSocial(),
							interessat.getDocumentNumero(),
							interessat.getLlinatge1(), 
							interessat.getLlinatge2(), 
							this.getInteressatTipus(interessat),
							interessat.getEmail(), 
							interessat.getTelefon(),
							expedient,
							interessat.getAdresa() != null && interessat.getCp() != null, // entregaPostal
							EntregaPostalTipus.SENSE_NORMALITZAR,
							interessat.getAdresa(), // adreça línia 1
							null, // linia2,
							interessat.getCp(),
							false, // entregaDeh,
							false // entregaDehObligat
							);
				} else {
					// Actualitza l'interessat existent
					logger.debug("Modificant l'interessat (interessat=" + interessat + ") a l'expedient " + expedient.getIdentificador());
					interessatEntity.setNom(interessat.getNom() != null? interessat.getNom() : interessat.getRaoSocial());
					interessatEntity.setNif(interessat.getDocumentNumero());
					interessatEntity.setLlinatge1(interessat.getLlinatge1());  
					interessatEntity.setLlinatge2(interessat.getLlinatge2());
					interessatEntity.setTipus(this.getInteressatTipus(interessat));
					interessatEntity.setEmail(interessat.getEmail());
					interessatEntity.setTelefon(interessat.getTelefon());
				}
				interessatRepository.saveAndFlush(interessatEntity);	
			}
		}
		
		// Canvia l'estat del registre a la BBDD
		// TODO DANIEL: MARCAR COM A PROCESSADA SI TOTS ELS ANNEXOS S'HAN PASSAT CORRECTAMENT
		anotacio.setEstat(AnotacioEstatEnumDto.PROCESSADA);
		anotacio.setDataProcessament(new Date());
		try {
			// Notifica el nou estat a Distribucio
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setClauAcces(anotacio.getDistribucioClauAcces());
			anotacioRegistreId.setIndetificador(anotacio.getDistribucioId());
			distribucioHelper.canviEstat(
					anotacioRegistreId,
					Estat.PROCESSADA,
					"Anotació incorporada a l'expedient d'Helium " + expedient.getIdentificadorLimitat());
		} catch (Exception e) {
			String errMsg = "Error comunicant l'estat de rebutjada a Distribucio:" + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
				
		return conversioTipusHelper.convertir(
				anotacio, 
				AnotacioDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 * @param anotacio 
	 * @param expedient 
	 * @param annex 
	 * @param resultat 
	 */
	//@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void incorporarAnnex(Expedient expedient, Anotacio anotacio, AnotacioAnnex annex, ArxiuResultat resultat) {
		
		// El títol contindrà el número de l'anotació
		String adjuntTitol = anotacio.getIdentificador() + "/" + annex.getNom();
		logger.debug("Incorporant l'annex (annex=" + adjuntTitol + ") a l'expedient " + expedient.getIdentificador());
		try {
			if (expedient.isArxiuActiu()) {
				// Crea un document al DocumentStore amb la informació de l'annex sense contingut
				ArxiuResultatAnnex resultatAnnex = resultat.getResultatAnnex(annex.getUuid());
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
					// TODO DANIEL: Guardar l'annex com un nou document del document store amb l'uuid i sense contingut
				}
			} else {
				// Recupera el contingut del document i crea un document a Helium
				byte[] contingut = annex.getContingut();
				if (contingut == null) {
					// Recupera el contingut de l'Arxiu
					Document documentArxiu = pluginHelper.arxiuDocumentInfo(annex.getUuid(), null, true, true);
					contingut = documentArxiu.getContingut() != null? documentArxiu.getContingut().getContingut() : null;
				}
				// Crea un document a Helium 
				documentHelper.crearDocument(
						null, 
						expedient.getProcessInstanceId(), 
						null, 
						annex.getNtiFechaCaptura(), 
						true, 
						annex.getTitol(), 
						annex.getNom(), 
						contingut, 
						annex.getTipusMime(), 
						false, //firmat, de moment guardarem sense firma 
						false, //firmaSeparada, 
						null, //firmaContingut, 
						annex.getNtiOrigen(), 
						annex.getNtiEstadoElaboracion(), 
						annex.getNtiTipoDocumental(), 
						annex.getNtiOrigen() != null ? annex.getNtiOrigen().toString() : null);
			}
			annex.setEstat(AnotacioAnnexEstatEnumDto.MOGUT);
		} catch(Exception e) {
			annex.setError("Error incorporant l'annex a l'expedient: " + e.getMessage());
			annex.setEstat(AnotacioAnnexEstatEnumDto.PENDENT);
		}
	}

	
	/** Resol el tipus d'interessat segons la infomració provinent de Distribució */
	private InteressatTipusEnumDto getInteressatTipus(AnotacioInteressat interessat) {
		InteressatTipusEnumDto tipus;
		if ("PERSONA_FISICA".equals(interessat.getTipus()))
				tipus = InteressatTipusEnumDto.FISICA;
		else if ("PERSONA_JURIDICA".equals(interessat.getTipus()))
			tipus = InteressatTipusEnumDto.JURIDICA;
		else if ("ADMINISTRACIO".equals(interessat.getTipus()))
			tipus = InteressatTipusEnumDto.ADMINISTRACIO;
		else {
			logger.error("Error incorporant l'interessat " + interessat + " a l'expedient: " +
					"El tipus d'interessat \"" + interessat.getTipus() + "\" no es reconeix. S'estableix el tipus FISICA");
			tipus = InteressatTipusEnumDto.FISICA;
		}
		return tipus;
	}
	
	/** Mètode per implementar la interfície {@link ArxiuPluginListener} de Distribució per rebre events de quan es crida l'Arxiu i afegir
	 * els logs al monitor d'integracions. 
	 * @param metode
	 * @param parametres
	 * @param correcte
	 * @param error
	 * @param e
	 * @param timeMs
	 */
	@Override
	public void event(String metode, Map<String, String> parametres, boolean correcte, String error, Exception e, long timeMs) {
		
		List<IntegracioParametreDto> parametresMonitor = new ArrayList<IntegracioParametreDto>();
		for (String nom : parametres.keySet())
			parametresMonitor.add(new IntegracioParametreDto(nom, parametres.get(nom)));
		
		if (correcte) {
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU, 
					"Invocació al mètode del plugin d'Arxiu " + metode, 
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					timeMs, 
					(IntegracioParametreDto[]) parametresMonitor.toArray());
		} else {
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU, 
					"Error invocant al mètode del plugin d'Arxiu " + metode, 
					IntegracioAccioTipusEnumDto.ENVIAMENT, 
					timeMs,
					error, 
					e, 
					(IntegracioParametreDto[]) parametresMonitor.toArray());	
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long anotacioId) {
		logger.debug(
				"Esborrant la petició d'anotació de registre (" +
				"anotacioId=" + anotacioId + ")");

		Anotacio anotacio = anotacioRepository.findOne(anotacioId);		
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que està pendent
		if (! AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot esborrar perquè està en estat " + anotacio.getEstat());
		}
		// Esborra l'anotació
		anotacioRepository.delete(anotacio);	
	}


	/** Poden veure el detall de l'anotació:
	 * - Usuaris administradors d'Helium
	 * - Usuaris amb permís de reassignació sobre el tipus d'expedient
	 * - Usuaris amb permís de lectura si l'expedient està processat
	 * 
	 * @param anotacio
	 */
	private void comprovaPermisLectura(Anotacio anotacio) {
		
		boolean isUsuariAdministrador = usuariActualHelper.isAdministrador();
		boolean potReassignar = false;
		boolean potLlegir = false;
		// Si no és administrador d'Helium
		if (!isUsuariAdministrador) {
			if (anotacio.getExpedientTipus() != null) {
				// Comprova si té el permís de reassignar sobre el tipus d'expedient
				potReassignar = expedientTipusHelper.comprovarPermisos(
						anotacio.getExpedientTipus(), 
						null, 
						new Permission[] {
								ExtendedPermission.RELATE
						});
				if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat())) {
					// Comprova si té permís de lectura
					potLlegir = true;
				}
			}
		}
		// Si no és administrador, ni pot reassignar ni llegir llença excepció
		if (!isUsuariAdministrador && !potReassignar && !potLlegir) {
			throw new PermisDenegatException(
					anotacio.getId(), 
					Anotacio.class, 
					new Permission[] {
							BasePermission.READ,
							ExtendedPermission.RELATE
					});
		}
	}

	/** Poden realitzar accions els usuaris:
	 * - Administradors d'Helium
	 * - Amb permís de reassignació sobre el tipus d'expedient 
	 * 
	 * @param anotacio
	 */
	private void comprovaPermisAccio(Anotacio anotacio) {
		
		boolean isUsuariAdministrador = usuariActualHelper.isAdministrador();
		boolean potReassignar = false;
		// Si no és administrador d'Helium
		if (!isUsuariAdministrador) {
			if (anotacio.getExpedientTipus() != null) {
				// Comprova si té el permís de reassignar sobre el tipus d'expedient
				potReassignar = expedientTipusHelper.comprovarPermisos(
						anotacio.getExpedientTipus(), 
						null, 
						new Permission[] {
								ExtendedPermission.RELATE
						});
			}
		}
		// Si no és administrador, ni pot reassignar ni llegir llença excepció
		if (!isUsuariAdministrador && !potReassignar) {
			throw new PermisDenegatException(
					anotacio.getId(), 
					Anotacio.class, 
					new Permission[] {
							BasePermission.READ,
							ExtendedPermission.RELATE
					});
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getAnnexContingut(Long annexId) {
		
		// Recupera l'annex
		AnotacioAnnex annex = anotacioAnnexRepository.findOne(annexId);
		if (annex == null)
			throw new NoTrobatException(AnotacioAnnex.class, annexId);
		// Comprova l'accés
		this.comprovaPermisLectura(annex.getAnotacio());
				
		ArxiuDto arxiu = new ArxiuDto();
		if (annex.getContingut() == null) {
			// Recupera el contingut de l'Arxiu
			es.caib.plugins.arxiu.api.Document document = pluginHelper.arxiuDocumentInfo(
					annex.getUuid(),
					null,
					true,
					annex.getFirmaTipus() != null);
			if (document != null && document.getContingut() != null)
				arxiu.setContingut(document.getContingut().getContingut());
		} else {
			arxiu.setContingut(annex.getContingut());
		}
		arxiu.setNom(annex.getNom());
		arxiu.setTipusMime(annex.getTipusMime());
		
		return arxiu;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ArxiuFirmaDto> getAnnexFirmes(Long annexId) {
		
		 List<ArxiuFirmaDto> firmes = new ArrayList<ArxiuFirmaDto>();
		// Recupera l'annex
		AnotacioAnnex annex = anotacioAnnexRepository.findOne(annexId);
		if (annex == null)
			throw new NoTrobatException(AnotacioAnnex.class, annexId);
		// Comprova l'accés
		this.comprovaPermisLectura(annex.getAnotacio());
				
		// Recupera el contingut de l'Arxiu
		es.caib.plugins.arxiu.api.Document document = pluginHelper.arxiuDocumentInfo(
				annex.getUuid(),
				null,
				true,
				true);
		if (document != null && document.getFirmes() != null) {
			firmes = PluginHelper.toArxiusFirmesDto(document.getFirmes());
		}
		
		return firmes;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AnotacioServiceImpl.class);

}