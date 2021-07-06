/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.repository.EntornRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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



	
	public List<EntornDto> findEntornsActiusPermisAdmin() {
		List<Entorn> entorns = entornRepository.findByActiuTrue();
		permisosHelper.filterGrantedAny(
				entorns,
				new ObjectIdentifierExtractor<Entorn>() {
					public Long getObjectIdentifier(Entorn entorn) {
						return entorn.getId();
					}
				},
				Entorn.class,
				new Permission[] {BasePermission.ADMINISTRATION},
				SecurityContextHolder.getContext().getAuthentication());
		List<EntornDto> dtos = conversioTipusHelper.convertirList(
				entorns,
				EntornDto.class);
		List<Long> ids = new ArrayList<Long>();
		for (EntornDto dto: dtos) {
			ids.add(dto.getId());
		}
		permisosHelper.omplirControlPermisosSegonsUsuariActual(
				ids,
				dtos,
				Entorn.class);
		return dtos;
	}
	
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
				new Permission[] {BasePermission.ADMINISTRATION, BasePermission.READ},
				SecurityContextHolder.getContext().getAuthentication());
		List<EntornDto> dtos = conversioTipusHelper.convertirList(
				entorns,
				EntornDto.class);
		List<Long> ids = new ArrayList<Long>();
		for (EntornDto dto: dtos) {
			ids.add(dto.getId());
		}
		permisosHelper.omplirControlPermisosSegonsUsuariActual(
				ids,
				dtos,
				Entorn.class);
		return dtos;
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
			throw new AuthenticationCredentialsNotFoundException(null);
		}
	}
	
	/** Consulta si l'usuari actual és administrador d'Helium */
	public boolean isAdministrador() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return isAdministrador(auth);
	}
	
	public static boolean isAdministrador(Authentication auth) {
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

}
