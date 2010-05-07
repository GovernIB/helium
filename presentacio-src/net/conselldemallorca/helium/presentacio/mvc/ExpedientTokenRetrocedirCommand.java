/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command per a la reassignació de tasques
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientTokenRetrocedirCommand {

	private String tokenId;
	private String nodeName;
	private boolean cancelTasks;



	public ExpedientTokenRetrocedirCommand() {}

	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public boolean isCancelTasks() {
		return cancelTasks;
	}
	public void setCancelTasks(boolean cancelTasks) {
		this.cancelTasks = cancelTasks;
	}

}
