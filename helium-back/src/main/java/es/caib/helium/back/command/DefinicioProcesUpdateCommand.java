/**
 * 
 */
package es.caib.helium.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import es.caib.helium.back.command.DefinicioProcesUpdateCommand.Modificacio;
import es.caib.helium.back.validator.DefinicioProcesUpdate;

/**
 * Command per editar la informació d'una definició de procés des de la pipella d'informació de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@DefinicioProcesUpdate(groups = {Modificacio.class})
public class DefinicioProcesUpdateCommand {

	private Long id;
	private String jbpmKey;
	private int versio = -1;
	@NotEmpty(groups = {Modificacio.class})
	@Size(max = 64, groups = {Modificacio.class})
	private String etiqueta;
	private boolean hasStartTask;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getJbpmKey() {
		return jbpmKey;
	}
	public void setJbpmKey(String jbpmKey) {
		this.jbpmKey = jbpmKey;
	}
	public int getVersio() {
		return versio;
	}
	public void setVersio(int versio) {
		this.versio = versio;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public void setHasStartTask(boolean hasStartTask) {
		this.hasStartTask = hasStartTask;
	}
	public boolean isHasStartTask() {
		return hasStartTask;
	}

	public interface Modificacio {}
}
