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
import org.springframework.security.core.context.SecurityContextHolder;
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
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
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

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true)
	public PaginaDto<DocumentNotificacioDto> findAmbFiltrePaginat(
			DocumentNotificacioDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les notificacions per datatable (" +
				"notificacioFiltreDto=" + filtreDto + ")");
		
		List<Long> entornsPermesos = null;
		//Si ets admin, no es filtra per entorn seleccionat
		if (usuariActualHelper.isAdministrador()) {
			filtreDto.setEntornId(null);
			List<EntornDto> entornsActiusAdmin = usuariActualHelper.findEntornsActiusPermesos(
					SecurityContextHolder.getContext().getAuthentication().getName());
			
			if (entornsActiusAdmin!=null && entornsActiusAdmin.size()>0) {
				entornsPermesos = new ArrayList<Long>();
				for (EntornDto entornDto: entornsActiusAdmin) {
					entornsPermesos.add(entornDto.getId());
				}
			}
		} else {
			//Si no ets admin, es filtra per l'entonr del filtre, que es de la sessió.
			//JA s'haurá comprovat al controller que tens permisos de administració sobre el entorn
			entornsPermesos = new ArrayList<Long>();
			entornsPermesos.add(filtreDto.getEntornId());
		}
		
		Date dataInicial = null;
		if (filtreDto.getDataInicial() != null) {
			// Corregeix la data final per arribar a les 00:00:00h del dia següent.
			Calendar c = new GregorianCalendar();
			c.setTime(filtreDto.getDataInicial());
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			dataInicial = c.getTime();
		}
		
		Date dataFinal = null;
		if (filtreDto.getDataFinal() != null) {
			// Corregeix la data final per arribar a les 00:00:00h del dia següent.
			Calendar c = new GregorianCalendar();
			c.setTime(filtreDto.getDataFinal());
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 999);
			dataFinal = c.getTime();
		}
		
		paginacioParams.canviaCamp("expedient.identificador", "expedient.numero");
		paginacioParams.canviaCamp("expedientTipusNom", "expedient.tipus.nom");
		paginacioParams.canviaCamp("nomDocument", "document.arxiuNom");

		PaginaDto<DocumentNotificacioDto> pagina =  paginacioHelper.toPaginaDto(documentNotificacioRepository.findAmbFiltrePaginat(
				entornsPermesos == null,
				entornsPermesos,				
				filtreDto.getTipus()==null,
				filtreDto.getTipus(),
				filtreDto.getConcepte() == null || filtreDto.getConcepte().isEmpty(), 
				filtreDto.getConcepte(), 
				filtreDto.getEstat() == null, 
				filtreDto.getEstat(), 
				dataInicial == null,
				dataInicial,
				dataFinal == null,
				dataFinal,
				filtreDto.getInteressat() == null || filtreDto.getInteressat().isEmpty(), 
				filtreDto.getInteressat(), 
				filtreDto.getExpedientId() == null,
				filtreDto.getExpedientId(),
				filtreDto.getExpedientNumero() == null,
				filtreDto.getExpedientNumero(),
				filtreDto.getNomDocument() == null || filtreDto.getNomDocument().isEmpty(), 
				filtreDto.getNomDocument(), 
				filtreDto.getUnitatOrganitzativaCodi() == null || filtreDto.getUnitatOrganitzativaCodi().isEmpty(),//organdesti 
				filtreDto.getUnitatOrganitzativaCodi(),//organdesti
				filtreDto.getExpedientTipusId() == null,
				filtreDto.getExpedientTipusId(),
				paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams)), DocumentNotificacioDto.class);
	
		for(DocumentNotificacioDto dto : pagina.getContingut()) {
			notificacioHelper.omplirModelNotificacioDto(dto);
		}
	return pagina;
	}
	

	@Override
	@Transactional(readOnly=true)
	public DocumentNotificacioDto findAmbId(Long id) {
		return conversioTipusHelper.convertir(documentNotificacioRepository.findById(id), DocumentNotificacioDto.class);
	}
}