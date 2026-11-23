package es.caib.helium.client.tramitacio.v1;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'un document d'una tasca en tramitació
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TramitacioDocument {
	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String arxiu;
	private Date data;
	private byte[] contingut;
}
