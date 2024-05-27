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
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Arrays;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.UnitatOrganitzativaHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.DocumentNotificacio;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;

import net.conselldemallorca.helium.v3.core.api.service.NotificacioService;
import net.conselldemallorca.helium.v3.core.repository.DocumentNotificacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusUnitatOrganitzativaRepository;


/**
 * Implementació del servei per a gestionar notificacions enviades a NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class NotificacioServiceImpl implements NotificacioService, ArxiuPluginListener {

	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Resource
	private ExpedientTipusUnitatOrganitzativaRepository expedientTipusUnitatOrganitzativaRepository;
	@Resource
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource 
	private DocumentHelperV3 documentHelperV3;
	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceImpl.class);
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private NotificacioHelper notificacioHelper;

	@Override
	public void event(String arg0, Map<String, String> arg1, boolean arg2, String arg3, Exception arg4, long arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional(readOnly=true)
	public PaginaDto<DocumentNotificacioDto> findAmbFiltrePaginat(Long entornId,
			List<ExpedientTipusDto> expedientTipusDtoAccessibles, DocumentNotificacioDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les notificacions per datatable (" +
				"notificacioFiltreDto=" + filtreDto + ")");
		
		// Llista d'expedients tipus amb permís de relacionar
		List<Long> expedientTipusIdsPermesos = new ArrayList<Long>();
		List<Long> expedientTipusIdsPermesosProcedimetComu = new ArrayList<Long>();
		List<String> unitatsOrganitvesCodis = new ArrayList<String>();
		List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList = new ArrayList<ExpedientTipusUnitatOrganitzativa>();
		Map<Long,List<String>> unitatsPerTipusComu = new HashMap<Long, List<String>>();

		// Pot veure:
		// - Totes les notificacions si és administrador d'Helium
		// - Les notificacions dels tipus d'expedient amb permís
		// - Les notificacions d'un expedient en el cas de passar-ho com a filtre
		// Si el filtre especifica l'id de l'expedient des de la pipella de notificacions de l'expedient
		if (filtreDto.getExpedientId() != null) {
			// Comprova que pugui llegir l'expedient pel cas de la pipella d'anotacions de l'expedient
			expedientHelper.getExpedientComprovantPermisos(filtreDto.getExpedientId(), true, false, false, false);
		} else {
			if (!usuariActualHelper.isAdministrador()) {
				// Classifiquem els tipusExpedients tipus sense procediment comú  i amb procediment comú, dels que portem des de la caché
				for(ExpedientTipusDto expTipusDtoCache: expedientTipusDtoAccessibles) {
					if(!expTipusDtoCache.isProcedimentComu()) {
						expedientTipusIdsPermesos.add(expTipusDtoCache.getId());
					} else {
						expedientTipusIdsPermesosProcedimetComu.add(expTipusDtoCache.getId());
					}
				}				
			}
			//Al filtre de notificacions només tindrem els expedientTipus amb permisos
			Permission[] permisosRequerits= new Permission[] {
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
		
		PaginaDto<DocumentNotificacioDto> pagina =  paginacioHelper.toPaginaDto(documentNotificacioRepository.findAmbFiltrePaginat(
				filtreDto.getTipus()==null,
				filtreDto.getTipus(),
				filtreDto.getConcepte() == null || filtreDto.getConcepte().isEmpty(), 
				filtreDto.getConcepte(), 
				filtreDto.getEstat() == null, 
				filtreDto.getEstat(), 
				filtreDto.getDataInicial() == null,
				filtreDto.getDataInicial(),
				dataFinal == null,
				dataFinal,
//				filtreDto.getInteressat() == null || filtreDto.getInteressat().isEmpty(), 
//				filtreDto.getInteressat(), 
//				filtreDto.getExpedientId() == null , 
//				filtreDto.getExpedientId(), 
//				filtreDto.getNomDocument() == null || filtreDto.getNomDocument().isEmpty(), 
//				filtreDto.getNomDocument(), 
//				filtreDto.getUnitatOrganitzativaCodi() == null || filtreDto.getUnitatOrganitzativaCodi().isEmpty(), 
//				filtreDto.getUnitatOrganitzativaCodi(),
//				expedientTipusIdsPermesos == null || expedientTipusIdsPermesos.isEmpty(),
//				expedientTipusIdsPermesos == null || expedientTipusIdsPermesos.isEmpty() ? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesos,
//				expedientTipusIdsPermesosProcedimetComu == null || expedientTipusIdsPermesosProcedimetComu.isEmpty(),
//				expedientTipusIdsPermesosProcedimetComu.isEmpty() ? Arrays.asList(ArrayUtils.toArray(0L)) : expedientTipusIdsPermesosProcedimetComu,
				paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams)), DocumentNotificacioDto.class);
	
		for(DocumentNotificacioDto dto : pagina.getContingut()) {
			notificacioHelper.omplirModelNotificacioDto(dto);
			dto.setProcessInstanceId(dto.getExpedient().getProcessInstanceId());
		}
	return pagina;
	}
	

	@Override
	public NotificacioDto findAmbId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
}