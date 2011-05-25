/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command pel canvi de versió d'un procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
