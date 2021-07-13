package es.caib.helium.jms.events;

import java.util.Date;
import java.util.List;

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
	
	private String id;
	private CodiIntegracio codi;
	private Long entornId;
	private Date data;
	private String descripcio;
	private long tempsResposta;	
	private List<Parametre> parametres;
	private TipusAccio tipus;
	private EstatAccio estat;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;
}
