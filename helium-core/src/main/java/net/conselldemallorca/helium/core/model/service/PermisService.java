package net.conselldemallorca.helium.core.model.service;

import java.util.List;

import net.conselldemallorca.helium.core.model.dao.PermisDao;
import net.conselldemallorca.helium.core.model.hibernate.Permis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar rols per als usuaris
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Service
public class PermisService {

	private PermisDao permisDao;



	public Permis getPermisByCodi(String codi) {
		return permisDao.getByCodi(codi);
	}
	public List<Permis> findAll() {
		return permisDao.findAll();
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
	public void createPermis(String codi, String descripcio) {
		permisDao.saveOrUpdate(
				permisFromCodiDescripcio(codi, descripcio));
	}
	public void updatePermis(String codi, String descripcio) {
		permisDao.saveOrUpdate(
				permisFromCodiDescripcio(codi, descripcio));
	}
	public void deletePermis(String id) {
		Permis permis = permisDao.getById(id, false);
		if (permis != null) {
			permisDao.delete(permis);
		}
	}

	@Autowired
	public void setPermisDao(PermisDao permisDao) {
		this.permisDao = permisDao;
	}



	private Permis permisFromCodiDescripcio(String codi, String descripcio) {
		Permis permis = new Permis();
		permis.setCodi(codi);
		permis.setDescripcio(descripcio);
		return permis;
	}

}
