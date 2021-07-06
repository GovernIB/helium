package es.caib.helium.client.integracio.arxiu.model;

import java.util.Date;
import java.util.List;

import es.caib.helium.client.integracio.arxiu.enums.DocumentEstat;
import es.caib.helium.client.integracio.arxiu.enums.DocumentExtensio;
import es.caib.helium.client.integracio.arxiu.enums.NtiEstadoElaboracionEnum;
import es.caib.helium.client.integracio.arxiu.enums.NtiOrigenEnum;
import es.caib.helium.client.integracio.arxiu.enums.NtiTipoDocumentalEnum;
import lombok.Data;

@Data
public class DocumentArxiu {

	private String uuid;
	private Arxiu arxiu;
	private String identificador;
	private String nom;
	private Arxiu fitxer;
	private boolean documentAmbFirma;
	private boolean firmaSeparada;
	private List<ArxiuFirma> firmes;
	private String ntiIdentificador;
	private NtiOrigenEnum ntiOrigen;
	private List<String> ntiOrgans;
	private Date ntiDataCaptura;
	private NtiEstadoElaboracionEnum ntiEstatElaboracio;
	private NtiTipoDocumentalEnum ntiTipusDocumental;
	private String ntiIdDocumentOrigen;
	private DocumentExtensio extensio;
	private DocumentEstat estat;
}
