/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import javax.annotation.Resource;

import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.PermisTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

/**
 * Helper per a gestionar els entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientTipusHelper {

	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;

	@Resource
	private JbpmHelper jbpmHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;



	public ExpedientTipus getExpedientTipusComprovantPermisos(
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete) throws NotFoundException, NotAllowedException {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(id);
		if (expedientTipus == null) {
			throw new NotFoundException(
					id,
					ExpedientTipus.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisRead) {
			if (!permisosHelper.isGrantedAny(
					id,
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						ExpedientTipus.class,
						PermisTipusEnumDto.READ);
			}
		}
		if (comprovarPermisWrite) {
			if (!permisosHelper.isGrantedAny(
					id,
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						ExpedientTipus.class,
						PermisTipusEnumDto.WRITE);
			}
		}
		if (comprovarPermisDelete) {
			if (!permisosHelper.isGrantedAny(
					id,
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.DELETE,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						ExpedientTipus.class,
						PermisTipusEnumDto.DELETE);
			}
		}
		return expedientTipus;
	}
	
	public ExpedientTipus getExpedientTipusComprovantPermisosReassignar(Long id) throws NotFoundException, NotAllowedException {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(id);
		if (expedientTipus == null) {
			throw new NotFoundException(
					id,
					ExpedientTipus.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAny(
				id,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.REASSIGNMENT,
					ExtendedPermission.ADMINISTRATION},
				auth)) {
			return null;
		}
		
		return expedientTipus;
	}
	
	public ExpedientTipus findAmbTaskId(
			String taskId) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		return findAmbProcessInstanceId(task.getProcessInstanceId());
	}

	public ExpedientTipus findAmbProcessInstanceId(
			String processInstanceId) {
		ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(processInstanceId);
		return expedientTipusRepository.findOne(piexp.getExpedientTipusId());
	}

}
