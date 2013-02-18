/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.conselldemallorca.helium.core.model.dto.ArxiuDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Dao per a l'enviament de correus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MailDao {

	private JavaMailSender mailSender;



	public MailDao() {
		
	}

	public void send(
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text) throws Exception {
		send(
				fromAddress,
				recipients,
				ccRecipients,
				bccRecipients,
				subject,
				text,
				null);
	}

	public void send(
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<ArxiuDto> attachments) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setFrom(new InternetAddress(fromAddress));
		if (recipients != null) {
			for (String recipient: recipients) {
				mimeMessage.addRecipient(
						Message.RecipientType.TO,
						new InternetAddress(recipient));
			}
		}
		if (ccRecipients != null) {
			for (String recipient: ccRecipients) {
				mimeMessage.addRecipient(
						Message.RecipientType.CC,
						new InternetAddress(recipient));
			}
		}
		if (bccRecipients != null) {
			for (String recipient: bccRecipients) {
				mimeMessage.addRecipient(
						Message.RecipientType.BCC,
						new InternetAddress(recipient));
			}
		}
		mimeMessage.setSubject(subject);
		if (attachments != null) {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			for (ArxiuDto arxiu: attachments) {
				helper.addAttachment(
						arxiu.getNom(),
						new ByteArrayResource(arxiu.getContingut()));
			}
			helper.setText(text);
		} else {
			mimeMessage.setText(text);
		}
        this.mailSender.send(mimeMessage);
	}



	@Autowired
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

}
