package es.caib.helium.client.integracio.arxiu.model;

import es.caib.helium.client.integracio.arxiu.enums.FirmaPerfil;
import es.caib.helium.client.integracio.firma.enums.FirmaTipus;
import lombok.Data;

@Data
public class Firma {

    private FirmaTipus tipus;
    private FirmaPerfil perfil;
    private String fitxerNom;
    private byte[] contingut;
    private long tamany = -1L;
    private String tipusMime;
    private String csvRegulacio;
}
