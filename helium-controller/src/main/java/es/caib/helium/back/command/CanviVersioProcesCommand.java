/**
 * 
 */
package es.caib.helium.back.command;

/**
 * Command pel canvi de versió d'un procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CanviVersioProcesCommand {

	private Long definicioProcesId;
	private Long[] subprocesId;



	public CanviVersioProcesCommand() {}

	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
	}

	public Long[] getSubprocesId() {
		return subprocesId;
	}
	public void setSubprocesId(Long[] subprocesId) {
		this.subprocesId = subprocesId;
	}

}
