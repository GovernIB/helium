package es.caib.helium.integracio.domini.notificacio;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import es.caib.helium.integracio.enums.notificacio.EnviamentTipus;
import es.caib.helium.integracio.enums.notificacio.NotificacioEstat;
import es.caib.helium.integracio.enums.notificacio.ServeiTipusEnum;
import lombok.Data;

@Data
public class DadesNotificacioDto {
	
	private Long id;
	private Date caducitat;
	@NotNull @NotEmpty
	private String concepte;
	private String descripcio;
	private String documentNom;
	@NotNull @NotEmpty
	private String documentArxiuNom;
	@NotNull @NotEmpty
	private byte[] documentArxiuContingut;
	private String documentArxiuUuid;
	private String documentArxiuCsv; //TODO PENDENT BUSCAR L'ARXIU ENLLOC DE PASSAR-LO
	@NotNull @NotEmpty
	private String emisorDir3Codi;
	private Date enviamentDataProgramada;
	@NotNull
	private EnviamentTipus enviamentTipus;
	private ServeiTipusEnum serveiTipusEnum;
	@NotNull @NotEmpty //Todo queda pendent validar els enviaments i subtipus
	private List<Enviament> enviaments;
	@NotNull @NotEmpty
	private String procedimentCodi;
	private Integer retard;
	private String grupCodi;
	@NotNull
	private Long documentId;
	private NotificacioEstat estat;
	private Date enviatData;
	private Boolean error;
	private String errorDescripcio;
	private Long justificantId;
	private String justificantArxiuNom;
	@NotNull
	private Long expedientId;
	@NotNull
	private String expedientIdentificadorLimitat;
	@NotNull
	private Long entornId;
	private boolean entregaPostalActiva;
	private String enviamentIdentificador;
	private String enviamentReferencia;
	
	@NotNull @NotEmpty
	private String usuariCodi;
	private String numExpedient;
}
