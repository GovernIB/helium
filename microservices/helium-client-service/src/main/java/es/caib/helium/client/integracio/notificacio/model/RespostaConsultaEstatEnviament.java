package es.caib.helium.client.integracio.notificacio.model;

import java.util.Date;

import es.caib.helium.client.integracio.notificacio.enums.EnviamentEstat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaConsultaEstatEnviament {

	private EnviamentEstat estat;
	private Date estatData;
	private String estatDescripcio;
	private String estatOrigen;
	private String receptorNif;
	private String receptorNom;
	private Date certificacioData;
	private String certificacioOrigen;
	private byte[] certificacioContingut;
	private String certificacioHash;
	private String certificacioMetadades;
	private String certificacioCsv;
	private String certificacioTipusMime;
	private boolean error;
	private String errorDescripcio;
}
