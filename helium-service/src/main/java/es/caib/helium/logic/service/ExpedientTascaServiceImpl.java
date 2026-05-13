/**
 *
 */
package es.caib.helium.logic.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import es.caib.comanda.model.management.TascaEstat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.commons.dto.ExpedientTascaDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exception.ValidacioException;
import es.caib.helium.logic.intf.dto.engine.WProcessInstance;
import es.caib.helium.logic.intf.dto.engine.WTaskInstance;
import es.caib.helium.logic.intf.service.ExpedientTascaService;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientLog;
import es.caib.helium.persistence.entity.ExpedientLog.ExpedientLogAccioTipus;
import es.caib.helium.persistence.entity.Registre;
import es.caib.helium.persistence.entity.Tasca;
import es.caib.helium.persistence.repository.RegistreRepository;
import es.caib.helium.logic.helper.ComandaHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientLoggerHelper;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.logic.helper.PermisosHelper;
import es.caib.helium.logic.security.ExtendedPermission;


/**
 * Implementació dels mètodes del servei ExpedientService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTascaServiceImpl implements ExpedientTascaService {

	@Resource
	private RegistreRepository registreRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private WorkflowEngineApi jbpmHelper;
	@Resource
	private ComandaHelper comandaHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientTascaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consulta de tasques de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		boolean mostrarTasquesAltresUsuaris = expedientHelper.isGrantedAny(
				expedient,
				new Permission[] {
						ExtendedPermission.TASK_SUPERV,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		// Tasques no finalitzades
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedientPerInstanciaProces(
				expedient,
				processInstanceId,
				false,
				true,
				mostrarTasquesAltresUsuaris);
		// Tasques finalitzades
		tasques.addAll(
				tascaHelper.findTasquesPerExpedientPerInstanciaProces(
						expedient,
						processInstanceId,
						true,
						false,
						mostrarTasquesAltresUsuaris));
		return tasques;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientTascaDto> findPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consulta de tasques pendents de l'expedient (" +
				"id=" + expedientId + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		boolean mostrarTasquesAltresUsuaris = expedientHelper.isGrantedAny(
				expedient,
				new Permission[] {
						ExtendedPermission.TASK_SUPERV,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		for (WProcessInstance jpi: jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId())) {
			resposta.addAll(
					tascaHelper.findTasquesPerExpedientPerInstanciaProces(
							jpi.getId().toString(),
							expedient,
							mostrarTasquesAltresUsuaris,
							nomesTasquesPersonals,
							nomesTasquesGrup));
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void cancelar(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException {
		logger.debug("Cancelar tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if (!expedient.isAnulat()) {
			WTaskInstance task = tascaHelper.comprovarTascaPertanyExpedient(
							tascaId,
							expedient);
			expedientLoggerHelper.afegirLogExpedientPerProces(
					task.getProcessInstanceId(),
					ExpedientLogAccioTipus.TASCA_CANCELAR,
					null);
			task = jbpmHelper.cancelTaskInstance(String.valueOf(tascaId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(tascaId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.CANCELAR);

			Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
			comandaHelper.upsertTasca(tascaId, tasca.getNom(), expedient.getNumero(), task, TascaEstat.CANCELADA);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void suspendre(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException {
		logger.debug("Reprende tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if (!expedient.isAnulat()) {
			WTaskInstance task = tascaHelper.comprovarTascaPertanyExpedient(
					tascaId,
					expedient);
			expedientLoggerHelper.afegirLogExpedientPerProces(
					task.getProcessInstanceId(),
					ExpedientLogAccioTipus.TASCA_SUSPENDRE,
					null);
			task = jbpmHelper.suspendTaskInstance(String.valueOf(tascaId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(tascaId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.ATURAR);
			Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
			comandaHelper.upsertTasca(tascaId, tasca.getNom(), expedient.getNumero(), task, TascaEstat.PENDENT);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reprendre(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException {
		logger.debug("Reprende tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if (!expedient.isAnulat()) {
			WTaskInstance task = tascaHelper.comprovarTascaPertanyExpedient(
					tascaId,
					expedient);
			expedientLoggerHelper.afegirLogExpedientPerProces(
					task.getProcessInstanceId(),
					ExpedientLogAccioTipus.TASCA_CONTINUAR,
					null);
			task = jbpmHelper.resumeTaskInstance(String.valueOf(tascaId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(tascaId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.REPRENDRE);
			Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
			comandaHelper.upsertTasca(tascaId, tasca.getNom(), expedient.getNumero(), task, TascaEstat.INICIADA);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reassignar(
			Long expedientId,
			String tascaId,
			String expressio) throws NoTrobatException, ValidacioException {
		logger.debug("Reassignar tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.TASK_ASSIGN,
						ExtendedPermission.REASSIGNMENT,
						ExtendedPermission.ADMINISTRATION});
		tascaHelper.comprovarTascaPertanyExpedient(
				tascaId,
				expedient);
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(tascaId);
		ExpedientLog expedientLog = null;
		expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				tascaId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				null);
		if (!expedient.isAturat()) {
			jbpmHelper.reassignTaskInstance(tascaId, expressio, expedient.getEntorn().getId());
			String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(tascaId);
			expedientLog.setAccioParams(previousActors + "::" + currentActors);
			String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			crearRegistreReassignarTasca(
					expedient.getId(),
					tascaId,
					usuari,
					expressio);

			WTaskInstance task = jbpmHelper.getTaskById(tascaId);
			Tasca tasca = tascaHelper.findTascaByJbpmTask(task);
			comandaHelper.upsertTasca(tascaId, tasca.getNom(), expedient.getNumero(), task, TascaEstat.PENDENT);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}



	private Registre crearRegistreTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.TASCA,
				tascaId);
		return registreRepository.save(registre);
	}
	private Registre crearRegistreReassignarTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String expression) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge("Reassignació de la tasca amb l'expressió \"" + expression + "\"");
		return registreRepository.save(registre);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaServiceImpl.class);

}
