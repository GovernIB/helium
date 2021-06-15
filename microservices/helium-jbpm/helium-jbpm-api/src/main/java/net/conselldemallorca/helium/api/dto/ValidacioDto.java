/**
 * 
 */
package net.conselldemallorca.helium.api.dto;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objecte de domini que representa una validació de dades
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"camp", "expressio", "tasca"})
public class ValidacioDto {

	private Long id;
	private String nom;
	private String expressio;
	private String missatge;
	int ordre;
	private ExpedientTascaDto tasca;
	private CampDto camp;

	public ValidacioDto(ExpedientTascaDto tasca, String expressio, String missatge) {
		this.expressio = expressio;
		this.missatge = missatge;
		this.tasca = tasca;
	}
	public ValidacioDto(CampDto camp, String expressio, String missatge) {
		this.expressio = expressio;
		this.missatge = missatge;
		this.camp = camp;
	}
}
