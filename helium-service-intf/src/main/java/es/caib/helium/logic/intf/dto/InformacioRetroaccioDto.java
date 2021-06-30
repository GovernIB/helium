/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Date;


/**
 * DTO per als logs de l'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformacioRetroaccioDto {

	private Long id;
	private Date data;
	private String usuari;
	private String accioTipus;
	private String accioParams;
	private String estat;
	private String targetId;
	private String tokenName;
	
	private boolean targetTasca;
	private boolean targetProces;
	private boolean targetExpedient;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public String getAccioTipus() {
		return accioTipus;
	}
	public void setAccioTipus(String accioTipus) {
		this.accioTipus = accioTipus;
	}
	public String getAccioParams() {
		if (accioParams != null)
			return accioParams.replaceAll(",", ", ");
		return null;
	}
	public void setAccioParams(String accioParams) {
		if(accioParams!=null){
			if(accioParams.length()>2047){
				this.accioParams = accioParams.substring(0, 2047);
			}else{
				this.accioParams = accioParams;
			}
		}
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getTokenName() {
		return tokenName;
	}
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
	public boolean isTargetTasca() {
		return targetTasca;
	}
	public void setTargetTasca(boolean targetTasca) {
		this.targetTasca = targetTasca;
	}
	public boolean isTargetProces() {
		return targetProces;
	}
	public void setTargetProces(boolean targetProces) {
		this.targetProces = targetProces;
	}
	public boolean isTargetExpedient() {
		return targetExpedient;
	}
	public void setTargetExpedient(boolean targetExpedient) {
		this.targetExpedient = targetExpedient;
	}

}
