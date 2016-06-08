/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.helperv26.PermisosHelper;
import net.conselldemallorca.helium.core.helperv26.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
	public List<EntornDto> findEntornsActiusPermesos(String usuariCodi) {
		List<Entorn> entorns = entornRepository.findByActiuTrue();
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

	@CacheEvict(value = "entornsUsuariActual", allEntries = true)
	public void netejarCacheUsuariTots() {
	}

	@CacheEvict(value = "entornsUsuariActual")
	public void netejarCacheUsuari(String usuariCodi) {
	}

	public String getUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			return auth.getName();
		} else {
			throw new NoTrobatException(Authentication.class);
		}
	}

}
