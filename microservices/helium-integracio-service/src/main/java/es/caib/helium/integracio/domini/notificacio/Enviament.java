package es.caib.helium.integracio.domini.notificacio;

import java.util.List;

import es.caib.helium.integracio.enums.notificacio.EntregaPostalTipus;
import es.caib.helium.integracio.enums.notificacio.EntregaPostalViaTipus;
import es.caib.helium.integracio.enums.notificacio.ServeiTipusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Enviament {

	private List<PersonaNotib> destinataris;

	private boolean entregaDehActiva;
	private boolean entregaDehObligat;
	private String entregaDehProcedimentCodi;
	
	private ServeiTipusEnum serveiTipusEnum;
	
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
	
	private PersonaNotib titular;

}
