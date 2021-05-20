/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Objecte de domini que representa una camp d'una consulta d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
public class ConsultaCampDto implements Serializable {

	public enum TipusConsultaCamp {
		FILTRE,
		INFORME,
		PARAM
	}
	public enum TipusParamConsultaCamp {
		TEXT,
		SENCER,
		FLOTANT,
		DATA,
		BOOLEAN
	}
	
	private Long id;
	private String campCodi;
	private String campEtiqueta;
	private String campDescripcio;
	private String defprocJbpmKey;
	private int defprocVersio = -1;
	private TipusConsultaCamp tipus;
	private int ordre;
	private int ampleCols;
	private int buitCols;
	private TipusParamConsultaCamp paramTipus;

	private CampTipusDto campTipus;

	public ConsultaCampDto(String campCodi, TipusConsultaCamp tipus) {
		this.campCodi = campCodi;
		this.tipus = tipus;
	}

	/** Retorna la combinacio "codi / etiqueta" si no comença amb el prefix de l'expedient. */
	public String getCodiEtiqueta() {
		if (campCodi.startsWith(ExpedientCampsDto.EXPEDIENT_PREFIX))
			return campEtiqueta != null? campEtiqueta : campCodi;
		else
			return campCodi + (campEtiqueta != null? " / " + campEtiqueta : "");		
	}

	private static final long serialVersionUID = 1L;
}
