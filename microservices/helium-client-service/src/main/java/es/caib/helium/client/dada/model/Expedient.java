package es.caib.helium.client.dada.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expedient {

	private String id;
	private Long expedientId;
	private Long entornId;
	private Long tipusId;
	private String numero;
	private String numeroDefault;
	private String titol;
	private String procesPrincipalId;
	private Integer estatId;
	private Date dataInici;
	private Date dataFi;

	private List<Dada> dades;
}
