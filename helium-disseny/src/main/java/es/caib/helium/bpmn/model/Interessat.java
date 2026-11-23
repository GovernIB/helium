package es.caib.helium.bpmn.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Informació d'un interessat de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class Interessat {

	private Long id;
	private String codi;
	private String documentIdent;
	private String dir3Codi;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String tipus;
	private String email;
	private String telefon;
	private String codiDesti;
	private Long expedientId;
	private boolean entregaPostal;
	private String entregaTipus;
	private String linia1;
	private String linia2;
	private String codiPostal;
	private boolean entregaDeh;
	private boolean entregaDehObligat;
	private String observacions;
	private String tipusDocIdent;
	private String codiDire;
	private String direccio;
	private String raoSocial;
	private boolean esRepresentant;
	private String municipiCodi;
	private String paisCodi;
	private String provinciaCodi;
	private String municipi;
	private String pais;
	private String provincia;
	private String canalNotif;
	private List<Interessat> representats; //només existeix quan es_representant=true
	private Interessat representant; //només existeix quan es_representant=false

}
