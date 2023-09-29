/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PermisEstatDto implements Serializable {

	@EqualsAndHashCode.Include
	private String principalNom;
	@EqualsAndHashCode.Include
	private PrincipalTipusEnumDto principalTipus;

	private boolean read;
	private boolean write;
	private boolean dataManagement;
	private boolean docManagement;

	public PermisDto toPermisDto() {
		PermisDto permis = new PermisDto();
		permis.setRead(this.read);
		permis.setWrite(this.write);
		permis.setDataManagement(this.dataManagement);
		permis.setDocManagement(this.docManagement);

		return permis;
	}
	private static final long serialVersionUID = -139254994389509932L;

}
