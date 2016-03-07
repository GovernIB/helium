/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

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
			boolean comprovarPermisDelete) throws NotFoundException, NotAllowedException {
		Entorn entorn = entornRepository.findOne(id);
		if (entorn == null) {
			throw new NotFoundException(
					id,
					Entorn.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisRead) {
			if (!permisosHelper.isGrantedAny(
					id,
					Entorn.class,
					new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						Entorn.class,
						PermisTipusEnumDto.READ);
			}
		}
		if (comprovarPermisWrite) {
			if (!permisosHelper.isGrantedAny(
					id,
					Entorn.class,
					new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						Entorn.class,
						PermisTipusEnumDto.WRITE);
			}
		}
		if (comprovarPermisDelete) {
			if (!permisosHelper.isGrantedAny(
					id,
					Entorn.class,
					new Permission[] {
						ExtendedPermission.DELETE,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						Entorn.class,
						PermisTipusEnumDto.DELETE);
			}
		}
		return entorn;
	}
	
	public List<EntornDto> findAll() {
		List<EntornDto> entornsDto = new ArrayList<EntornDto>();
		List<Entorn> entorns = entornRepository.findAll();
		for (Entorn entorn: entorns) {
			entornsDto.add(toEntornDto(entorn));
		}
		return entornsDto;
	}
	
	public EntornDto toEntornDto(Entorn entorn) {
		EntornDto dto = new EntornDto();
		dto.setId(entorn.getId());
		dto.setCodi(entorn.getCodi());
		dto.setNom(entorn.getNom());
		dto.setActiu(entorn.isActiu());
		dto.setDescripcio(entorn.getDescripcio());
		return dto;
	}
}
