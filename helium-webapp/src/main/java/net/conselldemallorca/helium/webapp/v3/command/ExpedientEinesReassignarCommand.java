/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command per reassignar expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientEinesReassignarCommand {

	private Long tasca;
	private String usuari;
	private Long grup;
	private String expression;
	private Long defProcId;
	
	public Long getTasca() {
		return tasca;
	}
	public void setTasca(Long tasca) {
		this.tasca = tasca;
	}
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
	public Long getDefProcId() {
		return defProcId;
	}
	public void setDefProcId(Long defProcId) {
		this.defProcId = defProcId;
	}
}
