/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import org.springframework.web.multipart.MultipartFile;

/**
 * Command per importar un fitxer de dades. Pot ser genèric. S'utilitza per exemple en el 
 * formulari d'importació d'estats pel tipus d'expedient.
 * 
 */
public class ImportarDadesCommand {

	private boolean eliminarValorsAntics;
	private MultipartFile multipartFile;
	
	public boolean isEliminarValorsAntics() {
		return eliminarValorsAntics;
	}
	public void setEliminarValorsAntics(boolean eliminarValorsAntics) {
		this.eliminarValorsAntics = eliminarValorsAntics;
	}
	public MultipartFile getMultipartFile() {
		return multipartFile;
	}
	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}
	
	public interface Importar {}
}
