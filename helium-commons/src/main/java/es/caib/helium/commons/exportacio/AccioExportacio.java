/**
 *
 */
package es.caib.helium.commons.exportacio;

import java.io.Serializable;

import es.caib.helium.commons.dto.AccioTipusEnumDto;
import lombok.Getter;
import lombok.Setter;


/**
 * DTO amb informació d'una acció per exportar
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class AccioExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private AccioTipusEnumDto tipus;
	private String defprocBpmKey;
	private String bpmAction;
	private boolean publica;
	private boolean oculta;
	private String rols;
	private String handlerClasse;
	private String handlerDades;
	private String script;



	public AccioExportacio(
			String codi,
			String nom,
			String descripcio,
			AccioTipusEnumDto tipus,
			String defprocBpmKey,
			String bpmAction,
			String handlerClasse,
			String handlerDades,
			String script,
			boolean publica,
			boolean oculta,
			String rols) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.defprocBpmKey = defprocBpmKey;
		this.bpmAction = bpmAction;
		this.handlerClasse = handlerClasse;
		this.handlerDades = handlerDades;
		this.setScript(script);
		this.publica = publica;
		this.oculta = oculta;
		this.rols = rols;
	}

	private static final long serialVersionUID = 1L;

}
