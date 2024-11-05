/**
 * 
 */
package net.conselldemallorca.helium.core.helper;


import java.util.ArrayList;
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

import net.conselldemallorca.helium.core.model.hibernate.AnotacioEmail;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.v3.core.api.dto.EmailTipusEnumDto;
import net.conselldemallorca.helium.v3.core.repository.AnotacioEmailRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;


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
		logger.trace("Enviament email anotació a destinatari");

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
		missatge.setSubject(this.getPrefixHelium() + subject);	
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
			logger.error("Error enviant l'email de l'anotació " + anotacioEmail.getId() + " al destinatari " + anotacioEmail.getDestinatariEmail() + ": " + e.getMessage());
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
			throw new Exception("Error enviant l'email de l'anotació " + anotacioEmail.getId() + " al destinatari " + anotacioEmail.getDestinatariEmail() + ": " + e.getMessage());
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
		String entorn = null; //configHelper.getConfig("es.caib.distribucio.default.user.entorn");
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
