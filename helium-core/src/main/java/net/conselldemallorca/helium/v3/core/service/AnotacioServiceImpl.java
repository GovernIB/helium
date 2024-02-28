/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Arrays;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultatAnnex;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultatAnnex.AnnexAccio;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtilsImpl;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.rest.client.integracio.domini.Annex;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helper.AlertaHelper;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DistribucioHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExceptionHelper;
import net.conselldemallorca.helium.core.helper.ExpedientDadaHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UnitatOrganitzativaHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.helper.VariableHelper;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Alerta.AlertaPrioritat;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioInteressat;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioAnnexEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioMapeigResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuEstat;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.InteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.MapeigSistraRepository;

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
	private ExceptionHelper exceptionHelper;
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
	private MapeigSistraRepository mapeigSistraRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;
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
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private AlertaHelper alertaHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ExpedientDadaHelper expedientDadaHelper;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
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
		List<String> unitatsOrganitvesCodis = new ArrayList<String>();
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
								ExtendedPermission.RELATE,
								ExtendedPermission.ADMINISTRATION
						});
				if (expedientTipusIdsPermesos.isEmpty())
					expedientTipusIdsPermesos.add(0L);
			}
			
			// Comprova l'accés al tipus d'expedient
			ExpedientTipus expedientTipus = null;
			List<Long> idsUnitatsOrganitzativesAmbPermisos = new ArrayList<Long>();
			if (filtreDto.getExpedientTipusId() != null) {
				expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
						filtreDto.getExpedientTipusId());
				
				//Obté la llista de unitats organitzatives per les quals té permisos (també retornarà les uo filles)
				if(expedientTipus.isProcedimentComu())
					idsUnitatsOrganitzativesAmbPermisos = expedientTipusHelper.findIdsUnitatsOrgAmbPermisosAdminOrRead(filtreDto.getExpedientTipusId());

			} else { //si no hi ha expedientTipus al filtre, hem de buscar totes les UO per las quals es té permís i obtenir els expedinetTipus
				idsUnitatsOrganitzativesAmbPermisos = expedientTipusHelper.findIdsUnitatsOrgAmbPermisosAdminOrRead(null);
				
			}
			for(Long id: idsUnitatsOrganitzativesAmbPermisos) {
				UnitatOrganitzativa uo = unitatOrganitzativaHelper.findById(id);
				if(uo!=null)
					unitatsOrganitvesCodis.add(uo.getCodi());
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
				filtreDto.getUnitatOrganitzativaCodi() == null || filtreDto.getUnitatOrganitzativaCodi().isEmpty(),
				filtreDto.getUnitatOrganitzativaCodi(),
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
				unitatsOrganitvesCodis.isEmpty() ? true : false,
				unitatsOrganitvesCodis,
				paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams));
		
		PaginaDto<AnotacioListDto> pagina = paginacioHelper.toPaginaDto(page, AnotacioListDto.class);
		
		for(AnotacioListDto anotacio: pagina.getContingut()){
			if(distribucioHelper.isProcessant(anotacio.getId())) {
				anotacio.setProcessant(true);
			}
		}
		
		return pagina;
	}

	@Override
	public List<Long> findIdsAmbFiltre(Long entornId, AnotacioFiltreDto filtreDto) {
		logger.debug(
				"Consultant eks identificadors les anotacions per datatable (" +
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
								ExtendedPermission.RELATE,
								ExtendedPermission.ADMINISTRATION
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
		List<Long> ids = anotacioRepository.findIdsAmbFiltre(
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
				expedientTipusIdsPermesos == null? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesos);
		
		return ids;	
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
		distribucioHelper.rebutjar(anotacio, observacions);
	
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
		if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat()) 
				|| AnotacioEstatEnumDto.REBUTJADA.equals(anotacio.getEstat())) {
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
	public AnotacioDto incorporarReprocessarExpedient(
			Long anotacioId, 
			Long expedientTipusId, 
			Long expedientId, 
			boolean associarInteressats,
			boolean comprovarPermis,
			boolean reprocessar) {
		logger.debug(
				"Incorporant la petició d'anotació de registre a un expedient(" +
				"anotacioId=" + anotacioId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"expedientId=" + expedientId + ", " +
				"associarInteressats=" + associarInteressats + ", " +
				"comprovarPermis=" + comprovarPermis + ")");
				
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		// Comprova els permisos
		if (comprovarPermis)
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

		if(reprocessar && expedientTipus.isDistribucioSistra()) {
			AnotacioMapeigResultatDto resultatMapeig = reprocessarMapeigAnotacioExpedient(expedientId, anotacioId);
			if (resultatMapeig.isError()) {
				Alerta alerta = alertaHelper.crearAlerta(
						expedient.getEntorn(), 
						expedient, 
						new Date(), 
						null, 
						resultatMapeig.getMissatgeAlertaErrors());
				alerta.setPrioritat(AlertaPrioritat.ALTA);	
			}
		}
		anotacio.setExpedientTipus(expedientTipus);
		anotacio.setExpedient(expedient);
		
		// Si l'anotació té annexos invàlids o en estat esborrany posa una alerta a l'expedient
		int nAnnexosInvalids = 0;
		int nAnnexosEsborrany = 0;
		for (AnotacioAnnex annex : anotacio.getAnnexos()) {
			if (!annex.isDocumentValid()) {
				nAnnexosInvalids++;
			}
			if (annex.getArxiuEstat() == ArxiuEstat.ESBORRANY) {
				nAnnexosEsborrany++;
			}
		}
		if (nAnnexosInvalids > 0 ) {
			Alerta alerta = alertaHelper.crearAlerta(
					expedient.getEntorn(), 
					expedient, 
					new Date(), 
					null, 
					"L'anotació " + anotacio.getIdentificador() + " té " + nAnnexosInvalids + " annexos invàlids que s'haurien de revisar i reparar.");
			alerta.setPrioritat(AlertaPrioritat.ALTA);
		}

		if (nAnnexosEsborrany > 0 ) {
			Alerta alerta = alertaHelper.crearAlerta(
					expedient.getEntorn(), 
					expedient, 
					new Date(), 
					null, 
					"L'anotació " + anotacio.getIdentificador() + " té " + nAnnexosEsborrany + " en estat esborrany.");
			alerta.setPrioritat(AlertaPrioritat.ALTA);
		}

		// Si l'expedient està integrat amb l'arxiu s'utlitzarà la llibreria d'utilitats de backoffice de Distribució per moure tots els annexos i incorporar
		// la informació dels interessats.
		ArxiuResultat resultat = null;
		if (expedient.isArxiuActiu()) {

			// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació de l'anotació directament a l'expedient dins l'Arxiu
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
			// Posarà els annexos en la carpeta de l'anotació
			backofficeUtils.setCarpeta(ArxiuConversioHelper.revisarContingutNom(anotacio.getIdentificador().replace("/", "_")));
			// S'enregistraran els events al monitor d'integració
			backofficeUtils.setArxiuPluginListener(this);
			// Consulta la informació de l'anotació 
			AnotacioRegistreEntrada anotacioRegistreEntrada = null;
			try {
				es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs = new AnotacioRegistreId();
				idWs.setClauAcces(anotacio.getDistribucioClauAcces());
				idWs.setIndetificador(anotacio.getDistribucioId());
				anotacioRegistreEntrada = distribucioHelper.consulta(idWs);
				
			} catch(Exception e) {
				// Error no controlat consultant la informació de l'expedient, es posa una alerta
				String errMsg = "Error consultant la informació de l'anotació " + 
						anotacio.getIdentificador() + " a l'hora d'incorporar la anotació a l'expedient, és necessari reintentar el processament dels annexos.";
				logger.error(errMsg, e);
				Alerta alerta = alertaHelper.crearAlerta(
						expedient.getEntorn(), 
						expedient, 
						new Date(), 
						null, 
						errMsg);
				alerta.setPrioritat(AlertaPrioritat.ALTA);
				resultat = new ArxiuResultat();
				for (AnotacioAnnex annex : anotacio.getAnnexos()) {
					annex.setEstat(AnotacioAnnexEstatEnumDto.PENDENT);
					annex.setError(errMsg);
				}
			}
			// Processa la informació amb la llibreria d'utilitats per moure els annexos
			if (anotacioRegistreEntrada != null) {
				resultat = backofficeUtils.crearExpedientAmbAnotacioRegistre(expedientArxiu, anotacioRegistreEntrada);
				if (resultat.getErrorCodi() != 0) {
					// Error en el processament
					String errMsg = "S'han produit errors processant l'anotació de Distribucio \"" + anotacio.getIdentificador() + "\" amb la llibreria de distribucio-backoffice-utils: " + resultat.getErrorCodi() + " " + resultat.getErrorMessage();
					logger.error(errMsg, resultat.getException());
					Alerta alerta = alertaHelper.crearAlerta(
							expedient.getEntorn(), 
							expedient, 
							new Date(), 
							null, 
							errMsg + ". Es necessari reintentar el processament.");
					alerta.setPrioritat(AlertaPrioritat.ALTA);
				}
			}
		} else {
			resultat = new ArxiuResultat();
		}
		
		// Si no s'integra amb Sistra2
		for ( AnotacioAnnex annex : anotacio.getAnnexos() ) {			
			// Incorpora cada annex de forma separada per evitar excepcions i continuar amb els altres
			// Si no s'integra amb Sistra crea un document per annex incorportat correctament
			distribucioHelper.incorporarAnnex(
					expedientTipus.isDistribucioSistra(),
					expedient, 
					anotacio, 
					annex, 
					resultat);
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
							interessat.getOrganCodi(), //codiDir3
							interessat.getLlinatge1(), 
							interessat.getLlinatge2(), 
							this.getInteressatTipus(interessat),
							interessat.getEmail(), 
							interessat.getTelefon(),
							expedient,
							false, //interessat.getAdresa() != null && interessat.getCp() != null, // entregaPostalActiva o el nom correcte de la propietat //Forcem false issue #1675
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
			String errMsg = "Error comunicant l'estat de processada a Distribucio:" + e.getMessage();
			logger.warn(errMsg, e);
		}
		
		// Afegeix el log a l'expedient
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.ANOTACIO_RELACIONAR,
				anotacio.getIdentificador()
				);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);

		
		return conversioTipusHelper.convertir(
				anotacio, 
				AnotacioDto.class);
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
		
		IntegracioParametreDto[] parametresMonitor = new IntegracioParametreDto[parametres.size()];
		int i = 0;
		for (String nom : parametres.keySet())
			parametresMonitor[i++] = new IntegracioParametreDto(nom, parametres.get(nom));
		
		if (correcte) {
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU, 
					"Invocació al mètode del plugin d'Arxiu " + metode, 
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					timeMs, 
					parametresMonitor);
		} else {
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU, 
					"Error invocant al mètode del plugin d'Arxiu " + metode, 
					IntegracioAccioTipusEnumDto.ENVIAMENT, 
					timeMs,
					error, 
					e, 
					parametresMonitor);	
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
		// Comprova que no està processada ni rebutjada
		if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat()) 
				|| AnotacioEstatEnumDto.REBUTJADA.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot esborrar perquè està en estat " + anotacio.getEstat());
		}
		if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat()) 
				&& ! AnotacioEstatEnumDto.REBUTJADA.equals(anotacio.getEstat())
				&& ! ! AnotacioEstatEnumDto.COMUNICADA.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot actualitzar perquè està en estat " + anotacio.getEstat());
		}
		// Esborra l'anotació
		anotacioRepository.delete(anotacio);	
	}
	
	@Override
	public AnotacioDto reprocessar(Long anotacioId) throws Exception {
		logger.debug(
				"Reprocessant la petició d'anotació de registre (" +
				"anotacioId=" + anotacioId + ")");
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);		
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que està en error de processament o que està rebutjada o pendent i sense expedient relacionat
		if (!AnotacioEstatEnumDto.ERROR_PROCESSANT.equals(anotacio.getEstat())
				&& !( Arrays.asList(ArrayUtils.toArray(AnotacioEstatEnumDto.PENDENT, AnotacioEstatEnumDto.REBUTJADA)).contains(anotacio.getEstat())
						&& anotacio.getExpedient() == null) ) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot reprocessar perquè està en estat " + anotacio.getEstat() + (anotacio.getExpedient() != null ? " i té un expedient associat" : ""));
		}
		try {
			anotacio = distribucioHelper.reprocessarAnotacio(anotacioId);//aquí es controla l'excepció i el canvi d'estat a Distribució
		} catch(Throwable e) {
			throw new Exception(e);	
		}
		return conversioTipusHelper.convertir(
				anotacio,
				AnotacioDto.class);
	}

	@Override
	@Transactional
	public AnotacioDto marcarPendent(Long anotacioId) throws Exception {

		Anotacio anotacio = anotacioRepository.findOne(anotacioId);		
		
		logger.debug(
				"Marcant com a pendent l'anotació de registre (" +
				"anotacioId=" + anotacioId + ", numero=" + anotacio.getIdentificador() + ")");

		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que està en error de processament
		if (!AnotacioEstatEnumDto.ERROR_PROCESSANT.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot consultar perquè està en estat " + anotacio.getEstat() 
			+ ", ha d'estar en estat de error de processament.");
		}
		anotacio.setEstat(AnotacioEstatEnumDto.PENDENT);
		anotacio.setDataProcessament(null);
		anotacio.setErrorProcessament(null);

		// Es comunica l'estat a Distribucio
		try {
			AnotacioRegistreId idWs = new AnotacioRegistreId();
			idWs.setIndetificador(anotacio.getIdentificador());
			idWs.setClauAcces(anotacio.getDistribucioClauAcces());

			distribucioHelper.canviEstat(
					idWs, 
					es.caib.distribucio.rest.client.integracio.domini.Estat.PENDENT,
					"Es marca l'anotació " + anotacio.getIdentificador() + " com a pendent des de l'estat de processament error.");
		} catch(Exception ed) {
			logger.error("Error comunicant l'error de processament a Distribucio de la petició amb id : " + anotacio.getIdentificador() + ": " + ed.getMessage(), ed);
		}
		
		return conversioTipusHelper.convertir(
				anotacio,
				AnotacioDto.class);
	}

	
	@Override
	@Transactional
	public AnotacioDto reintentarConsulta(Long anotacioId) throws Exception {
		logger.debug(
				"Consultant la petició d'anotació de registre (" +
				"anotacioId=" + anotacioId + ")");

		Anotacio anotacio = anotacioRepository.findOne(anotacioId);		
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		// Comprova que està en error de processament
		if (!AnotacioEstatEnumDto.COMUNICADA.equals(anotacio.getEstat())) {
			throw new RuntimeException("L'anotació " + anotacio.getIdentificador() + " no es pot consultar perquè està en estat " + anotacio.getEstat());
		}
		logger.debug("Fixant els intents de consulta a 0 per l'anotació " + anotacio.getIdentificador());
		distribucioHelper.updateConsulta(anotacioId, 0, null, null);
		return conversioTipusHelper.convertir(
				anotacio,
				AnotacioDto.class);
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
		boolean potRealacionar = false;
		boolean potLlegir = false;
		// Si no és administrador d'Helium
		if (!isUsuariAdministrador) {
			if (anotacio.getExpedientTipus() != null) {
				// Comprova si té el permís de relaqcionar sobre el tipus d'expedient
				potRealacionar = expedientTipusHelper.comprovarPermisos(
						anotacio.getExpedientTipus(), 
						null, 
						new Permission[] {
								ExtendedPermission.RELATE,
								ExtendedPermission.ADMINISTRATION
						});
				if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat())) {
					// Comprova si té permís de lectura
					potLlegir = true;
				}
			}
		}
		// Si no és administrador, ni pot reassignar ni llegir llença excepció
		if (!isUsuariAdministrador && !potRealacionar && !potLlegir) {
			throw new PermisDenegatException(
					anotacio.getId(), 
					Anotacio.class, 
					new Permission[] {
							BasePermission.READ,
							ExtendedPermission.RELATE,
							ExtendedPermission.ADMINISTRATION
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
								ExtendedPermission.RELATE,
								ExtendedPermission.ADMINISTRATION
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
	public ArxiuDto getAnnexContingutVersioImprimible(Long annexId) {
		
		// Recupera l'annex
		AnotacioAnnex annex = anotacioAnnexRepository.findOne(annexId);
		if (annex == null)
			throw new NoTrobatException(AnotacioAnnex.class, annexId);
		// Comprova l'accés
		this.comprovaPermisLectura(annex.getAnotacio());
				
		ArxiuDto arxiu = new ArxiuDto();
		if (annex.getContingut() == null) {
			// Recupera el contingut de l'Arxiu (versió imprimible)
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
	public ArxiuDto getAnnexContingutVersioOriginal(Long annexId) {
		
		// Recupera l'annex
		AnotacioAnnex annex = anotacioAnnexRepository.findOne(annexId);
		if (annex == null)
			throw new NoTrobatException(AnotacioAnnex.class, annexId);
		// Comprova l'accés
		this.comprovaPermisLectura(annex.getAnotacio());			
		ArxiuDto arxiu = new ArxiuDto();
		if (annex.getContingut() == null) {
			Document documentArxiu = pluginHelper.arxiuDocumentOriginal(annex.getUuid(), null);
			if (documentArxiu != null ) {
				byte[] contingut = documentArxiu.getContingut() != null? documentArxiu.getContingut().getContingut() : null;
				if (contingut!=null)
					arxiu.setContingut(contingut);
			}
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
			// Detalls de les firmes 
			for (ArxiuFirmaDto firma : firmes) {
				
				try {
					byte[] documentContingut = document.getContingut().getContingut();
					byte[] firmaContingut = documentContingut;
					if (NtiTipoFirmaEnumDto.XADES_DET.equals(firma.getTipus()) ||
							NtiTipoFirmaEnumDto.CADES_DET.equals(firma.getTipus())) {
						firmaContingut = firma.getContingut();
					}
					List<ArxiuFirmaDetallDto> detalls = pluginHelper.validaSignaturaObtenirDetalls(
							documentContingut, 
							firmaContingut);
					firma.setDetalls(detalls);
				} catch(Exception e) {
					logger.error("Error validant la firma " + firma.getFitxerNom() + " pel document " + document.getNom() + " de l'annex " + annexId + ": " + e.getMessage(), e);
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
				"Reintentant el processament de l'annex (" +
				"anotacioId=" + anotacioId + ", annexId=" + annexId + ")");

		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		if (anotacio.getExpedient() == null)
			throw new Exception("No es pot processar l'annex perquè l'anotació no té cap expedient associat.");
		Expedient expedient = anotacio.getExpedient();
		ExpedientTipus expedientTipus = expedient.getTipus();
		AnotacioAnnex annex = null;
		for(AnotacioAnnex a : anotacio.getAnnexos()){
			if (a.getId().equals(annexId)) {
				annex = a;
				break;
			}
		}
		if (annex == null)
			throw new Exception("No s'ha trobat l'annex amb id " + annexId + " a l'anotació " + anotacio.getIdentificador() + " amb id " + anotacioId);
		
		// Reintenta la incorporació
		ArxiuResultat resultat = new ArxiuResultat();
		annex.setError(null);
		
		String annexUuid = annex.getUuid();
		if (expedient.isArxiuActiu()) {			
			// Consulta el nom real a l'Arxiu
			String annexAnotacioNomArxiu = null;
			try {
				es.caib.plugins.arxiu.api.Document arxiuDocument = pluginHelper.arxiuDocumentInfo(
						annexUuid,
						null,
						false,
						annex.getFirmaTipus() != null);
				annexAnotacioNomArxiu = arxiuDocument.getNom();
			} catch (Exception e) {
				String errMsg = "Error reprocessant consultant els detalls de l'annex " + annex.getId() + " \"" + annex.getTitol() + "\" per reintentar la seva incorporació a l'expedient " + expedient.getNumero() + ": " + e.getMessage();
				logger.error(errMsg, e);
				distribucioHelper.updateErrorAnnex(annexId, errMsg);
				throw new SistemaExternException(errMsg, e);
			}

			// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació de l'anotació directament a l'expedient dins l'Arxiu
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
			// Posarà els annexos en la carpeta de l'anotació
			backofficeUtils.setCarpeta(ArxiuConversioHelper.revisarContingutNom(anotacio.getIdentificador().replace("/", "_")));
			// S'enregistraran els events al monitor d'integració
			backofficeUtils.setArxiuPluginListener(this);
			// Prepara la informació de l'anotació
			AnotacioRegistreEntrada anotacioRegistreEntrada = new AnotacioRegistreEntrada();
			try {
				List<Annex> annexos = new ArrayList<Annex>();
				Annex annexAnotacio = new Annex();
				annexAnotacio.setUuid(annex.getUuid());
				annexAnotacio.setTitol(annex.getTitol());
				annexAnotacio.setNom(annexAnotacioNomArxiu);
				annexos.add(annexAnotacio);		
				anotacioRegistreEntrada.setAnnexos(annexos);
				resultat = backofficeUtils.crearExpedientAmbAnotacioRegistre(expedientArxiu, anotacioRegistreEntrada);
			} catch(Exception e) {
				String errMsg = "Error reprocessant la informació de l'anotació de registre \"" + anotacio.getIdentificador() + "\" de Distribució: " + e.getMessage();
				logger.error(errMsg, e);
				throw new SistemaExternException(errMsg, e);
			}
		}
		// Incorpora l'annex a l'expedient
		distribucioHelper.incorporarAnnex(
				expedientTipus.isDistribucioSistra(), 
				anotacio.getExpedient(), 
				anotacio, 
				annex, 
				resultat);

		logger.debug(
				"Reintent de processament de l'annex (" +
				"anotacioId=" + anotacioId + " " + anotacio.getIdentificador() + ", annexId=" + annexId + 
				" " + annex.getNom() + ", annexEstat=" + annex.getEstat() + ", annexError=" + annex.getError() + ")");
		
		// Depenent del resultat llença excepció
		boolean error = false;
		StringBuilder errorMsg = new StringBuilder();
		if (resultat.getErrorCodi() != 0) {
			error = true;
			errorMsg.append("Error reprocessant l'anotació l'expedient amb la llibreria d'utilitats de Distribucio: " + resultat.getErrorCodi() + " " + resultat.getErrorMessage());
		}
		ArxiuResultatAnnex resultatAnnex = resultat.getResultatAnnex(annexUuid);
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
	public void reintentarTraspasAnotacio(Long anotacioId) throws Exception {
		
		logger.debug(
				"Reintentant el traspàs de l'anotació (" +
				"anotacioId=" + anotacioId + ")");

		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		// Comprova els permisos
		this.comprovaPermisAccio(anotacio);
		if (anotacio.getExpedient() == null)
			throw new Exception("No es pot processar l'annex perquè l'anotació no té cap expedient associat.");
		
		Expedient expedient = anotacio.getExpedient();
		ExpedientTipus expedientTipus = expedient.getTipus();
		
		// Reintenta la incorporació de l'anotació
		ArxiuResultat resultat = new ArxiuResultat();
		
		if (expedient.isArxiuActiu()) {
			// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació de l'anotació directament a l'expedient dins l'Arxiu
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
			// Posarà els annexos en la carpeta de l'anotació
			backofficeUtils.setCarpeta(ArxiuConversioHelper.revisarContingutNom(anotacio.getIdentificador().replace("/", "_")));
			// S'enregistraran els events al monitor d'integració
			backofficeUtils.setArxiuPluginListener(this);
			// Prepara la informació de l'anotació
			AnotacioRegistreEntrada anotacioRegistreEntrada = new AnotacioRegistreEntrada();
			List<Annex> annexos = new ArrayList<Annex>();
			String annexAnotacioNomArxiu;
			Annex annexAnotacio = null;
			Map<Annex, ArxiuResultatAnnex> errorsConsulta = new HashMap<Annex, ArxiuResultatAnnex>();
			for (AnotacioAnnex annex : anotacio.getAnnexos() ) {
				try {
					annexAnotacio = new Annex();
					annexAnotacio.setUuid(annex.getUuid());
					annexAnotacio.setTitol(annex.getTitol());
					// Consulta el nom real a l'Arxiu
					es.caib.plugins.arxiu.api.Document arxiuDocument = pluginHelper.arxiuDocumentInfo(
							annex.getUuid(),
							null,
							false,
							annex.getFirmaTipus() != null);
					annexAnotacioNomArxiu = arxiuDocument.getNom();
					// L'afegeix a la llista d'annexos a traspassar
					annexAnotacio.setNom(annexAnotacioNomArxiu);
					annexos.add(annexAnotacio);		
				} catch(Exception e) {
					String errMsg = "Error reprocessant consultant els detalls de l'annex " + annex.getId() + " \"" + annex.getTitol() + "\" per reintentar la seva incorporació a l'expedient " + expedient.getNumero() + ": " + e.getMessage();
					logger.error(errMsg, e);
					ArxiuResultatAnnex resultatAnnex = new ArxiuResultatAnnex();
					resultatAnnex.setAccio(AnnexAccio.ERROR);
					resultatAnnex.setAnnex(annexAnotacio);
					resultatAnnex.setErrorCodi(-1);
					resultatAnnex.setErrorMessage(errMsg);
					resultatAnnex.setException(e);
					resultatAnnex.setIdentificadorAnnex(annex.getUuid());
					errorsConsulta.put(annexAnotacio, resultatAnnex);
				}
			}
			anotacioRegistreEntrada.setAnnexos(annexos);
			resultat = backofficeUtils.crearExpedientAmbAnotacioRegistre(expedientArxiu, anotacioRegistreEntrada);
			// Al resultat hi afegeix els errors que s'havien produït per consulta dels detalls
			for (Annex annexAnotacioError : errorsConsulta.keySet()) {
				resultat.addResultatAnnex(annexAnotacioError, errorsConsulta.get(annexAnotacioError));
			}
		}
		
		// Depenent del resultat llença excepció
		boolean error = false;
		StringBuilder errorMsg = new StringBuilder();
		if (resultat.getErrorCodi() != 0) {
			error = true;
			errorMsg.append("Error reprocessant l'anotació l'expedient amb la llibreria d'utilitats de Distribucio: " + resultat.getErrorCodi() + " " + resultat.getErrorMessage());
		}
		
		String annexUuid;
		for(AnotacioAnnex a : anotacio.getAnnexos()){
			logger.debug(
					"Reintent de processament de l'annex (" +
					"anotacioId=" + anotacioId + " " + anotacio.getIdentificador() + ", annexId=" + a.getId() + 
					" " + a.getNom() + ", annexEstat=" + a.getEstat() + ", annexError=" + a.getError() + ")");
			annexUuid = a.getUuid();
			// Incorpora l'annex a l'expedient
			distribucioHelper.incorporarAnnex(
					expedientTipus.isDistribucioSistra(), 
					anotacio.getExpedient(), 
					anotacio, 
					a, 
					resultat);
			ArxiuResultatAnnex resultatAnnex = resultat.getResultatAnnex(annexUuid);
			if (resultatAnnex!=null && AnnexAccio.ERROR.equals(resultatAnnex.getAccio())) {
				error = true;
				errorMsg.append(resultatAnnex.getErrorCodi() + " - " + resultatAnnex.getErrorMessage());
			}
			if (error)
				throw new Exception(errorMsg.toString());
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void esborrarAnotacionsExpedient(Long expedientId) {
		logger.debug(
				"Esborrant o deslligant les anotacions de l'expedient (" +
				"expedientId=" + expedientId + ")");
		List<Anotacio> anotacions = anotacioRepository.findByExpedientId(expedientId);
		for (Anotacio anotacio : anotacions) {
			if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat()) )
				// Si les anotacions estan processades s'esborren
				anotacioRepository.delete(anotacio);
			else
				// Altrament les desrelaciona de l'expedient
				anotacio.setExpedient(null);
		}
	}
	
	@Override
	@Transactional
	public AnotacioMapeigResultatDto reprocessarMapeigAnotacioExpedient(Long expedientId, Long anotacioId) {
		AnotacioMapeigResultatDto resultatMapeig = new AnotacioMapeigResultatDto();
		logger.debug(
				"Reprocessant el mapeig de l'anotació de l'expedient ( " +
				"anotacioId=" + anotacioId + ", " +
				"expedientId=" + expedientId + ")");
		
		Anotacio anotacio = anotacioRepository.findOne(anotacioId);
		resultatMapeig.setAnotacioNumero(anotacio.getIdentificador());
	
		// Recupera la informació del tipus d'expedient i l'expedient
		ExpedientTipus expedientTipus = null;
		Expedient expedient = expedientRepository.findOne(expedientId);
		if(expedient!=null)
			expedientTipus = expedient.getTipus();
		
		// Comprovar que té integració amb Sistra2 activada
		if(expedientTipus.isDistribucioSistra()) {
			//Recuperar mapejos
			Map<String, Object> variables = null;
			Map<String, DadesDocumentDto> documents = null;
			List<DadesDocumentDto> annexos = null;
			MapeigSistra mapeigSistra = null;
			ExpedientDadaDto dada = null;
			DadesDocumentDto dadesDocumentDto = null;

			// Extreu variables i documents i annexos segons el mapeig sistra
			boolean ambContingut = expedient != null ? !expedient.isArxiuActiu() : !expedientTipus.isArxiuActiu(); 
			resultatMapeig = distribucioHelper.getMapeig(expedientTipus, anotacio, ambContingut);
			variables = resultatMapeig.getDades();
			documents = resultatMapeig.getDocuments();
			annexos = resultatMapeig.getAdjunts();			
			if(variables!=null) {
				for (String varCodi : variables.keySet()) {	
					// Obtenir la variable de l'expedient, comprovar si aquest mapeig existeix o no	
					mapeigSistra = mapeigSistraRepository.findByExpedientTipusAndCodiHelium(expedientTipus, varCodi);	
					dada = variableHelper.getDadaPerInstanciaProces(
							expedient.getProcessInstanceId(),
							varCodi,
							true);
					boolean variableExisteix = dada !=null && (dada.getMultipleValor() != null || dada.getVarValor() != null);
					processarVariablesAnotacio(
							expedientId,
							expedient.getProcessInstanceId(),
							varCodi,
							variables.get(varCodi),
							variableExisteix,
							mapeigSistra.isEvitarSobreescriptura());
				}
			}
			
			//Fem el mateix per els documents del mapeig
			for (String documentCodi : documents.keySet()) {
				mapeigSistra = mapeigSistraRepository.findByExpedientTipusAndCodiHelium(expedientTipus, documentCodi);	
				ExpedientDocumentDto document = documentHelper.findOnePerInstanciaProces(
						expedient.getProcessInstanceId(), 
						documentCodi);	
				
				boolean documentExisteix = document !=null ? true : false;
				
				
				if (documentExisteix && expedient.isArxiuActiu()) {
					// Si el document està firmat i a l'Arxiu llavors no es pot modifirar.
					if (document.isSignat() && !mapeigSistra.isEvitarSobreescriptura()) {
						// No es pot modificar un document firmat
						resultatMapeig.getErrorsDocuments().put(documentCodi, "El document no es pot sobreescriure perquè està firmat i no es pot modificar.");
						continue;						
					}
					// Si el document prové d'una anotació llavors ja està mapejat i no cal sobreescriure
					if (document.getAnotacioAnnexId() != null) {
						continue;
					}
				}	
				processarDocumentsAnotacio(
						dadesDocumentDto, 
						expedient, 
						document, 
						documentExisteix, 
						mapeigSistra.isEvitarSobreescriptura(), 
						documents, 
						mapeigSistra.getCodiHelium());
				
			}
			
			//Fem el mateix per els Annexos (adjunts), els creem encara que ja hi siguin
			for (DadesDocumentDto adjunt : annexos) {
				processarAdjuntsAnotacio( 
						expedient, 
						adjunt);		
			}
		}
		return resultatMapeig;
	}
	
	private void processarVariablesAnotacio(
			Long expedientId, String processInstanceId, String varCodi, Object varValor, boolean variableExisteix, boolean evitarSobreescriptura) {
		if (variableExisteix && !evitarSobreescriptura) {
			// actualitzar variable
			this.dadaUpdate(
					expedientId,
					processInstanceId,
					varCodi,
					varValor);	
		} else if (!variableExisteix){
			this.dadaCreate(
						expedientId,
						processInstanceId,								
						varCodi,
						varValor);
		}					
		
	}
	
	
	private void dadaCreate(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		logger.debug("Modificant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ", " +
				"varValor=" + varValor + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_CREAR,
				varCodi);
		expedientDadaHelper.optimitzarValorPerConsultesDominiGuardar(expedient.getTipus(), processInstanceId, varCodi, varValor);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
	}

	
	private void dadaUpdate(Long expedientId, String processInstanceId, String varCodi, Object varValor ) {
		logger.debug("Modificant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ", " +
				"varValor=" + varValor + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);		
		jbpmHelper.deleteProcessInstanceVariable(processInstanceId, varCodi);
		// Esborra la descripció per variables que mantenen el valor de la consulta
		Camp camp;
		InstanciaProcesDto instanciaProces = expedientHelper.getInstanciaProcesById(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findOne(instanciaProces.getDefinicioProces().getId());
		if (expedient.getTipus().isAmbInfoPropia()) {
			// obtenir el camp amb expedient tipus codi i codi de la variable
			camp = campRepository.findByExpedientTipusAndCodi(expedient.getTipus().getId(), varCodi, expedient.getTipus().getExpedientTipusPare() != null);
		}else {
			camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, varCodi);
		}
		if (camp != null && camp.isDominiCacheText())
			jbpmHelper.deleteProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varCodi);
		
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				varCodi);
		expedientDadaHelper.optimitzarValorPerConsultesDominiGuardar(
				expedient.getTipus(),
				processInstanceId,
				varCodi,
				varValor);
	}
	

	private void processarDocumentsAnotacio(
			DadesDocumentDto dadesDocumentDto, 
			Expedient expedient, 
			ExpedientDocumentDto document, 
			boolean documentExisteix,
			boolean evitarSobreescriptura,
			Map<String, DadesDocumentDto> documents,
			String codiHelium) {
		
		if (documentExisteix && !evitarSobreescriptura) {
			// Si existeix i es pot sobreescriure, l'actualitzem, sino el creem
			dadesDocumentDto = documents.get(codiHelium);
			DocumentStore documentStore = documentStoreRepository.findOne(document.getId());
			documentHelper.actualitzarDocument(
					documentStore.getId(),
					null,
					expedient.getProcessInstanceId(),
					dadesDocumentDto.getData(),
					dadesDocumentDto.getTitol(),
					dadesDocumentDto.getArxiuNom(),
					dadesDocumentDto.getArxiuContingut(),
					document.getArxiuExtensio(),
					document.isSignat(),
					false,
					null,
					document.getNtiOrigen(),
					document.getNtiEstadoElaboracion(),
					document.getNtiTipoDocumental(),
					document.getNtiIdOrigen(),
					dadesDocumentDto.isDocumentValid(),
					dadesDocumentDto.getDocumentError(),
					dadesDocumentDto.getAnnexId(), 
					dadesDocumentDto.getUuid());
			
			
			
		} else if (!documentExisteix) {
			dadesDocumentDto = documents.get(codiHelium);
			documentHelper.crearDocument(
					null, //taskInstanceId
					expedient.getProcessInstanceId(),
					dadesDocumentDto.getCodi(),
					dadesDocumentDto.getData(),
					false, // isAdjunt
					null, //adjuntTitol
					dadesDocumentDto.getArxiuNom(),
					dadesDocumentDto.getArxiuContingut(),
					dadesDocumentDto.getUuid(),
					dadesDocumentDto.getTipusMime(),
					expedient.isArxiuActiu() && dadesDocumentDto.getFirmaTipus() != null,	// amb firma
					false,	// firma separada
					null,	// firma contingut
					dadesDocumentDto.getNtiOrigen(),
					dadesDocumentDto.getNtiEstadoElaboracion(),
					dadesDocumentDto.getNtiTipoDocumental(),
					dadesDocumentDto.getNtiIdDocumentoOrigen(),
					dadesDocumentDto.isDocumentValid(),
					dadesDocumentDto.getDocumentError(),
					dadesDocumentDto.getAnnexId(),
					null);
		}
		
	}

	private void processarAdjuntsAnotacio(Expedient expedient, DadesDocumentDto adjunt ) {
		documentHelper.crearDocument(
				null,
				expedient.getProcessInstanceId(),
				null,
				adjunt.getData(),
				true, // isAdjunt
				adjunt.getTitol(),
				adjunt.getArxiuNom(),
				adjunt.getArxiuContingut(),
				adjunt.getUuid(),
				documentHelper.getContentType(adjunt.getArxiuNom()),
				expedient.isArxiuActiu() && adjunt.getFirmaTipus() != null,	// amb firma
				false,	// firma separada
				null,	// firma contingut
				adjunt.getNtiOrigen(),
				adjunt.getNtiEstadoElaboracion(),
				adjunt.getNtiTipoDocumental(),
				adjunt.getNtiIdDocumentoOrigen(),
				adjunt.isDocumentValid(),
				adjunt.getDocumentError(),
				adjunt.getAnnexId(),
				null);
	}

	private static final Logger logger = LoggerFactory.getLogger(AnotacioServiceImpl.class);
}