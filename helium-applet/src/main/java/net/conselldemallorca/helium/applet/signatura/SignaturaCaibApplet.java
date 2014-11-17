package net.conselldemallorca.helium.applet.signatura;

import java.applet.Applet;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import es.caib.signatura.api.Signer;
import es.caib.signatura.api.SignerFactory;

public class SignaturaCaibApplet extends Applet {

	private Signer signer;
	private final int MAX_SIZE_STRING = 500000;

	/**
	 * Consulta dels certificats disponibles per a la signatura
	 * 
	 * @param contentType
	 * @return
	 */
	public String[] findCertificats(
			String contentType) {
		try {
			return getSigner().getCertList(contentType);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Signatura en format PDF s'un document especificat en una url
	 * 
	 * @param url
	 * @param certName
	 * @param password
	 * @param contentType
	 * @return
	 */
	public String[] signaturaPdf(
			String url,
			String certName,
			String password,
			String contentType) {
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection)new URL(url).openConnection();
			conn.connect();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			getSigner().signPDF(
					conn.getInputStream(),
					baos,
					certName,
					password,
					contentType,
					null,
					Signer.PDF_SIGN_POSITION_NONE,
					true);
			String file = new String(Base64Coder.encode(baos.toByteArray()));
			return split(file, MAX_SIZE_STRING);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String[] split(String src, int len) {
	    String[] result = new String[(int)Math.ceil((double)src.length()/(double)len)];
	    for (int i=0; i<result.length; i++)
	        result[i] = src.substring(i*len, Math.min(src.length(), (i+1)*len));
	    return result;
	}

	private Signer getSigner() throws Exception {
		if (signer == null) {
			SignerFactory sf = new SignerFactory();
			return sf.getSigner();
		}
		return signer;
	}

	private static final long serialVersionUID = 4044611393629576909L;

}
