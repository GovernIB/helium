/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

/**
 * Helper per als tipus d'expedient.
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
			boolean comprovarPermisRead) {
		return getExpedientTipusComprovantPermisos(
				id,
				comprovarPermisRead,
				false,
				false,
				false);
	}

	public ExpedientTipus getExpedientTipusComprovantPermisos(
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete,
			boolean comprovarPermisDisseny) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(id);
		if (expedientTipus == null) {
			throw new NoTrobatException(ExpedientTipus.class,id);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = null;
		if (comprovarPermisRead) {
			permisos = new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.SUPERVISION,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					id,
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						ExpedientTipus.class,
						permisos);
			}
		}
		if (comprovarPermisWrite) {
			permisos = new Permission[] {
					ExtendedPermission.WRITE,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					id,
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						ExpedientTipus.class,
						permisos);
			}
		}
		if (comprovarPermisDelete) {
			permisos = new Permission[] {
					ExtendedPermission.DELETE,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					id,
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						ExpedientTipus.class,
						permisos);
			}
		}
		if (comprovarPermisDisseny) {
			permisos = new Permission[] {
					ExtendedPermission.DESIGN,
					ExtendedPermission.MANAGE,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					id,
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						ExpedientTipus.class,
						permisos);
			}
		}
		return expedientTipus;
	}

	public void comprovarPermisDissenyEntornITipusExpedient(
			Long entornId,
			Long expedientTipusId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean tePermis = false;
		if (permisosHelper.isGrantedAny(
				entornId,
				Entorn.class,
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION},
				auth)) {
			tePermis = true;
		}
		if (!tePermis && permisosHelper.isGrantedAny(
				expedientTipusId,
				ExpedientTipus.class,
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.MANAGE,
						ExtendedPermission.ADMINISTRATION},
				auth)) {
			tePermis = true;
		}
		if (!tePermis) {
			throw new PermisDenegatException(
					expedientTipusId,
					ExpedientTipus.class,
					new Permission[] {ExtendedPermission.DESIGN});
		}
	}

	public ExpedientTipus getExpedientTipusComprovantPermisReassignar(Long id) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(id);
		if (expedientTipus == null) {
			throw new NoTrobatException(ExpedientTipus.class,id);
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
	public ExpedientTipus getExpedientTipusComprovantPermisSupervisio(Long id) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(id);
		if (expedientTipus == null) {
			throw new NoTrobatException(ExpedientTipus.class,id);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAny(
				id,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.SUPERVISION,
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
		return expedientTipusRepository.findOne(piexp.getTipus().getId());
	}

	public List<ExpedientTipus> findAmbPermisRead(
			Entorn entorn) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<ExpedientTipus> tipusPermesos = this.findAmbEntorn(entorn);
		permisosHelper.filterGrantedAny(
				tipusPermesos,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION},
				auth);
		return tipusPermesos;
	}

	public List<Long> findIdsAmbPermisRead(
			Entorn entorn) {
		List<ExpedientTipus> tipusPermesos = findAmbPermisRead(entorn);
		List<Long> ids = new ArrayList<Long>();
		for (ExpedientTipus tipus: tipusPermesos) {
			ids.add(tipus.getId());
		}
		return ids;
	}

	public List<ExpedientTipus> findAmbEntorn(
			Entorn entorn) {
		return  expedientTipusRepository.findByEntorn(entorn);
	}

	public List<Long> findIdsAmbEntorn(
			Entorn entorn) {
		List<ExpedientTipus> tipus = findAmbEntorn(entorn);
		List<Long> ids = new ArrayList<Long>();
		for (ExpedientTipus t: tipus) {
			ids.add(t.getId());
		}
		return ids;
	}

}
