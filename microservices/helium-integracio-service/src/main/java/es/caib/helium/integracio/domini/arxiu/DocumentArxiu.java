package es.caib.helium.integracio.domini.arxiu;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;

import es.caib.helium.integracio.enums.arxiu.NtiEstadoElaboracionEnum;
import es.caib.helium.integracio.enums.arxiu.NtiOrigenEnum;
import es.caib.helium.integracio.enums.arxiu.NtiTipoDocumentalEnum;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import lombok.Data;

@Data
public class DocumentArxiu {

	@Size(max = 32)
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
