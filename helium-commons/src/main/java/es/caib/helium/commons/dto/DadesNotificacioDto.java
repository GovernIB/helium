/**
 *
 */
package es.caib.helium.commons.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una notificació per al seu enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DadesNotificacioDto {
	private Long id;
	private Date caducitat;
	private String concepte;
	private String descripcio;
	private String documentNom;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String documentArxiuUuid;
	private String documentArxiuCsv;
	private String emisorDir3Codi;
	private Date enviamentDataProgramada;
	private EnviamentTipusEnumDto enviamentTipus;
	private ServeiTipusEnumDto serveiTipusEnum;
	private List<DadesEnviamentDto> enviaments;
	private String procedimentCodi;
	private Integer retard;
	private String grupCodi;
	private Long documentId;
	private NotificacioEstatEnumDto estat;
	private Date enviatData;
	private Boolean error;
	private String errorDescripcio;
	private Long justificantId;
	private String justificantArxiuNom;
	private Long expedientId;
	private boolean entregaPostalActiva;
	private String enviamentIdentificador;
	private String enviamentReferencia;
	private IdiomaEnumDto idioma;
	private List<DocumentStoreDto> documentsDinsZip;
}
