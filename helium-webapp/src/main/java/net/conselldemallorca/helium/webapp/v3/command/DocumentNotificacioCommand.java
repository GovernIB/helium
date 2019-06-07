/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusConsultaVarCommand.Creacio;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentNotificacioCommand {


	
	private List<Long> interessatsIds = new ArrayList<Long>();
	
	private String concepte;
	

	public List<Long> getInteressatsIds() {
		return interessatsIds;
	}

	public void setInteressatsIds(List<Long> interessatsIds) {
		this.interessatsIds = interessatsIds;
	}

	public String getConcepte() {
		return concepte;
	}

	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	
}
