/**
 *
 */
package org.jbpm.graph.exe;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Transient;

import org.apache.commons.lang.StringEscapeUtils;
import org.jbpm.graph.def.Identifiable;

/**
 * Objecte de domini que representa un expedient de la instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcessInstanceExpedient implements Identifiable, Serializable {

	private long id;
	private String titol;
	private String numero;
	private Long unitatOrganitzativaId;
	private String numeroDefault;
	private Date dataInici;
	private Date dataFi;
	private ProcessInstanceEntorn entorn;
	private ProcessInstanceExpedientTipus tipus;
	private Long estatId;
	private Double geoPosX;
	private Double geoPosY;
	private String geoReferencia;
	private String registreNumero;
	private boolean anulat;
	private String errorDesc;
	private boolean errorsIntegs;
	private boolean reindexarError;
	private String processInstanceId;
	private boolean ambRetroaccio;
	private String grupCodi;
	private String errorArxiu;


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Long getUnitatOrganitzativaId() {
		return unitatOrganitzativaId;
	}
	public void setUnitatOrganitzativaId(Long unitatOrganitzativaId) {
		this.unitatOrganitzativaId = unitatOrganitzativaId;
	}
	public String getNumeroDefault() {
		return numeroDefault;
	}
	public void setNumeroDefault(String numeroDefault) {
		this.numeroDefault = numeroDefault;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public ProcessInstanceEntorn getEntorn() {
		return entorn;
	}
	public void setEntorn(ProcessInstanceEntorn entorn) {
		this.entorn = entorn;
	}
	public ProcessInstanceExpedientTipus getTipus() {
		return tipus;
	}
	public void setTipus(ProcessInstanceExpedientTipus tipus) {
		this.tipus = tipus;
	}
	public Long getEstatId() {
		return estatId;
	}
	public void setEstatId(Long estatId) {
		this.estatId = estatId;
	}
	public Double getGeoPosX() {
		return geoPosX;
	}
	public void setGeoPosX(Double geoPosX) {
		this.geoPosX = geoPosX;
	}
	public Double getGeoPosY() {
		return geoPosY;
	}
	public void setGeoPosY(Double geoPosY) {
		this.geoPosY = geoPosY;
	}
	public String getGeoReferencia() {
		return geoReferencia;
	}
	public void setGeoReferencia(String geoReferencia) {
		this.geoReferencia = geoReferencia;
	}
	public boolean isAnulat() {
		return anulat;
	}
	public void setAnulat(boolean anulat) {
		this.anulat = anulat;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	public boolean isErrorsIntegs() {
		return errorsIntegs;
	}
	public void setErrorsIntegs(boolean errorsIntegs) {
		this.errorsIntegs = errorsIntegs;
	}
	public boolean isReindexarError() {
		return reindexarError;
	}
	public void setReindexarError(boolean reindexarError) {
		this.reindexarError = reindexarError;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public boolean isAmbRetroaccio() {
		return ambRetroaccio;
	}
	public void setAmbRetroaccio(boolean ambRetroaccio) {
		this.ambRetroaccio = ambRetroaccio;
	}
	
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}	
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public String getErrorArxiu() {
		return errorArxiu;
	}
	public void setErrorArxiu(String errorArxiu) {
		this.errorArxiu = errorArxiu;
	}

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
