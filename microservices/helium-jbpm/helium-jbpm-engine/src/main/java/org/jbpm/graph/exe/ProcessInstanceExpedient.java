/**
 *
 */
package org.jbpm.graph.exe;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringEscapeUtils;
import org.jbpm.graph.def.Identifiable;

/**
 * Objecte de domini que representa un expedient de la instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcessInstanceExpedient implements Identifiable, Serializable {

	private long id;
	private String titol;
	private String numero;
	private String numeroDefault;
	private Date dataInici;
	private Date dataFi;
	private ProcessInstanceEntorn entorn;
	private ProcessInstanceExpedientTipus tipus;
	private Long estatId;
	private Double geoPosX;
	private Double geoPosY;
	private String geoReferencia;
	private boolean anulat;
	private String errorDesc;
	private boolean errorsIntegs;
	private boolean reindexarError;
	private String processInstanceId;
	private boolean ambRetroaccio;

	@Transient
	public String getNumeroIdentificador() {
		if (tipus.isTeNumero())
			return getNumero();
		return this.getNumeroDefault();
	}
	@Transient
	public String getIdentificador() {
		String identificador = null;
		if (tipus.isTeNumero() && tipus.isTeTitol())
			identificador = "[" + getNumero() + "] " + getTitol();
		else if (tipus.isTeNumero() && !tipus.isTeTitol())
			identificador = getNumero();
		else if (!tipus.isTeNumero() && tipus.isTeTitol())
			identificador = getTitol();
		if (identificador == null)
			return this.getNumeroDefault();
		else
			return identificador;
	}
	@Transient
	public String getIdentificadorLimitat() {
		if (getIdentificador() != null && getIdentificador().length() > 100)
			return StringEscapeUtils.escapeHtml(getIdentificador().substring(0, 100) + " (...)");
		else
			return StringEscapeUtils.escapeHtml(getIdentificador());
	}
	@Transient
	public String getIdPerOrdenacio() {
		if (!tipus.isTeNumero() && tipus.isTeTitol()) {
			return getIdentificador();
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici);
			int anyInici = cal.get(Calendar.YEAR);
			return new Integer(anyInici).toString() + new DecimalFormat("0000000000000000000").format(id);
		}
	}

	private static final long serialVersionUID = 1L;

}
