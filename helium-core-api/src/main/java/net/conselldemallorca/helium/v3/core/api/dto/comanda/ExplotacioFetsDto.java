package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExplotacioFetsDto {
	
	public ExplotacioFetsDto(
		Long entornId,
		Long tipusExpedientId,
		Long unitatOrganitzativaId,
		Long expedientsOberts,
		Long expedientsTancats,
		Long expedientsAnulats,
		Long expedientsArxiu,
		Long expedientsNoAnulats,
		Long expedientsTotals,
		Long tasquesPendents,
		Long tasquesFinalitzades,
		Long anotacionPendents,
		Long anotacionProcessades,
		Long peticionsPinbal,
		Long peticionsPortafib,
		Long peticionsNotib) {
		
		this.entornId = entornId;
		this.tipusExpedientId = tipusExpedientId;
		this.unitatOrganitzativaId = unitatOrganitzativaId;
		
		this.expedientsOberts = expedientsOberts;
		this.expedientsTancats = expedientsTancats;
		this.expedientsAnulats = expedientsAnulats;
		this.expedientsArxiu = expedientsArxiu;
		this.expedientsNoAnulats = expedientsNoAnulats;
		this.expedientsTotals = expedientsTotals;
		this.tasquesPendents = tasquesPendents;
		this.tasquesFinalitzades = tasquesFinalitzades;
		this.anotacionPendents = anotacionPendents;
		this.anotacionProcessades = anotacionProcessades;
		this.peticionsPinbal = peticionsPinbal;
		this.peticionsNotib = peticionsNotib;
		this.peticionsPortafib = peticionsPortafib;
	}
	
	private Long id;
	private Long expedientsOberts;
	private Long expedientsTancats;
	private Long expedientsAnulats;
	private Long expedientsArxiu;
	private Long expedientsNoAnulats;
	private Long expedientsTotals;
	private Long tasquesPendents;
	private Long tasquesFinalitzades;
	private Long anotacionPendents;
	private Long anotacionProcessades;
	private Long peticionsPinbal;
	private Long peticionsNotib;
	private Long peticionsPortafib;
	private ExplotacioDimensioDto dimensio;
	private ExplotacioTempsDto temps;
	
	private Long unitatOrganitzativaId;
	private Long tipusExpedientId;
	private Long entornId;
	
}
