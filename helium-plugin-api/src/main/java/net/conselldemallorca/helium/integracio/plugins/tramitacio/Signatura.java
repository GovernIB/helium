/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;


/**
 * 
 * @author Limit Tecnologies
 */
public class Signatura {

	protected byte[] signatura;
	protected String format;

	public byte[] getSignatura() {
		return signatura;
	}
	public void setSignatura(byte[] signatura) {
		this.signatura = signatura;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}

}
