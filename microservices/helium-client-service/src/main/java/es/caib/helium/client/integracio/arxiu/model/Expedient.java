package es.caib.helium.client.integracio.arxiu.model;

import es.caib.helium.client.integracio.arxiu.enums.ContingutTipus;
import lombok.Data;

import java.util.List;

@Data
public class Expedient extends ContingutArxiu {
// AQUESTA CLASSE HA DE SER IGUAL AL QUE RETORNA es.caib.plugins.arxiu.api

	private List<ContingutArxiu> continguts;

	public Expedient() {
		super(ContingutTipus.EXPEDIENT);
	}

	public ExpedientMetadades getMetadades() {
		return this.expedientMetadades;
	}

	public void setMetadades(ExpedientMetadades metadades) {
		this.expedientMetadades = metadades;
	}

	public List<ContingutArxiu> getContinguts() {
		return this.continguts;
	}

	public void setContinguts(List<ContingutArxiu> continguts) {
		this.continguts = continguts;
	}

//	private String nom;
//	private List<String> ntiOrgans;
//	private Date ntiDataObertura;
//	private String ntiClassificacio;
//	private boolean expedientFinalitzat;
//	private List<String> ntiInteressats;
//	private String serieDocumental;
//	private String arxiuUuid;
}
