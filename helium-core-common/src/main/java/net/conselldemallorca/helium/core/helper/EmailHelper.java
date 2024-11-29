/**
 * 
 */
package net.conselldemallorca.helium.core.helper;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioEmail;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.core.model.hibernate.UsuariPreferencies;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.EmailTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.repository.AnotacioEmailRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;
import net.conselldemallorca.helium.v3.core.repository.UsuariPreferenciesRepository;


/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EmailHelper {
	@Autowired
	private JavaMailSender mailSender;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private AnotacioEmailRepository anotacioEmailRepository;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource 
	private UsuariPreferenciesRepository usuariPreferenciesRepository;
	@Resource
	private PluginHelper pluginHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;

	/** Consulta els usuaris amb permís sobre l'anotació i l'expedient per usuari o rol i programa els enviaments.
	 * Per fer-ho primer cerca els pemisos sobre el tipus d'expedient o per tipus d'expedient i UO si és procediment comú.
	 * Seguidament cerca els usuaris assignats als permisos o els usuaris dels rols assignats en els permisos.
	 * Cerca en la configuració personal si estan configurats per enviar correus i s'envien els correus.
	 * 
	 * @param anotacio
	 * @param expedient
	 * @param emailTipus
	 */
	public void createEmailsAnotacioToSend(
			Anotacio anotacio, 
			Expedient expedient, 
			EmailTipusEnumDto emailTipus) {
		
		// Llistat de destinataris
		List<PersonaDto> persones = 
				this.findPersonesPermisAnotacio(anotacio, expedient, emailTipus);

		// Programa els correus
		if( persones != null && ! persones.isEmpty()) {
			for(PersonaDto persona: persones) {
				String usuariCodi = persona.getCodi();
				UsuariPreferencies usuariPreferencies = usuariPreferenciesRepository.findByCodi(usuariCodi);
				// Només s'envien a usuaris que ho indiquin en les preferències.
				if(usuariPreferencies != null 
						&& (usuariPreferencies.isCorreusBustia() 
								|| usuariPreferencies.isCorreusBustiaAgrupatsDia())) 
				{
					String email = usuariPreferencies.getEmailAlternatiu();
					if (email == null || "".equals(email)) {
						PersonaDto usuari =  pluginHelper.personaFindAmbCodi(usuariCodi);
						if (usuari != null) {
							email = usuari.getEmail();
						}
					}
					if (email != null) {
						AnotacioEmail anotacioEmail = new AnotacioEmail(
								anotacio, 
								expedient, 
								usuariCodi, 
								"Helium",
								emailTipus, 
								email,
								usuariPreferencies.isCorreusBustiaAgrupatsDia(),
								new Date(),
								0);
						anotacioEmailRepository.save(anotacioEmail);
					}
				}
			}	
		}
	}
	
	/** Mètode per trobar els destinataris per avisar per email de la nova anotació.
	 * 
	 * @param anotacio
	 * @param expedient
	 * @param emailTipus
	 * @return
	 */
	public List<PersonaDto> findPersonesPermisAnotacio (Anotacio anotacio, Expedient expedient, EmailTipusEnumDto emailTipus) {
		
		List<PersonaDto> personesAmbPermis = new ArrayList<PersonaDto>();
		
		ExpedientTipus expedientTipus = anotacio.getExpedientTipus();
		
		// Cerca els permisos sobre el tipus d'expedient 
		List<PermisDto> permisos = permisosHelper.findPermisos(expedientTipus.getId(), ExpedientTipus.class);

		// Si és un procediment comú llavors cerca tots els permisos pel tipus d'expedient i la unitat organitzativa de l'anotació o les seves superiors
		if (expedientTipus.isProcedimentComu()) {

			UnitatOrganitzativa uoAnotacio = 
					expedient != null && expedient.getUnitatOrganitzativa() != null? 
							expedient.getUnitatOrganitzativa()
							: unitatOrganitzativaRepository.findByCodi(anotacio.getEntitatCodi());
			if (uoAnotacio != null) {
				// Consulta totes les unitats des de la de l'expedient o anotació fins la unitat superior arrel
				List<UnitatOrganitzativaDto> unitats = unitatOrganitzativaHelper.findPath(uoAnotacio.getCodiUnitatArrel(), uoAnotacio.getCodi());
				for (UnitatOrganitzativaDto uo : unitats) {
					// Cerca la relació entre UO's i el tipus d'expedient
					ExpedientTipusUnitatOrganitzativa expedientTipusUnitatOrganitzativa = 
							unitatOrganitzativaHelper.findRelacioExpTipusUnitOrg(expedientTipus.getId(), uo.getId());
					// Cerca  el permisos sobre la relació entre la UO i el tipus d'expedient.
					if (expedientTipusUnitatOrganitzativa != null) {
						permisos.addAll(permisosHelper.findPermisos(
								expedientTipusUnitatOrganitzativa.getId(), 
								ExpedientTipusUnitatOrganitzativa.class));
					}
				}
			} else {
				logger.warn("No s'ha resolt la unitat organitzativa per l'anotació " + anotacio.getIdentificador() + " per poder comprovar permisos sobre UO's.");
			}			
		}
		// Si s'han trobat permisos llavors comença a resoldre les persones amb permís
		if (permisos != null && !permisos.isEmpty()) {
			PersonaDto persona;
			for (PermisDto permis: permisos) {
				// Si s'ha rebut sense associar expedient llavors ha de ser un permís de relacionar per veure l'anotació
				if (EmailTipusEnumDto.REBUDA_PENDENT.equals(emailTipus) 
						&& expedient == null
						&& ! permis.isRelate() && !permis.isAdministration()) {
					continue;
				}
				// Si s'ha processat llavors s'ha de ser un permís de relacionar o lectura per veure l'anotació
				if (!EmailTipusEnumDto.REBUDA_PENDENT.equals(emailTipus)
					&& ! permis.isRelate()
					&& ! permis.isRead()
					&& ! permis.isAdministration()) {
					continue;
				}
				
				switch (permis.getPrincipalTipus()) {
				case USUARI:
					try {
						persona = pluginHelper.personaFindAmbCodi(permis.getPrincipalNom());
						if (persona != null) {
							personesAmbPermis.add(persona);
						}
					} catch(Exception e) {
						logger.error("Error cercant les dades de la persona " + permis.getPrincipalNom(), e);
					}
					break;
				case ROL:
					try {
						String rol = permis.getPrincipalNom();
						if ("ROLE_ADMIN".equals(rol)) {
							rol = "HEL_ADMIN";
						} else if ("tothom".equals(rol.toLowerCase())) {
							continue; // NO es consulten els correus de tothom
						}
						
						List<PersonaDto> usuarisGrup = pluginHelper.personaFindAmbGrup(
								permis.getPrincipalNom());
						if (usuarisGrup != null) {
							for (PersonaDto usuariGrup: usuarisGrup) {
								personesAmbPermis.add(usuariGrup);
							}
						}
					} catch(Exception e) {
						logger.error("Error cercant les persones pel grup " + permis.getPrincipalNom(), e);
					}
					break;
				}
			}
		}
		return personesAmbPermis;
	}


	/** Envia un email avisant al destinatari que ha arribat una nova anotació 
	 * (ja sigui via incorporació, creació o pendent).
	 *  Es diferencia del mètode agrupat perquè només envia
	 * un email i canvia l'assumpte i el cos del missatge.
	 * 
	 * @param emailDestinatari
	 * 			Email a qui s'enviarà l'email.
	 * @param contingutEmail
	 * @throws Exception 
	 */
	@Transactional
	public void sendAnotacioEmailNoAgrupat(
			AnotacioEmail anotacioEmail,
			List<AnotacioEmail> anotacioEmailListNoAgrupats) throws Exception {
		
		logger.debug("Enviament email anotació " + anotacioEmail.getAnotacio().getIdentificador() + 
				" al destinatari " + anotacioEmail.getDestinatariCodi() + " " + anotacioEmail.getDestinatariEmail() +
				". Intent " + anotacioEmail.getNumIntents());

		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(anotacioEmail.getDestinatariEmail());
		missatge.setFrom(anotacioEmail.getRemitentCodi());
		//Depenent del tipus tindrem un Assumpte o un altre
		String subject = this.getPrefixHelium() + " Nova anotació " +anotacioEmail.getAnotacio().getIdentificador() ;
		if(EmailTipusEnumDto.PROCESSADA.equals(anotacioEmail.getEmailTipus())) {
			subject +=  " processada i creació de l'expedient: " + anotacioEmail.getExpedient().getNumeroIdentificador();
		}
		else if(EmailTipusEnumDto.INCORPORADA.equals(anotacioEmail.getEmailTipus())) {
			subject +=  " processada i incorporada a l'expedient: " + anotacioEmail.getExpedient().getNumeroIdentificador();
		}
		else if(EmailTipusEnumDto.REBUDA_PENDENT.equals(anotacioEmail.getEmailTipus())) {
			subject +=  " en estat pendent";
		}	
		missatge.setSubject(subject);	
		missatge.setText(
				subject +"\n" +
				"\tEntitat: " + ( anotacioEmail.getAnotacio() != null ?  anotacioEmail.getAnotacio().getEntitatDescripcio() : "") + "\n" +
				"\tUnitat organitzativa: " + ( anotacioEmail.getAnotacio() != null ?   this.getDadesNtiOrgan(anotacioEmail.getAnotacio().getExpedientTipus()): "") + "\n" +
				"\tAnotació: " + ( anotacioEmail.getAnotacio() != null ?  anotacioEmail.getAnotacio().getIdentificador() : "") + "\n" +
				"Dades de l'element: \n" +
				"\tTipus: Anotació " + 	messageHelper.getMessage("anotacio.email.tipus."+ anotacioEmail.getEmailTipus().toString()) + "\n" +
				"\tNom: "+ ( anotacioEmail.getAnotacio() != null ?  anotacioEmail.getAnotacio().getIdentificador() +" - " + anotacioEmail.getAnotacio().getExtracte() : "") + "\n" +
				"\tExpedient: "+((anotacioEmail.getAnotacio() != null && anotacioEmail.getAnotacio().getExpedient() != null) ?  
									(anotacioEmail.getAnotacio().getExpedient().getNumero() +" - " + anotacioEmail.getAnotacio().getExpedient().getTitol()) : 
									anotacioEmail.getAnotacio().getExpedientNumero() 
								)+ "\n" +				
				"\tRemitent: " + anotacioEmail.getRemitentCodi() + "\n" 
		);
		try {
			mailSender.send(missatge);
		} catch (Exception e) {
			String errMsg = "Error enviant l'email de l'anotació " + anotacioEmail.getId() + " al destinatari " + anotacioEmail.getDestinatariEmail() + ": " + e.getMessage();
			logger.error(errMsg);
			anotacioEmail.setNumIntents(anotacioEmail.getNumIntents()+1);
			List<AnotacioEmail> emailsToRemove = new ArrayList<AnotacioEmail>();
			if(anotacioEmail.getNumIntents()>2) {
				emailsToRemove.add(anotacioEmail);
				anotacioEmailRepository.delete(anotacioEmail);
			} else {
				anotacioEmailRepository.save(anotacioEmail);
			}
			if(!emailsToRemove.isEmpty()) {
				anotacioEmailListNoAgrupats.removeAll(emailsToRemove);
			}
			throw new Exception(errMsg);
		}
	}  
	
	private String getDadesNtiOrgan(ExpedientTipus expTipus) {
		if(expTipus!=null) {
			String codiUo = expTipus.getNtiOrgano();
			UnitatOrganitzativa uo = unitatOrganitzativaRepository.findByCodi(codiUo);
			if(uo!=null)
				return uo.getCodiAndNom();
		}
		return null;
	}
	
	public void sendAnotacioEmailsPendentsAgrupats(
			String emailDestinatari,
			List<AnotacioEmail> emailPendents) throws Exception {	
			
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(emailPendents.get(0).getRemitentCodi());
		missatge.setSubject(getPrefixHelium() + " Emails agrupats");
		
		// Agrupa per email tipus
		Map<EmailTipusEnumDto, List<AnotacioEmail>> emailTipusMap = new HashMap<EmailTipusEnumDto, List<AnotacioEmail>>();
		for (AnotacioEmail anotacioEmail : emailPendents) {
			if (emailTipusMap.containsKey(anotacioEmail.getEmailTipus())) {
				emailTipusMap.get(anotacioEmail.getEmailTipus()).add(anotacioEmail);
			} else {
				List<AnotacioEmail> anotacioEmailList = new ArrayList<AnotacioEmail>();
				anotacioEmailList.add(anotacioEmail);
				emailTipusMap.put(anotacioEmail.getEmailTipus(), anotacioEmailList);
			}
		}	
		String text = "";	
		for (Map.Entry<EmailTipusEnumDto, List<AnotacioEmail>> entry : emailTipusMap.entrySet()) {
			String header = "";
			if (entry.getKey() == EmailTipusEnumDto.REBUDA_PENDENT) {
				header = "Noves anotacions pendents";
			} else if (entry.getKey() == EmailTipusEnumDto.INCORPORADA) {
				header = "Noves anotacions incorporades a expedients";
			} else if(entry.getKey() == EmailTipusEnumDto.PROCESSADA) {
				header = "Noves anotacions processades";
			} 		
			text += header + "\n";
			text += "--------------------------------------------------------------------------\n\n";

			for (AnotacioEmail anotacioEmail : entry.getValue()) {
				String subject = this.getPrefixHelium() + " Nova anotació " +anotacioEmail.getAnotacio().getIdentificador() ;
				if(EmailTipusEnumDto.PROCESSADA.equals(anotacioEmail.getEmailTipus())) {
					subject +=  " processada i creació de l'expedient: " + anotacioEmail.getExpedient().getNumeroIdentificador();
				}
				else if(EmailTipusEnumDto.INCORPORADA.equals(anotacioEmail.getEmailTipus())) {
					subject +=  " processada i incorporada a l'expedient: " + anotacioEmail.getExpedient().getNumeroIdentificador();
				}
				else if(EmailTipusEnumDto.REBUDA_PENDENT.equals(anotacioEmail.getEmailTipus())) {
					subject +=  " en estat pendent";
				}	
				text += subject +"\n" +
						"\tEntitat: " + ( anotacioEmail.getAnotacio() != null ?  anotacioEmail.getAnotacio().getEntitatDescripcio() : "") + "\n" +
						"\tUnitat organitzativa: " + ( anotacioEmail.getAnotacio() != null ?   this.getDadesNtiOrgan(anotacioEmail.getAnotacio().getExpedientTipus()): "") + "\n" +
						"\tAnotació: " + ( anotacioEmail.getAnotacio() != null ?  anotacioEmail.getAnotacio().getIdentificador() : "") + "\n" +
						"Dades de l'element: \n" +
						"\tTipus: Anotació " + 	messageHelper.getMessage("anotacio.email.tipus."+ anotacioEmail.getEmailTipus().toString()) + "\n" +
						"\tNom: "+ ( anotacioEmail.getAnotacio() != null ?  anotacioEmail.getAnotacio().getIdentificador() +" - " + anotacioEmail.getAnotacio().getExtracte() : "") + "\n" +
						"\tExpedient: "+((anotacioEmail.getAnotacio() != null && anotacioEmail.getAnotacio().getExpedient() != null) ?  
											(anotacioEmail.getAnotacio().getExpedient().getNumero() +" - " + anotacioEmail.getAnotacio().getExpedient().getTitol()) : 
											anotacioEmail.getAnotacio().getExpedientNumero() 
										)+ "\n" +
						"\tRemitent: " + anotacioEmail.getRemitentCodi() + "\n" +
						"\n\n";
			}
			text += "\n";
		}
		missatge.setText(text);
		try {
			mailSender.send(missatge);	
		} catch (Exception e) {
			logger.info("Enviant el correu de " + emailPendents.size() + " anotacions agrupades al destinatari " + emailDestinatari);
			List<AnotacioEmail> emailsToRemove = new ArrayList<AnotacioEmail>();
			for (AnotacioEmail anotacioEmail : emailPendents) {
				anotacioEmail.setNumIntents(anotacioEmail.getNumIntents()+1);
				if(anotacioEmail.getNumIntents()>2) {
					emailsToRemove.add(anotacioEmail);
					anotacioEmailRepository.delete(anotacioEmail);
				} else {
					anotacioEmailRepository.save(anotacioEmail);
				}
			}
			if(!emailsToRemove.isEmpty()) {
				emailPendents.removeAll(emailsToRemove);
			}
			throw new Exception("Error enviant el correu de "  + emailPendents.size() + " anotacions agrupades al destinatari " + emailDestinatari);
		}
		for (AnotacioEmail emailPendent : emailPendents) {
			anotacioEmailRepository.delete(emailPendent);
		}	
	}
	
	
	private String getPrefixHelium() {
		String entorn = GlobalProperties.getInstance().getProperty("app.entorn.helium");
		String prefix;
		if (entorn != null) {
			prefix = "[HELIUM-" + entorn + "]";			
		}else {
			prefix = "[HELIUM]";
		}
		return prefix;
	}


	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

}
