/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per rebutjar una petició d'anotació de Distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioRebutjarCommand {

	@NotNull
	private Long anotacioId;
	@NotEmpty @Size(max=1024)
	private String observacions;
	
	public Long getAnotacioId() {
		return anotacioId;
	}
	public void setAnotacioId(Long anotacioId) {
		this.anotacioId = anotacioId;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(
			String observacions) {
		this.observacions = observacions;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}
}
