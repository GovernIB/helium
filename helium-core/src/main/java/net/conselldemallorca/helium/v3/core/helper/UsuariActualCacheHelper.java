/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.ConversioTipusHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

/**
 * Helper per a enviament de correus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UsuariActualCacheHelper {

	@Resource
	private EntornRepository entornRepository;

	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	@Cacheable(value="entornsUsuariActual")
	public List<EntornDto> findEntornsPermesosUsuariActual(String usuariCodi) {
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

}
