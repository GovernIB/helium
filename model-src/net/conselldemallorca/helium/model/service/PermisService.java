package net.conselldemallorca.helium.model.service;

import java.util.List;

import net.conselldemallorca.helium.model.dao.PermisDao;
import net.conselldemallorca.helium.model.hibernate.Permis;
import net.conselldemallorca.helium.presentacio.mvc.PermisCommand;
import net.conselldemallorca.helium.security.permission.RolesBasedAttributes2GrantedAuthoritiesMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar rols per als usuaris
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */

@Service
public class PermisService {

	private PermisDao permisDao;
	private RolesBasedAttributes2GrantedAuthoritiesMapper rolesMapper;



	public Permis getPermisByCodi(String codi) {
		return permisDao.getByCodi(codi);
	}
	public int countPermisosAll() {
		return permisDao.getCountAll();
	}
	public List<Permis> findPermisosPagedAndOrderedAll(
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return permisDao.findPagedAndOrderedAll(sort, asc, firstRow, maxResults);
	}
	public void createPermis(PermisCommand entity) {
		permisDao.saveOrUpdate(permisFromEntity(entity));
		rolesMapper.addRole(entity.getCodi());
	}
	public void updatePermis(PermisCommand entity) {
		permisDao.saveOrUpdate(permisFromEntity(entity));
	}
	public void deletePermis(String id) {
		Permis permis = permisDao.getById(id, false);
		if (permis != null) {
			permisDao.delete(permis);
			rolesMapper.removeRole(id);
		}
	}

	@Autowired
	public void setPermisDao(PermisDao permisDao) {
		this.permisDao = permisDao;
	}
	@Autowired
	public void setRolesMapper(
			RolesBasedAttributes2GrantedAuthoritiesMapper rolesMapper) {
		this.rolesMapper = rolesMapper;
	}



	private Permis permisFromEntity(PermisCommand entity) {
		Permis permis = new Permis();
		permis.setCodi(entity.getCodi());
		permis.setDescripcio(entity.getDescripcio());
		return permis;
	}

}
