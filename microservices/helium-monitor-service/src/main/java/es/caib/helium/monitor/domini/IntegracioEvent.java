package es.caib.helium.monitor.domini;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntegracioEvent {
	
	@Id
	private String id;
	@NotNull
	private CodiIntegracio codi;
	@NotNull
	private Long entornId;
	@NotNull
	private Date data;
	@NotNull
	private String descripcio;
	private long tempsResposta;	
	@NotNull @NotEmpty
	private List<Parametre> parametres;
	@NotNull
	private TipusAccio tipus;
	@NotNull
	private EstatAccio estat;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;
}
