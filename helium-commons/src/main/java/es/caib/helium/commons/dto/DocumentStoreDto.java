/**
 *
 */
package es.caib.helium.commons.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'un document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DocumentStoreDto implements Serializable {

	private Long id;
	private boolean adjunt = false;
	private String codiDocument;
	private String processInstanceId;
	private String codi;
	private String nom;
	private boolean signat;
	private String arxiuNom;

	private Date registreData;
	private String referenciaFont;
	private String registreNumero;
	private boolean registreEntrada;
	private String registreOrganCodi;
	private String referenciaCustodia;
	private String registreOficinaNom;
	private String registreOficinaCodi;

	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgan;
	private NtiOrigenEnumDto ntiOrigen;
	private String ntiEstatElaboracio;
	private String ntiNomFormat;
	private String ntiTipusDocumental;

	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	private NtiDocumentoFormato ntiNombreFormato;
	private NtiTipoFirmaEnumDto ntiTipoFirma;
	private String ntiIdDocumentoOrigen;
	private String ntiDefinicionGenCsv;
	private String ntiDefGenCsv;
	private String ntiValorCsv;
	private String ntiCsv;

	private String arxiuUuid;
	private Long annexId;

	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;

	private String ntiIdDocOrigen;

	/** Indica si en la consulta Distribucio el marca com a válid o invàlid */
	private boolean documentValid;

	/** Camp on distribucio informa dels possibles errors que pugui tenir el document. */
	private String documentError;

	/** Llista de documents continguts en el zip guardats a laa taula hel_document_contingut. S'usa en les notificacions de zips. */
	private List<DocumentStoreDto> continguts = new ArrayList<DocumentStoreDto>();

	/** Llista de documents zip que contenen aquest document guardats a la taula hel_document_contingut. S'usa en les notificacions de zips. */
	private List<DocumentStoreDto> zips = new ArrayList<DocumentStoreDto>();

	private static final long serialVersionUID = 774909297938469787L;

	public boolean isRegistrat() {
		return (registreNumero != null) || (registreData != null);
	}
	public boolean isRegistreSortida() {
		return !isRegistreEntrada();
	}

}
