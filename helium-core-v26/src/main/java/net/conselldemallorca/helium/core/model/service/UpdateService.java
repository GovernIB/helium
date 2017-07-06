/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.model.dao.EnumeracioDao;
import net.conselldemallorca.helium.core.model.dao.PermisDao;
import net.conselldemallorca.helium.core.model.dao.PersonaDao;
import net.conselldemallorca.helium.core.model.dao.UsuariDao;
import net.conselldemallorca.helium.core.model.dao.VersioDao;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.model.update.Versio;


/**
 * Servei per gestionar la inicialització del sistema i les
 * actualitzacions automàtiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class UpdateService {

	public static final String VERSIO_210_STR = "2.1.0";
	public static final int VERSIO_210_ORDRE = 210;
	public static final String VERSIO_220_STR = "2.2.0";
	public static final int VERSIO_220_ORDRE = 220;
	public static final String VERSIO_221_STR = "2.2.1";
	public static final int VERSIO_221_ORDRE = 221;
	public static final String VERSIO_230_STR = "2.3.0";
	public static final int VERSIO_230_ORDRE = 230;
	public static final String VERSIO_240_STR = "2.4.0";
	public static final int VERSIO_240_ORDRE = 240;
	public static final String VERSIO_250_STR = "2.5.0";
	public static final int VERSIO_250_ORDRE = 250;
	public static final String VERSIO_260_STR = "2.6.0";
	public static final int VERSIO_260_ORDRE = 260;
	public static final String VERSIO_300_STR = "3.0.0";
	public static final int VERSIO_300_ORDRE = 300;
	public static final String VERSIO_310_STR = "3.1.0";
	public static final int VERSIO_310_ORDRE = 310;
	public static final String VERSIO_320_STR = "3.2.0";
	public static final int VERSIO_320_ORDRE = 320;
	public static final String VERSIO_ACTUAL_STR = "3.2.0";
	public static final int VERSIO_ACTUAL_ORDRE = 320;

	public static final int VERSIO_ACTUAL_RELEASE = 62;

	private VersioDao versioDao;
	private PersonaDao personaDao;
	private UsuariDao usuariDao;
	private PermisDao permisDao;
	private EnumeracioDao enumeracioDao;

	private DissenyService dissenyService;

	private MessageSource messageSource;

	private String errorUpdate;



	public void updateToLastVersion() throws Exception {
		List<Versio> versions = versioDao.findAllOrdered();
		if (versions.size() == 0) {
			logger.info("Actualitzant la base de dades a la versió inicial");
			createInitialData();
		}
		for (Versio versio: versions) {
			if (!versio.isProcesExecutat()) {
				if (versio.getOrdre() == VERSIO_210_ORDRE) {
					boolean actualitzat = actualitzarV210();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_220_ORDRE) {
					boolean actualitzat = actualitzarV220();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_221_ORDRE) {
					boolean actualitzat = actualitzarV221();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_230_ORDRE) {
					boolean actualitzat = actualitzarV230();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_240_ORDRE) {
					boolean actualitzat = actualitzarV240();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_250_ORDRE) {
					boolean actualitzat = actualitzarV250();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_260_ORDRE) {
					boolean actualitzat = actualitzarV260();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_300_ORDRE) {
					boolean actualitzat = actualitzarV300();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_310_ORDRE) {
					boolean actualitzat = actualitzarV310();
					if (!actualitzat) break;
				}
			}
		}
		Versio darrera = versioDao.findLast();
		boolean actualitzat = true;
		if (actualitzat && darrera.getOrdre() < 210) {
			actualitzat = actualitzarV210();
		}
		if (actualitzat && darrera.getOrdre() < 220) {
			actualitzat = actualitzarV220();
		}
		if (actualitzat && darrera.getOrdre() < 221) {
			actualitzat = actualitzarV221();
		}
		if (actualitzat && darrera.getOrdre() < 230) {
			actualitzarV230();
		}
		if (actualitzat && darrera.getOrdre() < 240) {
			actualitzarV240();
		}
		if (actualitzat && darrera.getOrdre() < 250) {
			actualitzarV250();
		}
		if (actualitzat && darrera.getOrdre() < 260) {
			actualitzarV260();
		}
		if (actualitzat && darrera.getOrdre() < 300) {
			actualitzarV300();
		}
		if (actualitzat && darrera.getOrdre() < 310) {
			actualitzarV310();
		}
	}

	public String getVersioActual() {
		int numPunts = StringUtils.countMatches(VERSIO_ACTUAL_STR, ".");
		if (numPunts > 1) {
			String versioSenseRelease = VERSIO_ACTUAL_STR.substring(
					0,
					VERSIO_ACTUAL_STR.lastIndexOf("."));
			return versioSenseRelease + "." + VERSIO_ACTUAL_RELEASE;
		} else {
			return VERSIO_ACTUAL_STR;			
		}
	}

	public String getErrorUpdate() {
		return errorUpdate;
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
	public void setEnumeracioDao(EnumeracioDao enumeracioDao) {
		this.enumeracioDao = enumeracioDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}


/*---------- ACTUALITZACIÓ INICIAL -----------------------------------------------*/
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

	/*---------- ACTUALITZACIÓ V. 2.1.0 -----------------------------------------------*/
	private boolean actualitzarV210() {
		boolean actualitzat = false;
		Versio versio210 = obtenirOCrearVersio(VERSIO_210_STR, VERSIO_210_ORDRE);
		if (!versio210.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_210_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio210.isProcesExecutat()) {
			try {
				canviarMapeigSistraV210();
				canviarMapeigEnumeracionsV210();
				versio210.setProcesExecutat(true);
				versio210.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio210);
				logger.info("Actualització a la versió " + VERSIO_210_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_210_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_210_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}
	private void canviarMapeigSistraV210() throws Exception {
		if (dissenyService.findMapeigSistraTots().size() == 0) {
			List<ExpedientTipus> expedientsTipus = dissenyService.findExpedientTipusTots();
			for (ExpedientTipus expedientTipus : expedientsTipus) {
				if (expedientTipus.getSistraTramitMapeigCamps() != null) {
					String[] parts = expedientTipus.getSistraTramitMapeigCamps().split(";");
					for (int i = 0; i < parts.length; i++) {
						String[] parella = parts[i].split(":");
						if (parella.length > 1) {
							String varSistra = parella[0];
							String varHelium = parella[1];
							if (varHelium != null && (!"".equalsIgnoreCase(varHelium))) {
								if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varHelium) == null) {
									dissenyService.createMapeigSistra(varHelium, varSistra, MapeigSistra.TipusMapeig.Variable, expedientTipus);
								}
							}
						}
					}
				}
				if (expedientTipus.getSistraTramitMapeigDocuments() != null) {
					String[] parts = expedientTipus.getSistraTramitMapeigDocuments().split(";");
					for (int i = 0; i < parts.length; i++) {
						String[] parella = parts[i].split(":");
						if (parella.length > 1) {
							String varSistra = parella[0];
							String varHelium = parella[1];
							if (varHelium != null && (!"".equalsIgnoreCase(varHelium))) {
								if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varHelium) == null) {
									dissenyService.createMapeigSistra(varHelium, varSistra, MapeigSistra.TipusMapeig.Document, expedientTipus);
								}
							}
						}
					}
				}
				if (expedientTipus.getSistraTramitMapeigAdjunts() != null) {
					String[] parts = expedientTipus.getSistraTramitMapeigAdjunts().split(";");
					for (int i = 0; i < parts.length; i++) {
						String varSistra = parts[i];
						if (varSistra != null && (!"".equalsIgnoreCase(varSistra))) {
							if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varSistra) == null) {
								dissenyService.createMapeigSistra(varSistra, varSistra, MapeigSistra.TipusMapeig.Adjunt, expedientTipus);
							}
						}
					}
				}
			}
		}
	}
	private void canviarMapeigEnumeracionsV210() throws Exception {
		List<Enumeracio> enumeracions = enumeracioDao.findAll();
		if (enumeracions.size() > 0) {
			for (Enumeracio enumeracio : enumeracions) {
				if ((enumeracio.getValors() != null) && (!enumeracio.getValors().equals(""))) {
					List<String[]> valors = enumeracio.getLlistaValors();
					for (String[] parella : valors) {
						EnumeracioValors enumeracioValors = new EnumeracioValors();
						enumeracioValors.setCodi(parella[0]);
						enumeracioValors.setNom((String)parella[0]);
						enumeracioValors.setEnumeracio(enumeracio);
						dissenyService.createEnumeracioValors(enumeracioValors);
					}
				}
			}
		}
	}

	/*---------- ACTUALITZACIÓ V. 2.2.0 -----------------------------------------------*/
	private boolean actualitzarV220() {
		boolean actualitzat = false;
		Versio versio220 = obtenirOCrearVersio(VERSIO_220_STR, VERSIO_220_ORDRE);
		if (!versio220.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_220_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio220.isProcesExecutat()) {
			try {
				versio220.setProcesExecutat(true);
				versio220.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio220);
				logger.info("Actualització a la versió " + VERSIO_220_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_220_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_220_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}

	/*---------- ACTUALITZACIÓ V. 2.2.1 -----------------------------------------------*/
	private boolean actualitzarV221() {
		boolean actualitzat = false;
		Versio versio221 = obtenirOCrearVersio(VERSIO_221_STR, VERSIO_221_ORDRE);
		if (!versio221.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_221_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio221.isProcesExecutat()) {
			try {
				actualitzarOrdreValorsEnumeracionsV221();
				versio221.setProcesExecutat(true);
				versio221.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio221);
				logger.info("Actualització a la versió " + VERSIO_221_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_221_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_221_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}
	private void actualitzarOrdreValorsEnumeracionsV221() {
		List<Enumeracio> enumeracions = enumeracioDao.findAll();
		for (Enumeracio enumeracio: enumeracions) {
			int i = 0;
			for (EnumeracioValors valor: enumeracio.getEnumeracioValors()) {
				valor.setOrdre(i++);
			}
		}
	}

	/*---------- ACTUALITZACIÓ V. 2.3.0 -----------------------------------------------*/
	private boolean actualitzarV230() {
		boolean actualitzat = false;
		Versio versio230 = obtenirOCrearVersio(VERSIO_230_STR, VERSIO_230_ORDRE);
		if (!versio230.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_230_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio230.isProcesExecutat()) {
			try {
				versio230.setProcesExecutat(true);
				versio230.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio230);
				logger.info("Actualització a la versió " + VERSIO_230_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_230_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_230_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}

	/*---------- ACTUALITZACIÓ V. 2.4.0 -----------------------------------------------*/
	private boolean actualitzarV240() {
		boolean actualitzat = false;
		Versio versio240 = obtenirOCrearVersio(VERSIO_240_STR, VERSIO_240_ORDRE);
		if (!versio240.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_240_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio240.isProcesExecutat()) {
			try {
				versio240.setProcesExecutat(true);
				versio240.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio240);
				logger.info("Actualització a la versió " + VERSIO_240_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_240_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_240_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}
	
	/*---------- ACTUALITZACIÓ V. 2.5.0 -----------------------------------------------*/
	private boolean actualitzarV250() {
		boolean actualitzat = false;
		Versio versio250 = obtenirOCrearVersio(VERSIO_250_STR, VERSIO_250_ORDRE);
		if (!versio250.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_250_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio250.isProcesExecutat()) {
			try {
				versio250.setProcesExecutat(true);
				versio250.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio250);
				logger.info("Actualització a la versió " + VERSIO_250_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_250_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_250_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}
	
	/*---------- ACTUALITZACIÓ V. 2.6.0 -----------------------------------------------*/
	private boolean actualitzarV260() {
		boolean actualitzat = false;
		Versio versio260 = obtenirOCrearVersio(VERSIO_260_STR, VERSIO_260_ORDRE);
		if (!versio260.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_260_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio260.isProcesExecutat()) {
			try {
				versio260.setProcesExecutat(true);
				versio260.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio260);
				logger.info("Actualització a la versió " + VERSIO_260_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_260_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_260_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}

	/*---------- ACTUALITZACIÓ V. 3.0.0 -----------------------------------------------*/
	private boolean actualitzarV300() {
		boolean actualitzat = false;
		Versio versio300 = obtenirOCrearVersio(VERSIO_300_STR, VERSIO_300_ORDRE);
		if (!versio300.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_300_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio300.isProcesExecutat()) {
			try {
				versio300.setProcesExecutat(true);
				versio300.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio300);
				logger.info("Actualització a la versió " + VERSIO_300_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_300_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_300_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}

	/*---------- ACTUALITZACIÓ V. 3.1.0 -----------------------------------------------*/
	private boolean actualitzarV310() {
		boolean actualitzat = false;
		Versio versio310 = obtenirOCrearVersio(VERSIO_310_STR, VERSIO_310_ORDRE);
		if (!versio310.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_310_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio310.isProcesExecutat()) {
			try {
				versio310.setProcesExecutat(true);
				versio310.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio310);
				logger.info("Actualització a la versió " + VERSIO_310_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_310_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_310_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}



	private String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}
	private String getMessage(String key) {
		return getMessage(key, null);
	}

	private Versio obtenirOCrearVersio(String codi, Integer ordre) {
		Versio versio = versioDao.findAmbCodi(codi);
		if (versio == null) {
			versio = new Versio(codi, ordre);
			versioDao.saveOrUpdate(versio);
		}
		return versio;
	}

	private static final Log logger = LogFactory.getLog(UpdateService.class);

}
