/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import net.conselldemallorca.helium.api.service.WorkflowRetroaccioApi.ExpedientRetroaccioEstat;
import net.conselldemallorca.helium.api.service.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import lombok.Data;

import java.util.Date;


/**
 * DTO per als logs de l'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ExpedientLogDto {

	private Long id;
	private Date data;
	private String usuari;
	private ExpedientRetroaccioTipus accioTipus;
	private String accioParams;
	private ExpedientRetroaccioEstat estat;
	private String targetId;
	private String tokenName;
	private Long jbpmLogId;
	private Long expedientId;
	private String processInstanceId;
	private Long iniciadorRetroces;
	
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
