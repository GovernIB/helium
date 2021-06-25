package es.caib.helium.monitor.domini;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.TipusAccio;
import lombok.Data;

@Data
public class Consulta {

	private CodiIntegracio codi;
	@NotNull
	private Long entornId;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date dataInicial;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date dataFinal;
	private String descripcio;
	private Long tempsResposta;
	private TipusAccio tipus;
	private Boolean error;
	
	private Integer page;
	private Integer size;
}
