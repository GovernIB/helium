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
	private boolean docValid;
	private boolean psActiu;
	private boolean psPendent;
	private boolean psError;
	private boolean arxivat;
	private String signUrlVer;
	private String ntiCsv;
	private String psEstat;
	private Integer psDocId;
	private String arxiuUuid;
	private String expUuid;
	private Long anotacioId = null;
	private String anotacioIdf = null;
	private String error;
	private String docError;
	@Builder.Default
	private boolean visible = true;
	@Builder.Default
	private boolean editable = true;
	@Builder.Default
	private boolean obligatori = false;

	private static final long serialVersionUID = -2271231227621850341L;
}