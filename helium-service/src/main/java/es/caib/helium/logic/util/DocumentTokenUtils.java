/**
 * 
 */
package es.caib.helium.logic.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Classe per a la codificació i descodificació de tokens per a accedir als
 * documents
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentTokenUtils {

	public static final String DEFAULT_ENCRYPTION_KEY = "H3l1umKy";
	public static final String DEFAULT_ENCRYPTION_SCHEME = "DES/ECB/PKCS5Padding";
	public static final String DEFAULT_ENCRYPTION_ALGORITHM = "DES";

	private String encryptionKey;
	private String encryptionScheme;
	private String encryptionAlgorithm;

	public DocumentTokenUtils() {
	}
	public DocumentTokenUtils(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	public DocumentTokenUtils(
			String encryptionKey,
			String encryptionScheme,
			String encryptionAlgorithm) {
		this.encryptionKey = encryptionKey;
		this.encryptionScheme = encryptionScheme;
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	public String xifrarToken(String token) throws Exception {
		String tokenPad = null;
		if (isTokenLlarg() && token.length() < 15)
			tokenPad = String.format("%1$-" + (15 - token.length() + 2) + "s", token);
		else
			tokenPad = token;
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(
				getEncryptionAlgorithm());
		Cipher cipher = Cipher.getInstance(getEncryptionScheme());
		cipher.init(
				Cipher.ENCRYPT_MODE,
				secretKeyFactory.generateSecret(new DESKeySpec(getEncryptionKey().getBytes())));
		byte[] encryptedText = cipher.doFinal(tokenPad.getBytes());
		return encodeToken(encryptedText);
	}
	public String desxifrarToken(String tokenXifrat) throws Exception {
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(
				getEncryptionAlgorithm());
		Cipher cipher = Cipher.getInstance(getEncryptionScheme());
		cipher.init(
				Cipher.DECRYPT_MODE,
				secretKeyFactory.generateSecret(new DESKeySpec(getEncryptionKey().getBytes())));
		byte[] unencryptedText = cipher.doFinal(decodeToken(tokenXifrat));
		if (!isTokenLlarg())
			return new String(unencryptedText);
		else
			return new String(unencryptedText).trim();
	}
	public String xifrarTokenMultiple(String[] tokens) throws Exception {
		StringBuilder concat = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			concat.append(tokens[i]);
			if (i < tokens.length - 1)
				concat.append("#");
		}
		return xifrarToken(concat.toString());
	}
	public String[] desxifrarTokenMultiple(String tokensXifrats) throws Exception {
		return desxifrarToken(tokensXifrats).split("#");
	}



	private String getEncryptionKey() {
		if (encryptionKey != null && encryptionKey.length() > 0)
			return encryptionKey;
		return DEFAULT_ENCRYPTION_KEY;
	}
	private String getEncryptionScheme() {
		if (encryptionScheme != null && encryptionScheme.length() > 0)
			return encryptionScheme;
		return DEFAULT_ENCRYPTION_SCHEME;
	}
	private String getEncryptionAlgorithm() {
		if (encryptionAlgorithm != null && encryptionAlgorithm.length() > 0)
			return encryptionAlgorithm;
		return DEFAULT_ENCRYPTION_ALGORITHM;
	}

	private String encodeToken(byte[] tokenData) {
		if (!isTokenLlarg()) {
			byte[] base64Bytes = Base64.encodeBase64(tokenData);
			return new String(base64Bytes);
		} else {
			String hex = new String(Hex.encodeHex(tokenData)).toUpperCase();
			StringBuilder sb = new StringBuilder(hex);
			for (int i = hex.length() - 1; i > 0; i--) {
				if (i % 8 == 0)
					sb.insert(i, "-");
			}
			return sb.toString();
		}
	}
	private byte[] decodeToken(String tokenData) throws Exception {
		if (!isTokenLlarg()) {
			return Base64.decodeBase64(tokenData.getBytes());
		} else {
			return Hex.decodeHex(tokenData.replace("-", "").toCharArray());
		}
	}

	private boolean isTokenLlarg() {
		return "true".equals(GlobalPropertiesImpl.getPropietat("es.caib.helium.signatura.token.llarg"));
	}

}
