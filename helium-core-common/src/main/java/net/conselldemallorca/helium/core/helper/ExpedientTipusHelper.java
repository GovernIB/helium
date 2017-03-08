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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PrincipalTipusEnumDto;
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
	
	@Resource
	private EntornHelper entornHelper;

	/** Consulta el tipus d'expedient comprovant el permís de lectura. */
	public ExpedientTipus getExpedientTipusComprovantPermisLectura(Long id) {
		return getExpedientTipusComprovantPermisos(
				id,
				null,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
	}
	
	/** Consulta el tipus d'expedient comprovant el permís de disseny sobre el tipus d'expedient. */
	public ExpedientTipus getExpedientTipusComprovantPermisDisseny(Long id) {
		return getExpedientTipusComprovantPermisos(
				id, 
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION
				},
				new Permission[]{
						ExtendedPermission.DESIGN_ADMIN,
						ExtendedPermission.ADMINISTRATION	
				});
	}
	
	/** Consulta el tipus d'expedient comprovant el permís de disseny delegat sobre el tipus d'expedient. S'és
	 * administrador delegat si es té permís delegat, permís de disseny administrador sobre el tipus d'expedient o 
	 * administrador del tipus d'expedient. */
	public ExpedientTipus getExpedientTipusComprovantPermisDissenyDelegat(Long id) {
		return getExpedientTipusComprovantPermisos(
				id, 
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION
				},
				new Permission[]{
						ExtendedPermission.DESIGN_DELEG,
						ExtendedPermission.DESIGN_ADMIN,
						ExtendedPermission.ADMINISTRATION	
				});
	}
	
	public ExpedientTipus getExpedientTipusComprovantPermisReassignar(Long id) {
		return getExpedientTipusComprovantPermisos(
				id, 
				null,
				new Permission[] {
						ExtendedPermission.REASSIGNMENT,
						ExtendedPermission.ADMINISTRATION});
	}
	
	public ExpedientTipus getExpedientTipusComprovantPermisSupervisio(Long id) {
		return getExpedientTipusComprovantPermisos(
				id,
				null,
				new Permission[] {
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.TASK_SUPERV,
						ExtendedPermission.ADMINISTRATION});
	}
	
	/** Mètode genèric per obtenir el tipus d'expedient comprovant els permisos. Quan es compleix
	 * algun permís es retorna el tipus d'expedient, si no es llença una excepció de permisos.
	 * @param id
	 * @param permisos
	 * @return
	 */
	public ExpedientTipus getExpedientTipusComprovantPermisos(
			Long id,
			Permission[] permisosEntorn,
			Permission[] permisosTipusExpedient) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(id);
		if (expedientTipus == null) {
			throw new NoTrobatException(ExpedientTipus.class,id);
		}

		// Comprova els permisos contra el tipus d'expedient
		if (! comprovarPermisos(
				expedientTipus,
				permisosEntorn,
				permisosTipusExpedient))
			throw new PermisDenegatException(
					id,
					ExpedientTipus.class,
					permisosTipusExpedient);
		return expedientTipus;
	}
	
	public boolean comprovarPermisSupervisio(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		if (expedientTipus == null) {
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		}
		
		return comprovarPermisos(
				expedientTipus, 
				null,
				new Permission[] {
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.TASK_SUPERV,
						ExtendedPermission.ADMINISTRATION});
		
	}
	
	public boolean comprovarPermisos(
			ExpedientTipus expedientTipus,
			Permission[] permisosEntorn,
			Permission[] permisosTipusExpedient) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		boolean permes = false;
		
		if (permisosEntorn != null 
				&& permisosEntorn.length > 0 
				&& permisosHelper.isGrantedAny(
						expedientTipus.getEntorn().getId(), 
						Entorn.class, 
						permisosEntorn, 
						auth)) {
				permes = true;
		} else {
				// Comprova els permisos contra el tipus d'expedient
				permes = permisosHelper.isGrantedAny(
						expedientTipus.getId(),
						ExpedientTipus.class,
						permisosTipusExpedient,
						auth);
		}		
		return permes;
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
	
	public String getRolsTipusExpedient(Authentication auth, ExpedientTipus expedientTipus) {

		String rols = "";
		// Rols usuari
		List<String> rolsUsuari = new ArrayList<String>();
		if (auth != null && auth.getAuthorities() != null) {
			for (GrantedAuthority gauth : auth.getAuthorities()) {
				rolsUsuari.add(gauth.getAuthority());
			}
		}
		// Rols tipus expedient
		List<String> rolsTipusExpedient = new ArrayList<String>();
		rolsTipusExpedient.add("ROLE_ADMIN");
		rolsTipusExpedient.add("ROLE_USER");
		rolsTipusExpedient.add("ROLE_WS");
		if (expedientTipus != null) {
			List<PermisDto> permisos = permisosHelper.findPermisos(
					expedientTipus.getId(),
					ExpedientTipus.class);
			if (permisos != null)
				for (PermisDto permis: permisos) {
					if (PrincipalTipusEnumDto.ROL.equals(permis.getPrincipalTipus()))
						rolsTipusExpedient.add(permis.getPrincipalNom());
				}
		}
		rolsUsuari.retainAll(rolsTipusExpedient);
		
		for (String rol : rolsUsuari) {
			rols += rol + ",";
		}
		if (rols.length() > 0) {
			rols = rols.substring(0, rols.length() - 1);
			if (rols.length() > 2000) {
				rols = rols.substring(0, 2000);
				rols = rols.substring(0, rols.lastIndexOf(","));
			}
		} else {
			rols = null;
		}
		
		return rols;
	}

}
