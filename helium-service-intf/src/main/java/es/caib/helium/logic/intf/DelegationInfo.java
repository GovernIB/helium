/**
 * 
 */
package es.caib.helium.logic.intf;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe que conté informació sobre la delegació d'una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DelegationInfo implements Serializable {

	private String sourceTaskId;
	private String targetTaskId;
	private Date start;
	private Date end;
	private String comment;
	private boolean supervised;
	private String usuariDelegador;
	private String usuariDelegat;


	public String getSourceTaskId() {
		return sourceTaskId;
	}
	public void setSourceTaskId(String sourceTaskId) {
		this.sourceTaskId = sourceTaskId;
	}
	public String getTargetTaskId() {
		return targetTaskId;
	}
	public void setTargetTaskId(String targetTaskId) {
		this.targetTaskId = targetTaskId;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public boolean isSupervised() {
		return supervised;
	}
	public void setSupervised(boolean supervised) {
		this.supervised = supervised;
	}
	public String getUsuariDelegador() {
		return usuariDelegador;
	}
	public void setUsuariDelegador(String usuariDelegador) {
		this.usuariDelegador = usuariDelegador;
	}
	public String getUsuariDelegat() {
		return usuariDelegat;
	}
	public void setUsuariDelegat(String usuariDelegat) {
		this.usuariDelegat = usuariDelegat;
	}

	private static final long serialVersionUID = 774909297938469787L;

}
