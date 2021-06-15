/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * DTO amb informació d'un tràmit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class TramitDto {

	public enum TramitAutenticacioTipusDto {
		CERTIFICAT,
		USUARI,
	    ANONIMA
	}

	protected String numero;
	protected long clauAcces;
	protected String identificador;
	protected long unitatAdministrativa;
	protected int versio;
	protected Date data;
	protected String idioma;
	protected String registreNumero;
	protected Date registreData;
	protected String preregistreTipusConfirmacio;
	protected String preregistreNumero;
	protected Date preregistreData;
	protected TramitAutenticacioTipusDto autenticacioTipus;
	protected String tramitadorNif;
	protected String tramitadorNom;
	protected String interessatNif;
	protected String interessatNom;
	protected String representantNif;
	protected String representantNom;
	protected boolean signat;
	protected boolean avisosHabilitats;
	protected String avisosSms;
	protected String avisosEmail;
	protected boolean notificacioTelematicaHabilitada;
	protected List<TramitDocumentDto> documents;

}
