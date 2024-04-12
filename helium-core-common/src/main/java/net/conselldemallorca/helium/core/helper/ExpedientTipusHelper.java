/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa;
import net.conselldemallorca.helium.core.model.hibernate.Parametre;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PrincipalTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ParametreService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusUnitatOrganitzativaRepository;
import net.conselldemallorca.helium.v3.core.repository.ParametreRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;

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
	private ExpedientTipusUnitatOrganitzativaRepository expedientTipusUnitatOrganitzativaRepository;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private ParametreRepository parametreRepository;
	
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource 
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	
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
						ExtendedPermission.DESIGN, // permís antic
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
		// Comprova els permisos contra el tipus d'expedient: pel cas que sigui procediment comú es fa diferent
		if (!expedientTipus.isProcedimentComu()) {
			if (! comprovarPermisos(
					expedientTipus,
					permisosEntorn,
					permisosTipusExpedient))
				throw new PermisDenegatException(
						id,
						ExpedientTipus.class,
						permisosTipusExpedient);
			
		} else {
			return expedientTipus;
		}
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
	
	public boolean isAdministrador(Authentication auth) {
		boolean isAdministrador = false;
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
		for (GrantedAuthority grantedAuthority : authorities) {
	        if ("ROLE_ADMIN".equals(grantedAuthority.getAuthority())) {
	            isAdministrador = true;
	            break;
	        }
	    }
		return isAdministrador;
	}
	
	
	/** Consulta les unitats organitzatives per a un Tipus expedient amb procediment comú on l'usuari
	 * tingui algun dels permisos especificats.  També obtindrà les UO filles.
	 * @param expedientTipusId
	 * 			Id del tipus expedient
	 * @return
	 * 			Retorna la llista de id's de les unitats organitzatives sobre les quals l'usuari tingui permisos Admin o Read
	 */
	public List<Long> findIdsUnitatsOrgAmbPermisos(
			Long entornId,
			Long expedientTipusId,
			Permission[] permisosRequerits){
		//aquí obtinc la llista de les UO's per les quals l'usuari té permís (comptant les uo filles de l'arbre)
		List<Long> idsUnitatsOrganitzativesAmbPermisos = new ArrayList<Long>();
		List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList = new ArrayList<ExpedientTipusUnitatOrganitzativa>();
		if(expedientTipusId != null)
			expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusId(expedientTipusId);
		else
			expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusEntornId(entornId);
		if(expTipUnitOrgList!=null && !expTipUnitOrgList.isEmpty()) {
			//Mirem les unitats filles
			idsUnitatsOrganitzativesAmbPermisos = idsUOPermeses(expTipUnitOrgList,permisosRequerits);
		}
		return idsUnitatsOrganitzativesAmbPermisos;
	}
	
	/** Extreu la llista d'identificadors d'expedients tipus a partir de la llista de permisos sobre la relació ExpedientTipus-UnitatOrganitzativa. 
	 * 
	 * @param expTipUnitOrgList Llista de relacions d'unitats organitzatives i expedients tipus.
	 * @return La llista dels identificadors les tipus d'expedient.
	 */
	public List<Long> idsExpedientTipusComunsPermesos (List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList){
		List<Long> idsExpedientTipusComunsPermesos = new ArrayList<Long>();
		if (expTipUnitOrgList != null ) {
			for(ExpedientTipusUnitatOrganitzativa expTipUnitOrg: expTipUnitOrgList) {
				if (expTipUnitOrg.getExpedientTipus() != null 
						&& expTipUnitOrg.getExpedientTipus().isProcedimentComu()
						&& ! idsExpedientTipusComunsPermesos.contains(expTipUnitOrg.getExpedientTipus().getId())) 
				{
					idsExpedientTipusComunsPermesos.add(expTipUnitOrg.getExpedientTipus().getId());	
				}
			}
		}
		return idsExpedientTipusComunsPermesos;
	}
	
	/** Retorna una llista d'identificadors d'UO's a partir de la llista de relacio de tipus d'expedient amb una UO per procediments comuns.
	 * 
	 * @param expTipUnitOrgList Llista de relacions d'unitats organitzatives i expedients tipus.
	 * @param permisosRequerits Llista de permisos requerits sobre les uo's.
	 * @return La llista d'identificadors de les unitats organitztatives i les seves filles.
	 */
	public List<Long> idsUOPermeses (List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList, Permission[] permisosRequerits){
		List<Long> idsUnitatsOrganitzativesAmbPermisos = new ArrayList<Long>();
		List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgListAmbPermisos = new ArrayList<ExpedientTipusUnitatOrganitzativa>();
		List<PermisDto> permisosList = new ArrayList<PermisDto>();
		List<UnitatOrganitzativa> unitatsOrgFilles = new ArrayList<UnitatOrganitzativa>();
		boolean tePermisEnTotes = false;
		for(ExpedientTipusUnitatOrganitzativa expTipUnitOrg: expTipUnitOrgList) {
			Parametre parametreArrel = parametreRepository.findByCodi(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
			if(parametreArrel==null)
				throw new NoTrobatException(Parametre.class,ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
			String arrel = parametreArrel.getValor();
			if(arrel!=null && arrel.equals(expTipUnitOrg.getUnitatOrganitzativa().getCodi()) && !tePermisEnTotes){ //En cas que sigui l'arrel tindrà permís sobre totes les UO
				permisosList = permisosHelper.findPermisos(
						expTipUnitOrg.getId(),
						ExpedientTipusUnitatOrganitzativa.class);
					if(comprovarPermisosSobreUO(expTipUnitOrg.getId(),permisosRequerits)) {
						idsUnitatsOrganitzativesAmbPermisos = unitatOrganitzativaRepository.findAllUnitatOrganitzativaIds();
						//tenir en compte Estat = V vigent???
						tePermisEnTotes = true;
						break;
					}
			}
		}
		if(!tePermisEnTotes) {
			//Mirem si hi ha permisos sobre expTipusUO pare i afegir les UO filles d'aquesta
			expTipUnitOrgListAmbPermisos.addAll(expTipUnitOrgList);
			for(ExpedientTipusUnitatOrganitzativa etuo: expTipUnitOrgListAmbPermisos) {
				permisosList = permisosHelper.findPermisos(
						etuo.getId(),
						ExpedientTipusUnitatOrganitzativa.class);
				if(!permisosList.isEmpty() && !idsUnitatsOrganitzativesAmbPermisos.contains(etuo.getUnitatOrganitzativa().getId())) {
						if(comprovarPermisosSobreUO(etuo.getId(),permisosRequerits)) {
							idsUnitatsOrganitzativesAmbPermisos.add(etuo.getUnitatOrganitzativa().getId());
							//Afegir les UO filles d'aquesta que té permís
							unitatsOrgFilles = unitatOrganitzativaHelper.unitatsOrganitzativesFindLlistaTotesFilles
								(null, etuo.getUnitatOrganitzativa().getCodi(), null);
							for(UnitatOrganitzativa uoFilla: unitatsOrgFilles) {
								if(!idsUnitatsOrganitzativesAmbPermisos.contains(uoFilla.getId())) {
									idsUnitatsOrganitzativesAmbPermisos.add(uoFilla.getId());
								}
							}
					}		
				}
			}
		}
		return idsUnitatsOrganitzativesAmbPermisos;
	}
	
	public List<ExpedientTipus> expedientsTipusComunsPermesos(List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList){
		 List<ExpedientTipus> expedientsTipusComunsPermesos = new ArrayList<ExpedientTipus>();
		 List<Long> idsExpedientTipusComunsPermesos = this.idsExpedientTipusComunsPermesos(expTipUnitOrgList);
		 ExpedientTipus expTipusPermes = null;
		 for(Long expTipusId: idsExpedientTipusComunsPermesos) {
			 expTipusPermes = expedientTipusRepository.findById(expTipusId);
			 if(expTipusPermes!=null && !expedientsTipusComunsPermesos.contains(expTipusPermes)) {
				 expedientsTipusComunsPermesos.add(expTipusPermes);
			 }
		 }
		 return expedientsTipusComunsPermesos;		 
	}
	
	public Map<Long,List<String>> unitatsPerTipusComu (Long entornId,List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList, Permission[] permisosRequerits){
		Map<Long,List<String>> unitatsPerTipusComu = new HashMap<Long, List<String>>();
		List<Long> idsUnitatsOrganitzativesAmbPermisos = new ArrayList<Long>();
		for(ExpedientTipusUnitatOrganitzativa expTipUo : expTipUnitOrgList ) {
			if(expTipUo.getExpedientTipus().isProcedimentComu()) {
				List<String> unitatsOrganitvesCodis = new ArrayList<String>();
				idsUnitatsOrganitzativesAmbPermisos = this.findIdsUnitatsOrgAmbPermisos(entornId, expTipUo.getExpedientTipus().getId(), permisosRequerits);
				for(Long id: idsUnitatsOrganitzativesAmbPermisos) {
					UnitatOrganitzativa uo = unitatOrganitzativaHelper.findById(id);
					if(uo!=null && !unitatsOrganitvesCodis.contains(uo.getCodi()))
						unitatsOrganitvesCodis.add(uo.getCodi());
				}
				unitatsPerTipusComu.put(expTipUo.getExpedientTipus().getId(),unitatsOrganitvesCodis);
			}
		}
		return unitatsPerTipusComu;
	}
	
	public Map<Long,List<Long>> unitatsPerTipusComuIds (Long entornId,List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList, Permission[] permisosRequerits){
		Map<Long,List<Long>> unitatsPerTipusComuIds = new HashMap<Long, List<Long>>();
		List<Long> idsUnitatsOrganitzativesAmbPermisos = new ArrayList<Long>();
		for(ExpedientTipusUnitatOrganitzativa expTipUo : expTipUnitOrgList ) {
			if(expTipUo.getExpedientTipus().isProcedimentComu()) {
				idsUnitatsOrganitzativesAmbPermisos = this.findIdsUnitatsOrgAmbPermisos(entornId, expTipUo.getExpedientTipus().getId(), permisosRequerits);
				if(!idsUnitatsOrganitzativesAmbPermisos.isEmpty()) {
					unitatsPerTipusComuIds.put(expTipUo.getExpedientTipus().getId(),idsUnitatsOrganitzativesAmbPermisos);
				}
			}
		}
		return unitatsPerTipusComuIds;
	}
	
	public boolean comprovarPermisosSobreUO (
			Long expedientTipusUnitatOrganitzativaId, 
			Permission[] permisos) {
		boolean tePermis = permisosHelper.isGrantedAny(
				expedientTipusUnitatOrganitzativaId,
				ExpedientTipusUnitatOrganitzativa.class,
				permisos,
				SecurityContextHolder.getContext().getAuthentication());
		return tePermis;
	}
	
	public boolean comprovarPermisosAndRoleOrUser (
			PermisDto permis, 
			Authentication authOriginal, 
			Permission[] permisos) {

		List<Permission> permisosList = Arrays.asList(permisos);
		int permisosComplits = 0;
		
		for (Permission p: permisosList) {
			if (p.equals(ExtendedPermission.CANCEL) && permis.isCancel()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.DATA_MANAGE) && permis.isDataManagement()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.DEFPROC_UPDATE) && permis.isDefprocUpdate()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.DESIGN) && permis.isDesign()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.DESIGN_ADMIN) && permis.isDesignAdmin()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.DESIGN_DELEG) && permis.isDesignDeleg()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.DOC_MANAGE) && permis.isDocManagement()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.LOG_MANAGE) && permis.isLogManage()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.LOG_READ) && permis.isLogRead()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.MANAGE) && permis.isManage()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.ORGANIZATION) && permis.isOrganization()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.REASSIGNMENT) && permis.isReassignment()) {
				permisosComplits++;	
			} else if (p.equals(ExtendedPermission.RELATE) && permis.isRelate()) {
				permisosComplits++;	
			} else if (p.equals(ExtendedPermission.SCRIPT_EXE) && permis.isScriptExe()) {
				permisosComplits++;	
			} else if (p.equals(ExtendedPermission.STOP) && permis.isStop()) {
				permisosComplits++;	
			} else if (p.equals(ExtendedPermission.SUPERVISION) && permis.isSupervision()) {
				permisosComplits++;	
			} else if (p.equals(ExtendedPermission.TASK_ASSIGN) && permis.isTaskAssign()) {
				permisosComplits++;	
			} else if (p.equals(ExtendedPermission.TASK_MANAGE) && permis.isTaskManagement()) {
				permisosComplits++;	
			} else if (p.equals(ExtendedPermission.TASK_SUPERV) && permis.isTaskSupervision()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.TERM_MANAGE) && permis.isTermManagement()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.TOKEN_MANAGE) && permis.isTokenManage()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.TOKEN_READ) && permis.isTokenRead()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.UNDO_END) && permis.isUndoEnd()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.ADMINISTRATION) && permis.isAdministration()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.CREATE) && permis.isCreate()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.DELETE) && permis.isDelete()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.READ) && permis.isRead()) {
				permisosComplits++;
			} else if (p.equals(ExtendedPermission.WRITE) && permis.isWrite()) {
				permisosComplits++;				
			}
		}
		
		//No té cap dels permisos requerits
		if (permisosComplits==0) {
			return false;
		}
		
		if (this.isAdministrador(authOriginal)) {
			return true;
		}
		
		boolean tePermis = false;
		if (permis.getPrincipalNom()!=null 
				&& authOriginal!=null 
				&& authOriginal.getName()!=null)
		{
			if (PrincipalTipusEnumDto.USUARI.equals(permis.getPrincipalTipus())) {
				// permís per usuari
				tePermis =  permis.getPrincipalNom().equals(authOriginal.getName());
			} else {
				// permís per rol
				for (GrantedAuthority ga : authOriginal.getAuthorities()) {
					if (ga.getAuthority().equals(permis.getPrincipalNom())) {
						tePermis = true;
						break;
					}
				}
			}
		}
		return tePermis;
	}

}
