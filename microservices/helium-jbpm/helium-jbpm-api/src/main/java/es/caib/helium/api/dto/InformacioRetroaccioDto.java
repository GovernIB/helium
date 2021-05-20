/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * DTO per als logs de l'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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

}
