/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DefinicioProcesHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusParamConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaDefaultAnyDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.exportacio.AccioExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.AgrupacioExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.CampExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ConsultaCampExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ConsultaExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DocumentExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DominiExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.EnumeracioExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.EnumeracioValorExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.EstatExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacioCommandDto;
import net.conselldemallorca.helium.v3.core.api.exportacio.MapeigSistraExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.RegistreMembreExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.TerminiExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ValidacioExportacio;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampValidacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.FirmaTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.MapeigSistraRepository;
import net.conselldemallorca.helium.v3.core.repository.ReassignacioRepository;
import net.conselldemallorca.helium.v3.core.repository.SequenciaAnyRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Implementació del servei per a gestionar tipus d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTipusServiceImpl implements ExpedientTipusService {

	@Resource
	private EntornHelper entornHelper;
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private SequenciaAnyRepository sequenciaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private CampValidacioRepository campValidacioRepository;
	@Resource
	private CampRegistreRepository campRegistreRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;	
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private ReassignacioRepository reassignacioRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private MapeigSistraRepository mapeigSistraRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private FirmaTascaRepository firmaTascaRepository;
//	
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private JbpmHelper jbpmHelper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto create(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) {
		logger.debug(
				"Creant nou tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipus + ", " +
				"sequenciesAny=" + sequenciesAny + ", " +
				"sequenciesValor=" + sequenciesValor + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				true);
		ExpedientTipus entity = new ExpedientTipus();
		entity.setEntorn(entorn);
		entity.setCodi(expedientTipus.getCodi());
		entity.setNom(expedientTipus.getNom());
		entity.setAmbInfoPropia(expedientTipus.isAmbInfoPropia());
		entity.setTeTitol(expedientTipus.isTeTitol());
		entity.setDemanaTitol(expedientTipus.isDemanaTitol());
		entity.setTeNumero(expedientTipus.isTeNumero());
		entity.setDemanaNumero(expedientTipus.isDemanaNumero());
		entity.setExpressioNumero(expedientTipus.getExpressioNumero());
		entity.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		entity.setSequencia(expedientTipus.getSequencia());
		entity.setResponsableDefecteCodi(expedientTipus.getResponsableDefecteCodi());
		entity.setRestringirPerGrup(expedientTipus.isRestringirPerGrup());
		entity.setSeleccionarAny(expedientTipus.isSeleccionarAny());
		entity.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
		entity.setReindexacioAsincrona(expedientTipus.isReindexacioAsincrona());
		entity.setDiesNoLaborables(expedientTipus.getDiesNoLaborables());
		entity.setNotificacionsActivades(expedientTipus.isNotificacionsActivades());
		entity.setNotificacioOrganCodi(expedientTipus.getNotificacioOrganCodi());
		entity.setNotificacioOficinaCodi(expedientTipus.getNotificacioOficinaCodi());
		entity.setNotificacioUnitatAdministrativa(expedientTipus.getNotificacioUnitatAdministrativa());
		entity.setNotificacioCodiProcediment(expedientTipus.getNotificacioCodiProcediment());
		entity.setNotificacioAvisTitol(expedientTipus.getNotificacioAvisTitol());
		entity.setNotificacioAvisText(expedientTipus.getNotificacioAvisText());
		entity.setNotificacioAvisTextSms(expedientTipus.getNotificacioAvisTextSms());
		entity.setNotificacioOficiTitol(expedientTipus.getNotificacioOficiTitol());
		entity.setNotificacioOficiText(expedientTipus.getNotificacioOficiText());
		
		if (expedientTipus.isReiniciarCadaAny()) {
			for (int i = 0; i < sequenciesAny.size(); i++) {
				SequenciaAny anyEntity = new SequenciaAny(
						entity, 
						sequenciesAny.get(i), 
						sequenciesValor.get(i));
				entity.getSequenciaAny().put(anyEntity.getAny(), anyEntity);
			}
		}
		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto update(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny,
			List<Long> sequenciesValor) {
		logger.debug(
				"Modificant tipus d'expedient existent (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipus + ", " +
				"sequenciesAny=" + sequenciesAny + ", " +
				"sequenciesValor=" + sequenciesValor + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipus.getId());
		ExpedientTipus entity = expedientTipusRepository.findByEntornAndId(
				entorn,
				expedientTipus.getId());
		entity.setNom(expedientTipus.getNom());
		entity.setAmbInfoPropia(expedientTipus.isAmbInfoPropia());
		entity.setTeTitol(expedientTipus.isTeTitol());
		entity.setDemanaTitol(expedientTipus.isDemanaTitol());
		entity.setTeNumero(expedientTipus.isTeNumero());
		entity.setDemanaNumero(expedientTipus.isDemanaNumero());
		entity.setExpressioNumero(expedientTipus.getExpressioNumero());
		entity.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		entity.setSequencia(expedientTipus.getSequencia());
		entity.setResponsableDefecteCodi(expedientTipus.getResponsableDefecteCodi());
		entity.setRestringirPerGrup(expedientTipus.isRestringirPerGrup());
		entity.setSeleccionarAny(expedientTipus.isSeleccionarAny());
		entity.setDiesNoLaborables(expedientTipus.getDiesNoLaborables());
		entity.setNotificacionsActivades(expedientTipus.isNotificacionsActivades());
		entity.setNotificacioOrganCodi(expedientTipus.getNotificacioOrganCodi());
		entity.setNotificacioOficinaCodi(expedientTipus.getNotificacioOficinaCodi());
		entity.setNotificacioUnitatAdministrativa(expedientTipus.getNotificacioUnitatAdministrativa());
		entity.setNotificacioCodiProcediment(expedientTipus.getNotificacioCodiProcediment());
		entity.setNotificacioAvisTitol(expedientTipus.getNotificacioAvisTitol());
		entity.setNotificacioAvisText(expedientTipus.getNotificacioAvisText());
		entity.setNotificacioAvisTextSms(expedientTipus.getNotificacioAvisTextSms());
		entity.setNotificacioOficiTitol(expedientTipus.getNotificacioOficiTitol());
		entity.setNotificacioOficiText(expedientTipus.getNotificacioOficiText());
		while (entity.getSequenciaAny().size() > 0) {
			SequenciaAny s = entity.getSequenciaAny().get(entity.getSequenciaAny().firstKey());			
			entity.getSequenciaAny().remove(s.getAny());
			sequenciaRepository.delete(s);
		}
		if (expedientTipus.isReiniciarCadaAny()) {
			for (int i = 0; i < sequenciesAny.size(); i++) {
				SequenciaAny sequenciaEntity = new SequenciaAny(
						entity,
						sequenciesAny.get(i),
						sequenciesValor.get(i));
				entity.getSequenciaAny().put(sequenciaEntity.getAny(), sequenciaEntity);
				sequenciaRepository.save(sequenciaEntity);
			}
		}
		// Només poden configurar la retroacció els dissenyadors de l'entorn
		if (entornHelper.potDissenyarEntorn(entornId)) {
			entity.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
			entity.setReindexacioAsincrona(expedientTipus.isReindexacioAsincrona());
		}
		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto updateIntegracioForms(
			Long entornId, 
			Long expedientTipusId, 
			String url, 
			String usuari,
			String contrasenya) {
		logger.debug(
				"Modificant tipus d'expedient amb dades d'integracio amb formularis externs (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipusId + ", " +
				"url=" + url + ", " +
				"usuari=" + usuari + ", " +
				"contrasenya=" + contrasenya + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		ExpedientTipus entity = expedientTipusRepository.findByEntornAndId(
				entorn,
				expedientTipusId);
		entity.setFormextUrl(url);
		entity.setFormextUsuari(usuari);
		entity.setFormextContrasenya(contrasenya);

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto updateIntegracioTramits(
			Long entornId, 
			Long expedientTipusId, 
			String tramitCodi,
			boolean notificacionsActivades,
			String notificacioOrganCodi,
			String notificacioOficinaCodi,
			String notificacioUnitatAdministrativa,
			String notificacioCodiProcediment,
			String notificacioAvisTitol,
			String notificacioAvisText,
			String notificacioAvisTextSms,
			String notificacioOficiTitol,
			String notificacioOficiText) {
		logger.debug(
				"Modificant tipus d'expedient amb dades d'integracio amb tramits de Sistra (" +
				"entornId=" + entornId + ", " +
				"expedientTipus=" + expedientTipusId + ", " +
				"tramitCodi=" + tramitCodi + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		ExpedientTipus entity = expedientTipusRepository.findByEntornAndId(
				entorn,
				expedientTipusId);
		entity.setSistraTramitCodi(tramitCodi);
		entity.setNotificacionsActivades(notificacionsActivades);
		entity.setNotificacioOrganCodi(notificacioOrganCodi);
		entity.setNotificacioOficinaCodi(notificacioOficinaCodi);
		entity.setNotificacioUnitatAdministrativa(notificacioUnitatAdministrativa);
		entity.setNotificacioCodiProcediment(notificacioCodiProcediment);
		entity.setNotificacioAvisTitol(notificacioAvisTitol);
    	entity.setNotificacioAvisText(notificacioAvisText);
    	entity.setNotificacioAvisTextSms(notificacioAvisTextSms);
    	entity.setNotificacioOficiTitol(notificacioOficiTitol);
    	entity.setNotificacioOficiText(notificacioOficiText);

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(entity),
				ExpedientTipusDto.class);	
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Esborrant el tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				true);
		ExpedientTipus entity = expedientTipusRepository.findByEntornAndId(
				entorn,
				expedientTipusId);
		
		expedientTipusRepository.delete(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusExportacio exportar(
			Long entornId,
			Long expedientTipusId,
			ExpedientTipusExportacioCommandDto command) {
		logger.debug(
				"Exportant el tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + command.getId() + ", " + 
				"command = " + command + ")");
		// Control d'accés
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId)) {
			// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
			tipus = expedientTipusRepository.findByEntornAndId(
					entorn,
					command.getId());
		} else {
			// Si no te permisos de disseny a damunt l'entorn només es poden veure
			// els tipus amb permisos de disseny
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					command.getId(),
					false,
					false,
					false,
					false,
					true);
		}
		
		// Construeix l'objecte
		ExpedientTipusExportacio exportacio = new ExpedientTipusExportacio();
		// Dades del tipus d'expedient
		exportacio.setAnyActual(tipus.getAnyActual());
		exportacio.setCodi(tipus.getCodi());
		exportacio.setDemanaNumero(tipus.getDemanaNumero());
		exportacio.setDemanaTitol(tipus.getDemanaTitol());
		exportacio.setExpressioNumero(tipus.getExpressioNumero());
		exportacio.setJbpmProcessDefinitionKey(tipus.getJbpmProcessDefinitionKey());
		exportacio.setNom(tipus.getNom());
		exportacio.setReiniciarCadaAny(tipus.isReiniciarCadaAny());
		exportacio.setResponsableDefecteCodi(tipus.getResponsableDefecteCodi());
		exportacio.setRestringirPerGrup(tipus.isRestringirPerGrup());
		exportacio.setSeleccionarAny(tipus.isSeleccionarAny());
		exportacio.setAmbRetroaccio(tipus.isAmbRetroaccio());
		exportacio.setAmbInfoPropia(tipus.isAmbInfoPropia());
		exportacio.setReindexacioAsincrona(tipus.isReindexacioAsincrona());
		exportacio.setSequencia(tipus.getSequencia());
		exportacio.setSequenciaDefault(tipus.getSequenciaDefault());
		exportacio.setTeNumero(tipus.getTeNumero());
		exportacio.setTeTitol(tipus.getTeTitol());
		exportacio.setTramitacioMassiva(tipus.isTramitacioMassiva());						
		Map<Integer,SequenciaAnyDto> sequenciaAnyMap = new HashMap<Integer, SequenciaAnyDto>();
		for (Entry<Integer, SequenciaAny> entry : tipus.getSequenciaAny().entrySet()) {
			SequenciaAny value = entry.getValue();
			SequenciaAnyDto valueDto = new SequenciaAnyDto();
			valueDto.setAny(value.getAny());
			valueDto.setId(value.getId());
			valueDto.setSequencia(value.getSequencia());
			sequenciaAnyMap.put(entry.getKey(), valueDto);
		}					
		exportacio.setSequenciaAny(sequenciaAnyMap);
		Map<Integer,SequenciaDefaultAnyDto> sequenciaAnyDefaultMap = new HashMap<Integer, SequenciaDefaultAnyDto>();
		for (Entry<Integer, SequenciaDefaultAny> entry : tipus.getSequenciaDefaultAny().entrySet()) {
			SequenciaDefaultAny value = entry.getValue();
			SequenciaDefaultAnyDto valueDto = new SequenciaDefaultAnyDto();
			valueDto.setAny(value.getAny());
			valueDto.setId(value.getId());
			valueDto.setSequenciaDefault(value.getSequenciaDefault());							
			sequenciaAnyDefaultMap.put(entry.getKey(), valueDto);
		}					    
		exportacio.setSequenciaDefaultAny(sequenciaAnyDefaultMap);
		// Integració amb forms
		if (command.isIntegracioForms()) {
			exportacio.setFormextUrl(tipus.getFormextUrl());
			exportacio.setFormextUsuari(tipus.getFormextUsuari());
			exportacio.setFormextContrasenya(tipus.getFormextContrasenya());
		}
		// Integració amb Sistra
		if (command.isIntegracioSistra()) {
			exportacio.setSistraTramitCodi(tipus.getSistraTramitCodi());
			for (MapeigSistra mapeig : tipus.getMapeigSistras())
				exportacio.getSistraMapejos().add(
						new MapeigSistraExportacio(
								mapeig.getCodiHelium(), 
								mapeig.getCodiSistra(), 
								MapeigSistraDto.TipusMapeig.valueOf(mapeig.getTipus().toString())));
		}
		// Estats
		if (command.getEstats().size() > 0)
			for (Estat estat: tipus.getEstats()) 
				if (command.getEstats().contains(estat.getCodi()))
					exportacio.getEstats().add(new EstatExportacio(estat.getCodi(), estat.getNom(), estat.getOrdre()));
		// Variables
		if (command.getVariables().size() > 0)
			for (Camp camp : tipus.getCamps()) 
				if (command.getVariables().contains(camp.getCodi())) {
					boolean necessitaDadesExternes = 
							TipusCamp.SELECCIO.equals(camp.getTipus()) 
							|| TipusCamp.SUGGEST.equals(camp.getTipus());			
					CampExportacio campExportacio = new CampExportacio(
		                    camp.getCodi(),
		                    CampTipusDto.valueOf(camp.getTipus().toString()),
		                    camp.getEtiqueta(),
		                    camp.getObservacions(),
		                    (necessitaDadesExternes) ? camp.getDominiId() : null,
		                    (necessitaDadesExternes) ? camp.getDominiParams() : null,
		                    (necessitaDadesExternes) ? camp.getDominiCampText() : null,
		                    (necessitaDadesExternes) ? camp.getDominiCampValor() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaParams() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaCampText() : null,
		                    (necessitaDadesExternes) ? camp.getConsultaCampValor() : null,
		                    camp.isMultiple(),
		                    camp.isOcult(),
		                    camp.isDominiIntern(),
		                    camp.isDominiCacheText(),
		                    (necessitaDadesExternes && camp.getEnumeracio() != null) ? camp.getEnumeracio().getCodi() : null,
		                    (necessitaDadesExternes && camp.getDomini() != null) ? camp.getDomini().getCodi() : null,
		                    (necessitaDadesExternes && camp.getConsulta() != null) ? camp.getConsulta().getCodi() : null,
		                    (camp.getAgrupacio() != null) ? camp.getAgrupacio().getCodi() : null,
		                    camp.getDefprocJbpmKey(),
		                    camp.getJbpmAction(),
		                    camp.getOrdre(),
		                    camp.isIgnored());
					exportacio.getCamps().add(campExportacio);
					// Afegeix les validacions del camp
					for (Validacio validacio: camp.getValidacions()) {
						campExportacio.addValidacio(new ValidacioExportacio(
								validacio.getNom(),
								validacio.getExpressio(),
								validacio.getMissatge(),
								validacio.getOrdre()));
					}
					// Afegeix els membres dels camps de tipus registre
					for (CampRegistre membre: camp.getRegistreMembres()) {
						campExportacio.addRegistreMembre(new RegistreMembreExportacio(
								membre.getMembre().getCodi(),
								membre.isObligatori(),
								membre.isLlistar(),
								membre.getOrdre()));
					}
				}
		// Agrupacions
		if (command.getAgrupacions().size() > 0)
			for (CampAgrupacio agrupacio: tipus.getAgrupacions()) 
				if (command.getAgrupacions().contains(agrupacio.getCodi()))
					exportacio.getAgrupacions().add(new AgrupacioExportacio(
							agrupacio.getCodi(),
							agrupacio.getNom(),
							agrupacio.getDescripcio(),
							agrupacio.getOrdre()));
		
		// Definicions de procés
		if (command.getDefinicionsProces().size() > 0)
			for (DefinicioProces definicio: tipus.getDefinicionsProces()) 
				if (command.getDefinicionsProces().contains(definicio.getJbpmKey()) 
						&& command.getDefinicionsVersions().get(definicio.getJbpmKey()).equals(definicio.getVersio()))							
					exportacio.getDefinicions().add(
								definicioProcesHelper.getExportacio(
									definicio.getId(),
									null /* no especifica el filtre per a la exportació. */)
							);
		// Enumeracions
		if (command.getEnumeracions().size() > 0) {
			EnumeracioExportacio enumeracioExportacio;
			for (Enumeracio enumeracio : tipus.getEnumeracions())
				if (command.getEnumeracions().contains(enumeracio.getCodi())) {
					enumeracioExportacio = new EnumeracioExportacio(
							enumeracio.getCodi(), 
							enumeracio.getNom());
					for (EnumeracioValors enumeracioValor : enumeracio.getEnumeracioValors())
						enumeracioExportacio.getValors().add(
								new EnumeracioValorExportacio(
										enumeracioValor.getCodi(),
										enumeracioValor.getNom(),
										enumeracioValor.getOrdre()));
					exportacio.getEnumeracions().add(enumeracioExportacio);
				}
		}
		// Documents
		if (command.getDocuments().size() > 0) {
			DocumentExportacio documentExportacio;
			for (Document document : tipus.getDocuments())
				if (command.getDocuments().contains(document.getCodi())) {
					documentExportacio = new DocumentExportacio(
							document.getCodi(),
							document.getNom(),
							document.getDescripcio(),
							document.getArxiuContingut(),
							document.getArxiuNom(),
							document.isPlantilla());
					documentExportacio.setCustodiaCodi(document.getCustodiaCodi());
					documentExportacio.setContentType(document.getContentType());
					documentExportacio.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
					documentExportacio.setAdjuntarAuto(document.isAdjuntarAuto());
					if (document.getCampData() != null)
						documentExportacio.setCodiCampData(document.getCampData().getCodi());
					documentExportacio.setConvertirExtensio(document.getConvertirExtensio());
					documentExportacio.setExtensionsPermeses(document.getExtensionsPermeses());
					exportacio.getDocuments().add(documentExportacio);
				}
		}		
		// Terminis
		if (command.getTerminis().size() > 0) {
			TerminiExportacio terminiExportacio;
			for (Termini termini : tipus.getTerminis())
				if (command.getTerminis().contains(termini.getCodi())) {
					terminiExportacio = new TerminiExportacio(
							termini.getCodi(),
							termini.getNom(),
							termini.getDescripcio(),
							termini.isDuradaPredefinida(),
							termini.getAnys(),
							termini.getMesos(),
							termini.getDies(),
							termini.isLaborable(),
							termini.getDiesPrevisAvis(),
							termini.isAlertaPrevia(),
							termini.isAlertaFinal(),
							termini.isAlertaCompletat(),
							termini.isManual());
					exportacio.getTerminis().add(terminiExportacio);
				}
		}	
		// Accions
		if (command.getAccions().size() > 0) {
			AccioExportacio accioExportacio;
			for (Accio accio : tipus.getAccions())
				if (command.getAccions().contains(accio.getCodi())) {
					accioExportacio = new AccioExportacio(
							accio.getCodi(),
							accio.getNom(),
							accio.getDescripcio(),
							accio.getDefprocJbpmKey(),
							accio.getJbpmAction(),
							accio.isPublica(),
							accio.isOculta(),
							accio.getRols());
					exportacio.getAccions().add(accioExportacio);
				}
		}	
		// Dominis
		if (command.getDominis().size() > 0) {
			DominiExportacio dominiExportacio;
			for (Domini domini : tipus.getDominis())
				if (command.getDominis().contains(domini.getCodi())) {
					dominiExportacio = new DominiExportacio(
							domini.getCodi(),
							domini.getNom(),
							domini.getTipus().toString());
					dominiExportacio.setDescripcio(domini.getDescripcio());
					dominiExportacio.setUrl(domini.getUrl());
					if (domini.getTipusAuth() != null)
						dominiExportacio.setTipusAuth(DominiDto.TipusAuthDomini.valueOf(domini.getTipusAuth().toString()));
					if (domini.getOrigenCredencials() != null)
						dominiExportacio.setOrigenCredencials(DominiDto.OrigenCredencials.valueOf(domini.getOrigenCredencials().toString()));
					dominiExportacio.setUsuari(domini.getUsuari());
					dominiExportacio.setContrasenya(domini.getContrasenya());
					dominiExportacio.setJndiDatasource(domini.getJndiDatasource());
					dominiExportacio.setSql(domini.getSql());
					dominiExportacio.setCacheSegons(domini.getCacheSegons());
					if (domini.getTimeout() != null)
						dominiExportacio.setTimeout(domini.getTimeout().intValue());
					exportacio.getDominis().add(dominiExportacio);
				}
		}	
		// Consultes
		if (command.getConsultes().size() > 0) {
			ConsultaExportacio consultaExportacio;
			for (Consulta consulta : tipus.getConsultes())
				if (command.getConsultes().contains(consulta.getCodi())) {
					consultaExportacio = new ConsultaExportacio(
							consulta.getCodi(),
							consulta.getNom());
					consultaExportacio.setDescripcio(consulta.getDescripcio());
					consultaExportacio.setValorsPredefinits(consulta.getValorsPredefinits());
					consultaExportacio.setExportarActiu(consulta.isExportarActiu());
					consultaExportacio.setOcultarActiu(consulta.isOcultarActiu());
					consultaExportacio.setOrdre(consulta.getOrdre());
					if (consulta.getInformeContingut() != null && consulta.getInformeContingut().length > 0) {
						consultaExportacio.setInformeNom(consulta.getInformeNom());
						consultaExportacio.setInformeContingut(consulta.getInformeContingut());
					}
					exportacio.getConsultes().add(consultaExportacio);
					// Camps de la consulta
					for (ConsultaCamp consultaCamp: consulta.getCamps()) {
						ConsultaCampExportacio consultaCampExp = new ConsultaCampExportacio(
								consultaCamp.getCampCodi(),
								consultaCamp.getDefprocJbpmKey(),
								ConsultaCampDto.TipusConsultaCamp.valueOf(consultaCamp.getTipus().toString()),
								consultaCamp.getParamTipus() != null?
										ConsultaCampDto.TipusParamConsultaCamp.valueOf(consultaCamp.getParamTipus().toString())
										: null,
								consultaCamp.getCampDescripcio(),
								consultaCamp.getOrdre());
						consultaExportacio.getCamps().add(consultaCampExp);
					}
				}
		}	

		return exportacio;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ExpedientTipusDto importar(
			Long entornId, 
			Long expedientTipusId, 
			ExpedientTipusExportacioCommandDto command,
			ExpedientTipusExportacio importacio) {
		logger.debug(
				"Important un tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + command.getId() + ", " + 
				"command = " + command + ", " + 
				"importacio = " + importacio + ")");
		// Control d'accés
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// Dades del tipus d'expedient
		boolean expedientTipusExisteix = expedientTipusId != null;
		ExpedientTipus expedientTipus;
		if (!expedientTipusExisteix) {
			// Nou tipus d'expedient
			expedientTipus = new ExpedientTipus();
			expedientTipus.setEntorn(entorn);
			expedientTipus.setCodi(command.getCodi());
			expedientTipus.setNom(importacio.getNom());
			expedientTipus = expedientTipusRepository.save(expedientTipus);
		} else			
			// Recupera el tipus d'expedient existent
			if (entornHelper.potDissenyarEntorn(entornId)) {
				// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
				expedientTipus = expedientTipusRepository.findByEntornAndId(
						entorn,
						command.getId());
			} else {
				// Si no te permisos de disseny a damunt l'entorn només es poden veure
				// els tipus amb permisos de disseny
				expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
						command.getId(),
						false,
						false,
						false,
						false,
						true);
			}
		if (!expedientTipusExisteix || command.isDadesBasiques()) {
			expedientTipus.setAmbInfoPropia(importacio.isAmbInfoPropia());
			expedientTipus.setTeTitol(importacio.isTeTitol());
			expedientTipus.setDemanaTitol(importacio.isDemanaTitol());
			expedientTipus.setTeNumero(importacio.isTeNumero());
			expedientTipus.setDemanaNumero(importacio.isDemanaNumero());
			expedientTipus.setExpressioNumero(importacio.getExpressioNumero());
			expedientTipus.setJbpmProcessDefinitionKey(importacio.getJbpmProcessDefinitionKey());
			expedientTipus.setReiniciarCadaAny(importacio.isReiniciarCadaAny());
			expedientTipus.setSequencia(importacio.getSequencia());
			expedientTipus.setResponsableDefecteCodi(importacio.getResponsableDefecteCodi());
			expedientTipus.setRestringirPerGrup(importacio.isRestringirPerGrup());
			expedientTipus.setSeleccionarAny(importacio.isSeleccionarAny());
			expedientTipus.setAmbRetroaccio(importacio.isAmbRetroaccio());
			expedientTipus.setReindexacioAsincrona(importacio.isReindexacioAsincrona());
			expedientTipus.setTramitacioMassiva(importacio.isTramitacioMassiva());
			if (importacio.isReiniciarCadaAny()) {
				Collection<SequenciaAnyDto> sequencies = importacio.getSequenciaAny().values();
				for (SequenciaAnyDto sequencia : sequencies) {
					SequenciaAny anyexpedientTipus = new SequenciaAny(
							expedientTipus, 
							sequencia.getAny(), 
							sequencia.getSequencia());
					expedientTipus.getSequenciaAny().put(anyexpedientTipus.getAny(), anyexpedientTipus);
				}
			}
		}
		// Integració amb formularis externs
		if (command.isIntegracioForms()) {
			expedientTipus.setFormextUrl(importacio.getFormextUrl());
			expedientTipus.setFormextUsuari(importacio.getFormextUsuari());
			expedientTipus.setFormextContrasenya(importacio.getFormextContrasenya());
		}
		// Integració amb Sistra
		if (command.isIntegracioSistra()) {
			expedientTipus.setSistraTramitCodi(importacio.getSistraTramitCodi());
			if (expedientTipusExisteix) {
				// esborra els mapegos existents
				for (MapeigSistra mapeig : expedientTipus.getMapeigSistras())
					mapeigSistraRepository.delete(mapeig);
				expedientTipus.getMapeigSistras().clear();
				mapeigSistraRepository.flush();
			}
			for (MapeigSistraExportacio mapeig : importacio.getSistraMapejos())
				expedientTipus.getMapeigSistras().add(
						new MapeigSistra(
								expedientTipus,
								mapeig.getCodiHelium(), 
								mapeig.getCodiSistra(), 
								MapeigSistra.TipusMapeig.valueOf(mapeig.getTipus().toString())));
		}
		
		boolean sobreEscriure = command.isSobreEscriure();
		// Estats
		Estat estat;
		if (command.getEstats().size() > 0)
			for(EstatExportacio estatExportat : importacio.getEstats() )
				if (command.getEstats().contains(estatExportat.getCodi())){
					estat = null;
					if (expedientTipusExisteix) {
						estat = estatRepository.findByExpedientTipusAndCodi(expedientTipus, estatExportat.getCodi());
					}
					if (estat == null || sobreEscriure) {
						if (estat == null) {
							estat = new Estat(
									expedientTipus, 
									estatExportat.getCodi(), 
									estatExportat.getNom());
							expedientTipus.getEstats().add(estat);
							estatRepository.save(estat);
						} else {
							estat.setNom(estatExportat.getNom());
						}
						estat.setOrdre(estatExportat.getOrdre());
					}
				}
		
		// Agrupacions
		Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
		CampAgrupacio agrupacio;
		if (command.getAgrupacions().size() > 0)
			for(AgrupacioExportacio agrupacioExportat : importacio.getAgrupacions() )
				if (command.getAgrupacions().contains(agrupacioExportat.getCodi())){
					agrupacio = null;
					if (expedientTipusExisteix) {
						agrupacio = campAgrupacioRepository.findByExpedientTipusIdAndCodi(expedientTipus.getId(), agrupacioExportat.getCodi());
					}
					if (agrupacio == null || sobreEscriure) {
						if (agrupacio == null) {
							agrupacio = new CampAgrupacio(
									expedientTipus, 
									agrupacioExportat.getCodi(), 
									agrupacioExportat.getNom(),
									agrupacioExportat.getOrdre());
							expedientTipus.getAgrupacions().add(agrupacio);
							campAgrupacioRepository.save(agrupacio);
						} else {
							agrupacio.setNom(agrupacioExportat.getNom());
							agrupacio.setOrdre(agrupacioExportat.getOrdre());
						}
						agrupacio.setDescripcio(agrupacioExportat.getDescripcio());
					}
					agrupacions.put(agrupacio.getCodi(), agrupacio);
				}

		// Enumeracions
		Map<String, Enumeracio> enumeracions = new HashMap<String, Enumeracio>();
		Enumeracio enumeracio;
		if (command.getEnumeracions().size() > 0)
			for(EnumeracioExportacio enumeracioExportat : importacio.getEnumeracions() )
				if (command.getEnumeracions().contains(enumeracioExportat.getCodi())){
					enumeracio = null;
					if (expedientTipusExisteix) {
						enumeracio = enumeracioRepository.findByExpedientTipusAndCodi(expedientTipus, enumeracioExportat.getCodi());
					}
					if (enumeracio == null || sobreEscriure) {
						if (enumeracio == null) {
							enumeracio = new Enumeracio(
									entorn, 
									enumeracioExportat.getCodi(), 
									enumeracioExportat.getNom());
							enumeracio.setExpedientTipus(expedientTipus);
							expedientTipus.getEnumeracions().add(enumeracio);
							enumeracioRepository.save(enumeracio);
						} else {
							enumeracio.setNom(enumeracioExportat.getNom());
							// Esborra tots els valors existents
							for (EnumeracioValors valor : enumeracio.getEnumeracioValors())
								enumeracioValorsRepository.delete(valor);
							enumeracio.getEnumeracioValors().clear();
							enumeracioValorsRepository.flush();
						}
						// Afegeix tots els valors
						for (EnumeracioValorExportacio valorExportat : enumeracioExportat.getValors()) {
							EnumeracioValors valor = new EnumeracioValors(enumeracio, valorExportat.getCodi(), valorExportat.getNom());
							valor.setOrdre(valorExportat.getOrdre());
							enumeracio.getEnumeracioValors().add(valor);
							enumeracioValorsRepository.save(valor);
						}
					}
					enumeracions.put(enumeracio.getCodi(), enumeracio);
				}
		
		// Dominis
		Map<String, Domini> dominis = new HashMap<String, Domini>();
		Domini domini;
		if (command.getDominis().size() > 0)
			for(DominiExportacio dominiExportat : importacio.getDominis() )
				if (command.getDominis().contains(dominiExportat.getCodi())){
					domini = null;
					if (expedientTipusExisteix) {
						domini = dominiRepository.findByExpedientTipusAndCodi(expedientTipus, dominiExportat.getCodi());
					}
					if (domini == null || sobreEscriure) {
						if (domini == null) {
							domini = new Domini(
									dominiExportat.getCodi(), 
									dominiExportat.getNom(),
									entorn);
							domini.setExpedientTipus(expedientTipus);
							expedientTipus.getDominis().add(domini);
							domini.setTipus(TipusDomini.valueOf(dominiExportat.getTipus().toString()));
							dominiRepository.save(domini);
						} else {
							domini.setNom(dominiExportat.getNom());
							domini.setTipus(TipusDomini.valueOf(dominiExportat.getTipus().toString()));
						}
						domini.setDescripcio(dominiExportat.getDescripcio());
						domini.setCacheSegons(dominiExportat.getCacheSegons());
						domini.setTimeout(dominiExportat.getTimeout());
						// SQL
						domini.setJndiDatasource(dominiExportat.getJndiDatasource());
						domini.setSql(dominiExportat.getSql());
						// WS
						domini.setUrl(dominiExportat.getUrl());
						domini.setTipusAuth(dominiExportat.getTipusAuth() != null?
											 Domini.TipusAuthDomini.valueOf(dominiExportat.getTipusAuth().toString())
											 : null);
						domini.setOrigenCredencials(dominiExportat.getOrigenCredencials() != null?
											Domini.OrigenCredencials.valueOf(dominiExportat.getOrigenCredencials().toString())
											: null);
						domini.setUsuari(dominiExportat.getUsuari());
						domini.setContrasenya(dominiExportat.getContrasenya());
					}
					dominis.put(domini.getCodi(), domini);
				}
		
		// Camps
		Map<String, Camp> camps = new HashMap<String, Camp>();
		Map<Camp, CampExportacio> registres = new HashMap<Camp, CampExportacio>();
		Map<Camp, CampExportacio> campsTipusConsulta = new HashMap<Camp, CampExportacio>();
		Camp camp;
		if (command.getVariables().size() > 0) {
			for(CampExportacio campExportat : importacio.getCamps() )
				if (command.getVariables().contains(campExportat.getCodi())){
					camp = null;
					if (expedientTipusExisteix) {
						camp = campRepository.findByExpedientTipusAndCodi(expedientTipus, campExportat.getCodi());
					}
					if (camp == null || sobreEscriure) {
						if (camp == null) {
							camp = new Camp(
									expedientTipus, 
									campExportat.getCodi(),
									TipusCamp.valueOf(campExportat.getTipus().toString()),
									campExportat.getEtiqueta());
							expedientTipus.getCamps().add(camp);
							camp = campRepository.save(camp);
						} else {
							camp.setTipus(TipusCamp.valueOf(campExportat.getTipus().toString()));
							camp.setEtiqueta(campExportat.getEtiqueta());
						}
						camp.setIgnored(campExportat.isIgnored());
						camp.setObservacions(campExportat.getObservacions());
						camp.setDominiId(campExportat.getDominiId());
						camp.setDominiParams(campExportat.getDominiParams());
						camp.setDominiCampText(campExportat.getDominiCampText());
						camp.setDominiCampValor(campExportat.getDominiCampValor());
						camp.setDominiCacheText(campExportat.isDominiCacheText());
						camp.setConsultaParams(campExportat.getConsultaParams());
						camp.setConsultaCampText(campExportat.getConsultaCampText());
						camp.setConsultaCampValor(campExportat.getConsultaCampValor());
						camp.setMultiple(campExportat.isMultiple());
						camp.setOcult(campExportat.isOcult());
						camp.setDominiIntern(campExportat.isDominiIntern());
						camp.setDefprocJbpmKey(campExportat.getDefprocJbpmKey());
						camp.setJbpmAction(campExportat.getJbpmAction());
						camp.setOrdre(campExportat.getOrdre());
						
						// Esborra les validacions existents
						for (Validacio validacio : camp.getValidacions())
							campValidacioRepository.delete(validacio);
						camp.getValidacions().clear();
						campValidacioRepository.flush();
						// Afegeix les noves validacions
						for (ValidacioExportacio validacioExportat : campExportat.getValidacions()) {
							Validacio validacio = new Validacio(
									camp, 
									validacioExportat.getExpressio(), 
									validacioExportat.getMissatge());
							validacio.setNom(validacioExportat.getNom());
							validacio.setOrdre(validacioExportat.getOrdre());
							campValidacioRepository.save(validacio);
							camp.getValidacions().add(validacio);
						}
						// Agrupació del camp
						if (campExportat.getAgrupacioCodi() != null && agrupacions.containsKey(campExportat.getAgrupacioCodi()))
							camp.setAgrupacio(agrupacions.get(campExportat.getAgrupacioCodi()));
						// Enumeració del camp
						if (campExportat.getCodiEnumeracio() != null && enumeracions.containsKey(campExportat.getCodiEnumeracio()))
							camp.setEnumeracio(enumeracions.get(campExportat.getCodiEnumeracio()));
						// Domini del camp
						if (campExportat.getCodiDomini() != null && dominis.containsKey(campExportat.getCodiDomini()))
							camp.setDomini(dominis.get(campExportat.getCodiDomini()));
						// Guarda els camps de tipus consulta per processar-los després de les consultes
						if (campExportat.getCodiConsulta() != null)
							campsTipusConsulta.put(camp, campExportat);
						// Guarda els registres per processar-los després de tots els camps
						if (camp.getTipus() == TipusCamp.REGISTRE) {
							registres.put(camp, campExportat);
						}						
					}
					camps.put(camp.getCodi(), camp);
				}		
			// Tracta els registres
			CampExportacio campExportat;
			for (Camp registre : registres.keySet()) {
				// Esborra la relació amb els membres existents
				if (!registre.getRegistreMembres().isEmpty()) {
					for (CampRegistre campRegistre : registre.getRegistreMembres()) {
						campRegistre.getMembre().getRegistrePares().remove(campRegistre);
						campRegistre.setMembre(null);
						campRegistre.setRegistre(null);
						campRegistreRepository.delete(campRegistre);
					}
					registre.getRegistreMembres().clear();
					campRegistreRepository.flush();
				}
				// afegeix la informació dels registres
				campExportat = registres.get(registre);
				for (RegistreMembreExportacio registreMembre : campExportat.getRegistreMembres()) {
					CampRegistre campRegistre = new CampRegistre(
							camps.get(registre.getCodi()),
							camps.get(registreMembre.getCodi()),
							registreMembre.getOrdre());
					campRegistre.setLlistar(registreMembre.isLlistar());
					campRegistre.setObligatori(registreMembre.isObligatori());
					registre.getRegistreMembres().add(campRegistre);
					campRegistreRepository.save(campRegistre);
				}
			}
		}
		
		// Documents
		Map<String, Document> documents = new HashMap<String, Document>();
		Document document;
		if (command.getDocuments().size() > 0)
			for(DocumentExportacio documentExportat : importacio.getDocuments() )
				if (command.getDocuments().contains(documentExportat.getCodi())){
					document = null;
					if (expedientTipusExisteix) {
						document = documentRepository.findByExpedientTipusAndCodi(expedientTipus, documentExportat.getCodi());
					}
					if (document == null || sobreEscriure) {
						if (document == null) {
							document = new Document(
									expedientTipus, 
									documentExportat.getCodi(), 
									documentExportat.getNom());
							expedientTipus.getDocuments().add(document);
							documentRepository.save(document);
						} else {
							document.setNom(documentExportat.getNom());
						}
						document.setDescripcio(documentExportat.getDescripcio());
						document.setArxiuNom(documentExportat.getArxiuNom());
						document.setArxiuContingut(documentExportat.getArxiuContingut());
						document.setPlantilla(documentExportat.isPlantilla());
						document.setCustodiaCodi(documentExportat.getCustodiaCodi());
						document.setContentType(documentExportat.getContentType());
						document.setTipusDocPortasignatures(documentExportat.getTipusDocPortasignatures());
						document.setAdjuntarAuto(documentExportat.isAdjuntarAuto());
						if (documentExportat.getCodiCampData() != null)
							document.setCampData(camps.get(documentExportat.getCodiCampData()));
						document.setConvertirExtensio(documentExportat.getConvertirExtensio());
						document.setExtensionsPermeses(documentExportat.getExtensionsPermeses());
					}
					documents.put(documentExportat.getCodi(), document);
				}	
		
		// Terminis
		Termini termini;
		if (command.getTerminis().size() > 0)
			for(TerminiExportacio terminiExportat : importacio.getTerminis() )
				if (command.getTerminis().contains(terminiExportat.getCodi())){
					termini = null;
					if (expedientTipusExisteix) {
						termini = terminiRepository.findByExpedientTipusAndCodi(expedientTipus, terminiExportat.getCodi());
					}
					if (termini == null || sobreEscriure) {
						if (termini == null) {
							termini = new Termini(
									expedientTipus,
									terminiExportat.getCodi(),
									terminiExportat.getNom(),
									terminiExportat.getAnys(),
									terminiExportat.getMesos(),
									terminiExportat.getDies(),
									terminiExportat.isLaborable());
							expedientTipus.getTerminis().add(termini);
							terminiRepository.save(termini);
						} else {
							termini.setNom(terminiExportat.getNom());
							termini.setNom(terminiExportat.getNom());
							termini.setAnys(terminiExportat.getAnys());
							termini.setMesos(terminiExportat.getMesos());
							termini.setDies(terminiExportat.getDies());
							termini.setLaborable(terminiExportat.isLaborable());
						}
						termini.setDuradaPredefinida(terminiExportat.isDuradaPredefinida());
						termini.setDescripcio(terminiExportat.getDescripcio());
						termini.setDiesPrevisAvis(terminiExportat.getDiesPrevisAvis());
						termini.setAlertaPrevia(terminiExportat.isAlertaPrevia());
						termini.setAlertaFinal(terminiExportat.isAlertaFinal());
						termini.setAlertaCompletat(terminiExportat.isAlertaCompletat());
						termini.setManual(terminiExportat.isManual());
					}
				}

		// Accions
		Accio accio;
		if (command.getAccions().size() > 0)
			for(AccioExportacio accioExportat : importacio.getAccions() )
				if (command.getAccions().contains(accioExportat.getCodi())){
					accio = null;
					if (expedientTipusExisteix) {
						accio = accioRepository.findByExpedientTipusIdAndCodi(expedientTipus.getId(), accioExportat.getCodi());
					}
					if (accio == null || sobreEscriure) {
						if (accio == null) {
							accio = new Accio(
									expedientTipus, 
									accioExportat.getCodi(), 
									accioExportat.getNom(),
									accioExportat.getDefprocJbpmKey(),
									accioExportat.getJbpmAction());
							expedientTipus.getAccions().add(accio);
							accioRepository.save(accio);
						} else {
							accio.setNom(accioExportat.getNom());
							accio.setJbpmAction(accioExportat.getJbpmAction());
						}
						accio.setDescripcio(accioExportat.getDescripcio());
						accio.setPublica(accioExportat.isPublica());
						accio.setOculta(accioExportat.isOculta());
						accio.setRols(accioExportat.getRols());
					}
				}
		
		// Consultes
		Map<String, Consulta> consultes = new HashMap<String, Consulta>();
		Consulta consulta;
		if (command.getConsultes().size() > 0)
			for(ConsultaExportacio consultaExportat : importacio.getConsultes() )
				if (command.getConsultes().contains(consultaExportat.getCodi())) {
					consulta = null;
					if (expedientTipusExisteix) {
						consulta = consultaRepository.findByExpedientTipusAndCodi(expedientTipus, consultaExportat.getCodi());
					}
					if (consulta == null || sobreEscriure) {
						if (consulta == null) {
							consulta = new Consulta(
									consultaExportat.getCodi(), 
									consultaExportat.getNom());
							consulta.setEntorn(entorn);
							consulta.setExpedientTipus(expedientTipus);
							expedientTipus.getConsultes().add(consulta);
							consultaRepository.save(consulta);
						} else {
							consulta.setNom(consultaExportat.getNom());
							// Esborra els paràmetres existents
							for (ConsultaCamp consultaCamp : consulta.getCamps())
								consultaCampRepository.delete(consultaCamp);
							consulta.getCamps().clear();
							consultaCampRepository.flush();
						}
						consulta.setDescripcio(consultaExportat.getDescripcio());
						consulta.setValorsPredefinits(consultaExportat.getValorsPredefinits());
						consulta.setExportarActiu(consultaExportat.isExportarActiu());
						consulta.setOcultarActiu(consultaExportat.isOcultarActiu());
						consulta.setOrdre(consultaExportat.getOrdre());	
						if (consultaExportat.getInformeContingut() != null) {
							consulta.setInformeNom(consultaExportat.getInformeNom());
							consulta.setInformeContingut(consultaExportat.getInformeContingut());
						} else {
							consulta.setInformeNom(null);
							consulta.setInformeContingut(null);
						}
						// Variables i paràmetres de la consulta
						for (ConsultaCampExportacio consultaCampExportacio : consultaExportat.getCamps()) {
							ConsultaCamp consultaCamp = new ConsultaCamp(
									consultaCampExportacio.getCampCodi(), 
									ConsultaCamp.TipusConsultaCamp.valueOf(
											consultaCampExportacio.getTipusConsultaCamp().toString()));
							consultaCamp.setConsulta(consulta);
							consultaCamp.setDefprocJbpmKey(consultaCampExportacio.getJbpmKey());
							consultaCamp.setCampDescripcio(consultaCampExportacio.getCampDescripcio());
							consultaCamp.setParamTipus(
									consultaCampExportacio.getTipusParamConsultaCamp() != null?
									TipusParamConsultaCamp.valueOf(consultaCampExportacio.getTipusParamConsultaCamp().toString())
									: null);
							consultaCamp.setOrdre(consultaCampExportacio.getOrdre());
							consultaCampRepository.save(consultaCamp);
							consulta.getCamps().add(consultaCamp);
						}
					}
					consultes.put(consulta.getCodi(), consulta);
				}
		
		// Definicions
		DefinicioProces definicioProces;
		if (command.getDefinicionsProces().size() > 0)
			for(DefinicioProcesExportacio definicioExportat : importacio.getDefinicions() )
				if (command.getDefinicionsProces().contains(definicioExportat.getDefinicioProcesDto().getJbpmKey())){
					definicioProces = definicioProcesHelper.importar(
							entornId, 
							expedientTipus.getId(),
							definicioExportat,
							null /* no especifica el filtre per a la importació. */,
							command.isSobreEscriure());
					expedientTipus.addDefinicioProces(definicioProces);
				}
		
		// Tracta camps de tipus consulta completant la referència a la consulta
		CampExportacio campExportat;
		for (Camp campConsulta : campsTipusConsulta.keySet()) {
			// afegeix la informació dels registres
			campExportat = campsTipusConsulta.get(campConsulta);
			if (consultes.containsKey(campExportat.getCodiConsulta()))
				campConsulta.setConsulta(consultes.get(campExportat.getCodiConsulta()));
		}

		return conversioTipusHelper.convertir(
				expedientTipusRepository.save(expedientTipus),
				ExpedientTipusDto.class);
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisConsultar(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permisos de consulta (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntorn(entorn);
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.DESIGN_ADMIN,
							ExtendedPermission.DESIGN_DELEG,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisConsultar(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permisos de consulta (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId)) {
			// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
			tipus = expedientTipusRepository.findByEntornAndId(
					entorn,
					expedientTipusId);
		} else {
			// Si no te permisos de disseny a damunt l'entorn només es poden veure
			// els tipus amb permisos de disseny
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					false,
					false,
					false,
					false,
					true);
		}
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisDissenyar(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permisos de disseny (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntorn(entorn);
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.DESIGN_ADMIN,
							ExtendedPermission.DESIGN_DELEG,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisDissenyar(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permisos de disseny (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId)) {
			// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
			tipus = expedientTipusRepository.findByEntornAndId(
					entornHelper.getEntornComprovantPermisos(
							entornId,
							true),
					expedientTipusId);
		} else {
			// Si no te permisos de disseny a damunt l'entorn només es poden veure
			// els tipus amb permisos de disseny
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					false,
					false,
					false,
					false,
					true);
		}
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<ExpedientTipusDto> findAmbEntornPermisCrear(
			Long entornId) {
		logger.debug(
				"Consultant tipus d'expedient per un entorn i amb permis de creació (" +
				"entornId=" + entornId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<ExpedientTipus> tipuss = expedientTipusRepository.findByEntorn(entorn);
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAny(
					tipuss,
					new ObjectIdentifierExtractor<ExpedientTipus>() {
						@Override
						public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.CREATE,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return conversioTipusHelper.convertirList(
				tipuss,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbIdPermisCrear(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant tipus d'expedient amb id i amb permis de creació (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId = " + expedientTipusId + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus tipus;
		if (entornHelper.potDissenyarEntorn(entornId)) {
			// Si te permisos de disseny a damunt l'entorn pot veure tots els tipus
			tipus = expedientTipusRepository.findByEntornAndId(
					entorn,
					expedientTipusId);
		} else {
			// Si no te permisos de disseny a damunt l'entorn només es poden veure
			// els tipus amb permisos de disseny
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					false,
					false,
					true,
					false,
					false);
		}
		return conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findAmbCodiPerValidarRepeticio(
			Long entornId,
			String codi) {
		logger.debug(
				"Consultant tipus d'expedient amb codi (" +
				"entornId=" + entornId + ", " +
				"codi = " + codi + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		return conversioTipusHelper.convertir(
				expedientTipusRepository.findByEntornAndCodi(entorn, codi),
				ExpedientTipusDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTipusDto> findPerDatatable(
			Long entornId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant tipus d'expedient per datatable (" +
				"entornId=" + entornId + ", " +
				"filtre=" + filtre + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbEntorn(entorn);
		// Filtra els expedients deixant només els permesos
		if (!entornHelper.potDissenyarEntorn(entornId)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Iterator<Long> it = tipusPermesosIds.iterator();
			while (it.hasNext()) {
				Long id = it.next();
				if (!permisosHelper.isGrantedAny(
						id,
						ExpedientTipus.class,
						new Permission[] {
								ExtendedPermission.DESIGN,
								ExtendedPermission.MANAGE,
								ExtendedPermission.ADMINISTRATION},
						auth)) {
					it.remove();
				}
			}
		}
		PaginaDto<ExpedientTipusDto> pagina = paginacioHelper.toPaginaDto(
				tipusPermesosIds.size() > 0 ?
					expedientTipusRepository.findByFiltreGeneralPaginat(
							entorn, 
							tipusPermesosIds,
							filtre == null || "".equals(filtre),
							filtre,
							paginacioHelper.toSpringDataPageable(
									paginacioParams))
						// pàgina buida si no hi ha tipus permesos
					:	new PageImpl<ExpedientTipus>(new ArrayList<ExpedientTipus>()),
				ExpedientTipusDto.class);
		// Afegeix el contador de permisos
		List<Long> ids = new ArrayList<Long>();
		for (ExpedientTipusDto dto: pagina.getContingut()) {
			ids.add(dto.getId());
		}
		Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(
				ids,
				ExpedientTipus.class);
		for (ExpedientTipusDto dto: pagina.getContingut()) {
			if (permisos.get(dto.getId()) != null) {
				dto.setPermisCount(permisos.get(dto.getId()).size());
			}
		}
		return pagina;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			PermisDto permis) {
		logger.debug(
				"Creant permis per al tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permis=" + permis + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		permisosHelper.updatePermis(
				expedientTipusId,
				ExpedientTipus.class,
				permis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId) {
		logger.debug(
				"Esborrant permis per al tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permisId=" + permisId + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		permisosHelper.deletePermis(
				expedientTipusId,
				ExpedientTipus.class,
				permisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PermisDto> permisFindAll(
			Long entornId,
			Long expedientTipusId) {
		logger.debug(
				"Consultant permisos del tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		return permisosHelper.findPermisos(
				expedientTipusId,
				ExpedientTipus.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId) {
		logger.debug(
				"Consultant un permis donat el seu id (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"permisId=" + permisId + ")");
		entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		expedientTipusHelper.comprovarPermisDissenyEntornITipusExpedient(
				entornId,
				expedientTipusId);
		List<PermisDto> permisos = permisosHelper.findPermisos(
				expedientTipusId,
				ExpedientTipus.class);
		for (PermisDto permis: permisos) {
			if (permis.getId().equals(permisId)) {
				return permis;
			}
		}
		throw new NoTrobatException(PermisDto.class, permisId);
	}
	
	// MANTENIMENT D'ENUMERACIONS

	@Override
	@Transactional(readOnly = true)
	public List<EnumeracioDto> enumeracioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<Enumeracio> enumeracions = enumeracioRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									enumeracions, 
									EnumeracioDto.class);
	}
	
	// MANTENIMENT DE DOMINIS

	@Override
	@Transactional(readOnly = true)
	public List<DominiDto> dominiFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<Domini> dominins = dominiRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									dominins, 
									DominiDto.class);
	}

	// MANTENIMENT DE CONSULTES

	@Override
	@Transactional(readOnly = true)
	public List<ConsultaDto> consultaFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<Consulta> consultans = consultaRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									consultans, 
									ConsultaDto.class);
	}
	
	/***********************************************/
	/*****************ENUMERACIONS******************/
	/***********************************************/
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<EnumeracioDto> enumeracioFindPerDatatable(Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		logger.debug(
				"Consultant les enumeracions per al tipus d'expedient per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");
		
		Page<Enumeracio> resultats = enumeracioRepository.findByFiltrePaginat(
				expedientTipusId,
				filtre == null || "".equals(filtre),
				filtre,
				paginacioHelper.toSpringDataPageable(paginacioParams));
		
		PaginaDto<EnumeracioDto> out = paginacioHelper.toPaginaDto(resultats, EnumeracioDto.class);
		
		//Recuperam el nombre de valors per cada enumerat
		if (out!=null) {
			for (int o=0; o<out.getContingut().size(); o++) {
				List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrdenat(out.getContingut().get(o).getId());
				if (valors!=null) {
					out.getContingut().get(o).setNumValors(valors.size());
				}else{
					out.getContingut().get(o).setNumValors(new Integer(0));
				}
			}
		}
		
		return out;
	}
	
	@Override
	@Transactional
	public EnumeracioDto enumeracioCreate(Long expedientTipusId, Long entornId, EnumeracioDto enumeracio)
			throws PermisDenegatException {

		logger.debug(
				"Creant nova enumeracio per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"entornId =" + entornId + ", " +
				"document=" + enumeracio + ")");
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		Entorn entorn = entornHelper.getEntornComprovantPermisos(entornId, true, true);
		
		Enumeracio entity = new Enumeracio();
		entity.setCodi(enumeracio.getCodi());
		entity.setNom(enumeracio.getNom());

		entity.setExpedientTipus(expedientTipus);
		entity.setEntorn(entorn);

		return conversioTipusHelper.convertir(
				enumeracioRepository.save(entity),
				EnumeracioDto.class);
	}
	
	@Override
	@Transactional
	public EnumeracioDto enumeracioFindAmbCodi(Long expedientTipusId, String codi) {
		EnumeracioDto ret = null;
		logger.debug(
				"Consultant l'enumeració del tipus d'expedient per codi (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		Enumeracio enumeracio = enumeracioRepository.findByExpedientTipusAndCodi(expedientTipus, codi);
		if (enumeracio != null)
			ret = conversioTipusHelper.convertir(
					enumeracio,
					EnumeracioDto.class);
		return ret;
	}
	
	@Override
	@Transactional
	public void enumeracioDelete(Long enumeracioId) throws NoTrobatException, PermisDenegatException, ValidacioException {
		
		logger.debug(
				"Esborrant l'enumeració del tipus d'expedient (" +
				"enumeracioId=" + enumeracioId +  ")");
		
		Enumeracio entity = enumeracioRepository.findOne(enumeracioId);

		if (entity.getCamps()!=null && entity.getCamps().size()>0) {
			throw new ValidacioException(messageHelper.getMessage("expedient.tipus.enumeracio.controller.eliminat.us"));
		}

		List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrdenat(entity.getId());
		if (valors!=null) {
			for (int o=0; o<valors.size(); o++) {
				enumeracioValorsRepository.delete(valors.get(o));
			}
		}
		enumeracioValorsRepository.flush();
		
		enumeracioRepository.delete(entity);
		enumeracioRepository.flush();
	}
	
	@Override
	@Transactional
	public EnumeracioDto enumeracioFindAmbId(Long enumeracioId) throws NoTrobatException {
		logger.debug(
				"Consultant l'enumeracio del tipus d'expedient amb id (" +
				"enumeracioId=" + enumeracioId +  ")");
		Enumeracio enumeracio = enumeracioRepository.findOne(enumeracioId);
		if (enumeracio == null) {
			throw new NoTrobatException(Enumeracio.class, enumeracioId);
		}
		return conversioTipusHelper.convertir(
				enumeracio,
				EnumeracioDto.class);
	}
	
	@Override
	@Transactional
	public EnumeracioDto enumeracioUpdate(EnumeracioDto enumeracio)
			throws NoTrobatException, PermisDenegatException {
		
		logger.debug(
				"Modificant l'enumeració del tipus d'expedient existent (" +
				"enumeracio.id=" + enumeracio.getId() + ", " +
				"enumeracio =" + enumeracio + ")");
		
		Enumeracio entity = enumeracioRepository.findOne(enumeracio.getId());
		entity.setCodi(enumeracio.getCodi());
		entity.setNom(enumeracio.getNom());
		
		return conversioTipusHelper.convertir(
				enumeracioRepository.save(entity),
				EnumeracioDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTipusEnumeracioValorDto> enumeracioValorsFindPerDatatable(Long expedientTipusId,
			Long enumeracioId, String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		
		logger.debug(
				"Consultant els valors de enumeracio per al tipus d'expedient per datatable (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"enumeracioId=" + enumeracioId + ", " +
				"filtre=" + filtre + ")");
		
		Page<EnumeracioValors> resultats = enumeracioValorsRepository.findByFiltrePaginat(
				enumeracioId,
				filtre == null || "".equals(filtre),
				filtre,
				paginacioHelper.toSpringDataPageable(paginacioParams));
		
		PaginaDto<ExpedientTipusEnumeracioValorDto> out = paginacioHelper.toPaginaDto(resultats, ExpedientTipusEnumeracioValorDto.class);
		
		return out;
	}

	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto enumeracioValorsCreate(Long expedientTipusId, Long enumeracioId,
			Long entornId, ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException {

		logger.debug(
				"Creant nou valor de enumeracio per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"enumeracioId =" + enumeracioId + ", " +
				"entornId =" + entornId + ", " +
				"document=" + enumeracio + ")");
		
		//Es llançará un PermisDenegatException si escau
		entornHelper.getEntornComprovantPermisos(entornId, true, true);
		
		Enumeracio enumer = enumeracioRepository.findOne(enumeracioId);
		
		EnumeracioValors entity = new EnumeracioValors();
		entity.setCodi(enumeracio.getCodi());
		entity.setNom(enumeracio.getNom());
		entity.setOrdre(enumeracio.getOrdre());
		entity.setEnumeracio(enumer);

		return conversioTipusHelper.convertir(
				enumeracioValorsRepository.save(entity),
				ExpedientTipusEnumeracioValorDto.class);
	}

	@Override
	@Transactional
	public void enumeracioValorDelete(Long valorId) throws NoTrobatException, PermisDenegatException {
		enumeracioValorsRepository.delete(valorId);
		enumeracioValorsRepository.flush();
	}

	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto enumeracioValorFindAmbId(Long valorId) throws NoTrobatException {
		logger.debug(
				"Consultant el valor de l'enumeracio del tipus d'expedient amb id (" +
				"valorId=" + valorId +  ")");
		EnumeracioValors valor = enumeracioValorsRepository.findOne(valorId);
		if (valor == null) {
			throw new NoTrobatException(EnumeracioValors.class, valorId);
		}
		return conversioTipusHelper.convertir(
				valor,
				ExpedientTipusEnumeracioValorDto.class);
	}

	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto enumeracioValorUpdate(ExpedientTipusEnumeracioValorDto enumeracioValor)
			throws NoTrobatException, PermisDenegatException {

		logger.debug(
				"Modificant el valor de l'enumeració del tipus d'expedient existent (" +
				"enumeracioValor.id=" + enumeracioValor.getId() + ", " +
				"enumeracioValor =" + enumeracioValor + ")");
		
		EnumeracioValors entity = enumeracioValorsRepository.findOne(enumeracioValor.getId());
		entity.setCodi(enumeracioValor.getCodi());
		entity.setNom(enumeracioValor.getNom());
		entity.setOrdre(enumeracioValor.getOrdre());
		
		return conversioTipusHelper.convertir(
				enumeracioValorsRepository.save(entity),
				ExpedientTipusEnumeracioValorDto.class);
	}
	
	@Override
	@Transactional
	public void enumeracioDeleteAllByEnumeracio(Long enumeracioId) throws NoTrobatException, PermisDenegatException, ValidacioException {
		
		logger.debug(
				"Esborrant els valors de l'enumeració del tipus d'expedient (" +
				"enumeracioId=" + enumeracioId +  ")");
		
		Enumeracio entity = enumeracioRepository.findOne(enumeracioId);

		if (entity.getCamps()!=null && entity.getCamps().size()>0) {
			throw new ValidacioException(messageHelper.getMessage("expedient.tipus.enumeracio.controller.eliminat.us"));
		}

		List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrdenat(entity.getId());
		if (valors!=null) {
			for (int o=0; o<valors.size(); o++) {
				enumeracioValorsRepository.delete(valors.get(o));
			}
		}
		enumeracioValorsRepository.flush();
	}	

	@Override
	@Transactional
	public ExpedientTipusEnumeracioValorDto enumeracioValorFindAmbCodi(Long expedientTipusId, Long enumeracioId,
			String codi) throws NoTrobatException {

		logger.debug(
				"Consultant el valor de l'enumeració del tipus d'expedient per codi (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"enumeracioId=" + enumeracioId + ", " +
				"codi = " + codi + ")");
		
		Enumeracio enumeracio = enumeracioRepository.findOne(enumeracioId);
		
		return conversioTipusHelper.convertir(
				enumeracioValorsRepository.findByEnumeracioAndCodi(enumeracio, codi),
				ExpedientTipusEnumeracioValorDto.class);
	}

	@Override
	@Transactional
	public boolean enumeracioValorMourer(Long valorId, int posicio) throws NoTrobatException {
		logger.debug(
				"Moguent el valor de l'enumerat (" +
				"valorId=" + valorId + ", " +
				"posicio=" + posicio + ")");
		boolean ret = false;
		EnumeracioValors camp = enumeracioValorsRepository.findOne(valorId);
		if (camp != null && camp.getEnumeracio() != null) {
			List<EnumeracioValors> camps = enumeracioValorsRepository.findByEnumeracioIdOrderByOrdreAsc(camp.getEnumeracio().getId());
			if(posicio != camps.indexOf(camp)) {
				camps.remove(camp);
				camps.add(posicio, camp);
				int i = 0;
				for (EnumeracioValors c : camps) {
					c.setOrdre(i++);
					enumeracioValorsRepository.save(c);
				}
			}
		}
		return ret;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void definicioProcesDelete(Long id) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la definicioProces del tipus d'expedient (" +
				"definicioProcesId=" + id +  ")");
		DefinicioProces entity = definicioProcesRepository.findOne(id);
		definicioProcesRepository.delete(entity);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DefinicioProcesDto> definicioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<DefinicioProces> definicions = definicioProcesRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									definicions, 
									DefinicioProcesDto.class);
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<String> definicioProcesFindJbjmKey(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals) {
		logger.debug(
				"Consultant les jbpmKey de les definicions de processos per al tipus d'expedient (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"incloureGlobals=" + incloureGlobals + ")");
		return definicioProcesRepository.findJbpmKeys(
				entornId, 
				expedientTipusId, 
				incloureGlobals);
	}	

	@Override
	@Transactional
	public boolean definicioProcesSetInicial(
			Long expedientTipusId, 
			Long id) {
		logger.debug(
				"Modificant la clau de la definicio de proces inicial d'un tipus d'expedient(" +
				"expedientTipusId=" + expedientTipusId +  ", definicioProcesId=" + id + ")");
		boolean ret = false;
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		DefinicioProces definicioProces = definicioProcesRepository.findOne(id);
		if (expedientTipus != null 
				&& definicioProces != null 
				&& expedientTipus.getEntorn().getId().equals(definicioProces.getEntorn().getId())) {
			expedientTipus.setJbpmProcessDefinitionKey(definicioProces.getJbpmKey());
			ret = true;
		}
		return ret;
	}
		
	/***********************************************/
	/******************DOMINIS**********************/
	/***********************************************/
	
	@Override
	@Transactional(readOnly = true)
	public List<DominiDto> dominiFindAll(Long expedientTipusId, PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException {
		// Recupera el tipus d'expedient
//		ExpedientTipus expedientTipus =	
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId, 
						true);
		List<Domini> dominis = null;
		// Recupera la informació dels terminis de l'expedient
		dominis = dominiRepository.findByExpedientTipusId(
				expedientTipusId, 
				paginacioHelper.toSpringDataPageable(paginacioParams));
		return conversioTipusHelper.convertirList(
				dominis, 
				DominiDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public DominiDto dominiFindAmbId(Long dominiId) {
		logger.debug(
				"Consultant el domini amb id (" +
				"dominiId=" + dominiId +  ")");
		Domini domini = dominiRepository.findOne(dominiId);
		if (domini == null) {
			throw new NoTrobatException(Domini.class, dominiId);
		}
		return conversioTipusHelper.convertir(
				domini, 
				DominiDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public DominiDto dominiFindAmbCodi(Long expedientTipusId, String codi) {
		DominiDto ret = null;
		logger.debug(
				"Consultant el domini del tipus d'expedient per codi (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		Domini domini = dominiRepository.findByExpedientTipusAndCodi(expedientTipus, codi);
		if (domini != null)
			ret = conversioTipusHelper.convertir(
					domini,
					DominiDto.class);
		return ret;
	}	

	@Override
	@Transactional
	public DominiDto dominiCreate(Long expedientTipusId, DominiDto dto) {
		ExpedientTipus expedientTipus =	
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId, 
						true,
						false,
						false,
						false,
						true);
		
		Domini domini = new Domini();
		domini.setCodi(dto.getCodi());
		domini.setNom(dto.getNom());
		domini.setDescripcio(dto.getDescripcio());
		if (dto.getTipus() != null)
			domini.setTipus(TipusDomini.valueOf(dto.getTipus().name()));
		domini.setUrl(dto.getUrl());
		if (dto.getTipusAuth() != null)
			domini.setTipusAuth(TipusAuthDomini.valueOf(dto.getTipusAuth().name()));
		if (dto.getOrigenCredencials() != null)
			domini.setOrigenCredencials(OrigenCredencials.valueOf(dto.getOrigenCredencials().name()));
		domini.setUsuari(dto.getUsuari());
		domini.setContrasenya(dto.getContrasenya());
		domini.setSql(dto.getSql());
		domini.setJndiDatasource(dto.getJndiDatasource());
		domini.setCacheSegons(dto.getCacheSegons());
		domini.setTimeout(dto.getTimeout());
		domini.setOrdreParams(dto.getOrdreParams());
		domini.setEntorn(expedientTipus.getEntorn());
		domini.setExpedientTipus(expedientTipus);
		return conversioTipusHelper.convertir(
				dominiRepository.save(domini),
				DominiDto.class);
	}

	@Override
	@Transactional
	public DominiDto dominiUpdate(DominiDto dto) {
		Domini domini = dominiRepository.findOne(dto.getId());
		if (domini == null) {
			throw new NoTrobatException(Domini.class, dto.getId());
		}
		
		expedientTipusHelper.getExpedientTipusComprovantPermisos(
				domini.getExpedientTipus().getId(), 
				true,
				false,
				false,
				false,
				true);
		
		domini.setCodi(dto.getCodi());
		domini.setNom(dto.getNom());
		domini.setDescripcio(dto.getDescripcio());
		if (dto.getTipus() != null)
			domini.setTipus(TipusDomini.valueOf(dto.getTipus().name()));
		domini.setUrl(dto.getUrl());
		if (dto.getTipusAuth() != null)
			domini.setTipusAuth(TipusAuthDomini.valueOf(dto.getTipusAuth().name()));
		if (dto.getOrigenCredencials() != null)
			domini.setOrigenCredencials(OrigenCredencials.valueOf(dto.getOrigenCredencials().name()));
		domini.setUsuari(dto.getUsuari());
		domini.setContrasenya(dto.getContrasenya());
		domini.setSql(dto.getSql());
		domini.setJndiDatasource(dto.getJndiDatasource());
		domini.setCacheSegons(dto.getCacheSegons());
		domini.setTimeout(dto.getTimeout());
		domini.setOrdreParams(dto.getOrdreParams());
		
		return conversioTipusHelper.convertir(
				dominiRepository.save(domini),
				DominiDto.class);
	}

	@Override
	@Transactional
	public void dominiDelete(Long dominiId) throws NoTrobatException, PermisDenegatException {
		Domini domini = dominiRepository.findOne(dominiId);
		if (domini == null) {
			throw new NoTrobatException(Domini.class, dominiId);
		}
		
		expedientTipusHelper.getExpedientTipusComprovantPermisos(
				domini.getExpedientTipus().getId(), 
				true,
				false,
				false,
				false,
				true);
		
		if (domini.getExpedientTipus() != null) {
			domini.getExpedientTipus().removeDomini(domini);
		}
		if (domini.getCamps() != null) {
			for (Camp camp : domini.getCamps()) {
				camp.removeDomini(domini);
			}
		}
		
		dominiRepository.delete(domini);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DominiDto> dominiFindPerDatatable(
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els dominis per al tipus d'expedient per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");
						
		
		PaginaDto<DominiDto> pagina = paginacioHelper.toPaginaDto(
				dominiRepository.findByFiltrePaginat(
						expedientTipusId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				DominiDto.class);		
		return pagina;		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ReassignacioDto reassignacioCreate(
			Long expedientTipusId, 
			ReassignacioDto reassignacio) throws PermisDenegatException {

		logger.debug(
				"Creant nova reassignacio per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"reassignacio=" + reassignacio + ")");
		
		Reassignacio entity = new Reassignacio();
				
		entity.setTipusExpedientId(reassignacio.getTipusExpedientId());
		entity.setId(reassignacio.getId());
		entity.setUsuariOrigen(reassignacio.getUsuariOrigen());
		entity.setUsuariDesti(reassignacio.getUsuariDesti());
		entity.setDataInici(reassignacio.getDataInici());
		entity.setDataFi(reassignacio.getDataFi());
		entity.setDataCancelacio(reassignacio.getDataCancelacio());

		return conversioTipusHelper.convertir(
				reassignacioRepository.save(entity),
				ReassignacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ReassignacioDto reassignacioUpdate(ReassignacioDto reassignacio) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la reassignacio del tipus d'expedient existent (" +
				"reassignacio.id=" + reassignacio.getId() + ", " +
				"reassignacio =" + reassignacio + ")");
		Reassignacio entity = reassignacioRepository.findOne(reassignacio.getId());

		entity.setTipusExpedientId(reassignacio.getTipusExpedientId());
		entity.setId(reassignacio.getId());
		entity.setUsuariOrigen(reassignacio.getUsuariOrigen());
		entity.setUsuariDesti(reassignacio.getUsuariDesti());
		entity.setDataInici(reassignacio.getDataInici());
		entity.setDataFi(reassignacio.getDataFi());
		entity.setDataCancelacio(reassignacio.getDataCancelacio());
				
		return conversioTipusHelper.convertir(
				reassignacioRepository.save(entity),
				ReassignacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reassignacioDelete(Long reassignacioReassignacioId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la reassignacio del tipus d'expedient (" +
				"reassignacioId=" + reassignacioReassignacioId +  ")");
		Reassignacio entity = reassignacioRepository.findOne(reassignacioReassignacioId);
		reassignacioRepository.delete(entity);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReassignacioDto reassignacioFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la reassignacio del tipus d'expedient amb id (" +
				"reassignacioId=" + id +  ")");
		Reassignacio reassignacio = reassignacioRepository.findOne(id);
		if (reassignacio == null) {
			throw new NoTrobatException(Reassignacio.class, id);
		}
		return conversioTipusHelper.convertir(
				reassignacio,
				ReassignacioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReassignacioDto> reassignacioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<Reassignacio> reassignacions = reassignacioRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
									reassignacions, 
									ReassignacioDto.class);
	}	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ReassignacioDto> reassignacioFindPerDatatable(
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les reassignacions per al tipus d'expedient per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");
						
		
		PaginaDto<ReassignacioDto> pagina = paginacioHelper.toPaginaDto(
				reassignacioRepository.findByFiltrePaginat(
						expedientTipusId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				ReassignacioDto.class);		
		return pagina;		
	}
	
	/***********************************************/
	/*******************ESTATS**********************/
	/***********************************************/
	
	@Override
	@Transactional(readOnly = true)
	public List<EstatDto> estatFindAll(Long expedientTipusId, PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException {
		// Recupera el tipus d'expedient
		expedientTipusHelper.getExpedientTipusComprovantPermisos(
				expedientTipusId, 
				true);
		List<Estat> estats = null;
		// Recupera la informació dels terminis de l'expedient
		estats = estatRepository.findByExpedientTipusId(
				expedientTipusId, 
				paginacioHelper.toSpringDataPageable(paginacioParams));
		return conversioTipusHelper.convertirList(
				estats, 
				EstatDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public EstatDto estatFindAmbId(Long estatId) {
		logger.debug(
				"Consultant el l'estat amb id (" +
				"estatId=" + estatId +  ")");
		Estat estat = estatRepository.findOne(estatId);
		if (estat == null) {
			throw new NoTrobatException(Estat.class, estatId);
		}
		return conversioTipusHelper.convertir(
				estat, 
				EstatDto.class);
	}

	@Override
	@Transactional
	public EstatDto estatCreate(Long expedientTipusId, EstatDto dto) {
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
				expedientTipusId, 
				true,
				false,
				false,
				false,
				true);
		Estat estat = new Estat();
		estat.setExpedientTipus(expedientTipus);
		estat.setCodi(dto.getCodi());
		estat.setNom(dto.getNom());
		Integer seguentOrdre = estatRepository.getSeguentOrdre(expedientTipusId); 
		estat.setOrdre(seguentOrdre == null ? 0 : seguentOrdre + 1);
		return conversioTipusHelper.convertir(
				estatRepository.save(estat),
				EstatDto.class);
	}

	@Override
	@Transactional
	public EstatDto estatUpdate(EstatDto dto) {
		Estat estat = estatRepository.findOne(dto.getId());
		if (estat == null) {
			throw new NoTrobatException(Estat.class, dto.getId());
		}
		
		expedientTipusHelper.getExpedientTipusComprovantPermisos(
				estat.getExpedientTipus().getId(), 
				true,
				false,
				false,
				false,
				true);
		
		estat.setCodi(dto.getCodi());
		estat.setNom(dto.getNom());
		
		return conversioTipusHelper.convertir(
				estatRepository.save(estat),
				EstatDto.class);
	}

	@Override
	@Transactional
	public void estatDelete(Long estatId) throws NoTrobatException, PermisDenegatException {
		Estat estat = estatRepository.findOne(estatId);
		if (estat == null) {
			throw new NoTrobatException(Estat.class, estatId);
		}
		
		List<Estat> estats = estatRepository.findByExpedientTipusOrderByOrdreAsc(
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						estat.getExpedientTipus().getId(), 
						true,
						false,
						false,
						false,
						true));
		int i = 0;
		for (Estat e: estats) {
			if (estatId.equals(e.getId())) {
				estatRepository.delete(e);	
			} else {
				e.setOrdre(i++);
			}		
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<EstatDto> estatFindPerDatatable(
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els estats per al tipus d'expedient per datatable (" +
				"entornId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");
						
		
		PaginaDto<EstatDto> pagina = paginacioHelper.toPaginaDto(
				estatRepository.findByFiltrePaginat(
						expedientTipusId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				EstatDto.class);		
		return pagina;		
	}
	
	@Override
	@Transactional
	public boolean estatMoure(Long estatId, int posicio) throws NoTrobatException {
		logger.debug(
				"Moguent l'estat (" +
				"estatId=" + estatId + ", " +
				"posicio=" + posicio + ")");
		boolean ret = false;
		Estat estat = estatRepository.findOne(estatId);
		if (estat == null) {
			throw new NoTrobatException(Estat.class, estatId);
		}
		
		List<Estat> estats = estatRepository.findByExpedientTipusOrderByOrdreAsc(
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						estat.getExpedientTipus().getId(), 
						true,
						false,
						false,
						false,
						true));
		if(posicio != estats.indexOf(estat)) {
			estats.remove(estat);
			estats.add(posicio, estat);
			int i = 0;
			for (Estat e : estats) {
				e.setOrdre(i++);
				estatRepository.save(e);
			}
		}
		return ret;
	}
	
	
	
	
	
	@Override
	@Transactional
	public boolean definicioProcesIncorporar(
			Long expedientTipusId, 
			Long id,
			boolean sobreescriure) {
		logger.debug(
				"Importació la informació de la definicio de proces al tipus d'expedient(" +
				"expedientTipusId=" + expedientTipusId +  ", " + 
				"definicioProcesId=" + id +  ", " + 
				"sobreescriure=" + sobreescriure + ")");
		boolean ret = false;
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		Entorn entorn = expedientTipus.getEntorn();
		DefinicioProces definicioProces = definicioProcesRepository.findOne(id);
		if (expedientTipus != null 
				&& definicioProces != null) {
			// Propaga les agrupacions
			Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
			for (CampAgrupacio agrupacio : definicioProces.getAgrupacions()) {
				CampAgrupacio nova = campAgrupacioRepository.findByExpedientTipusIdAndCodi(
						expedientTipus.getId(),
						agrupacio.getCodi());
				if (nova == null || sobreescriure) {
					if (nova == null) {
						nova = new CampAgrupacio(
								expedientTipus,
								agrupacio.getCodi(),
								agrupacio.getNom(),
								agrupacio.getOrdre());
						expedientTipus.getAgrupacions().add(nova);
					} else if (sobreescriure) {
						nova.setNom(agrupacio.getNom());
						nova.setOrdre(agrupacio.getOrdre());
					}
					nova.setDescripcio(agrupacio.getDescripcio());
				}
				agrupacions.put(agrupacio.getCodi(), nova);
			}
			// Propaga les accions
			for (Accio accio: definicioProces.getAccions()) {
				Accio nova = accioRepository.findByExpedientTipusIdAndCodi(
						expedientTipus.getId(),
						accio.getCodi());
				if (nova == null || sobreescriure) {
					if (nova == null) {
						nova = new Accio(
								expedientTipus,
								accio.getCodi(),
								accio.getNom(),
								definicioProces.getJbpmKey(),
								accio.getJbpmAction());
						expedientTipus.getAccions().add(nova);
					} else if (sobreescriure) {
						nova.setNom(accio.getNom());
						nova.setJbpmAction(accio.getJbpmAction());
					}
					nova.setDescripcio(accio.getDescripcio());
					nova.setPublica(accio.isPublica());
					nova.setOculta(accio.isOculta());
					nova.setRols(accio.getRols());
				}
			}
			// Propaga els camps
			Map<String, Camp> camps = new HashMap<String, Camp>();
			Map<String, Camp> registres = new HashMap<String, Camp>();
			for (Camp camp: definicioProces.getCamps()) {
				Camp nou = campRepository.findByExpedientTipusAndCodi(
						expedientTipus,
						camp.getCodi());
				if (nou == null || sobreescriure) {
					if (nou == null) {
						nou = new Camp(
								expedientTipus,
								camp.getCodi(),
								camp.getTipus(),
								camp.getEtiqueta());
						expedientTipus.getCamps().add(nou);
					} else {
						nou.setTipus(camp.getTipus());
						nou.setEtiqueta(camp.getEtiqueta());
					}
					nou.setIgnored(camp.isIgnored());
					nou.setObservacions(camp.getObservacions());
					nou.setDominiId(camp.getDominiId());
					nou.setDominiParams(camp.getDominiParams());
					nou.setDominiCampText(camp.getDominiCampText());
					nou.setDominiCampValor(camp.getDominiCampValor());
					nou.setDominiCacheText(camp.isDominiCacheText());
					nou.setMultiple(camp.isMultiple());
					nou.setOcult(camp.isOcult());
					nou.setDominiIntern(camp.isDominiIntern());
					nou.setJbpmAction(camp.getJbpmAction());
					nou.setOrdre(camp.getOrdre());
					
					if (camp.getEnumeracio() != null && camp.getEnumeracio().getCodi() != null) {
						// Propaga la enumeració referenciada pel camp
						Enumeracio enumeracioEntorn = enumeracioRepository.findByEntornAndCodiAndExpedientTipusNull(
								entorn, 
								camp.getEnumeracio().getCodi());
						if(enumeracioEntorn==null){
							Enumeracio enumeracio = enumeracioRepository.findByEntornAndExpedientTipusAndCodi(
									entorn, 
									expedientTipus, 
									camp.getEnumeracio().getCodi());
							if (enumeracio == null || sobreescriure) {
								if (enumeracio == null) {
									enumeracio = new Enumeracio();
									enumeracio.setEntorn(entorn);
									expedientTipus.getEnumeracions().add(enumeracio);
								}
								enumeracio.setNom(camp.getEnumeracio().getNom());
							}											
						}else{
							nou.setEnumeracio(enumeracioEntorn);
						}						
					}
					// Propaga les validacions del camp
					for (Validacio validacio: nou.getValidacions())
						campValidacioRepository.delete(validacio);
					nou.getValidacions().clear();
					for (Validacio validacio: camp.getValidacions()) {
						Validacio nova = new Validacio(
								nou,
								validacio.getExpressio(),
								validacio.getMissatge());
						nova.setOrdre(validacio.getOrdre());
						nou.addValidacio(nova);
					}				
					if (nou.getTipus() == TipusCamp.REGISTRE) {
						registres.put(nou.getCodi(), nou);
					}
				}
				camps.put(nou.getCodi(), nou);				
				if (camp.getDomini() != null &&  !camp.isDominiIntern()) {
					// Propaga el domini referenciat 
					Domini domini = dominiRepository.findByExpedientTipusAndCodi(
							expedientTipus,
							camp.getDomini().getCodi());
					if (domini != null) {
						nou.setDomini(domini);
					} else {
						domini = new Domini();
						domini.setCodi(camp.getDomini().getCodi());
						domini.setEntorn(entorn);
						domini.setNom(camp.getDomini().getNom());
						domini.setTipus(camp.getDomini().getTipus());
						domini.setExpedientTipus(expedientTipus);
						expedientTipus.getDominis().add(domini);
					}
				}
				if (camp.getAgrupacio() != null)
					nou.setAgrupacio(agrupacions.get(camp.getAgrupacio().getCodi()));
				campRepository.save(nou);
			} // Fi propagació camps
			// Propaga els membres dels camps de tipus registre
			for (Camp camp: registres.values()) {
				for (CampRegistre campRegistre: camp.getRegistreMembres()) {
					campRegistre.getMembre().getRegistrePares().remove(campRegistre);
					campRegistre.setMembre(null);
					campRegistre.setRegistre(null);
					campRegistreRepository.delete(campRegistre);
				}
				camp.getRegistreMembres().clear();
			}
			campRegistreRepository.flush();
			for (Camp camp: definicioProces.getCamps()) {
				if (camp.getTipus() == TipusCamp.REGISTRE && registres.containsKey(camp.getCodi())) {
					for (CampRegistre membre: camp.getRegistreMembres()) {
						CampRegistre campRegistre = new CampRegistre(
								camps.get(camp.getCodi()),
								camps.get(membre.getMembre().getCodi()),
								membre.getOrdre());
						campRegistre.setLlistar(membre.isLlistar());
						campRegistre.setObligatori(membre.isObligatori());
						registres.get(camp.getCodi()).getRegistreMembres().add(campRegistre);
					}
				}
			}
			// Propaga els documents
			Map<String, Document> documents = new HashMap<String, Document>();
			for (Document document: definicioProces.getDocuments()) {
				Document nou = documentRepository.findByExpedientTipusAndCodi(
						expedientTipus,
						document.getCodi());
				if (nou == null || sobreescriure) {
					if (nou == null) {
						nou = new Document(
								expedientTipus,
								document.getCodi(),
								document.getNom());
						expedientTipus.getDocuments().add(nou);
					} else if (sobreescriure) {
						nou.setNom(document.getNom());
					}
					nou.setDescripcio(document.getDescripcio());
					nou.setArxiuContingut(document.getArxiuContingut());
					nou.setPlantilla(document.isPlantilla());
					nou.setCustodiaCodi(document.getCustodiaCodi());
					nou.setContentType(document.getContentType());
					nou.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
					nou.setAdjuntarAuto(document.isAdjuntarAuto());
					if (document.getCampData() != null && document.getCampData().getCodi() != null)
						nou.setCampData(camps.get(document.getCampData().getCodi()));
					nou.setConvertirExtensio(document.getConvertirExtensio());
					nou.setExtensionsPermeses(document.getExtensionsPermeses());
					documents.put(nou.getCodi(), nou);
				}				
			}
			// Propaga els terminis
			for (Termini termini: definicioProces.getTerminis()) {
				Termini nou = terminiRepository.findByExpedientTipusAndCodi(
						expedientTipus,
						termini.getCodi());
				if (nou == null || sobreescriure) {
					if (nou == null) {
						nou = new Termini(
								expedientTipus,
								termini.getCodi(),
								termini.getNom(),
								termini.getAnys(),
								termini.getMesos(),
								termini.getDies(),
								termini.isLaborable());
						expedientTipus.getTerminis().add(nou);
					} else if (sobreescriure) {
						nou.setNom(termini.getNom());
						nou.setAnys(termini.getAnys());
						nou.setMesos(termini.getMesos());
						nou.setDies(termini.getDies());
						nou.setLaborable(termini.isLaborable());
					}
					nou.setDescripcio(termini.getDescripcio());
					nou.setDiesPrevisAvis(termini.getDiesPrevisAvis());
					nou.setAlertaPrevia(termini.isAlertaPrevia());
					nou.setAlertaFinal(termini.isAlertaFinal());
					nou.setAlertaCompletat(termini.isAlertaCompletat());
					nou.setManual(termini.isManual());
				}	
			}
			ret = true;
		}
		return ret;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ConsultaDto consultaCreate(
			Long expedientTipusId, 
			ConsultaDto consulta) throws PermisDenegatException {

		logger.debug(
				"Creant nova consulta per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"consulta=" + consulta + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		Consulta entity = new Consulta();

		entity.setCodi(consulta.getCodi());
		entity.setNom(consulta.getNom());
		entity.setDescripcio(consulta.getDescripcio());
		entity.setFormatExport(consulta.getFormatExport());
		entity.setValorsPredefinits(consulta.getValorsPredefinits());
		entity.setExportarActiu(consulta.isExportarActiu());
		entity.setOcultarActiu(consulta.isOcultarActiu());
		entity.setInformeNom(consulta.getInformeNom());
		entity.setInformeContingut(consulta.getInformeContingut());
		entity.setOrdre(consultaRepository.getNextOrdre(expedientTipusId));

		entity.setExpedientTipus(expedientTipus);		
		entity.setEntorn(expedientTipus.getEntorn());		

		return conversioTipusHelper.convertir(
				consultaRepository.save(entity),
				ConsultaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ConsultaDto consultaUpdate(
			ConsultaDto consulta,
			boolean actualitzarContingut) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant la consulta del tipus d'expedient existent (" +
				"consulta.id=" + consulta.getId() + ", " +
				"consulta =" + consulta + ")");
		Consulta entity = consultaRepository.findOne(consulta.getId());

		entity.setCodi(consulta.getCodi());
		entity.setNom(consulta.getNom());
		entity.setDescripcio(consulta.getDescripcio());
		entity.setFormatExport(consulta.getFormatExport());
		entity.setValorsPredefinits(consulta.getValorsPredefinits());
		entity.setExportarActiu(consulta.isExportarActiu());
		entity.setOcultarActiu(consulta.isOcultarActiu());
		entity.setInformeNom(consulta.getInformeNom());
		if (actualitzarContingut)
			entity.setInformeContingut(consulta.getInformeContingut());
				
		return conversioTipusHelper.convertir(
				consultaRepository.save(entity),
				ConsultaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void consultaDelete(Long consultaConsultaId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la consulta del tipus d'expedient (" +
				"consultaId=" + consultaConsultaId +  ")");
		Consulta entity = consultaRepository.findOne(consultaConsultaId);
		consultaRepository.delete(entity);	
		consultaRepository.flush();
		reordenarConsultes(entity.getExpedientTipus().getId());		
	}

	/** Funció per reasignar el valor d'ordre per a les agrupacions d'un tipus d'expedient */
	@Transactional
	private int reordenarConsultes(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		Set<Consulta> consultes = expedientTipus.getConsultes();
		int i = 0;
		for (Consulta consulta: consultes)
			consulta.setOrdre(i++);
		return consultes.size();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConsultaDto consultaFindAmbId(Long id) throws NoTrobatException {
		logger.debug(
				"Consultant la consulta del tipus d'expedient amb id (" +
				"consultaId=" + id +  ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class, id);
		}
		return conversioTipusHelper.convertir(
				consulta,
				ConsultaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConsultaDto consultaFindAmbCodiPerValidarRepeticio(Long expedientTipusId, String codi) throws NoTrobatException {
		logger.debug(
				"Consultant la consulta del tipus d'expedient per codi per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codi = " + codi + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return conversioTipusHelper.convertir(
				consultaRepository.findByExpedientTipusAndCodi(expedientTipus, codi),
				ConsultaDto.class);
	}	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ConsultaDto> consultaFindPerDatatable(
			Long entornId,
			Long expedientTipusId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les consultes per al tipus d'expedient per datatable (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"filtre=" + filtre + ")");
								
		PaginaDto<ConsultaDto> pagina = paginacioHelper.toPaginaDto(
				consultaRepository.findByFiltrePaginat(
						entornId,
						expedientTipusId == null,
						expedientTipusId,
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				ConsultaDto.class);
		// Consulta els valors pels comptadors 
		// List< Object[Long consultaId, TipusConsultaCamp tipus, Long count]>
		List<Long> paginaIds = new ArrayList<Long>();
		for (ConsultaDto c : pagina.getContingut()) {
			paginaIds.add(c.getId());
		}
		if (paginaIds.size() > 0) {
			List<Object[]> countCamps = consultaRepository.countCamps(paginaIds);
			List<Object[]> processats = new ArrayList<Object[]>();	// per esborrar la informació processada i reduir la cerca
			// Omple els comptadors de tipus de camps
			for (ConsultaDto consulta: pagina.getContingut()) {
				for (Object[] countCamp: countCamps) {
					Long campId = (Long) countCamp[0];
					if (campId.equals(consulta.getId())) {
						Long count = (Long)countCamp[2];
						ConsultaCamp.TipusConsultaCamp tipus = (ConsultaCamp.TipusConsultaCamp) countCamp[1]; 
						switch(tipus) {
						case FILTRE:
							consulta.setVarsFiltreCount(count.intValue());
							break;
						case INFORME:
							consulta.setVarsInformeCount(count.intValue());
							break;
						case PARAM:
							consulta.setParametresCount(count.intValue());
							break;
						default:
							break;
						}
						processats.add(countCamp);
					}
				}
				countCamps.removeAll(processats);
				processats.clear();
			}		
		}
		return pagina;		
	}	
	
	@Override
	@Transactional
	public boolean consultaMourePosicio(
			Long id, 
			int posicio) {
		boolean ret = false;
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta != null) {
			List<Consulta> consultes = consultaRepository.findConsultesAmbEntornIExpedientTipusOrdenat(
					consulta.getEntorn().getId(),
					consulta.getExpedientTipus().getId());
			if(posicio != consultes.indexOf(consulta)) {
				consultes.remove(consulta);
				consultes.add(posicio, consulta);
				int i = 0;
				for (Consulta c : consultes) {
					c.setOrdre(i++);
				}
			}
		}
		return ret;
	}	
	
	@Override
	@Transactional
	public ConsultaCampDto consultaCampCreate(
			Long consultaId, 
			ConsultaCampDto consultaCamp) throws PermisDenegatException {

		logger.debug(
				"Creant nou camp per una consulta del tipus d'expedient (" +
				"consultaId =" + consultaId + ", " +
				"consultaCamp=" + consultaCamp + ")");

		ConsultaCamp entity = new ConsultaCamp();
		
		entity.setCampCodi(consultaCamp.getCampCodi());
		entity.setCampDescripcio(consultaCamp.getCampDescripcio());
		entity.setConsulta(consultaRepository.findOne(consultaId));
		entity.setTipus(ConsultaCamp.TipusConsultaCamp.valueOf(consultaCamp.getTipus().toString()));

		entity.setDefprocJbpmKey(consultaCamp.getDefprocJbpmKey());
		entity.setDefprocVersio(consultaCamp.getDefprocVersio());

		entity.setOrdre(consultaCampRepository.getNextOrdre(
				consultaId,
				entity.getTipus()));
		if (consultaCamp.getParamTipus() != null )
			entity.setParamTipus(ConsultaCamp.TipusParamConsultaCamp.valueOf(consultaCamp.getParamTipus().toString()));
		
		return conversioTipusHelper.convertir(
				consultaCampRepository.save(entity),
				ConsultaCampDto.class);
	}

	@Override
	@Transactional
	public void consultaCampDelete(Long consultaCampId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant la consultaCamp del tipus d'expedient (" +
				"consultaCampId=" + consultaCampId +  ")");
		
		ConsultaCamp consultaCamp = consultaCampRepository.findOne(consultaCampId);
		consultaCampRepository.delete(consultaCamp);	
		consultaCampRepository.flush();
		reordenarCampsConsulta(consultaCamp.getConsulta().getId(), consultaCamp.getTipus());
	}
	
	@Override
	@Transactional
	public void consultaCampCols(Long consultaCampId, String propietat, int valor) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Canviant ample de columnes de filtre de la consultaCamp del tipus d'expedient (" +
				"consultaCampId=" + consultaCampId +  ")");
		
		ConsultaCamp consultaCamp = consultaCampRepository.findOne(consultaCampId);
		if ("ampleCols".equals(propietat)) {
			consultaCamp.setAmpleCols(valor);
		} else if ("buitCols".equals(propietat)) {
			consultaCamp.setBuitCols(valor);
		}
		
		definirAmpleBuit(consultaCamp);
		
		consultaCampRepository.saveAndFlush(consultaCamp);
	}

	/** Funció per reasignar el valor d'ordre dins dels camps d'una consulta de tipus registre. */
	private void reordenarCampsConsulta(
			Long consultaId,
			ConsultaCamp.TipusConsultaCamp tipus) {
		List<ConsultaCamp> consultaCamps = consultaCampRepository.findAmbConsultaIdOrdenats(
				consultaId,
				tipus);		
		int i = 0;
		for (ConsultaCamp c : consultaCamps) {
			c.setOrdre(i);
			consultaCampRepository.saveAndFlush(c);
			i++;
		}
	}

	@Override
	@Transactional
	public boolean consultaCampMourePosicio(
			Long id, 
			int posicio) {
		
		boolean ret = false;
		ConsultaCamp consultaCamp = consultaCampRepository.findOne(id);
		if (consultaCamp != null) {
			List<ConsultaCamp> campsRegistre = consultaCampRepository.findAmbConsultaIdOrdenats(
					consultaCamp.getConsulta().getId(),
					consultaCamp.getTipus());
			int index = campsRegistre.indexOf(consultaCamp);
			if(posicio != index) {	
				consultaCamp = campsRegistre.get(index);
				campsRegistre.remove(consultaCamp);
				campsRegistre.add(posicio, consultaCamp);
				int i = 0;
				for (ConsultaCamp c : campsRegistre) {
					c.setOrdre(i);
					consultaCampRepository.saveAndFlush(c);
					i++;
				}
			}
		}
		return ret;				
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ConsultaCampDto> consultaCampFindPerDatatable(
			Long consultaId,
			TipusConsultaCamp tipus,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els consultaCamps per un camp registre del tipus d'expedient per datatable (" +
				"entornId=" + consultaId + ", " +
				"filtre=" + filtre + ")");
						
		Map<String, String> mapeigPropietatsOrdenacio = new HashMap<String, String>();
		mapeigPropietatsOrdenacio.put("campTipus", "camp.tipus");
		mapeigPropietatsOrdenacio.put("campEtiqueta", "camp.etiqueta");
		
		PaginaDto<ConsultaCampDto> paginaConsultaCamps = paginacioHelper.toPaginaDto(
				consultaCampRepository.findByFiltrePaginat(
						consultaId, 
						ConsultaCamp.TipusConsultaCamp.valueOf(tipus.toString()),
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								mapeigPropietatsOrdenacio)),
				ConsultaCampDto.class);
		
		// Completa el contingut de les etiquetes i els tipus
		for (ConsultaCampDto consultaCamp : paginaConsultaCamps.getContingut()) {
			if (consultaCamp.getCampCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				// camp de l'expedient
				consultaCamp.setCampTipus(CampTipusDto.STRING);
				consultaCamp.setCampEtiqueta(this.getEtiquetaCampExpedient(consultaCamp.getCampCodi()));
			} else if (consultaCamp.getDefprocJbpmKey() != null) {
				// camp de la definició de procés
				DefinicioProces definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
						consultaCamp.getDefprocJbpmKey(),
						consultaCamp.getDefprocVersio());
				if (definicioProces != null) {
					Camp camp = campRepository.findByDefinicioProcesAndCodi(
							definicioProces, 
							consultaCamp.getCampCodi());
					if (camp != null) {
						consultaCamp.setCampTipus(CampTipusDto.valueOf(camp.getTipus().toString()));
						consultaCamp.setCampEtiqueta(camp.getEtiqueta());
					}
				}
			} else {
				// camp de l'expedient
				Consulta consulta = consultaRepository.findOne(consultaId);
				Camp camp = campRepository.findByExpedientTipusAndCodi(
						consulta.getExpedientTipus(), 
						consultaCamp.getCampCodi());
				if (camp != null) {
					consultaCamp.setCampTipus(CampTipusDto.valueOf(camp.getTipus().toString()));
					consultaCamp.setCampEtiqueta(camp.getEtiqueta());
				}
			}
				
		}
		return paginaConsultaCamps;		
	}		
	
	/** Transforma el codi del camp de l'expedient pel seu literal corresponent. */
	private String getEtiquetaCampExpedient(String campCodi) {
		String etiqueta;
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.id");
		else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.numero");
		else if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.titol");
		else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.comentari");
		else if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.indicador");
		else if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.responsable");
		else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.data_ini");
		else if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("etiqueta.exp.estat");
		else if (ExpedientCamps.EXPEDIENT_CAMP_GEOREF.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("comuns.georeferencia.codi");
		else if (ExpedientCamps.EXPEDIENT_CAMP_GEOX.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("comuns.georeferencia.coordenadaX");
		else if (ExpedientCamps.EXPEDIENT_CAMP_GEOY.equals(campCodi)) 
			etiqueta = messageHelper.getMessage("comuns.georeferencia.coordenadaY");
		else 
			etiqueta = campCodi;
		return etiqueta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConsultaCampDto> consultaCampFindCampAmbConsultaIdAndTipus(
			Long consultaId, 
			TipusConsultaCamp tipus) {
		logger.debug(
				"Consultant els camps per una consulta i tipus (" +
				"consultaId=" + consultaId + ", " +
				"tipus=" + tipus + ")");
		return conversioTipusHelper.convertirList(
				consultaCampRepository.findCampsConsulta(
						consultaId,
						ConsultaCamp.TipusConsultaCamp.valueOf(tipus.toString())), 
				ConsultaCampDto.class);
	}
	
	@Override
	@Transactional
	public ConsultaCampDto consultaCampUpdate(ConsultaCampDto consultaCamp) 
						throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el parametre de la consulta del tipus d'expedient existent (" +
				"consultaCamp.id=" + consultaCamp.getId() + ", " +
				"consultaCamp =" + consultaCamp + ")");
		
		ConsultaCamp entity = consultaCampRepository.findOne(consultaCamp.getId());

		entity.setCampCodi(consultaCamp.getCampCodi());
		entity.setCampDescripcio(consultaCamp.getCampDescripcio());
		entity.setTipus(ConsultaCamp.TipusConsultaCamp.valueOf(consultaCamp.getTipus().toString()));
		entity.setParamTipus(entity.getParamTipus());
		
		return conversioTipusHelper.convertir(
				consultaCampRepository.save(entity),
				ConsultaCampDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ConsultaCampDto consultaCampFindAmbTipusICodiPerValidarRepeticio(
			Long consultaId, 
			TipusConsultaCamp tipus,
			String campCodi) throws NoTrobatException {
		logger.debug(
				"Consultant el camp de la consulta del tipus d'expedient per codi per validar repetició (" +
				"consultaId=" + consultaId + ", " +
				"tipus=" + tipus + ", " +
				"campCodi = " + campCodi + ")");
		
		return conversioTipusHelper.convertir(
				consultaCampRepository.findByConsultaAndTipusAndCampCodi(
						consultaRepository.findOne(consultaId),
						ConsultaCamp.TipusConsultaCamp.valueOf(tipus.toString()),
						campCodi),
				ConsultaCampDto.class);
	}		
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<String> mapeigFindCodiHeliumAmbTipus(
			Long expedientTipusId, 
			TipusMapeig tipus) {
		logger.debug(
				"Consultant els codis helium dels mapegos segons un tipus de filtre");
		
		ExpedientTipus expedientTipus = 
				expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId, 
						true);
		List<String> codisHelium = mapeigSistraRepository.findCodiHeliumByExpedientTipusAndTipus(
				expedientTipus, 
				MapeigSistra.TipusMapeig.valueOf(tipus.toString()));

		return codisHelium;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<TipusMapeig, Long> mapeigCountsByTipus(Long expedientTipusId) {
		logger.debug(
				"Consultant els codis helium dels mapegos segons un tipus de filtre");
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		List<Object[]> mapejosCount = mapeigSistraRepository.countTipus(
				expedientTipus);
		Map<TipusMapeig, Long> recomptes = new HashMap<TipusMapeig, Long>();
		MapeigSistra.TipusMapeig tipus;
		Long count;
		for (Object[] mc : mapejosCount) {
			tipus = (MapeigSistra.TipusMapeig) mc[0];
			count = (Long) mc[1];
			recomptes.put(TipusMapeig.valueOf(tipus.toString()), count);
		}
		// Assegura que el valor hi sigui en el resultat del recompte
		for (TipusMapeig t : TipusMapeig.values())
			if (! recomptes.containsKey(t))
				recomptes.put(t, 0L);

		return recomptes;
	}	
	
	@Override
	public PaginaDto<MapeigSistraDto> mapeigFindPerDatatable(
			Long expedientTipusId, 
			TipusMapeig tipus, 
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els mapejos per un tipus d'expedient per datatable (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"tipus=" + tipus + ")");
		
		return paginacioHelper.toPaginaDto(
				mapeigSistraRepository.findByFiltrePaginat(
						expedientTipusId, 
						MapeigSistra.TipusMapeig.valueOf(tipus.toString()),
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				MapeigSistraDto.class);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public MapeigSistraDto mapeigCreate(
			Long expedientTipusId, 
			MapeigSistraDto mapeig) throws PermisDenegatException {

		logger.debug(
				"Creant un nou mapeig per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"mapeig=" + mapeig + ")");
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		MapeigSistra entity = new MapeigSistra();
				
		entity.setCodiSistra(mapeig.getCodiSistra());
		if (mapeig.getTipus() == TipusMapeig.Adjunt) 
			entity.setCodiHelium(mapeig.getCodiSistra());
		else
			entity.setCodiHelium(mapeig.getCodiHelium());
		entity.setTipus(MapeigSistra.TipusMapeig.valueOf(mapeig.getTipus().toString()));
		// MapeigSistra associat a l'expedient
		entity.setExpedientTipus(expedientTipus);		

		return conversioTipusHelper.convertir(
				mapeigSistraRepository.save(entity),
				MapeigSistraDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public MapeigSistraDto mapeigUpdate(MapeigSistraDto mapeig) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Modificant el mapeig del tipus d'expedient existent (" +
				"mapeig.id=" + mapeig.getId() + ", " +
				"mapeig =" + mapeig + ")");
		MapeigSistra entity = mapeigSistraRepository.findOne(mapeig.getId());

		entity.setCodiSistra(mapeig.getCodiSistra());
		if (mapeig.getTipus() == TipusMapeig.Adjunt) 
			entity.setCodiHelium(mapeig.getCodiSistra());
		else
			entity.setCodiHelium(mapeig.getCodiHelium());
				
		return conversioTipusHelper.convertir(
				mapeigSistraRepository.save(entity),
				MapeigSistraDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void mapeigDelete(Long mapeigMapeigSistraId) throws NoTrobatException, PermisDenegatException {
		logger.debug(
				"Esborrant el mapeig del tipus d'expedient (" +
				"mapeigId=" + mapeigMapeigSistraId +  ")");
		MapeigSistra entity = mapeigSistraRepository.findOne(mapeigMapeigSistraId);
		mapeigSistraRepository.delete(entity);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public MapeigSistraDto mapeigFindAmbCodiHeliumPerValidarRepeticio(
			Long expedientTipusId, 
			String codiHelium) {
		logger.debug(
				"Consultant el mapeig del tipus d'expedient per codi helium per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codiHelium = " + codiHelium + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return conversioTipusHelper.convertir(
				mapeigSistraRepository.findByExpedientTipusAndCodiHelium(expedientTipus, codiHelium),
				MapeigSistraDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public MapeigSistraDto mapeigFindAmbCodiSistraPerValidarRepeticio(
			Long expedientTipusId, 
			String codiSistra) {
		logger.debug(
				"Consultant el mapeig del tipus d'expedient per codi sistra per validar repetició (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"codiSistra = " + codiSistra + ")");
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return conversioTipusHelper.convertir(
				mapeigSistraRepository.findByExpedientTipusAndCodiSistra(expedientTipus, codiSistra),
				MapeigSistraDto.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<MapeigSistraDto> mapeigFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException {
		List<MapeigSistra> mapejosSistra = mapeigSistraRepository.findAmbExpedientTipus(expedientTipusId);
		return conversioTipusHelper.convertirList(
				mapejosSistra, 
				MapeigSistraDto.class);
	}	
	
	private void definirAmpleBuit(ConsultaCamp consultaCamp) {
		int ample = consultaCamp.getAmpleCols();
		int buit = consultaCamp.getBuitCols();
		
		if (ample > 12)
			ample = 12;
		else if (ample <= 0)
			ample = 1;
			
		if (buit > 12)
			buit = 12;
		else if (buit < -12)
			buit = -12;
		
		int absAmple = Math.abs(ample);
		int absBuit = Math.abs(buit);
		int totalCols = absAmple + absBuit;
		if (totalCols > 12) {
			int diff = totalCols - 12;
			if (buit >= 0)
				buit -= diff;
			else
				buit += diff;
		}
		
		consultaCamp.setAmpleCols(ample);
		consultaCamp.setBuitCols(buit);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);
}