/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.model.dao.PermisDao;
import net.conselldemallorca.helium.model.dao.PersonaDao;
import net.conselldemallorca.helium.model.dao.UsuariDao;
import net.conselldemallorca.helium.model.dao.UsuariPreferenciesDao;
import net.conselldemallorca.helium.model.dto.PersonaUsuariDto;
import net.conselldemallorca.helium.model.exception.NotFoundException;
import net.conselldemallorca.helium.model.hibernate.Permis;
import net.conselldemallorca.helium.model.hibernate.Persona;
import net.conselldemallorca.helium.model.hibernate.Usuari;
import net.conselldemallorca.helium.model.hibernate.UsuariPreferencies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les persones
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class PersonaService {

	private PersonaDao personaDao;
	private UsuariDao usuariDao;
	private PermisDao permisDao;
	private UsuariPreferenciesDao usuariPreferenciesDao;



	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Persona getPerfilInfo(Long id) {
		return personaDao.getById(id, false);
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Persona updatePerfil(Persona persona) {
		return personaDao.merge(persona);
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public void canviContrasenyaPerfil(Long id, String contrasenya) {
		canviContrasenya(id, contrasenya);
	}

	@Secured({"ROLE_ADMIN"})
	public PersonaUsuariDto getPersonaUsuariById(Long id) {
		return toPersonaUsuariDto(personaDao.getById(id, false));
	}
	@Secured({"ROLE_ADMIN"})
	public PersonaUsuariDto createPersonaUsuari(PersonaUsuariDto entity) {
		// Guarda les dades de la persona
		Persona personaSaved = personaDao.saveOrUpdate(personaFromEntity(entity));
		// Crea l'usuari si hi ha login
		createUsuariSiLogin(entity);
		return toPersonaUsuariDto(personaSaved);
	}
	@Secured({"ROLE_ADMIN"})
	public PersonaUsuariDto updatePersonaUsuari(PersonaUsuariDto entity) {
		// Si hi ha un canvi de codi de persona esborra l'usuari
		if (entity.getId() != null) {
			Persona personaVella = personaDao.getById(entity.getId(), false);
			if (personaVella != null) {
				Usuari usuariVell = usuariDao.getById(personaVella.getCodi(), false);
				if (usuariVell != null) {
					if (!personaVella.getCodi().equals(usuariVell.getId()) || !entity.isLogin())
						usuariDao.delete(personaVella.getCodi());
				}
				personaDao.evict(personaVella);
			}
		}
		// Guarda les dades de la persona
		Persona personaSaved = personaDao.saveOrUpdate(personaFromEntity(entity));
		// Crea l'usuari si hi ha login
		createUsuariSiLogin(entity);
		return toPersonaUsuariDto(personaSaved);
	}
	@Secured({"ROLE_ADMIN"})
	public void deletePersona(Long id) {
		Persona persona = personaDao.getById(id, false);
		if (persona != null) {
			Usuari usuari = usuariDao.getById(persona.getCodi(), false);
			if (usuari != null)
				usuariDao.delete(usuari);
			personaDao.delete(persona);
		}
	}
	@Secured({"ROLE_ADMIN"})
	public void canviContrasenya(Long id, String contrasenya) {
		Persona persona = personaDao.getById(id, false);
		if (persona!= null) {
			Usuari usuari = usuariDao.getById(persona.getCodi(), false);
			if (usuari != null)
				usuariDao.canviContrasenya(usuari.getId(), contrasenya);
			else
				throw new NotFoundException("La persona #" + id + " no té el login activat");
		} else {
			throw new NotFoundException("La persona #" + id + " no existeix");
		}
	}
	@Secured({"ROLE_ADMIN"})
	public List<PersonaUsuariDto> findPersonaUsuariAll() {
		return toPersonaUsuariDto(personaDao.findAll());
	}
	@Secured({"ROLE_ADMIN"})
	public List<PersonaUsuariDto> findPersonaUsuariOrderedAll(String sort, boolean asc) {
		return toPersonaUsuariDto(personaDao.findOrderedAll(sort, asc));
	}
	@Secured({"ROLE_ADMIN"})
	public List<PersonaUsuariDto> findPersonaUsuariPagedAndOrderedAll(
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return toPersonaUsuariDto(personaDao.findPagedAndOrderedAll(
				sort,
				asc,
				firstRow,
				maxResults));
	}
	@Secured({"ROLE_ADMIN"})
	public int countPersonaUsuariAll() {
		return personaDao.getCountAll();
	}
	@Secured({"ROLE_ADMIN"})
	public List<PersonaUsuariDto> findPersonaUsuariPagedAndOrderedFiltre(
			String sort,
			boolean asc,
			int firstRow,
			int maxResults,
			String codi,
			String nomLike,
			String emailLike) {
		return toPersonaUsuariDto(
				personaDao.findPagedAndOrderedFiltre(
						sort,
						asc,
						firstRow,
						maxResults,
						codi,
						nomLike,
						emailLike));
	}
	@Secured({"ROLE_ADMIN"})
	public int countPersonaUsuariFiltre(
			String codi,
			String nomLike,
			String emailLike) {
		return personaDao.getCountFiltre(
				codi,
				nomLike,
				emailLike);
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Persona findPersonaAmbCodi(String codi) {
		return personaDao.findAmbCodi(codi);
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public List<Permis> findPermisosAll() {
		return permisDao.findAll();
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public UsuariPreferencies getUsuariPreferencies() {
		return usuariPreferenciesDao.getById(
				SecurityContextHolder.getContext().getAuthentication().getName(),
				false);
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public void savePrefDefaultEntorn(String entornCodi) {
		UsuariPreferencies preferencies = getUsuariPreferencies();
		if (preferencies == null) {
			preferencies = new UsuariPreferencies(
					SecurityContextHolder.getContext().getAuthentication().getName());
			usuariPreferenciesDao.saveOrUpdate(preferencies);
		}
		preferencies.setDefaultEntornCodi(entornCodi);
	}



	@Autowired
	public void setPersonaDao(PersonaDao personaDao) {
		this.personaDao = personaDao;
	}
	@Autowired
	public void setUsuariDao(UsuariDao usuariDao) {
		this.usuariDao = usuariDao;
	}
	@Autowired
	public void setPermisDao(PermisDao permisDao) {
		this.permisDao = permisDao;
	}
	@Autowired
	public void setUsuariPreferenciesDao(UsuariPreferenciesDao usuariPreferenciesDao) {
		this.usuariPreferenciesDao = usuariPreferenciesDao;
	}



	private Persona personaFromEntity(PersonaUsuariDto entity) {
		Persona persona = new Persona();
		persona.setId(entity.getId());
		persona.setCodi(entity.getCodi());
		persona.setNom(entity.getNom());
		persona.setLlinatge1(entity.getLlinatge1());
		persona.setLlinatge2(entity.getLlinatge2());
		persona.setDataNaixement(entity.getDataNaixement());
		persona.setEmail(entity.getEmail());
		persona.setSexe(entity.getSexe());
		persona.setAvisCorreu(entity.getAvisCorreu());
		persona.setFont(entity.getFont());
		persona.setRelleu(entity.getRelleu());
		return persona;
	}
	private void createUsuariSiLogin(PersonaUsuariDto entity) {
		if (entity.isLogin()) {
			// Si la persona té permís per entrar a l'aplicació crea també l'usuari associat
			Usuari usuari = usuariDao.getById(entity.getCodi(), false);
			if (usuari == null) {
				usuari = new Usuari();
				usuari.setCodi(entity.getCodi());
				usuari.setContrasenya(entity.getContrasenya());
				usuari.setPermisos(entity.getPermisos());
			} else {
				usuari.setPermisos(entity.getPermisos());
			}
			usuariDao.saveOrUpdate(usuari);
		}
	}

	private List<PersonaUsuariDto> toPersonaUsuariDto(List<Persona> persones) {
		List<PersonaUsuariDto> resposta = new ArrayList<PersonaUsuariDto>();
		for (Persona persona: persones)
			resposta.add(toPersonaUsuariDto(persona));
		return resposta;
	}
	private PersonaUsuariDto toPersonaUsuariDto(Persona persona) {
		if (persona == null)
			return null;
		PersonaUsuariDto dto = new PersonaUsuariDto();
		dto.setId(persona.getId());
		dto.setCodi(persona.getCodi());
		dto.setNom(persona.getNom());
		dto.setLlinatge1(persona.getLlinatge1());
		dto.setLlinatge2(persona.getLlinatge2());
		dto.setDataNaixement(persona.getDataNaixement());
		dto.setEmail(persona.getEmail());
		dto.setSexe(persona.getSexe());
		dto.setAvisCorreu(persona.getAvisCorreu());
		dto.setFont(persona.getFont());
		dto.setRelleu(persona.getRelleu());
		Usuari usuari = usuariDao.getById(persona.getCodi(), false);
		if (usuari != null) {
			dto.setLogin(true);
			dto.setContrasenya(usuari.getContrasenya());
			dto.setPermisos(usuari.getPermisos());
		}
		return dto;
	}

}
