/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Acció realitzada sobre una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IntegracioAccioDto implements Serializable {

	private String integracioCodi;
	private Long entornId;
	private Long index;
	private Date data;
	private String descripcio;
	private long tempsResposta;
	private List<IntegracioParametreDto> parametres;
	private IntegracioAccioTipusEnumDto tipus;
	private IntegracioAccioEstatEnumDto estat;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;



	public String getIntegracioCodi() {
		return integracioCodi;
	}
	public void setIntegracioCodi(String integracioCodi) {
		this.integracioCodi = integracioCodi;
	}
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public Long getIndex() {
		return index;
	}
	public void setIndex(Long index) {
		this.index = index;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public long getTempsResposta() {
		return tempsResposta;
	}
	public void setTempsResposta(long tempsResposta) {
		this.tempsResposta = tempsResposta;
	}
	public List<IntegracioParametreDto> getParametres() {
		return parametres;
	}
	public void setParametres(List<IntegracioParametreDto> parametres) {
		this.parametres = parametres;
	}
	public IntegracioAccioTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(IntegracioAccioTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public IntegracioAccioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(IntegracioAccioEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public String getExcepcioMessage() {
		return excepcioMessage;
	}
	public void setExcepcioMessage(String excepcioMessage) {
		this.excepcioMessage = excepcioMessage;
	}
	public String getExcepcioStacktrace() {
		return excepcioStacktrace;
	}
	public void setExcepcioStacktrace(String excepcioStacktrace) {
		this.excepcioStacktrace = excepcioStacktrace;
	}

	public boolean isEstatOk() {
		return IntegracioAccioEstatEnumDto.OK.equals(estat);
	}
	public boolean isEstatError() {
		return IntegracioAccioEstatEnumDto.ERROR.equals(estat);
	}
	public int getParametresCount() {
		if (parametres == null) {
			return 0;
		} else {
			return parametres.size();
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
