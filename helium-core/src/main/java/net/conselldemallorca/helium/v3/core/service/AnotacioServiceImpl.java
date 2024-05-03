/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
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
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;
import net.conselldemallorca.helium.core.helper.AlertaHelper;
import net.conselldemallorca.helium.core.helper.AnotacioHelper;
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
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
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
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusUnitatOrganitzativaRepository;
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
	private ExpedientTipusUnitatOrganitzativaRepository expedientTipusUnitatOrganitzativaRepository;
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
	@Resource
	private AnotacioHelper anotacioHelper;
	
	@PersistenceContext
    private EntityManager entityManager;

	
	private static Boolean consultaDinamica = Boolean.TRUE;
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(
			Long entornId,
			List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions,
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les anotacions per datatable (" +
				"anotacioFiltreDto=" + filtreDto + ")");

		// Llista d'expedients tipus amb permís de relacionar
		List<Long> expedientTipusIdsPermesos = new ArrayList<Long>();
		List<Long> expedientTipusIdsPermesosProcedimetComu = new ArrayList<Long>();
		List<String> unitatsOrganitvesCodis = new ArrayList<String>();
		List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList = new ArrayList<ExpedientTipusUnitatOrganitzativa>();
		Map<Long,List<String>> unitatsPerTipusComu = new HashMap<Long, List<String>>();

		// Pot veure:
		// - Totes les anotacions si és administrador d'Helium
		// - Les anotacions dels tipus d'expedient amb permís de relacionar en el cas de no ser-ho
		// - Les anotacions d'un expedient en el cas de passar-ho com a filtre
		// Si el filtre especifica l'id de l'expedient des de la pipella d'anotacions de l'expedient
		if (filtreDto.getExpedientId() != null) {
			// Comprova que pugui llegir l'expedient pel cas de la pipella d'anotacions de l'expedient
			expedientHelper.getExpedientComprovantPermisos(filtreDto.getExpedientId(), true, false, false, false);
		} else {
			if (!usuariActualHelper.isAdministrador()) {
				// Classifiquem els tipusExpedients tipus sense procediment comú  i amb procediment comú, dels que portem des de la caché
				for(ExpedientTipusDto expTipusDtoCache: expedientTipusDtoAccessiblesAnotacions) {
					if(!expTipusDtoCache.isProcedimentComu()) {
						expedientTipusIdsPermesos.add(expTipusDtoCache.getId());
					} else {
						expedientTipusIdsPermesosProcedimetComu.add(expTipusDtoCache.getId());
					}
				}				
			}
			//Al filtre d'anotacions només tindrem els expedientTipus amb permisos d'admin o de relacionar
			Permission[] permisosRequerits= new Permission[] {
					ExtendedPermission.RELATE,
					ExtendedPermission.ADMINISTRATION};
			if (filtreDto.getExpedientTipusId() != null) {
				expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusId(filtreDto.getExpedientTipusId());
				unitatsPerTipusComu = expedientTipusHelper.unitatsPerTipusComu(entornId, expTipUnitOrgList, permisosRequerits);										
			} else { //si no hi ha expedientTipus al filtre, hem de buscar totes les UO per las quals es té permís i obtenir els expedinetTipus
				if (!usuariActualHelper.isAdministrador()) {
					expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findAll();
					unitatsPerTipusComu = expedientTipusHelper.unitatsPerTipusComu(entornId, expTipUnitOrgList, permisosRequerits);										
				}
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
		
		// Consulta dinàmica
		Page<Anotacio> page;
		
		if (consultaDinamica.booleanValue()) {
			// Nova consulta dinàmica
			page = (Page<Anotacio>) this.findAmbFiltrePaginat(
					false, // per retornar una pàgina
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
					expedientTipusIdsPermesos == null || expedientTipusIdsPermesos.isEmpty(),
					expedientTipusIdsPermesos == null || expedientTipusIdsPermesos.isEmpty() ? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesos,
					unitatsPerTipusComu == null || unitatsPerTipusComu.isEmpty(),
					unitatsPerTipusComu,
					paginacioHelper.toSpringDataPageable(paginacioParams));
		} else {
			// Consulta per repositor
			page = anotacioRepository.findAmbFiltrePaginat(
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
					expedientTipusIdsPermesos == null || expedientTipusIdsPermesos.isEmpty(),
					expedientTipusIdsPermesos == null || expedientTipusIdsPermesos.isEmpty() ? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesos,
					expedientTipusIdsPermesosProcedimetComu == null || expedientTipusIdsPermesosProcedimetComu.isEmpty(),
					expedientTipusIdsPermesosProcedimetComu.isEmpty() ? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesosProcedimetComu,
					unitatsOrganitvesCodis.isEmpty() ? true : false,
					unitatsOrganitvesCodis.isEmpty() ? Arrays.asList(ArrayUtils.toArray("")) : unitatsOrganitvesCodis,
					paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
					paginacioParams.getFiltre(),
					paginacioHelper.toSpringDataPageable(paginacioParams));
		}
		
		PaginaDto<AnotacioListDto> pagina = paginacioHelper.toPaginaDto(page, AnotacioListDto.class);
		
		for(AnotacioListDto anotacio: pagina.getContingut()){
			if(distribucioHelper.isProcessant(anotacio.getId())) {
				anotacio.setProcessant(true);
			}
		}
		
		return pagina;
	}

	/** Crea la query dinàmicament segons els paràmetres de cerca i retoran una llista d'indentificadors o un resultat paginat.
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	private Object findAmbFiltrePaginat(
			boolean nomesIds,
			boolean esNullCodiProcediment,
			String codiProcediment,
			boolean esNullDestiCodi,//unitatOrganitzativa
			String destiCodi,//unitatOrganitzativa
			boolean esNullCodiAssumpte,
			String codiAssumpte,
			boolean esNullNumeroExpedient,
			String numeroExpedient,
			boolean esNullNumero,
			String numero,
			boolean esNullExtracte,
			String extracte,
			boolean esNullDataInicial,
			Date dataInicial,
			boolean esNullDataFinal,
			Date dataFinal,
			boolean esNullEstat,
			AnotacioEstatEnumDto estat, 
			boolean esNullExpedientTipusId,
			Long expedientTipusId, 
			boolean esNullExpedientId,
			Long expedientId, 
			boolean esNullExpedientTipusIds,
			List<Long> expedientTipusIds,
			boolean esNullExpedientTipusIdsPermesosProcedimetComu,
			Map<Long, List<String>> unitatsPerTipusComu, 
			Pageable pageable) {

		Object ret = null; // Retorna una llista d'identificadors o una pàgina

		// Construeix la select
		String sqlFrom = "from Anotacio a ";
		// Where
		Map<String, Object> parametres = new HashMap<String, Object>();
		StringBuilder sqlWhere = new StringBuilder(" where ");

		StringBuilder sqlOrder = new StringBuilder();
		if (pageable != null && pageable.getSort() != null) {
			Iterator<Order> orders = pageable.getSort().iterator();
			if (orders.hasNext()) {
				sqlOrder.append(" order by ");
			}
			Order order;
			while (orders.hasNext()) {
				order = orders.next();
				sqlOrder.append(order.getProperty()).append(" ").append(order.getDirection());
				if (orders.hasNext()) {
					sqlOrder.append(", ");
				}
			}
		}
		sqlWhere.append(" (:esNullEstat = true or a.estat = :estat) ");
		parametres.put("esNullEstat", esNullEstat);
		parametres.put("estat", estat);

		if (!esNullCodiProcediment) {
			sqlWhere.append(" and lower(a.procedimentCodi) like lower('%'||:codiProcediment||'%') ");
			parametres.put("codiProcediment", codiProcediment);
		}
		if (!esNullDestiCodi) {
			sqlWhere.append(" and lower(a.destiCodi) like lower('%'||:destiCodi||'%') ");
			parametres.put("destiCodi", destiCodi);
		}
		if (!esNullCodiAssumpte) {
			sqlWhere.append(" and lower(a.assumpteCodiCodi) like lower('%'||:codiAssumpte||'%') ");
			parametres.put("codiAssumpte", codiAssumpte);
		}
		if (!esNullNumeroExpedient) {
			sqlWhere.append(" and lower(a.expedientNumero) like lower('%'||:numeroExpedient||'%') ");
			parametres.put("numeroExpedient", numeroExpedient);
		}
		if (!esNullNumero) {
			sqlWhere.append(" and lower(a.identificador) like lower('%'||:numero||'%') ");
			parametres.put("numero", numero);
		}
		if (!esNullExtracte) {
			sqlWhere.append(" and lower(a.extracte) like lower('%'||:extracte||'%') ");
			parametres.put("extracte", extracte);
		}
		if (!esNullDataInicial) {
			sqlWhere.append(" and a.data >= :dataInicial ");
			parametres.put("dataInicial", dataInicial);
		}
		if (!esNullDataFinal) {
			sqlWhere.append(" and a.data <= :dataFinal ");
			parametres.put("dataFinal", dataFinal);
		}
		if (!esNullExpedientId) {
			sqlWhere.append(" and a.expedient.id = :expedientId ");
			parametres.put("expedientId", expedientId);
		}

		if (!esNullExpedientTipusId) {
			sqlWhere.append(" and a.expedientTipus.id = :expedientTipusId ");
			parametres.put("expedientTipusId", expedientTipusId);
		} 
		
		if (!esNullExpedientTipusIds || !esNullExpedientTipusIdsPermesosProcedimetComu) {
			// filtre per tipus d'expedients
			boolean filtrePerTipus = false;
			StringBuilder whereTipus = new StringBuilder();
			// filtre per tipus d'expedients normals
			if (!esNullExpedientTipusIds) {
				whereTipus.append(" ( a.expedientTipus.id in (:expedientTipusIds) ) ");
				parametres.put("expedientTipusIds", expedientTipusIds);
				filtrePerTipus = true;
			}
			// Filtre per tipus d'expedients comuns i les seves UO's
			if (!esNullExpedientTipusIdsPermesosProcedimetComu 
					&& unitatsPerTipusComu != null 
					&& !unitatsPerTipusComu.isEmpty()) 
			{
				List<String> codisUos;
				for (Long expedientTipusComuId : unitatsPerTipusComu.keySet()) {
					codisUos = unitatsPerTipusComu.get(expedientTipusComuId);
					if (!codisUos.isEmpty()) {
						if (filtrePerTipus) {
							whereTipus.append(" or ");
						}
						whereTipus.append(" ( a.expedientTipus.id = " + expedientTipusComuId);
						// en subllistes
						whereTipus.append(" and ( ");
						for (int i = 0; i <= codisUos.size() / 1000; i++ ) {
							if( i > 0) { 
								whereTipus.append(" or ");
							}
							whereTipus.append(" a.destiCodi in (:uos_" + expedientTipusComuId + "_" + i + ") ");
							parametres.put("uos_" + expedientTipusComuId + "_" + i, codisUos.subList(i*1000, Math.min(codisUos.size(), (i + 1)*1000)));
						}
						whereTipus.append(" ) ) ");
						filtrePerTipus = true;
					}
				}
			}
			if (filtrePerTipus) {
				sqlWhere.append(" and ( ").append(whereTipus.toString()).append(" ) ");
			}
		}
		
		String sqlSelect = "select " + (nomesIds ? " a.id " : "a ") + sqlFrom + sqlWhere + sqlOrder;
		String sqlCount = "select count(a.id) " + sqlFrom + sqlWhere;
		Session session = entityManager.unwrap(Session.class);
		Query selectQuery = session.createQuery(sqlSelect);
		if (pageable != null) {
			selectQuery = selectQuery.
					setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).
					setMaxResults(pageable.getPageSize());
		}
		Query countQuery = session.createQuery(sqlCount);
		Object value;
		for (String parametre : parametres.keySet()) {
			value = parametres.get(parametre);
			if (value instanceof Collection) {
				selectQuery.setParameterList(parametre, (Collection) value);
				countQuery.setParameterList(parametre, (Collection) value);
			} else {
				selectQuery.setParameter(parametre, value);
				countQuery.setParameter(parametre, value);
			}
		}
		if (nomesIds) {
			// select
			List<Long> ids = (List<Long>) selectQuery.list();
			ret = ids;
		} else {
			// select
			List<Anotacio> resultats = (List<Anotacio>)selectQuery.list();
			// count
			Page<Anotacio> pagina = (Page<Anotacio>) new PageImpl<Anotacio>(
					resultats,
					pageable, 
					(Long)countQuery.uniqueResult()); 
			ret = pagina;
		}
		return ret;
	}

	@SuppressWarnings({ "unchecked", "unchecked" })
	@Override
	public List<Long> findIdsAmbFiltre(
				Long entornId, 
				List<ExpedientTipusDto> expedientTipusDtoAccessiblesAnotacions,
				AnotacioFiltreDto filtreDto) {
		logger.debug(
				"Consultant els identificadors les anotacions per datatable (" +
				"anotacioFiltreDto=" + filtreDto + ")");

		// Llista d'expedients tipus amb permís de relacionar
		List<Long> expedientTipusIdsPermesos = new ArrayList<Long>();
		List<Long> expedientTipusIdsPermesosProcedimetComu = new ArrayList<Long>();
		List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList = new ArrayList<ExpedientTipusUnitatOrganitzativa>();
		Map<Long,List<String>> unitatsPerTipusComu = new HashMap<Long, List<String>>();
		// Pot veure:
		// - Totes les anotacions si és administrador d'Helium
		// - Les anotacions dels tipus d'expedient amb permís de relacionar en el cas de no ser-ho
		// - Les anotacions d'un expedient en el cas de passar-ho com a filtre
		// Si el filtre especifica l'id de l'expedient des de la pipella d'anotacions de l'expedient

		if (filtreDto.getExpedientId() != null) {
			// Comprova que pugui llegir l'expedient pel cas de la pipella d'anotacions de l'expedient
			expedientHelper.getExpedientComprovantPermisos(filtreDto.getExpedientId(), true, false, false, false);
		} else {
			if (!usuariActualHelper.isAdministrador()) {
				// Classifiquem els tipusExpedients tipus sense procediment comú  i amb procediment comú, dels que portem des de la caché
				for(ExpedientTipusDto expTipusDtoCache: expedientTipusDtoAccessiblesAnotacions) {
					if(!expTipusDtoCache.isProcedimentComu()) {
						expedientTipusIdsPermesos.add(expTipusDtoCache.getId());
					} else {
						expedientTipusIdsPermesosProcedimetComu.add(expTipusDtoCache.getId());
					}
				}				
			}	
			//Al filtre d'anotacions només tindrem els expedientTipus amb permisos d'admin o de relacionar
			Permission[] permisosRequerits= new Permission[] {
					ExtendedPermission.RELATE,
					ExtendedPermission.ADMINISTRATION};
			if (filtreDto.getExpedientTipusId() != null) {
				expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusId(filtreDto.getExpedientTipusId());
				unitatsPerTipusComu = expedientTipusHelper.unitatsPerTipusComu(entornId, expTipUnitOrgList, permisosRequerits);	
			} else { //si no hi ha expedientTipus al filtre, hem de buscar totes les UO per las quals es té permís i obtenir els expedinetTipus
				if (!usuariActualHelper.isAdministrador()) {
					expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findAll();
					unitatsPerTipusComu = expedientTipusHelper.unitatsPerTipusComu(entornId, expTipUnitOrgList, permisosRequerits);					
				}
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
		List<Long> ids = (List<Long>) this.findAmbFiltrePaginat(
				true, // per retornar una pàgina
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
				expedientTipusIdsPermesos == null || expedientTipusIdsPermesos.isEmpty(),
				expedientTipusIdsPermesos == null || expedientTipusIdsPermesos.isEmpty() ? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesos,
				unitatsPerTipusComu == null || unitatsPerTipusComu.isEmpty(),
				unitatsPerTipusComu,
				null);
		
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
		BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
		backofficeUtils.setArxiuPluginListener(this);
		return anotacioHelper.incorporarReprocessarExpedient(
				null,
				anotacioId, 
				expedientTipusId, 
				expedientId, 
				associarInteressats, 
				comprovarPermis, 
				reprocessar,
				backofficeUtils);
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
	@Transactional
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
			BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
			backofficeUtils.setArxiuPluginListener(this);
			anotacio = distribucioHelper.reprocessarAnotacio(anotacioId, backofficeUtils);
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

	private static final Logger logger = LoggerFactory.getLogger(AnotacioServiceImpl.class);
}