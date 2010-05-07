/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command pel canvi de versió d'un procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CanviVersioProcesCommand {

	private Long definicioProcesId;



	public CanviVersioProcesCommand() {}

	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
	}

}
