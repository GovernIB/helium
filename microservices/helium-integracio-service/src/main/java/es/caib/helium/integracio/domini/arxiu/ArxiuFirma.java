package es.caib.helium.integracio.domini.arxiu;

import java.util.List;

import es.caib.helium.integracio.enums.arxiu.ArxiuFirmaPerfilEnum;
import es.caib.helium.integracio.enums.arxiu.NtiTipoFirmaEnum;
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
