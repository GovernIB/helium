/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command per importar la informació d'una definició de procés dins
 * del llistat de definicios al disseny del tipus d'expedient. 
 */
public class ExpedientTipusDefinicioProcesImportarCommand {
	
	private Long definicioProcesId;
	private boolean sobreescriure;
	
	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
	}
	public boolean isSobreescriure() {
		return sobreescriure;
	}
	public void setSobreescriure(boolean sobreescriure) {
		this.sobreescriure = sobreescriure;
	}
	
	public interface Importar {}
}
