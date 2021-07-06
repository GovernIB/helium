package es.caib.helium.client.integracio.registre.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DadesInteressat {

	private String entitatCodi;
	private boolean autenticat;
	private String nif;
	private String nomAmbCognoms;
	private String nom;
	private String cognom1;
	private String cognom2;
	private String paisCodi;
	private String paisNom;
	private String provinciaCodi;
	private String provinciaNom;
	private String municipiCodi;
	private String municipiNom;
	private String email;
	private String mobil;
}
