package es.caib.helium.bpmn.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Dades d'una anotació de registre de sortida d'un document de l'expedient feta des d'un handler.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DadesRegistreSortida {

	private String entitatCodi;
	private String organOrigen;
	private String oficinaCodi;
	private String llibreCodi;
	/** Extracte. Si no s'informa, es construeix amb el número i el títol de l'expedient i el títol del document. */
	private String extracte;
	private String documentacioFisicaCodi;
	private String assumpteTipusCodi;
	private String assumpteCodi;
	/** Usuari que fa l'anotació. Si no s'informa, l'usuari actual. */
	private String usuariCodi;
	/** Idioma. Si no s'informa, català. */
	private String idiomaCodi;
	private DadesRegistreInteressat interessat;
	/** Representant de l'interessat. Només s'hi afegeix si en té el tipus. */
	private DadesRegistreInteressat representant;

	/** Codi del document de l'expedient que es registra. */
	private String documentCodi;
	private String documentValidesa;
	private String documentTipus;
	private String documentTipusDocumental;
	private Integer documentOrigen;
	private String documentObservacions;
	private Integer documentModeFirma;

}
