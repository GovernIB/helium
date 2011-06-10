/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import net.conselldemallorca.helium.core.model.dto.ArxiuDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;


/**
 * Servei per a gestionar les descàrregues d'arxius
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DocumentService {

	private DtoConverter dtoConverter;
	private MessageSource messageSource;



	public DocumentDto arxiuDocumentInfo(Long documentStoreId) {
		return dtoConverter.toDocumentDto(documentStoreId, false, false, false, false);
	}

	public ArxiuDto arxiuDocumentOriginal(Long documentStoreId) {
		DocumentDto document = dtoConverter.toDocumentDto(documentStoreId, true, false, false, false);
		if (document == null)
			return null;
		return new ArxiuDto(
				document.getArxiuNom(),
				document.getArxiuContingut());
	}

	public ArxiuDto arxiuDocumentVista(Long documentStoreId) {
		DocumentDto document = dtoConverter.toDocumentDto(documentStoreId, true, true, false, false);
		if (document == null)
			return null;
		return new ArxiuDto(
				document.getVistaNom(),
				document.getVistaContingut());
	}

	public ArxiuDto arxiuDocumentPerMostrar(Long documentStoreId) {
		DocumentDto document = arxiuDocumentInfo(documentStoreId);
		if (document == null)
			return null;
		if (document.isSignat() || document.isRegistrat()) {
			return arxiuDocumentVista(documentStoreId);
		} else {
			return arxiuDocumentOriginal(documentStoreId);
		}
	}

	public ArxiuDto arxiuDocumentPerSignar(String token, boolean estampar) {
		String tokenDesxifrat = desxifrarToken(token);
		String[] parts = tokenDesxifrat.split("#");
		if (parts.length == 2) {
			Long documentStoreId = Long.parseLong(parts[1]);
			DocumentDto document = dtoConverter.toDocumentDto(
					documentStoreId,
					true,
					true,
					true,
					estampar);
			if (document == null)
				return null;
			return new ArxiuDto(
					document.getVistaNom(),
					document.getVistaContingut());
		} else {
			throw new IllegalArgumentsException( getMessage("error.documentService.formatIncorrecte") );
		}
	}



	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private String desxifrarToken(String token) {
		try {
			String secretKey = GlobalProperties.getInstance().getProperty("app.signatura.secret");
			if (secretKey == null)
				secretKey = TascaService.DEFAULT_SECRET_KEY;
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(TascaService.DEFAULT_KEY_ALGORITHM);
			Cipher cipher = Cipher.getInstance(TascaService.DEFAULT_ENCRYPTION_SCHEME);
			cipher.init(
					Cipher.DECRYPT_MODE,
					secretKeyFactory.generateSecret(new DESKeySpec(secretKey.getBytes())));
			byte[] base64Bytes = Base64.decodeBase64(Hex.decodeHex(token.toCharArray()));
			byte[] encryptedText = cipher.doFinal(base64Bytes);
			return new String(encryptedText);
		} catch (Exception ex) {
			logger.error("No s'ha pogut desxifrar el token", ex);
			return token;
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

	private static final Log logger = LogFactory.getLog(DocumentService.class);

}
