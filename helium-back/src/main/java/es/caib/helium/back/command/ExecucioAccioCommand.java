/**
 * 
 */
package es.caib.helium.back.command;

/**
 * Command pel l'execució d'una acció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExecucioAccioCommand {

	private String accioCodi;



	public ExecucioAccioCommand() {}

	public String getAccioCodi() {
		return accioCodi;
	}
	public void setAccioCodi(String accioCodi) {
		this.accioCodi = accioCodi;
	}

}
