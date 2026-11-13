/**
 *
 */
package es.caib.helium.bpmn.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una notificació per al seu enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DadesNotificacio {
	private Date caducitat;
	private String concepte;
	private String serveiTipus;
	private String grupCodi;
	private String descripcio;
	private Long documentId;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String emisorDir3Codi;
	private String documentArxiuUuid;
	private Date enviamentDataProgramada;
	public enum EnviamentTipus {
		NOTIFICACIO,
		COMUNICACIO
	}
	private EnviamentTipus enviamentTipus;
	private List<DadesEnviament> enviaments;
	private String procedimentCodi;
	private Integer retard;
	public enum Idioma {
		ES,
		CA
	}
	private Idioma idioma;
}
