/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

/**
 * Applet per a la signatura de documents digitals emprant el plugin de la CAIB
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SignaturaAppletCaib extends SignaturaApplet {

	private static final long serialVersionUID = 1L;

	@Override
	public String getPluginClass() {
		return "net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPluginCaib";
	}

}
