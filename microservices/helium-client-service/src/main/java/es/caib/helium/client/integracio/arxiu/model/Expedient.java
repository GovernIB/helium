package es.caib.helium.client.integracio.arxiu.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Expedient {

	private String nom;
	private List<String> ntiOrgans;
	private Date ntiDataObertura;
	private String ntiClassificacio;
	private boolean expedientFinalitzat;
	private List<String> ntiInteressats;
	private String serieDocumental;
	private String arxiuUuid;
}
