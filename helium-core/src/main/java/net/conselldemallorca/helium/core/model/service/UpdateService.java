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
		
		Versio darreraOK = versioDao.findLastOK(); // obtenir darrera versió
		
		// Si no hi ha cap versió correctament passada, actualitzam al menys la versió inicial.
		if (darreraOK == null){
			//crearVersio("inicial", ordre)
			Versio versioInicial = versioDao.findAmbCodi("inicial");
			versioInicial.setProcesExecutat(true);
			versioInicial.setDataExecucioProces(new Date());
			versioInicial.setScriptExecutat(true);
			versioInicial.setDataExecucioScript(new Date());
			versioDao.saveOrUpdate(versioInicial);
			
			darreraOK = versioInicial;
		}
		
		Versio versioAnterior = darreraOK;
		if (darreraOK.getOrdre().intValue() < 210) {
			versioAnterior = actualitzarV210("2.1.0", 210, versioAnterior);
		}
		// ... afegir les modificacions de les seguents versions (if (darreraOK... < 211) ...)
		/*if (darreraOK.getOrdre().intValue() < 211) {
			versioAnterior = actualitzarV211("2.1.1", 211, versioAnterior);
		}*/
		
		Versio.setVersion("v." + versioDao.findLast().getCodi());
		if (versioAnterior.getErrorVersio() != null){
			Versio.setError(versioAnterior.getErrorVersio());
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
		versioInicial.setDataExecucioProces(new Date());
		versioInicial.setScriptExecutat(true);
		versioInicial.setDataExecucioScript(new Date());
		versioDao.saveOrUpdate(versioInicial);
	}

	private void errorVersio(Versio versio, String msg, Exception error){
		logger.error(msg, error);
		versio.setErrorVersio(msg);
		versioDao.saveOrUpdate(versio);		
	}

	private void actualitzarVersio(Versio versio){
		versio.setDataExecucioProces(new Date());
		versio.setProcesExecutat(true);
		versio.setErrorVersio(null);
		versioDao.saveOrUpdate(versio);
	}
	
	private Versio crearVersio(String codi, Integer ordre){
		Versio versio = new Versio(codi, ordre);
		versioDao.saveOrUpdate(versio);
		return versio;
	}
	
	private Versio getVersio(String codi, Integer ordre){
		Versio versio = versioDao.findAmbCodi(codi);
		if (versio == null){
			versio = crearVersio(codi, ordre);
		}
		return versio;
	}
	
	private Versio actualitzarV210(String codi, Integer ordre, Versio versioAnterior) {
		// Recollim/Cream la versio corresponent. Si no es necessita script, actualitzar la variable scriptExecutat a true!!
		Versio versio210 = getVersio(codi, ordre);
		
		// Comprovam que les versions anteriors han passat correctament.
		if (versioAnterior.isProcesExecutat()){
			// Comprovam si s'ha passat l'script corresponent a la versió
			if (versio210.isScriptExecutat()){
				try {
					canviVersioMapeigSistraService.canviarVersioMapeigSistra();
					canviVersioEnumeracionsService.canviarVersioEnumeracions();
					actualitzarVersio(versio210);
				} catch (Exception e) {
					errorVersio(versio210, getMessage("error.updateService.actualitzarVersio") + " " + codi + " " + getMessage("error.updateService.contactiAdministrador"), e);
				}
			} else {
				// Si no s'ha executat l'script no actualitzam la versió i 
				// posam un error a la bbdd per mostrar-lo a l'aplicació.
				errorVersio(versio210, getMessage("error.updateService.actualitzarVersio") + " " + codi + " " + getMessage("error.updateService.noScript") + " update210.sql. " + getMessage("error.updateService.contactiAdministrador"), null);
			}
			return versio210;
		} else {
			return versioAnterior;
		}
	}
	
	/*private Versio actualitzarV211(String codi, Integer ordre, Versio versioAnterior) {
		// Comprovam si s'ha passat l'script corresponent a la versió (només si és necessari un script, si no hi ha script per aquesta versió, actualitzar la variable script passat a true).
		Versio versio211 = getVersio(codi, ordre);
		if (versioAnterior.isProcesExecutat()){
			if (versio211.isScriptExecutat()){
				try {
					canviVersioEnumeracionsService.canviarVersioEnumeracions();
					actualitzarVersio(versio211);
				} catch (Exception e) {
					errorVersio(versio211, getMessage("error.updateService.actualitzarVersio") + " " + codi + " " + getMessage("error.updateService.contactiAdministrador"), e);
				}
			} else {
				// Si no s'ha executat l'script no actualitzam la versió i posam un error a la versió Anterior per mostrar-lo a l'aplicació.
				errorVersio(versio211, getMessage("error.updateService.actualitzarVersio") + " " + codi + " " + getMessage("error.updateService.noScript") + " update211.sql. " + getMessage("error.updateService.contactiAdministrador"), null);
			}
			return versio211;
		} else {
			return versioAnterior;
		}
	}*/
	
	
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
