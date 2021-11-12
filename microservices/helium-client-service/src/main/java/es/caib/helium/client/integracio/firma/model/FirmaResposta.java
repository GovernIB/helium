package es.caib.helium.client.integracio.firma.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació de la resposta de la firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class FirmaResposta {

	private byte[] contingut;
	private String nom;
	private String mime;
	private String tipusFirma;
	private String tipusFirmaEni;
	private String perfilFirmaEni;
}
