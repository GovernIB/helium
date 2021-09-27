/**
 * 
 */
package es.caib.helium.logic.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.helium.client.engine.model.WTaskInstance;
import es.caib.helium.client.expedient.proces.ProcesClientService;
import es.caib.helium.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.PermisDto;
import es.caib.helium.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.Estat;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.repository.EstatRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;

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
	private EstatRepository estatRepository;

	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private ProcesClientService procesClientService;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	

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
	
	/** Consulta el tipus d'expedient comprovant el permís de lectura. */
	public ExpedientTipus getExpedientTipusComprovantPermisEscriptura(Long id) {
		return getExpedientTipusComprovantPermisos(
				id,
				null,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});
	}
	
	/** Consulta el tipus d'expedient comprovant el permís de lectura. */
	public ExpedientTipus getExpedientTipusComprovantPermisEsborrar(Long id) {
		return getExpedientTipusComprovantPermisos(
				id,
				null,
				new Permission[] {
						ExtendedPermission.DELETE,
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
						ExtendedPermission.DESIGN, // permís antic
						ExtendedPermission.DESIGN_ADMIN,
						ExtendedPermission.ADMINISTRATION	
				});
	}

	/** Consulta el tipus d'expedient comprovant el permís de creació sobre el tipus d'expedient. */
	public ExpedientTipus getExpedientTipusComprovantPermisCrear(Long id) {
		return getExpedientTipusComprovantPermisos(
				id, 
				new Permission[] {
						ExtendedPermission.ADMINISTRATION
				},
				new Permission[]{
						ExtendedPermission.CREATE,
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
						ExtendedPermission.DESIGN, // permís antic
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
	 * @param permisosEntorn
	 * @param permisosTipusExpedient
	 * @return
	 */
	public ExpedientTipus getExpedientTipusComprovantPermisos(
			Long id,
			Permission[] permisosEntorn,
			Permission[] permisosTipusExpedient) {
		Optional<ExpedientTipus> expedientTipusOptional = expedientTipusRepository.findById(id);
		if (expedientTipusOptional.isEmpty()) {
			throw new NoTrobatException(ExpedientTipus.class,id);
		}
		ExpedientTipus expedientTipus = expedientTipusOptional.get();

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
		Optional<ExpedientTipus> expedientTipusOptional = expedientTipusRepository.findById(expedientTipusId);
		if (expedientTipusOptional.isEmpty()) {
			throw new NoTrobatException(ExpedientTipus.class,expedientTipusId);
		}
		ExpedientTipus expedientTipus = expedientTipusOptional.get();
		
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
		WTaskInstance task = workflowEngineApi.getTaskById(taskId);
		return findAmbProcessInstanceId(task.getProcessInstanceId());
	}

	public ExpedientTipus findAmbProcessInstanceId(
			String processInstanceId) {
		Long expId = procesClientService.getProcesExpedientId(processInstanceId);
		
		Expedient expedient = expedientRepository.getById(expId);
		return expedient.getTipus(); //expedientTipusRepository.findById(expedient.getTipus().getId()).get();
	}

	public Long findIdByProcessInstanceId(String processInstanceId) {
		ExpedientTipus expedientTipus = this.findAmbProcessInstanceId(processInstanceId);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, processInstanceId);
		return expedientTipus.getId();
	}

	/** Consulta tots els tipus d'expedients per a un etorn filtrant per aquells on l'usuari
	 * tingui algun dels permisos especificats
	 * @param entorn
	 * 			Entorn actual
	 * @param permisos
	 * 			Llistat de permisos
	 * @return
	 * 			Retorna la llista de tipus d'expedients sobre els quals l'usuari tingui algun dels permisos especificats com
	 * a paràmetre, per a retornar un tipus d'expedient basta que l'usuari tingui algun dels permisos de la llista, no tots.
	 */
	public List<ExpedientTipus> findAmbPermisos(
			Entorn entorn,
			Permission[] permisos) {
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
				permisos,
				auth);
		return tipusPermesos;
	}
	
	public List<Long> findIdsAmbPermisos(
			Entorn entorn,
			Permission[] permisos) {
		List<ExpedientTipus> tipusPermesos = findAmbPermisos(entorn, permisos);
		List<Long> ids = new ArrayList<Long>();
		for (ExpedientTipus tipus: tipusPermesos) {
			ids.add(tipus.getId());
		}
		return ids;
	}
	
	public List<ExpedientTipus> findAmbPermisRead(
			Entorn entorn) {
		return this.findAmbPermisos(
				entorn, new Permission[] {
				ExtendedPermission.READ,
				ExtendedPermission.ADMINISTRATION});
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

	public void initializeDefinicionsProces() {
		List<ExpedientTipus> llistat = expedientTipusRepository.findAll();
		for (ExpedientTipus expedientTipus: llistat) {
			Hibernate.initialize(expedientTipus.getDefinicionsProces());
		}
	}

	public EstatDto estatFindAmbId(Long expedientTipusId, Long estatId) {
		ExpedientTipus tipus = expedientTipusRepository.getById(expedientTipusId);
		Estat estat = estatRepository.getById(estatId);
		if (estat == null) {
			throw new NoTrobatException(Estat.class, estatId);
		}
		EstatDto dto = conversioTipusServiceHelper.convertir(
				estat, 
				EstatDto.class);
		// Herencia
		if (tipus.getExpedientTipusPare() != null) {
			if (tipus.getExpedientTipusPare().getId().equals(estat.getExpedientTipus().getId()))
				dto.setHeretat(true);
			else
				dto.setSobreescriu(estatRepository.findByExpedientTipusIdAndCodi(tipus.getExpedientTipusPare().getId(), estat.getCodi()) != null);					
		}
		return dto;
	}

}
