/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.integracio.plugins.persones.Persona.Sexe;
import net.conselldemallorca.helium.model.dao.AreaDao;
import net.conselldemallorca.helium.model.dao.AreaMembreDao;
import net.conselldemallorca.helium.model.dao.AreaTipusDao;
import net.conselldemallorca.helium.model.dao.CarrecDao;
import net.conselldemallorca.helium.model.dao.CarrecJbpmIdDao;
import net.conselldemallorca.helium.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.model.exception.NotFoundException;
import net.conselldemallorca.helium.model.hibernate.Area;
import net.conselldemallorca.helium.model.hibernate.AreaMembre;
import net.conselldemallorca.helium.model.hibernate.AreaTipus;
import net.conselldemallorca.helium.model.hibernate.Carrec;
import net.conselldemallorca.helium.model.hibernate.CarrecJbpmId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar l'organització d'un entorn
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class OrganitzacioService {

	private AreaDao areaDao;
	private AreaMembreDao areaMembreDao;
	private AreaTipusDao areaTipusDao;
	private PluginPersonaDao pluginPersonaDao;
	private CarrecDao carrecDao;
	private CarrecJbpmIdDao carrecJbpmIdDao;



	public Area getAreaById(Long id) {
		return areaDao.getById(id, false);
	}
	public Area createArea(Area entity) {
		//createGroupIfNotExists(entity);
		return areaDao.saveOrUpdate(entity);
	}
	public Area updateArea(Area entity) {
		//createGroupIfNotExists(entity);
		return areaDao.merge(entity);
	}
	public void deleteArea(Long id) {
		/*Area area = getAreaById(id);
		if (area != null)
			deleteGroupIfExists(area);*/
		areaDao.delete(id);
	}
	public List<Area> findAreaPagedAndOrderedAmbEntorn(
			Long entornId,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return areaDao.findPagedAndOrderedAmbEntorn(
				entornId,
				sort,
				asc,
				firstRow,
				maxResults);
	}
	public int countAreaAmbEntorn(Long entornId) {
		return areaDao.getCountAmbEntorn(entornId);
	}
	public Area findAreaAmbEntornICodi(
			Long entornId,
			String codi) {
		return areaDao.findAmbEntornICodi(entornId, codi);
	}
	public List<Area> findAreaLikeNom(Long entornId, String text) {
		return areaDao.findLikeNom(entornId, text);
	}
	public List<Area> findAreaAmbTipus(Long tipusAreaId) {
		return areaDao.findAmbTipus(tipusAreaId);
	}

	public void afegirMembre(
			Long areaId,
			String personaCodi,
			Long carrecId) {
		Area area = areaDao.getById(areaId, false);
		if (area == null)
			throw new NotFoundException("No s'ha trobat l'area amb id:" + areaId);
		Persona persona = pluginPersonaDao.findAmbCodiPlugin(personaCodi);
		if (persona == null)
			throw new NotFoundException("No s'ha trobat la persona amb el codi:" + personaCodi);
		if (carrecId != null) {
			Carrec carrec = carrecDao.getById(carrecId, false);
			if (carrec == null)
				throw new NotFoundException("No s'ha trobat el càrrec amb id:" + carrecId);
			if (carrec.getPersonaCodi() != null)
				throw new NotFoundException("No està disponible el càrrec amb id:" + carrecId);
			carrec.setPersonaCodi(persona.getCodi());
			carrec.setPersonaSexe(
					(persona.getSexe().equals(Sexe.SEXE_HOME)) ?
							net.conselldemallorca.helium.model.hibernate.Persona.Sexe.SEXE_HOME :
							net.conselldemallorca.helium.model.hibernate.Persona.Sexe.SEXE_DONA);
		}
		AreaMembre membre = new AreaMembre(area, persona.getCodi());
		if (!area.getMembres().contains(membre))
			area.addMembre(membre);
	}
	public void esborrarMembre(
			Long areaId,
			String personaCodi) {
		Area area = areaDao.getById(areaId, false);
		if (area == null)
			throw new NotFoundException("No s'ha trobat l'area amb id:" + areaId);
		Persona persona = pluginPersonaDao.findAmbCodiPlugin(personaCodi);
		if (persona == null)
			throw new NotFoundException("No s'ha trobat la persona amb el codi:" + personaCodi);
		List<Carrec> carrecs = carrecDao.findAmbArea(areaId);
		for (Carrec carrec: carrecs) {
			if (personaCodi.equals(carrec.getPersonaCodi())) {
				carrec.setPersonaCodi(null);
				carrec.setPersonaSexe(null);
			}
		}
		Iterator<AreaMembre> it = area.getMembres().iterator();
		while (it.hasNext()) {
			AreaMembre membre = it.next();
			if (membre.getCodi().equals(personaCodi)) {
				it.remove();
				areaMembreDao.delete(membre.getId());
				break;
			}
		}
	}
	public List<Persona> findMembresArea(Long areaId) {
		Set<AreaMembre> membres = getAreaById(areaId).getMembres();
		List<Persona> resposta = new ArrayList<Persona>();
		for (AreaMembre membre: membres) {
			Persona persona = pluginPersonaDao.findAmbCodiPlugin(membre.getCodi());
			if (persona != null)
				resposta.add(persona);
		}
		return resposta;
	}

	public AreaTipus getAreaTipusById(Long id) {
		return areaTipusDao.getById(id, false);
	}
	public AreaTipus createAreaTipus(AreaTipus entity) {
		return areaTipusDao.saveOrUpdate(entity);
	}
	public AreaTipus updateAreaTipus(AreaTipus entity) {
		return areaTipusDao.merge(entity);
	}
	public void deleteAreaTipus(Long id) {
		areaTipusDao.delete(id);
	}
	public List<AreaTipus> findAreaTipusAmbEntorn(Long entornId) {
		return areaTipusDao.findAmbEntorn(entornId);
	}
	public List<AreaTipus> findAreaTipusPagedAndOrderedAmbEntorn(
			Long entornId,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return areaTipusDao.findPagedAndOrderedAmbEntorn(
				entornId,
				sort,
				asc,
				firstRow,
				maxResults);
	}
	public int countAreaTipusAmbEntorn(Long entornId) {
		return areaTipusDao.getCountAmbEntorn(entornId);
	}
	public AreaTipus findAreaTipusAmbEntornICodi(
			Long entornId,
			String codi) {
		return areaTipusDao.findAmbEntornICodi(entornId, codi);
	}

	public Carrec getCarrecById(Long id) {
		return carrecDao.getById(id, false);
	}
	public Carrec createCarrec(Carrec entity) {
		return carrecDao.saveOrUpdate(entity);
	}
	public Carrec updateCarrec(Carrec entity) {
		return carrecDao.merge(entity);
	}
	public void deleteCarrec(Long id) {
		carrecDao.delete(id);
	}
	public List<Carrec> findCarrecPagedAndOrderedAmbEntorn(
			Long entornId,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return carrecDao.findPagedAndOrderedAmbEntorn(
				entornId,
				sort,
				asc,
				firstRow,
				maxResults);
	}
	public int countCarrecAmbEntorn(Long entornId) {
		return carrecDao.getCountAmbEntorn(entornId);
	}
	public Carrec findCarrecAmbEntornICodi(
			Long entornId,
			String codi) {
		return carrecDao.findAmbEntornICodi(entornId, codi);
	}
	public List<Carrec> findCarrecsAmbArea(
			Long areaId) {
		return carrecDao.findAmbArea(areaId);
	}
	public Map<String, List<Carrec>> findCarrecsMapAmbArea(
			Long areaId) {
		Map<String, List<Carrec>> resposta = new HashMap<String, List<Carrec>>();
		List<Carrec> carrecs = carrecDao.findAmbArea(areaId);
		for (Carrec carrec: carrecs) {
			if (carrec.getPersonaCodi() != null) {
				List<Carrec> carrecsPersona = resposta.get(carrec.getPersonaCodi());
				if (carrecsPersona == null) {
					carrecsPersona = new ArrayList<Carrec>();
					resposta.put(carrec.getPersonaCodi(), carrecsPersona);
				}
				carrecsPersona.add(carrec);
			}
		}
		return resposta;
	}

	@Secured({"ROLE_ADMIN"})
	public CarrecJbpmId getCarrecJbpmIdById(Long id) {
		return carrecJbpmIdDao.getById(id, false);
	}
	@Secured({"ROLE_ADMIN"})
	public CarrecJbpmId createCarrecJbpmId(CarrecJbpmId entity) {
		return carrecJbpmIdDao.saveOrUpdate(entity);
	}
	@Secured({"ROLE_ADMIN"})
	public CarrecJbpmId updateCarrecJbpmId(CarrecJbpmId entity) {
		return carrecJbpmIdDao.merge(entity);
	}
	@Secured({"ROLE_ADMIN"})
	public void deleteCarrecJbpmId(Long id) {
		CarrecJbpmId vell = getCarrecJbpmIdById(id);
		if (vell != null)
			carrecJbpmIdDao.delete(id);
	}
	@Secured({"ROLE_ADMIN"})
	public List<CarrecJbpmId> findCarrecJbpmIdAll() {
		return carrecJbpmIdDao.findAll();
	}
	@Secured({"ROLE_ADMIN"})
	public List<CarrecJbpmId> findCarrecJbpmIdSenseAssignar() {
		return carrecJbpmIdDao.findSenseAssignar();
	}

	public List<String> findCodisPersonaAmbJbpmIdGroup(String group) {
		return carrecJbpmIdDao.findPersonesAmbGroup(group);
	}
	public List<String> findCodisPersonaAmbArea(Long areaId) {
		Area area = getAreaById(areaId);
		List<String> resposta = new ArrayList<String>();
		for (AreaMembre membre: area.getMembres())
			resposta.add(membre.getCodi());
		return resposta;
	}
	public List<String> findCodisPersonaAmbJbpmIdCarrec(String carrecCodi) {
		return carrecJbpmIdDao.findPersonesAmbCarrecCodi(carrecCodi);
	}



	@Autowired
	public void setAreaDao(AreaDao areaDao) {
		this.areaDao = areaDao;
	}
	@Autowired
	public void setAreaMembreDao(AreaMembreDao areaMembreDao) {
		this.areaMembreDao = areaMembreDao;
	}
	@Autowired
	public void setAreaTipusDao(AreaTipusDao areaTipusDao) {
		this.areaTipusDao = areaTipusDao;
	}
	@Autowired
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
	}
	@Autowired
	public void setCarrecDao(CarrecDao carrecDao) {
		this.carrecDao = carrecDao;
	}
	@Autowired
	public void setCarrecJbpmIdDao(CarrecJbpmIdDao carrecJbpmIdDao) {
		this.carrecJbpmIdDao = carrecJbpmIdDao;
	}



	/*private void createUserIfNotExists(Persona persona) {
		User user = identityService.findUserById(persona.getCodi());
		if (user != null)
			identityService.deleteUser(persona.getCodi());
		identityService.createUser(
				persona.getCodi(),
				persona.getNom(),
				persona.getLlinatges(),
				persona.getEmail());
	}
	private void createGroupIfNotExists(Area area) {
		String groupId = getGroupIdForArea(area);
		Group group = identityService.findGroupById(groupId);
		if (group == null) {
			String groupName = getGroupNameForArea(area);
			if (area.getPare() != null) {
				createGroupIfNotExists(area.getPare());
				identityService.createGroup(
						groupName,
						area.getTipus().getCodi(),
						getGroupIdForArea(area.getPare()));
			} else {
				identityService.createGroup(
						groupName,
						area.getTipus().getCodi());
			}
		}
	}
	private void deleteGroupIfExists(Area area) {
		String groupId = getGroupIdForArea(area);
		identityService.deleteGroup(groupId);
	}
	private void createMembershipIfNotExists(
			Area area,
			Persona persona,
			Carrec carrec) {
		String groupId = getGroupIdForArea(area);
		if (carrec != null)
			identityService.createMembership(
					persona.getCodi(),
					groupId,
					carrec.getCodi());
		else
			identityService.createMembership(
					persona.getCodi(),
					groupId);
	}
	private void deleteMembershipIfExists(
			Area area,
			Persona persona,
			Carrec carrec) {
		String groupId = getGroupIdForArea(area);
		try {
			identityService.deleteMembership(
					persona.getCodi(),
					groupId,
					null);
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
	}
	private String getGroupNameForArea(Area area) {
		String entornCodi = area.getEntorn().getCodi();
		return entornCodi + "#" + area.getCodi();
	}
	private String getGroupIdForArea(Area area) {
		String entornCodi = area.getEntorn().getCodi();
		String tipusCodi = area.getTipus().getCodi();
		return tipusCodi + "." + entornCodi + "#" + area.getCodi();
	}*/

}
