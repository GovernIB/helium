/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO amb informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DocumentDto extends HeretableDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;
	private boolean plantilla;
	private boolean notificable;
	private boolean required;
	private boolean readOnly;

	private Long documentId;
	private String documentCodi;
	private String documentNom;
	private String documentContentType;
	private String documentCustodiaCodi;
	private Integer documentTipusDocPortasignatures;
	private boolean documentPendentSignar = false;
	private boolean signatRequired = false;

	private boolean signat = false;
	private Long portasignaturesId;
	private String signaturaUrlVerificacio;

	private boolean registrat = false;
	private String registreNumero;
	private Date registreData;
	private String registreOficinaCodi;
	private String registreOficinaNom;
	private boolean registreEntrada = true;

	private boolean adjunt = false;
	private String adjuntId;
	private String adjuntTitol;

	private String arxiuNom;
	private byte[] arxiuContingut;
	private String signatNom;
	private byte[] signatContingut;
	private String vistaNom;
	private byte[] vistaContingut;
	
	private String arxiuCsv;
	private String arxiuUuid;

	private String convertirExtensio;
	private String extensionsPermeses;

	private String processInstanceId;
	private String contentType;
	private String custodiaCodi;
	private Integer tipusDocPortasignatures;

	private String tokenSignatura;
	private String tokenSignaturaMultiple;
	private boolean signatEnTasca;
	private boolean adjuntarAuto;
	private String urlVerificacioCustodia;

	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;

	private ExpedientTipusDto expedientTipus;
	private CampDto campData;
	/* Indica si permetre o no la retroacció.
	 * Si isIgnored = true llavors no es realitzarà la retroacció i no s'esborrarà
	 * el contingut del document. */
	private boolean ignored;

	public String getArxiuNomSenseExtensio() {
		if (getArxiuNom() == null)
			return null;
		int indexPunt = getArxiuNom().lastIndexOf(".");
		if (indexPunt != -1) {
			return getArxiuNom().substring(0, indexPunt);
		} else {
			return getArxiuNom();
		}
	}
	public String getArxiuExtensio() {
		if (getArxiuNom() == null)
			return null;
		int indexPunt = getArxiuNom().lastIndexOf(".");
		if (indexPunt != -1) {
			return getArxiuNom().substring(indexPunt + 1);
		} else {
			return null;
		}
	}

	private static final long serialVersionUID = 774909297938469787L;
}
