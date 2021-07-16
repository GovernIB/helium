package es.caib.helium.client.dada.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class Expedient {

	private String id;
	private Long expedientId;
	private Long entornId;
	private Long tipusId;
	private String numero;
	private String titol;
	private String procesPrincipalId;
	private Integer estatId;
	private Date dataInici;
	private Date dataFi;

	private List<Dada> dades;
}
