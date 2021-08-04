/**
 * 
 */
package es.caib.helium.logic.service;

import java.util.ArrayList;
import java.util.Arrays;
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

import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultatAnnex;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultatAnnex.AnnexAccio;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtilsImpl;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.ws.backofficeintegracio.Annex;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.DistribucioHelper;
import es.caib.helium.logic.helper.DocumentHelper;
import es.caib.helium.logic.helper.EntornHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientTipusHelper;
import es.caib.helium.logic.helper.MonitorIntegracioHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.helper.UsuariActualHelper;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.dto.AnotacioAnnexEstatEnumDto;
import es.caib.helium.logic.intf.dto.AnotacioDto;
import es.caib.helium.logic.intf.dto.AnotacioEstatEnumDto;
import es.caib.helium.logic.intf.dto.AnotacioFiltreDto;
import es.caib.helium.logic.intf.dto.AnotacioListDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.helium.logic.intf.dto.ArxiuFirmaDto;
import es.caib.helium.logic.intf.dto.DadesEnviamentDto.EntregaPostalTipus;
import es.caib.helium.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.logic.intf.dto.IntegracioParametreDto;
import es.caib.helium.logic.intf.dto.InteressatTipusEnumDto;
import es.caib.helium.logic.intf.dto.NtiTipoFirmaEnumDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.AnotacioService;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.Anotacio;
import es.caib.helium.persist.entity.AnotacioAnnex;
import es.caib.helium.persist.entity.AnotacioInteressat;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Interessat;
import es.caib.helium.persist.repository.AnotacioAnnexRepository;
import es.caib.helium.persist.repository.AnotacioRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
import es.caib.helium.persist.repository.InteressatRepository;
import es.caib.plugins.arxiu.api.Document;

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
	@Resource
	private DocumentHelper documentHelper;
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
//	@Resource
//	private MessageHelper messageHelper;
//	@Resource
//	private PermisosHelper permisosHelper;
	@Resource
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(Long entornId, AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant les anotacions per datatable (" + "anotacioFiltreDto=" + filtreDto + ")");

		// Llista d'expedients tipus amb permís de relacionar
		List<Long> expedientTipusIdsPermesos = null;
		// Pot veure:
		// - Totes les anotacions si és administrador d'Helium
		// - Les anotacions dels tipus d'expedient amb permís de relacionar en el cas de
		// no ser-ho
		// - Les anotacions d'un expedient en el cas de passar-ho com a filtre
		// Si el filtre especifica l'id de l'expedient des de la pipella d'anotacions de
		// l'expedient
		if (filtreDto.getExpedientId() != null) {
			// Comprova que pugui llegir l'expedient pel cas de la pipella d'anotacions de
			// l'expedient
			expedientHelper.getExpedientComprovantPermisos(filtreDto.getExpedientId(), true, false, false, false);
		} else {
			// Comporova que sigui administrador o recupera els tipus permesos per la vista
			// d'anotacions
			if (!usuariActualHelper.isAdministrador()) {
				expedientTipusIdsPermesos = expedientTipusHelper.findIdsAmbPermisos(entornHelper.getEntorn(entornId),
						new Permission[] { ExtendedPermission.RELATE });
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
				filtreDto.getNumeroExpedient(), filtreDto.getNumero() == null || filtreDto.getNumero().isEmpty(),
				filtreDto.getNumero(), filtreDto.getExtracte() == null || filtreDto.getExtracte().isEmpty(),
				filtreDto.getExtracte(), filtreDto.getDataInicial() == null, filtreDto.getDataInicial(),
				dataFinal == null, dataFinal, filtreDto.getEstat() == null, filtreDto.getEstat(),
				filtreDto.getExpedientTipusId() == null, filtreDto.getExpedientTipusId(),
				filtreDto.getExpedientId() == null, filtreDto.getExpedientId(), expedientTipusIdsPermesos == null,
				expedientTipusIdsPermesos == null ? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesos,
				paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
				paginacioParams.getFiltre(), paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<AnotacioListDto> pagina = paginacioHelper.toPaginaDto(page, AnotacioListDto.class);

		return pagina;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public AnotacioDto findAmbId(Long id) {
		logger.debug("Consultant l'anotació amb id (" + "id=" + id + ")");
		Anotacio anotacio = anotacioRepository.findById(id).get();

		// Comprovar permís de lectura
		this.comprovaPermisLectura(anotacio);

		if (anotacio == null) {
			throw new NoTrobatException(Anotacio.class, id);
		}
		AnotacioDto dto = conversioTipusServiceHelper.convertir(anotacio, AnotacioDto.class);
		return dto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void rebutjar(Long anotacioId, String observacions) {
		logger.debug("Rebutjant petició d'anotació de registre (" + "anotacioId=" + anotacioId + ", " + "observacions="
				+ observacions + ")");

		Anotacio anotacio = anotacioRepository.findById(anotacioId).get();
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que no està processada ni rebutjada
		if (!AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador()
					+ " no es pot rebutjar perquè està en estat " + anotacio.getEstat());
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

			distribucioHelper.canviEstat(anotacioRegistreId, Estat.REBUTJADA, observacions);
		} catch (Exception e) {
			String errMsg = "Error comunicant l'estat de rebutjada a Distribucio:" + e.getMessage();
			logger.warn(errMsg, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AnotacioDto updateExpedient(Long anotacioId, Long expedientTipusId, Long expedientId) {
		logger.debug("Actualitzant la petició d'anotació de registre (" + "anotacioId=" + anotacioId + ", "
				+ "expedientTipusId=" + expedientTipusId + ", " + "expedientId=" + expedientId + ")");

		Anotacio anotacio = anotacioRepository.findById(anotacioId).get();
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que no està processada ni rebutjada
		if (!AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador()
					+ " no es pot actualitzar perquè està en estat " + anotacio.getEstat());
		}
		// Actualitza l'expedient tipus
		ExpedientTipus expedientTipus = null;
		Expedient expedient = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.findById(expedientTipusId).get();
			if (expedientId != null)
				expedient = expedientRepository.findById(expedientId).get();
		}
		anotacio.setExpedientTipus(expedientTipus);
		anotacio.setExpedient(expedient);

		return conversioTipusServiceHelper.convertir(anotacio, AnotacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AnotacioDto incorporarExpedient(Long anotacioId, Long expedientTipusId, Long expedientId,
			boolean associarInteressats, boolean comprovarPermis) {
		logger.debug("Incorporant la petició d'anotació de registre a un expedient(" + "anotacioId=" + anotacioId + ", "
				+ "expedientTipusId=" + expedientTipusId + ", " + "expedientId=" + expedientId + ", "
				+ "associarInteressats=" + associarInteressats + ", " + "comprovarPermis=" + comprovarPermis + ")");

		Anotacio anotacio = anotacioRepository.findById(anotacioId).get();
		// Comprova els permisos
		if (comprovarPermis)
			this.comprovaPermisAccio(anotacio);
		// Comprova que està pendent
		if (!AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador()
					+ " no es pot actualitzar perquè està en estat " + anotacio.getEstat());
		}

		// Recupera la informació del tipus d'expedient i l'expedient
		ExpedientTipus expedientTipus = null;
		Expedient expedient = null;
		expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		expedient = expedientRepository.findById(expedientId).orElse(null);
		if (expedientTipus == null || expedient == null)
			throw new RuntimeException("No es pot incorporar l'anotació " + anotacioId + " a l'expedient " + expedientId
					+ " i tipus d'expedient " + expedientTipusId
					+ " perquè no s'ha pogut recuperar la informació associada.");
		anotacio.setExpedientTipus(expedientTipus);
		anotacio.setExpedient(expedient);

		// Si l'expedient està integrat amb l'arxiu s'utlitzarà la llibreria d'utilitats
		// de backoffice de Distribució per moure tots els annexos i incorporar
		// la informació dels interessats.
		ArxiuResultat resultat = null;
		if (expedient.isArxiuActiu()) {

			// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació
			// de l'anotació directament a l'expedient dins l'Arxiu
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper
					.arxiuExpedientInfo(expedient.getArxiuUuid());
			BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
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
				if (resultat.getErrorCodi() != 0)
					throw new Exception("Error creant l'expedient amb la llibreria d'utilitats de Distribucio: "
							+ resultat.getErrorCodi() + " " + resultat.getErrorMessage());

			} catch (Exception e) {
				String errMsg = "Error consultant l'anotació de registre \"" + anotacio.getIdentificador()
						+ "\" de Distribució:_" + e.getMessage();
				logger.error(errMsg, e);
				throw new SistemaExternException(errMsg, e);
			}
		} else {
			resultat = new ArxiuResultat();
		}

		// Associa tots els annexos de l'anotació com annexos de l'expedient
		for (AnotacioAnnex annex : anotacio.getAnnexos()) {
			// Incorpora cada annex de forma separada per evitar excepcions i continuar amb
			// els altres
			this.incorporarAnnex(expedient, anotacio, annex, resultat);
		}

		if (associarInteressats) {
			// Incorpora els interessats a l'expedient
			Interessat interessatEntity;
			for (AnotacioInteressat interessat : anotacio.getInteressats()) {
				// Comprova si ja existeix
				interessatEntity = interessatRepository.findByCodiAndExpedient(interessat.getDocumentNumero(),
						expedient);
				if (interessatEntity == null) {
					// Crea el nou interessat
					logger.debug("Creant l'interessat (interessat=" + interessat + ") a l'expedient "
							+ expedient.getIdentificador());
					interessatEntity = new Interessat(interessat.getId(), interessat.getDocumentNumero(), // Codi
							interessat.getNom() != null ? interessat.getNom() : interessat.getRaoSocial(),
							interessat.getDocumentNumero(), interessat.getOrganCodi(), // codiDir3
							interessat.getLlinatge1(), interessat.getLlinatge2(), this.getInteressatTipus(interessat),
							interessat.getEmail(), interessat.getTelefon(), expedient,
							interessat.getAdresa() != null && interessat.getCp() != null, // entregaPostal
							EntregaPostalTipus.SENSE_NORMALITZAR, interessat.getAdresa(), // adreça línia 1
							null, // linia2,
							interessat.getCp(), false, // entregaDeh,
							false // entregaDehObligat
					);
				} else {
					// Actualitza l'interessat existent
					logger.debug("Modificant l'interessat (interessat=" + interessat + ") a l'expedient "
							+ expedient.getIdentificador());
					interessatEntity
							.setNom(interessat.getNom() != null ? interessat.getNom() : interessat.getRaoSocial());
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
		anotacio.setEstat(AnotacioEstatEnumDto.PROCESSADA);
		anotacio.setDataProcessament(new Date());
		try {
			// Notifica el nou estat a Distribucio
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setClauAcces(anotacio.getDistribucioClauAcces());
			anotacioRegistreId.setIndetificador(anotacio.getDistribucioId());
			distribucioHelper.canviEstat(anotacioRegistreId, Estat.PROCESSADA,
					"Anotació incorporada a l'expedient d'Helium " + expedient.getIdentificadorLimitat());
		} catch (Exception e) {
			String errMsg = "Error comunicant l'estat de processada a Distribucio:" + e.getMessage();
			logger.warn(errMsg, e);
		}

		// Afegeix el log a l'expedient
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(expedient.getId(),
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.ANOTACIO_RELACIONAR, anotacio.getIdentificador(),
				WorkflowRetroaccioApi.ExpedientRetroaccioEstat.IGNORAR);

		return conversioTipusServiceHelper.convertir(anotacio, AnotacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param anotacio
	 * @param expedient
	 * @param annex
	 * @param resultat
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void incorporarAnnex(Expedient expedient, Anotacio anotacio, AnotacioAnnex annex, ArxiuResultat resultat) {

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
				switch (resultatAnnex.getAccio()) {
				case ERROR:
					annex.setEstat(AnotacioAnnexEstatEnumDto.PENDENT);
					annex.setError(resultatAnnex.getErrorCodi() + " - " + resultatAnnex.getErrorMessage());
					break;
				case EXISTENT:
				case MOGUT:
					annex.setEstat(AnotacioAnnexEstatEnumDto.MOGUT);
					annex.setUuid(resultatAnnex.getIdentificadorAnnex());
					arxiuUuid = annex.getUuid();
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
					contingut = documentArxiu.getContingut() != null ? documentArxiu.getContingut().getContingut()
							: null;
				}
				annex.setEstat(AnotacioAnnexEstatEnumDto.MOGUT);
				// Posa el resultat per evitar error
				resultatAnnex.setAccio(AnnexAccio.MOGUT);
			}
			if (AnotacioAnnexEstatEnumDto.MOGUT.equals(annex.getEstat())) {
				// Crea un document a Helium
				Long documentStoreId = documentHelper.crearDocument(null, expedient.getProcessInstanceId(), null,
						annex.getNtiFechaCaptura(), true, annex.getTitol(), annex.getNom(), contingut, arxiuUuid,
						annex.getTipusMime(), expedient.isArxiuActiu() && annex.getFirmaTipus() != null, false, // firmaSeparada,
						null, // firmaContingut,
						annex.getNtiOrigen(), annex.getNtiEstadoElaboracion(), annex.getNtiTipoDocumental(),
						annex.getNtiOrigen() != null ? annex.getNtiOrigen().toString() : null);
				annex.setDocumentStoreId(documentStoreId);
			}
		} catch (Exception e) {
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
			logger.error(
					"Error incorporant l'interessat " + interessat + " a l'expedient: " + "El tipus d'interessat \""
							+ interessat.getTipus() + "\" no es reconeix. S'estableix el tipus FISICA");
			tipus = InteressatTipusEnumDto.FISICA;
		}
		return tipus;
	}

	/**
	 * Mètode per implementar la interfície {@link ArxiuPluginListener} de
	 * Distribució per rebre events de quan es crida l'Arxiu i afegir els logs al
	 * monitor d'integracions.
	 * 
	 * @param metode
	 * @param parametres
	 * @param correcte
	 * @param error
	 * @param e
	 * @param timeMs
	 */
	@Override
	public void event(String metode, Map<String, String> parametres, boolean correcte, String error, Exception e,
			long timeMs) {

		IntegracioParametreDto[] parametresMonitor = new IntegracioParametreDto[parametres.size()];
		int i = 0;
		for (String nom : parametres.keySet())
			parametresMonitor[i++] = new IntegracioParametreDto(nom, parametres.get(nom));

		if (correcte) {
			monitorIntegracioHelper.addAccioOk(MonitorIntegracioHelper.INTCODI_ARXIU,
					"Invocació al mètode del plugin d'Arxiu " + metode, IntegracioAccioTipusEnumDto.ENVIAMENT, timeMs,
					parametresMonitor);
		} else {
			monitorIntegracioHelper.addAccioError(MonitorIntegracioHelper.INTCODI_ARXIU,
					"Error invocant al mètode del plugin d'Arxiu " + metode, IntegracioAccioTipusEnumDto.ENVIAMENT,
					timeMs, error, e, parametresMonitor);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(Long anotacioId) {
		logger.debug("Esborrant la petició d'anotació de registre (" + "anotacioId=" + anotacioId + ")");

		Anotacio anotacio = anotacioRepository.findById(anotacioId).get();
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que està pendent
		if (!AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador()
					+ " no es pot esborrar perquè està en estat " + anotacio.getEstat());
		}
		// Esborra l'anotació
		anotacioRepository.delete(anotacio);
	}

	/**
	 * Poden veure el detall de l'anotació: - Usuaris administradors d'Helium -
	 * Usuaris amb permís de reassignació sobre el tipus d'expedient - Usuaris amb
	 * permís de lectura si l'expedient està processat
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
				potReassignar = expedientTipusHelper.comprovarPermisos(anotacio.getExpedientTipus(), null,
						new Permission[] { ExtendedPermission.RELATE });
				if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat())) {
					// Comprova si té permís de lectura
					potLlegir = true;
				}
			}
		}
		// Si no és administrador, ni pot reassignar ni llegir llença excepció
		if (!isUsuariAdministrador && !potReassignar && !potLlegir) {
			throw new PermisDenegatException(anotacio.getId(), Anotacio.class,
					new Permission[] { BasePermission.READ, ExtendedPermission.RELATE });
		}
	}

	/**
	 * Poden realitzar accions els usuaris: - Administradors d'Helium - Amb permís
	 * de reassignació sobre el tipus d'expedient
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
				potReassignar = expedientTipusHelper.comprovarPermisos(anotacio.getExpedientTipus(), null,
						new Permission[] { ExtendedPermission.RELATE });
			}
		}
		// Si no és administrador, ni pot reassignar ni llegir llença excepció
		if (!isUsuariAdministrador && !potReassignar) {
			throw new PermisDenegatException(anotacio.getId(), Anotacio.class,
					new Permission[] { BasePermission.READ, ExtendedPermission.RELATE });
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getAnnexContingut(Long annexId) {

		// Recupera l'annex
		AnotacioAnnex annex = anotacioAnnexRepository.findById(annexId)
				.orElseThrow(() -> new NoTrobatException(AnotacioAnnex.class, annexId));
		// Comprova l'accés
		this.comprovaPermisLectura(annex.getAnotacio());

		ArxiuDto arxiu = new ArxiuDto();
		if (annex.getContingut() == null) {
			// Recupera el contingut de l'Arxiu
			es.caib.plugins.arxiu.api.Document document = pluginHelper.arxiuDocumentInfo(annex.getUuid(), null, true,
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
		AnotacioAnnex annex = anotacioAnnexRepository.findById(annexId)
				.orElseThrow(() -> new NoTrobatException(AnotacioAnnex.class, annexId));
		// Comprova l'accés
		this.comprovaPermisLectura(annex.getAnotacio());

		// Recupera el contingut de l'Arxiu
		es.caib.plugins.arxiu.api.Document document = pluginHelper.arxiuDocumentInfo(annex.getUuid(), null, true, true);
		if (document != null && document.getFirmes() != null) {
			firmes = PluginHelper.toArxiusFirmesDto(document.getFirmes());
			// Detalls de les firmes
			for (ArxiuFirmaDto firma : firmes) {

				try {
					byte[] documentContingut = document.getContingut().getContingut();
					byte[] firmaContingut = documentContingut;
					if (NtiTipoFirmaEnumDto.XADES_DET.equals(firma.getTipus())
							|| NtiTipoFirmaEnumDto.CADES_DET.equals(firma.getTipus())) {
						firmaContingut = firma.getContingut();
					}
					List<ArxiuFirmaDetallDto> detalls = pluginHelper.validaSignaturaObtenirDetalls(documentContingut,
							firmaContingut);
					firma.setDetalls(detalls);
				} catch (Exception e) {
					logger.error("Error validant la firma " + firma.getFitxerNom() + " pel document "
							+ document.getNom() + " de l'annex " + annexId + ": " + e.getMessage(), e);
				}
			}
		}

		return firmes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reintentarAnnex(Long anotacioId, Long annexId) throws Exception {

		logger.debug(
				"Reintentant el processament de l'annex (" + "anotacioId=" + anotacioId + ", annexId=" + annexId + ")");

		Anotacio anotacio = anotacioRepository.findById(anotacioId).get();
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		if (anotacio.getExpedient() == null)
			throw new Exception("No es pot processar l'annex perquè l'anotació no té cap expedient associat.");
		Expedient expedient = anotacio.getExpedient();
		AnotacioAnnex annex = null;
		for (AnotacioAnnex a : anotacio.getAnnexos()) {
			if (a.getId().equals(annexId)) {
				annex = a;
				break;
			}
		}
		if (annex == null)
			throw new Exception("No s'ha trobat l'annex amb id " + annexId + " a l'anotació "
					+ anotacio.getIdentificador() + " amb id " + anotacioId);

		// Reintenta la incorporació
		ArxiuResultat resultat = new ArxiuResultat();
		annex.setError(null);

		if (expedient.isArxiuActiu()) {
			// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació
			// de l'anotació directament a l'expedient dins l'Arxiu
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper
					.arxiuExpedientInfo(expedient.getArxiuUuid());
			BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
			// Posarà els annexos en la carpeta de l'anotació
			backofficeUtils.setCarpeta(anotacio.getIdentificador());
			// S'enregistraran els events al monitor d'integració
			backofficeUtils.setArxiuPluginListener(this);
			// Prepara la consulta a Distribució
			es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId idWs = new AnotacioRegistreId();
			idWs.setClauAcces(anotacio.getDistribucioClauAcces());
			idWs.setIndetificador(anotacio.getDistribucioId());
			AnotacioRegistreEntrada anotacioRegistreEntrada;
			try {
				anotacioRegistreEntrada = distribucioHelper.consulta(idWs);
				resultat = backofficeUtils.crearExpedientAmbAnotacioRegistre(expedientArxiu, anotacioRegistreEntrada);
			} catch (Exception e) {
				String errMsg = "Error reprocessant la informació de l'anotació de registre \""
						+ anotacio.getIdentificador() + "\" de Distribució: " + e.getMessage();
				logger.error(errMsg, e);
				throw new SistemaExternException(errMsg, e);
			}
		}
		this.incorporarAnnex(anotacio.getExpedient(), anotacio, annex, resultat);

		logger.debug("Reintent de processament de l'annex (" + "anotacioId=" + anotacioId + " "
				+ anotacio.getIdentificador() + ", annexId=" + annexId + " " + annex.getNom() + ", annexEstat="
				+ annex.getEstat() + ", annexError=" + annex.getError() + ")");

		// Depenent del resultat llença excepció
		boolean error = false;
		StringBuilder errorMsg = new StringBuilder();
		if (resultat.getErrorCodi() != 0) {
			error = true;
			errorMsg.append("Error reprocessant l'anotació l'expedient amb la llibreria d'utilitats de Distribucio: "
					+ resultat.getErrorCodi() + " " + resultat.getErrorMessage());
		}
		ArxiuResultatAnnex resultatAnnex = resultat.getResultatAnnex(annex.getUuid());
		if (AnnexAccio.ERROR.equals(resultatAnnex.getAccio())) {
			error = true;
			errorMsg.append(resultatAnnex.getErrorCodi() + " - " + resultatAnnex.getErrorMessage());
		}
		if (error)
			throw new Exception(errorMsg.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void esborrarAnotacionsExpedient(Long expedientId) {
		logger.debug("Esborrant o deslligant les anotacions de l'expedient (" + "expedientId=" + expedientId + ")");
		List<Anotacio> anotacions = anotacioRepository.findByExpedientId(expedientId);
		for (Anotacio anotacio : anotacions) {
			if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat()))
				// Si les anotacions estan processades s'esborren
				anotacioRepository.delete(anotacio);
			else
				// Altrament les desrelaciona de l'expedient
				anotacio.setExpedient(null);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(AnotacioServiceImpl.class);

}