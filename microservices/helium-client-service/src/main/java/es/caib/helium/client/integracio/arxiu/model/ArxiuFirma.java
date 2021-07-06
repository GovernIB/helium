package es.caib.helium.client.integracio.arxiu.model;

import java.util.List;

import es.caib.helium.client.integracio.arxiu.enums.ArxiuFirmaPerfilEnum;
import es.caib.helium.client.integracio.arxiu.enums.NtiTipoFirmaEnum;
import lombok.Data;

@Data
public class ArxiuFirma {

	private NtiTipoFirmaEnum tipus;
	private ArxiuFirmaPerfilEnum perfil;
	private String fitxerNom;
	private byte[] contingut;
	private String tipusMime;
	private String csvRegulacio;
	private boolean autofirma;
	private List<ArxiuFirmaDetall> detalls;
}
