package es.caib.helium.integracio.domini.registre;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistreAnnex {

	private String titol;
	private String fitxerNom;
	private int fitxerTamany;
	private String fitxerTipusMime;
	private String fitxerArxiuUuid;
	private byte[] fitxerContingut;
	private Date dataCaptura;
	private String localitzacio;
	private String origenCiutadaAdmin;
	private String ntiTipusDocument;
	private String sicresTipusDocument;
	private String ntiElaboracioEstat;
	private String observacions;
	private Integer firmaMode;
	private String firmaFitxerNom;
	private int firmaFitxerTamany;
	private String firmaFitxerTipusMime;
	private String firmaFitxerArxiuUuid;
	private String firmaFitxerContingutBase64;
	private String firmaCsv;
	private String timestamp;
	private String validacioOCSP;
	
	private String tipusDocument;
	private String tipusDocumental;
	private Integer origen;
	private String validesa;
}
