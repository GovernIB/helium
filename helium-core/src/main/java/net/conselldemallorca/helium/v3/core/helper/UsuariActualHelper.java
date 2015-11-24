/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.PermisosHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

/**
 * Helper per a enviament de correus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UsuariActualHelper {

	@Resource
	private EntornRepository entornRepository;

	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	@Cacheable(value="entornsUsuariActual")
	public List<EntornDto> findEntornsPermesos(String usuariCodi) {
		List<Entorn> entorns = entornRepository.findAll();
		permisosHelper.filterGrantedAny(
				entorns,
				new ObjectIdentifierExtractor<Entorn>() {
					public Long getObjectIdentifier(Entorn entorn) {
						return entorn.getId();
					}
				},
				Entorn.class,
				new Permission[] {BasePermission.ADMINISTRATION, BasePermission.READ});
		return conversioTipusHelper.convertirList(
				entorns,
				EntornDto.class);
	}

	public String getUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			return auth.getName();
		} else {
			throw new NotFoundException(Authentication.class);
		}
	}

}
