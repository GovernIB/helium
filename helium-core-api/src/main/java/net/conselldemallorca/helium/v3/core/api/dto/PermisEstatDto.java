/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PermisEstatDto implements Serializable {

//	private Long id;
	@EqualsAndHashCode.Include
	private String principalNom;
	@EqualsAndHashCode.Include
	private PrincipalTipusEnumDto principalTipus;

	private boolean read;
	private boolean write;
	private boolean dataManagement;
	private boolean docManagement;

	private static final long serialVersionUID = -139254994389509932L;

}
