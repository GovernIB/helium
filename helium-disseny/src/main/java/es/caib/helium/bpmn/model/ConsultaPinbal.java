package es.caib.helium.bpmn.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Dades d'una consulta a PINBAL feta des d'un handler.
 *
 * <p>El titular es pot indicar amb el codi d'un interessat de l'expedient ({@code interessatCodi}) o amb les seves
 * dades. El justificant es desa al document {@code documentCodi}. Si la consulta és asíncrona, el procés espera la
 * resposta en una <i>receive task</i> després de la tasca del handler i continua per {@code transicioOK} quan
 * arriba.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ConsultaPinbal {

	/** Codi del servei. Si és null, el del handler específic. */
	private String serveiCodi;
	/** Codi del document on es desa el justificant. */
	private String documentCodi;
	private String finalitat;
	private String consentiment;
	/** Codi d'un interessat de l'expedient que fa de titular. */
	private String interessatCodi;
	private String titularTipusDocumentacio;
	private String titularDocumentacio;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	/** Nom complet del titular (no tots els serveis l'accepten). */
	private String titularNomComplet;
	/** XML de les dades específiques del servei (consulta genèrica). */
	private String dadesEspecifiques;
	private String anyNaixement;
	private boolean asincrona;
	private String transicioOK;
	private String transicioKO;

}
