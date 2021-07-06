package es.caib.helium.client.integracio.registre.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentRegistre {

	private String nom;
	private Date data;
	private String idiomaCodi;
	private String arxiuNom;
	private byte[] arxiuContingut;
	
	private String tipusDocument;
	private String tipusDocumental;
	private Integer origen;
	private Integer modeFirma;
	private String observacions;
	private String validesa;
}
