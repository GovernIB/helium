/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO amb informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentListDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private Date dataCreacio;
	private Date dataDocument;
	private NtiTipoDocumentalEnumDto tipoDocumental;
	private String processInstanceId;
	private Long expedientId;

	private boolean adjunt;
	private String extensio;

	private boolean signat;
	private boolean notificable;
	private boolean notificat;
	private boolean arxiuActiu;
	private boolean ntiActiu;
	private boolean registrat;
	private boolean documentValid;
	private boolean psignaPendent;
	private boolean psignaError;
	private boolean arxivat;
	private boolean deAnotacio;

	private String signaturaUrlVerificacio;
	private String ntiCsv;

//	private String psignaEstat;
//	private Integer psignaDocumentId;
//	private Date psignaDataEnviat;
//	private Date psignaDataProcessamentPrimer;
//	private Date psignaDataProcessamentDarrer;
	private String psignaErrorProcessant;
	private String psignaMotiuRebuig;

	private String arxiuUuid;
	private String expedientArxiuUuid;

	private Long anotacioId = null;
	private String anotacioIdentificador = null;

	private String error;
	private String documentError;

	private boolean visible = true;
	private boolean editable = true;
	private boolean obligatori = false;

}
