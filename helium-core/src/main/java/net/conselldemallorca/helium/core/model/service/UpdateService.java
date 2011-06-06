/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dao.PermisDao;
import net.conselldemallorca.helium.core.model.dao.PersonaDao;
import net.conselldemallorca.helium.core.model.dao.UsuariDao;
import net.conselldemallorca.helium.core.model.dao.VersioDao;
import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.model.update.Versio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar la inicialització del sistema i les
 * actualitzacions automàtiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class UpdateService {

	private VersioDao versioDao;
	private PersonaDao personaDao;
	private UsuariDao usuariDao;
	private PermisDao permisDao;
	private MessageSource messageSource;
	
	private CanviVersioMapeigSistraService canviVersioMapeigSistraService;
	private CanviVersioEnumeracionsService canviVersioEnumeracionsService;

	public static final String VERSIO_ACTUAL_STR = "2.1.0";
	public static final int VERSIO_ACTUAL_INT = 210;

	public void updateToLastVersion() throws Exception {
		List<Versio> versions = versioDao.findAll();
		if (versions.size() == 0) {
			// El sistema s'ha acabat de crear i s'han de crear 
			// les dades inicials
			logger.info("Actualitzant la base de dades a la versió inicial");
			createInitialData();
		}
		
		Versio darrera = versioDao.findLast(); // obtenir darrera versió
		
		if (darrera == null){
			Versio versioInicial = versioDao.findAmbCodi("inicial");
			versioInicial.setProcesExecutat(true);
			versioInicial.setDataExecucio(new Date());
			versioDao.saveOrUpdate(versioInicial);
			
			darrera = versioInicial;
		}
		
		boolean correcte = true;
		if (darrera.getOrdre().intValue() < 210 && correcte) {
			correcte = actualitzarV210(darrera);
		}
		if (darrera.getOrdre().intValue() < 211 && correcte) {
			correcte = actualitzarV211(darrera);
		}
		// ... afegir les modificacions de les seguents versions (if (darrera... < 211 && correcte) ...)
		
		
		Versio.setVersion("v." + versioDao.findLast().getCodi());
		if (!correcte){
			Versio.setError(versioDao.findLast().getDescripcio());
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
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@Autowired
	public void setCanviVersioMapeigService(CanviVersioMapeigSistraService canviVersioMapeigSistraService) {
		this.canviVersioMapeigSistraService = canviVersioMapeigSistraService;
	}
	@Autowired
	public void setCanviVersioEnumeracioService(CanviVersioEnumeracionsService canviVersioEnumeracionsService) {
		this.canviVersioEnumeracionsService = canviVersioEnumeracionsService;
	}

	private void createInitialData() throws Exception {
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
		versioInicial.setProcesExecutat(true);
		versioInicial.setDataExecucio(new Date());
		versioDao.saveOrUpdate(versioInicial);
	}

	private void errorVersio(Versio versioAnterior, String msg, Exception error){
		logger.error(msg, error);
		versioAnterior.setDescripcio(msg);
		versioDao.saveOrUpdate(versioAnterior);		
	}

	private void actualitzarVersio(Versio versio){
		versio.setDataExecucio(new Date());
		versio.setProcesExecutat(true);
		versioDao.saveOrUpdate(versio);
	}
	
	private boolean actualitzarV210(Versio versioAnterior) {
		// Comprovam si s'ha passat l'script corresponent a la versió (només si és necessari un script, si no hi ha script per aquesta versió, crear la versió desde el procés).
		Versio versio210 = versioDao.findAmbCodi("2.1.0");
		if (versio210 != null){
			try {
				canviVersioMapeigSistraService.canviarVersioMapeigSistra();
				actualitzarVersio(versio210);
				return true;
			} catch (Exception e) {
				errorVersio(versioAnterior, getMessage("error.updateService.actualitzarVersio") + " 2.1.0. " + getMessage("error.updateService.contactiAdministrador"), e);
				return false;
			}
		} else {
			// Si no s'ha executat l'script no actualitzam la versió i posam un error a la versió Anterior per mostrar-lo a l'aplicació.
			errorVersio(versioAnterior, getMessage("error.updateService.actualitzarVersio") + " 2.1.0. " + getMessage("error.updateService.noScript") + " update210.sql. " + getMessage("error.updateService.contactiAdministrador"), null);
			return false;
		}
	}
	
	private boolean actualitzarV211(Versio versioAnterior) {
		// Comprovam si s'ha passat l'script corresponent a la versió (només si és necessari un script, si no hi ha script per aquesta versió, crear la versió desde el procés).
		Versio versio211 = versioDao.findAmbCodi("2.1.1");
		if (versio211 != null) {
			try {
				canviVersioEnumeracionsService.canviarVersioEnumeracions();
				actualitzarVersio(versio211);
				return true;
			} catch (Exception e) {
				errorVersio(versioAnterior, getMessage("error.updateService.actualitzarVersio") + " 2.1.1. " + getMessage("error.updateService.contactiAdministrador"), e);
				return false;
			}
		} else {
			// Si no s'ha executat l'script no actualitzam la versió i posam un error a la versió Anterior per mostrar-lo a l'aplicació.
			errorVersio(versioAnterior, getMessage("error.updateService.actualitzarVersio") + " 2.1.1. " + getMessage("error.updateService.noScript") + " update211.sql. " + getMessage("error.updateService.contactiAdministrador"), null);
			return false;
		}
	}
	
	
	protected String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}

	protected String getMessage(String key) {
		return getMessage(key, null);
	}
	
	
	private static final Log logger = LogFactory.getLog(UpdateService.class);

}
