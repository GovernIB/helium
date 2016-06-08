/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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

	public Entorn getEntornComprovantPermisos(
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete) {
		Entorn entorn = entornRepository.findOne(id);
		if (entorn == null) {
			throw new NoTrobatException(Entorn.class,id);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = null;
		if (comprovarPermisRead) {
			permisos = new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					id,
					Entorn.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Entorn.class,
						permisos);
			}
		}
		if (comprovarPermisWrite) {
			permisos = new Permission[] {
					ExtendedPermission.WRITE,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					id,
					Entorn.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Entorn.class,
						permisos);
			}
		}
		if (comprovarPermisDelete) {
			permisos = new Permission[] {
					ExtendedPermission.DELETE,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					id,
					Entorn.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Entorn.class,
						permisos);
			}
		}
		return entorn;
	}
}
