package es.caib.helium.client.expedient.tasca.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponsableDto {

	private Long id;
	private String usuariCodi;	
}
