package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command per reasignacio de tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReassignacioTasquesCommand {
	private String usuari;
	private Long grup;
	private String expression;
	
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public Long getGrup() {
		return grup;
	}
	public void setGrup(Long grup) {
		this.grup = grup;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
}
