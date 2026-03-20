/**
 * 
 */
package es.caib.helium.back.command;

/**
 * Command per importar la informació d'una definició de procés dins
 * del llistat de definicios al disseny del tipus d'expedient. 
 */
public class ExpedientTipusDefinicioProcesIncorporarCommand {
	
	private Long definicioProcesId;
	private boolean sobreescriure;
	/** Indica si canviar la relació de les tasques cap a la informació definida a nivell de tipus d'expedient. */
	private boolean tasques;
	
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
	public boolean isTasques() {
		return tasques;
	}
	public void setTasques(boolean tasques) {
		this.tasques = tasques;
	}

	public interface Incorporar {}
}
