package es.caib.helium.client.integracio.registre.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistreAssentament {

	private int numero;
	private Date data;
	private String identificador;
	private String entitatCodi;
	private String organ;
	private String organDescripcio;
	private String oficinaCodi;
	private String oficinaDescripcio;
	private String llibreCodi;
	private String llibreDescripcio;
	private String extracte;
	private String assumpteTipusCodi;
	private String assumpteTipusDescripcio;
	private String assumpteCodi;
	private String assumpteDescripcio;
	private String referencia;
	private String expedientNumero;
	private String idiomaCodi;
	private String idiomaDescripcio;
	private String transportTipusCodi;
	private String transportTipusDescripcio;
	private String transportNumero;
	private String usuariCodi;
	private String usuariNom;
	private String usuariContacte;
	private String aplicacioCodi;
	private String aplicacioVersio;
	private String documentacioFisicaCodi;
	private String documentacioFisicaDescripcio;
	private String observacions;
	private String exposa;
	private String solicita;
	private List<RegistreAssentamentInteressat> interessats;
	private List<DocumentRegistre> documents;

}
