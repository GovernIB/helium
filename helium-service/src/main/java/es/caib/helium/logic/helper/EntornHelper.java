/**
 * 
 */
package es.caib.helium.logic.helper;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.repository.EntornRepository;

/**
 * Helper per a gestionar els entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntornHelper {

	@Resource
	private EntornRepository entornRepository;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;


	/** Consulta l'entorn per id sense comprovar permisos
	 * 
	 * @param entornId
	 * @return
	 */
	@Transactional
	public Entorn getEntorn(Long entornId) {
		return entornRepository.findById(entornId).get();
	}

	public Entorn getEntornComprovantPermisos(
			Long entornId,
			boolean comprovarPermisAcces) {
		return getEntornComprovantPermisos(
				entornId,
				comprovarPermisAcces,
				false);
	}
	public Entorn getEntornComprovantPermisos(
			Long entornId,
			boolean comprovarPermisAcces,
			boolean comprovarPermisDisseny) {
		Entorn entorn = entornRepository.findById(entornId).get();
		if (entorn == null) {
			throw new NoTrobatException(Entorn.class, entornId);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = null;
		if (comprovarPermisAcces) {
			permisos = new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					entornId,
					Entorn.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						entornId,
						Entorn.class,
						permisos);
			}
		}
		if (comprovarPermisDisseny) {
			permisos = new Permission[] {
					ExtendedPermission.DESIGN,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					entornId,
					Entorn.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						entornId,
						Entorn.class,
						permisos);
			}
		}
		return entorn;
	}

	public boolean potDissenyarEntorn(
			Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return permisosHelper.isGrantedAny(
				entornId,
				Entorn.class,
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION},
				auth);
	}
	
	public boolean esAdminEntorn(
			Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return permisosHelper.isGrantedAny(
				entornId,
				Entorn.class,
				new Permission[] {
						ExtendedPermission.ADMINISTRATION},
				auth);
	}
}
