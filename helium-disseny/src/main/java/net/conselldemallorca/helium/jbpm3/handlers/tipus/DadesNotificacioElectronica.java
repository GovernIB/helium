/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.io.Serializable;
import java.util.List;


/**
 * Notificacio electronica
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesNotificacioElectronica implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private DadesRegistreNotificacio anotacio;	
	private List<DocumentInfo> annexos;
	
	public List<DocumentInfo> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<DocumentInfo> annexos) {
		this.annexos = annexos;
	}
	public DadesRegistreNotificacio getAnotacio() {
		return anotacio;
	}
	public void setAnotacio(DadesRegistreNotificacio anotacio) {
		this.anotacio = anotacio;
	}
	
	@Override
	public String toString() {
		return "NotificacioElectronica [anotacio=" + anotacio + ", annexos=" + annexos + "]";
	}
}
