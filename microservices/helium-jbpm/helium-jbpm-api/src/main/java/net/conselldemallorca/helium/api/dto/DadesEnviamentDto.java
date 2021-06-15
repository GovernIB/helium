/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Informació d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DadesEnviamentDto {

	public enum EntregaPostalTipus {
		NACIONAL,
		ESTRANGER,
		APARTAT_CORREUS,
		SENSE_NORMALITZAR
	}
	
	public enum EntregaPostalViaTipus {
		ALAMEDA,
		CALLE,
		CAMINO,
		CARRER,
		CARRETERA,
		GLORIETA,
		KALEA,
		PASAJE,
		PASEO,
		PLAÇA,
		PLAZA,
		RAMBLA,
		RONDA,
		RUA,
		SECTOR,
		TRAVESIA,
		URBANIZACION,
		AVENIDA,
		AVINGUDA,
		BARRIO,
		CALLEJA,
		CAMI,
		CAMPO,
		CARRERA,
		CUESTA,
		EDIFICIO,
		ENPARANTZA,
		ESTRADA,
		JARDINES,
		JARDINS,
		PARQUE,
		PASSEIG,
		PRAZA,
		PLAZUELA,
		PLACETA,
		POBLADO,
		VIA,
		TRAVESSERA,
		PASSATGE,
		BULEVAR,
		POLIGONO,
		OTROS
	}
	
	private ServeiTipusEnumDto serveiTipusEnum;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	
	private boolean entregaPostalActiva;
	private EntregaPostalTipus entregaPostalTipus;
	private EntregaPostalViaTipus  entregaPostalViaTipus;
	private String entregaPostalViaNom;
	private String entregaPostalNumeroCasa;
	private String entregaPostalNumeroQualificador;
	private String entregaPostalPuntKm;
	private String entregaPostalApartatCorreus;
	private String entregaPostalPortal;
	private String entregaPostalEscala;
	private String entregaPostalPlanta;
	private String entregaPostalPorta;
	private String entregaPostalBloc;
	private String entregaPostalComplement;
	private String entregaPostalCodiPostal;
	private String entregaPostalPoblacio;
	private String entregaPostalMunicipiCodi;
	private String entregaPostalProvinciaCodi;
	private String entregaPostalPaisCodi;
	private String entregaPostalLinea1;
	private String entregaPostalLinea2;
	private Integer entregaPostalCie;
	private String entregaPostalFormatSobre;
	private String entregaPostalFormatFulla;
	private boolean entregaDehActiva;
	private boolean entregaDehObligat;
	private String entregaDehProcedimentCodi;
	private NotificacioEnviamentEstatEnumDto estat;
	private Date estatData;

}
