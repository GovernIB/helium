package es.caib.helium.client.integracio.notificacio.model;

import es.caib.helium.client.integracio.notificacio.enums.EnviamentTipus;
import es.caib.helium.client.integracio.notificacio.enums.Idioma;
import es.caib.helium.client.integracio.notificacio.enums.NotificacioEstat;
import es.caib.helium.client.integracio.notificacio.enums.ServeiTipusEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DadesNotificacioDto {
	
	private Long id;
	private Date caducitat;
	private String concepte;
	private String descripcio;
	private String documentNom;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String documentArxiuUuid;
	private String documentArxiuCsv; //TODO PENDENT BUSCAR L'ARXIU ENLLOC DE PASSAR-LO
	private String emisorDir3Codi;
	private Date enviamentDataProgramada;
	private EnviamentTipus enviamentTipus;
	private ServeiTipusEnum serveiTipusEnum;
	private List<DadesEnviamentDto> enviaments;
	private String procedimentCodi;
	private Integer retard;
	private String grupCodi;
	private Long documentId;
	private NotificacioEstat estat;
	private Date enviatData;
	private Boolean error;
	private String errorDescripcio;
	private Long justificantId;
	private String justificantArxiuNom;
	private Long expedientId;
	private String expedientIdentificadorLimitat;
	private Long entornId;
	private boolean entregaPostalActiva;
	private String enviamentIdentificador;
	private String enviamentReferencia;
	private Idioma idioma;
	
	private String usuariCodi;
	private String numExpedient;
}
