package es.caib.helium.disseny.engine;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class WExpedientDto {
	private Long id;
	private String titol;
	private String numero;
	private String numeroDefault;
	private Date dataInici;
	private Date dataFi;
	private String processInstanceId;
}

