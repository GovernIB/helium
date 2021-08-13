/**
 * 
 */
package es.caib.helium.logic.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.service.ExpedientTascaService;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.Registre;
import es.caib.helium.persist.repository.RegistreRepository;


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
	private TascaHelper tascaHelper;
//	@Resource
//	private PermisosHelper permisosHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	@Resource
	private TascaClientService tascaClientService;



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
			workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					task.getProcessInstanceId(),
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_CANCELAR,
					null);
			workflowEngineApi.cancelTaskInstance(tascaId);
			tascaClientService.setCancelada(tascaId, true);
			crearRegistreTasca(
					expedientId,
					tascaId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.CANCELAR);
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
			workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					task.getProcessInstanceId(),
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_SUSPENDRE,
					null);
			workflowEngineApi.suspendTaskInstance(tascaId);
			tascaClientService.setSuspesa(tascaId, true);
			crearRegistreTasca(
					expedientId,
					tascaId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.ATURAR);
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
			workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					task.getProcessInstanceId(),
					WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_CONTINUAR,
					null);
			workflowEngineApi.resumeTaskInstance(tascaId);
			tascaClientService.setSuspesa(tascaId, false);
			crearRegistreTasca(
					expedientId,
					tascaId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.REPRENDRE);
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
		WTaskInstance task = tascaHelper.comprovarTascaPertanyExpedient(
				tascaId,
				expedient);
		String previousActors = task.getStringActors(); //expedientLoggerHelper.getActorsPerReassignacioTasca(tascaId);
		Long informacioRetroaccioId = workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
				tascaId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_REASSIGNAR,
				null);
		if (!expedient.isAturat()) {
			task = workflowEngineApi.reassignTaskInstance(tascaId, expressio, expedient.getEntorn().getId());
			String currentActors = task.getStringActors(); //expedientLoggerHelper.getActorsPerReassignacioTasca(tascaId);
			workflowRetroaccioApi.actualitzaParametresAccioInformacioRetroaccio(
					informacioRetroaccioId,
					previousActors + "::" + currentActors);
			String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			
			// Actualitza la informació al MS
			tascaClientService.setResponsablesV1(
					task.getId(), new 
					ArrayList<String>(task.getPooledActors()));
			tascaClientService.setUsuariAssignat(tascaId, task.getActorId());
			
			crearRegistreReassignarTasca(
					expedient.getId(),
					tascaId,
					usuari,
					expressio);
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
