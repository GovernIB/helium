/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;



/**
 * Un paràmetre per a cridar al web service d'un domini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ParellaCodiValorDto implements Serializable {

	@EqualsAndHashCode.Include
	private String codi;
	private Object valor;

	public ParellaCodiValorDto(
			String codiValor) {
		this.codi = codiValor;
		this.valor = codiValor;
	}

	private static final long serialVersionUID = 1L;

}
