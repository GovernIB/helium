/**
 * 
 */
package net.conselldemallorca.helium.util;

import java.security.MessageDigest;

import net.conselldemallorca.helium.model.exception.DigesterException;

/**
 * Classe per a la codificació de contrasenyes
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PasswordDigester {

	private String algorithm;



	public PasswordDigester(String algorithm) {
		this.algorithm = algorithm;
	}

	public String digest(String passwd) {
		if (algorithm.equalsIgnoreCase("plain") || algorithm.equalsIgnoreCase("none"))
			return passwd;
		try {
			MessageDigest md = (MessageDigest)MessageDigest.getInstance(algorithm).clone();
			md.update(passwd.getBytes());
			byte[] bytes = md.digest();
			StringBuffer sb = new StringBuffer(bytes.length * 2);
			for (int i = 0; i < bytes.length; i++) {
				sb.append(convertDigit((int) (bytes[i] >> 4)));
				sb.append(convertDigit((int) (bytes[i] & 0x0f)));
			}
			return sb.toString();
		} catch (Exception ex) {
			throw new DigesterException("No s'ha pogut codificar la contrasenya", ex);
		}
	}



	private static char convertDigit(int value) {
		value &= 0x0f;
		if (value >= 10)
			return ((char) (value - 10 + 'a'));
		else
			return ((char) (value + '0'));
	}

}
