package es.caib.helium.integracio.domini.arxiu;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ExpedientArxiu {

	private String identificador;
	private List<String> ntiOrgans;
	private Date ntiDataObertura;
	private String ntiClassificacio;
	private boolean expedientFinalitzat;
	private List<String> ntiInteressats;
	private String serieDocumental;
	private String arxiuUuid;
}
