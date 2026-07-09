/**
 *
 */
package es.caib.helium.logic.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.commons.dto.ExpedientLogDto;
import es.caib.helium.commons.dto.ExpedientTascaDto;
import es.caib.helium.commons.dto.InstanciaProcesDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.engine.WTaskInstance;
import es.caib.helium.logic.intf.dto.engine.WToken;
import es.caib.helium.logic.intf.service.ExpedientRegistreService;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientLog;
import es.caib.helium.persistence.entity.ExpedientLog.ExpedientLogAccioTipus;
import es.caib.helium.persistence.entity.ExpedientLog.ExpedientLogEstat;
import es.caib.helium.persistence.repository.ExpedientLoggerRepository;
import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientLoggerHelper;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.logic.helpers.MesuresTemporalsHelper;
import es.caib.helium.logic.security.ExtendedPermission;


/**
 * Implementació dels mètodes del servei ExpedientService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientRegistreServiceImpl implements ExpedientRegistreService {

	@Resource
	private ExpedientLoggerRepository expedientLogRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private WorkflowEngineApi jbpmHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Autowired
	private MesuresTemporalsHelper mesuresTemporalsHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public SortedSet<Entry<InstanciaProcesDto, List<ExpedientLogDto>>> registreFindLogsOrdenatsPerData(
			Long expedientId,
			boolean detall) throws NoTrobatException, PermisDenegatException {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		Map<InstanciaProcesDto, List<ExpedientLogDto>> resposta = new HashMap<InstanciaProcesDto, List<ExpedientLogDto>>();
		List<InstanciaProcesDto> arbre = expedientHelper.getArbreInstanciesProces(
				expedient.getProcessInstanceId());
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedient.getId());
		List<String> taskIds = new ArrayList<String>();
		String parentProcessInstanceId = null;
		Map<String, String> processos = new HashMap<String, String>();
		for (InstanciaProcesDto ip: arbre) {
			resposta.put(ip, new ArrayList<ExpedientLogDto>());
			for (ExpedientLog log: logs) {
				if (log.getProcessInstanceId().toString().equals(ip.getId())) {
					// Inclourem el log si:
					//    - Estam mostrant el log detallat
					//    - El log no se correspon a una tasca
					//    - Si el log pertany a una tasca i encara
					//      no s'ha afegit cap log d'aquesta tasca
					if (detall || !log.isTargetTasca() || !taskIds.contains(log.getTargetId())) {
						taskIds.add(log.getTargetId());
						resposta.get(ip).addAll(
								getLogs(
										processos,
										log,
										parentProcessInstanceId,
										ip.getId(),
										detall));
					}
				}
			}
		}
		SortedSet<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>> sortedEntries = new TreeSet<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>>(new Comparator<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>>() {
			@Override
			public int compare(Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>> e1, Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>> e2) {
				if (e1.getKey() == null || e2.getKey() == null)
					return 0;
				int res = e1.getKey().getId().compareTo(e2.getKey().getId());
				if (e1.getKey().getId().equals(e2.getKey().getId())) {
					return res;
				} else {
					return res != 0 ? res : 1;
				}
			}
		});
		sortedEntries.addAll(resposta.entrySet());
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens tasca");
		return sortedEntries;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientLogDto> registreFindExpedientCanvisEstat(
			Long expedientId,
			boolean detall) throws NoTrobatException {

		List<ExpedientLogDto> logs = new ArrayList<ExpedientLogDto>();
		for (ExpedientLog log : expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedientId)) {
			if (detall || log.getAccioTipus().equals(ExpedientLogAccioTipus.EXPEDIENT_ESTAT_CANVIAR)) {
				logs.add(conversioTipusHelper.convertir(log, ExpedientLogDto.class));
			}
		}
		return logs;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<String, ExpedientTascaDto> registreFindTasquesPerLogExpedient(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consultant tasques per la pipella de registre de l'expedient (expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedientId);
		Map<String, ExpedientTascaDto> tasquesPerLogs = new HashMap<String, ExpedientTascaDto>();
		for (ExpedientLog log: logs) {
			if (log.isTargetTasca()) {
				WTaskInstance task = jbpmHelper.getTaskById(log.getTargetId());
				if (task != null) {
					tasquesPerLogs.put(
							log.getTargetId(),
							tascaHelper.toExpedientTascaDto(
									task,
									expedient,
									false,
									false));
				}
			}
		}
		return tasquesPerLogs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void registreRetrocedir(
			Long expedientId,
			Long logId,
			boolean retrocedirPerTasques) throws NoTrobatException, PermisDenegatException {
		logger.debug("Retrocedint expedient amb els logs (" +
				"expedientId=" + expedientId + ", " +
				"logId=" + logId + ", " +
				"retrocedirPerTasques=" + retrocedirPerTasques + ")");
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_MANAGE,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		ExpedientLog log = expedientLogRepository.findById(logId).orElse(null);
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
		if (log.getExpedient().isAmbRetroaccio()) {
			ExpedientLog logRetroces = expedientLoggerHelper.afegirLogExpedientPerExpedient(
					log.getExpedient().getId(),
					retrocedirPerTasques ? ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR,
							log.getAccioParams());
			expedientLoggerHelper.retrocedirFinsLog(log, retrocedirPerTasques, logRetroces.getId());
			logRetroces.setEstat(ExpedientLogEstat.IGNORAR);
		}
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void registreBuidarLog(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Buidant logs de l'expedient("
				+ "expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_MANAGE,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		jbpmHelper.deleteProcessInstanceTreeLogs(expedient.getProcessInstanceId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientLogDto> registreFindLogsTascaOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consultant logs d'una tasca de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"logId=" + logId + ")");
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		List<ExpedientLog> logs = expedientLogRepository.findLogsTascaByIdOrdenatsPerData(String.valueOf(logId));
		return conversioTipusHelper.convertirList(logs, ExpedientLogDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientLogDto> registreFindLogsRetroceditsOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consultant logs d'un retrocés (" +
				"expedientId=" + expedientId + ", " +
				"logId=" + logId + ")");
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		List<ExpedientLog> logs = expedientLoggerHelper.findLogsRetrocedits(logId);
		return conversioTipusHelper.convertirList(logs, ExpedientLogDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpedientLogDto registreFindLogById(
			Long logId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consultant log donat el seu id (" +
				//"expedientId=" + expedientId + ", " +
				"logId=" + logId + ")");
		/*expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});*/
		return conversioTipusHelper.convertir(
				expedientLogRepository.findById(logId),
				ExpedientLogDto.class);
	}



	private List<ExpedientLogDto> getLogs(
			Map<String, String> processos,
			ExpedientLog log,
			String parentProcessInstanceId,
			String piId,
			boolean detall) {
		// Obtenim el token de cada registre
		WToken token = null;
		if (log.getJbpmLogId() != null) {
			token = expedientLoggerHelper.getTokenByJbpmLogId(log.getJbpmLogId());
		}
		String tokenName = null;
		String processInstanceId = null;
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		if (token != null) {
			tokenName = token.getName();
			processInstanceId = token.getProcessInstanceId();

			// Entram per primera vegada
			if (parentProcessInstanceId == null) {
				parentProcessInstanceId = processInstanceId;
				processos.put(processInstanceId, "");
			} else {
				// Canviam de procés
				if (!parentProcessInstanceId.equals(token.getProcessInstanceId())){
					// Entram en un nou subproces
					if (!processos.containsKey(processInstanceId)) {
						//TODO: revisar quan es faci la retroacció
						processos.put(processInstanceId, token.getSuperRootTokenId());

						if (parentProcessInstanceId.equals(piId)){
							// Añadimos una nueva línea para indicar la llamada al subproceso
							ExpedientLogDto dto = new ExpedientLogDto();
							dto.setId(log.getId());
							dto.setData(log.getData());
							dto.setUsuari(log.getUsuari());
							dto.setEstat(ExpedientLogEstat.IGNORAR.name());
							dto.setAccioTipus(ExpedientLogAccioTipus.PROCES_LLAMAR_SUBPROCES.name());
							String titol = null;
							if (token.getProcessInstanceKey() == null)
								titol = token.getProcessDefinitionName() + " " + log.getProcessInstanceId();
							else
								titol = token.getProcessInstanceKey();
							dto.setAccioParams(titol);
							dto.setTargetId(log.getTargetId());
							dto.setTargetTasca(false);
							dto.setTargetProces(false);
							dto.setTargetExpedient(true);
							if (detall || (!("RETROCEDIT".equals(dto.getEstat()) || "RETROCEDIT_TASQUES".equals(dto.getEstat()) || "EXPEDIENT_MODIFICAR".equals(dto.getAccioTipus()))))
								resposta.add(dto);
						}
					}
				}
				tokenName = processos.get(processInstanceId) + tokenName;
			}
		}

		if (piId == null || log.getProcessInstanceId().equals(piId)) {
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(token == null ? ExpedientLogEstat.IGNORAR.name() : log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTokenName(tokenName);
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			if (detall || (!("RETROCEDIT".equals(dto.getEstat()) || "RETROCEDIT_TASQUES".equals(dto.getEstat()) || "EXPEDIENT_MODIFICAR".equals(dto.getAccioTipus()))))
				resposta.add(dto);
		}
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientRegistreServiceImpl.class);

}
