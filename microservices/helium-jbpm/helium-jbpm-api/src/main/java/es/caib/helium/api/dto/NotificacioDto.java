/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'una notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioDto implements Serializable {
	
	private Long id;
	private ExpedientDto expedient;
	private DocumentEnviamentEstatEnumDto estat;
	private String assumpte;
	private Date dataEnviament;
	private String observacions;
	
	private DocumentNotificacioTipusEnumDto tipus;
	private Date dataRecepcio;
	private String registreNumero;
	private InteressatDocumentTipusEnumDto interessatDocumentTipus;
	private String interessatDocumentNum;
	private String interessatNom;
	private String interessatLlinatge1;
	private String interessatLlinatge2;
	private String interessatPaisCodi;
	private String interessatProvinciaCodi;
	private String interessatMunicipiCodi;
	private String interessatEmail;
	private boolean interessatRepresentant;
	private String unitatAdministrativa;
	private String organCodi;
	private String oficinaCodi;
	private String avisTitol;
	private String avisText;
	private String avisTextSms;
	private String oficiTitol;
	private String oficiText;
	private InteressatIdiomaEnumDto idioma;
	private Date enviamentData;
	private Integer enviamentCount;
	private boolean enviamentError;
	private String enviamentErrorDescripcio;
	private Date processamentData;
	private Integer processamentCount;
	private boolean processamentError;
	private String processamentErrorDescripcio;
	private Long rdsCodi;
	private String rdsClau;
	private DocumentNotificacioDto document;
	private List<DocumentNotificacioDto> annexos = new ArrayList<DocumentNotificacioDto>();
	private String error;
	
	private static final long serialVersionUID = 1715501096089688125L;

}
