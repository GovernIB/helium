/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.model.hibernate.AreaMembre;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.repository.AreaJbpmIdRepository;
import net.conselldemallorca.helium.v3.core.repository.AreaMembreRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

/**
 * Helper per consultes de permisos sobre l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UsuariActualHelper {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private AreaJbpmIdRepository areaJbpmIdRepository;
	@Resource
	private AreaMembreRepository areaMembreRepository;

	@Resource(name="permisosHelperV3")
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
	
	/** Mètode per obtenir la llista d'àrees o grups per l'usuari actual.
	 * 
	 * @return
	 */
	public String[] getAreesGrupsUsuariActual() {
		String[] areesGrups;
		String usuariCodi = getUsuariActual();
		if (esIdentitySourceHelium()) {
			List<AreaMembre> membres = areaMembreRepository.findByCodi(usuariCodi);

			List<String> codisArea = new ArrayList<String>();
			for (AreaMembre membre: membres) {
				codisArea.add(membre.getArea().getCodi());
			}
			areesGrups = codisArea.toArray(new String[membres.size()]);
		} else {
			List<String> codisArea = areaJbpmIdRepository.findRolesAmbUsuariCodi(usuariCodi);
			areesGrups = codisArea.toArray(new String[codisArea.size()]);
		}
		return areesGrups;
	}
	
	private boolean esIdentitySourceHelium() {
		String identitySource = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		return (identitySource.equalsIgnoreCase("helium"));
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
