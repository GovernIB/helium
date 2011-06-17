/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

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

	public DocumentTokenUtils(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public DocumentTokenUtils(String encryptionKey, String encryptionScheme,
			String encryptionAlgorithm) {
		this.encryptionKey = encryptionKey;
		this.encryptionScheme = encryptionScheme;
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	public String xifrarToken(String token) throws Exception {
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(
				getEncryptionAlgorithm());
		Cipher cipher = Cipher.getInstance(getEncryptionScheme());
		cipher.init(
				Cipher.ENCRYPT_MODE,
				secretKeyFactory.generateSecret(new DESKeySpec(getEncryptionKey().getBytes())));
		byte[] encryptedText = cipher.doFinal(token.getBytes());
		byte[] base64Bytes = Base64.encodeBase64(encryptedText);
		return new String(Hex.encodeHex(base64Bytes));
	}
	public String desxifrarToken(String tokenXifrat) throws Exception {
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(
				getEncryptionAlgorithm());
		Cipher cipher = Cipher.getInstance(DEFAULT_ENCRYPTION_SCHEME);
		cipher.init(
				Cipher.DECRYPT_MODE,
				secretKeyFactory.generateSecret(new DESKeySpec(getEncryptionKey().getBytes())));
		byte[] base64Bytes = Base64.decodeBase64(Hex.decodeHex(tokenXifrat.toCharArray()));
		byte[] unencryptedText = cipher.doFinal(base64Bytes);
		return new String(unencryptedText);
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

}
