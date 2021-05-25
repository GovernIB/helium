/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.DominiDto.TipusDomini;

/**
 * Objecte per definir una consulta a domini. Serveix per fer
 * la consulta massiva a diferents valors de dominis a la vegada.
 * També conté un mètode per crear un identificador a partir de l'id de domini,
 * l'identificador i els paràmetres per no repetir les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaDominiDto extends HeretableDto implements Serializable {

	/** Referència a l'objecte per fixar el valor text. Pot ser una instància de DadaIndexadaDto, ExpedientDadaDto o TascaDadaDto.*/
	private List<Object> dadesDto = new ArrayList<Object>();
	
	/** Dades del domini per fer la petició */
	private Long campId;
	private String campCodi;
	/** Indica la columna amb el text. */
	private String campText;
	/** Tipus de camp */
	private CampTipusDto campTipus;
	/** Valor per si s'ha de passar al domini. */
	private Object valor;
	
	/** Per realitzar la consulta */
	private Long dominiId;
	private String dominiWsId;	
	private TipusDomini dominiTipus;
	
	private Map<String, Object> parametres;

	
	/** Construeix un identificador a partir de les hash internes del dominiId, dominiWsId i 
	 * dels paràmetres.
	 * 
	 * @return
	 */
	public int getIdentificadorConsulta() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dominiId == null) ? 0 : dominiId.hashCode());
		result = prime * result + ((dominiWsId == null) ? 0 : dominiWsId.hashCode());
		if (parametres != null) {
			for (String key : parametres.keySet()) {
				result = prime * result + key.hashCode();
				result = prime * result + ((parametres.get(key) == null) ? 0 : parametres.get(key).hashCode());
			}
		}
		result = prime * result + ((campCodi == null) ? 0 : campCodi.hashCode());
		result = prime * result + ((campText == null) ? 0 : campText.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;

	}
	
	
	
	public Long getCampId() {
		return campId;
	}

	public void setCampId(Long campId) {
		this.campId = campId;
	}

	public String getCampCodi() {
		return campCodi;
	}

	public void setCampCodi(String campCodi) {
		this.campCodi = campCodi;
	}

	public String getCampText() {
		return campText;
	}

	public void setCampText(String campText) {
		this.campText = campText;
	}

	public CampTipusDto getCampTipus() {
		return this.campTipus;
	}
	public void setCampTipus(CampTipusDto tipus) {
		this.campTipus = tipus;
	}

	
	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public Long getDominiId() {
		return dominiId;
	}

	public void setDominiId(Long dominiId) {
		this.dominiId = dominiId;
	}

	public String getDominiWsId() {
		return dominiWsId;
	}

	public void setDominiWsId(String dominiWsId) {
		this.dominiWsId = dominiWsId;
	}
	
	public TipusDomini getDominiTipus() {
		return this.dominiTipus;
	}
	public void setDominiTipus(TipusDomini dominiTipus) {
		this.dominiTipus = dominiTipus;
	}

	public Map<String, Object> getParametres() {
		return parametres;
	}

	public void setParametres(Map<String, Object> parametres) {
		this.parametres = parametres;
	}
	
	public List<Object> getDadesDto() {
		return this.dadesDto;
	}

	private static final long serialVersionUID = 1L;
}
