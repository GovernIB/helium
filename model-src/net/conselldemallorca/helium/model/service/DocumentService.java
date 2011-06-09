/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import net.conselldemallorca.helium.model.dto.ArxiuDto;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per a gestionar les descàrregues d'arxius
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class DocumentService {

	private DtoConverter dtoConverter;



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
			throw new IllegalArgumentsException("El format del token és incorrecte");
		}
	}



	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
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

	private static final Log logger = LogFactory.getLog(DocumentService.class);

}
