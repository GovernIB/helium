/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.List;

import net.conselldemallorca.helium.model.dao.PermisDao;
import net.conselldemallorca.helium.model.dao.PersonaDao;
import net.conselldemallorca.helium.model.dao.UsuariDao;
import net.conselldemallorca.helium.model.dao.VersioDao;
import net.conselldemallorca.helium.model.hibernate.Permis;
import net.conselldemallorca.helium.model.hibernate.Persona;
import net.conselldemallorca.helium.model.hibernate.Usuari;
import net.conselldemallorca.helium.model.update.Versio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar la inicialització del sistema i les
 * actualitzacions automàtiques
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class UpdateService {

	private VersioDao versioDao;
	private PersonaDao personaDao;
	private UsuariDao usuariDao;
	private PermisDao permisDao;



	public void updateToLastVersion() {
		List<Versio> versions = versioDao.findAll();
		if (versions.size() == 0) {
			// El sistema s'ha acabat de crear i s'han de crear 
			// les dades inicials
			logger.info("Actualitzant la base de dades a la versió inicial");
			createInitialData();
		}
	}



	@Autowired
	public void setVersioDao(VersioDao versioDao) {
		this.versioDao = versioDao;
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



	private void createInitialData() {
		Permis permisAdmin = new Permis(
				"HEL_ADMIN",
				"Administrador");
		permisDao.saveOrUpdate(permisAdmin);
		Permis permisUser = new Permis(
				"HEL_USER",
				"Usuari");
		permisDao.saveOrUpdate(permisUser);
		Usuari usuariAdmin = new Usuari(
				"admin",
				"admin",
				true);
		usuariAdmin.addPermis(permisAdmin);
		usuariDao.saveOrUpdate(usuariAdmin);
		usuariDao.canviContrasenya("admin", "admin");
		Persona personaAdmin = new Persona(
				"admin",
				"Usuari",
				"Administrador",
				"admin@helium.org");
		personaDao.saveOrUpdate(personaAdmin);
		Versio versioInicial = new Versio(
				"inicial",
				0);
		versioDao.saveOrUpdate(versioInicial);
	}

	private static final Log logger = LogFactory.getLog(UpdateService.class);

}
