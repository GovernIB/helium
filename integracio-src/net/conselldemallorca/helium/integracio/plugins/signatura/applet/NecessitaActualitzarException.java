/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.applet;

import java.net.URL;


/**
 * Excepció que indica que és necessari actualitzar l'API de signatura
 * digital
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class NecessitaActualitzarException extends Exception {

	private URL url;
	private static final long serialVersionUID = 1L;

	public NecessitaActualitzarException(String msg, URL url) {
		super(msg);
		this.url = url;
	}

	public NecessitaActualitzarException(String msg, URL url, Throwable t) {
		super(msg, t);
		this.url = url;
	}
	
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}

}
