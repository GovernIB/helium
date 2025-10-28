package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExplotacioDimensioDto {
	private Long id;
	private Long unitatOrganitzativaId;
	private String unitatOrganitzativaCodi;
	private Long entornId;
	private String entornCodi;
	private Long tipusId;
	private String tipusCodi;
	
	public ExplotacioDimensioDto(Long unitatOrganitzativaId, String unitatOrganitzativaCodi, Long entornId,
			String entornCodi, Long tipusId, String tipusCodi) {
		super();
		this.unitatOrganitzativaId = unitatOrganitzativaId;
		this.unitatOrganitzativaCodi = unitatOrganitzativaCodi;
		this.entornId = entornId;
		this.entornCodi = entornCodi;
		this.tipusId = tipusId;
		this.tipusCodi = tipusCodi;
	}
	
	
}

