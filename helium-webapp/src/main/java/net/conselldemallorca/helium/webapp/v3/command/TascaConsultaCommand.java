/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import javax.validation.constraints.AssertTrue;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Command per a realitzar consultes d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaConsultaCommand {

	private String titol;
	private String tasca;
	private String expedient;
	private Long expedientTipusId;
	private Integer prioritat;
	private Long estatId;
	private String responsable;
	
	private Date dataCreacioInicial;
	private Date dataCreacioFinal;
	private Date dataLimitInicial;
	private Date dataLimitFinal;
	
	private boolean nomesMeves;
	private boolean nomesTasquesPersonals;
	private boolean nomesTasquesGrup;
	
	private boolean filtreDesplegat = false;
	private boolean consultaRealitzada = false;
	private String consultaTramitacioMassivaTascaId;

	public TascaConsultaCommand() {
	}
	public String getResponsable() {
		return responsable;
	}
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getEstatId() {
		return estatId;
	}
	public void setEstatId(Long estatId) {
		this.estatId = estatId;
	}
	public boolean isFiltreDesplegat() {
		return filtreDesplegat;
	}
	public void setFiltreDesplegat(boolean filtreDesplegat) {
		this.filtreDesplegat = filtreDesplegat;
	}
	public boolean isConsultaRealitzada() {
		return consultaRealitzada;
	}
	public void setConsultaRealitzada(boolean consultaRealitzada) {
		this.consultaRealitzada = consultaRealitzada;
	}
	public String getTasca() {
		return tasca;
	}
	public void setTasca(String tasca) {
		this.tasca = tasca;
	}
	public String getExpedient() {
		return expedient;
	}
	public void setExpedient(String expedient) {
		this.expedient = expedient;
	}
	public Integer getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(Integer prioritat) {
		this.prioritat = prioritat;
	}
	public boolean isNomesMeves() {
		return nomesMeves;
	}
	public void setNomesMeves(boolean nomesMeves) {
		this.nomesMeves = nomesMeves;
	}
	public boolean isNomesTasquesPersonals() {
		return nomesTasquesPersonals;
	}
	public void setNomesTasquesPersonals(boolean nomesTasquesPersonals) {
		this.nomesTasquesPersonals = nomesTasquesPersonals;
	}
	public boolean isNomesTasquesGrup() {
		return nomesTasquesGrup;
	}
	public void setNomesTasquesGrup(boolean nomesTasquesGrup) {
		this.nomesTasquesGrup = nomesTasquesGrup;
	}
	public Date getDataCreacioInicial() {
		return dataCreacioInicial;
	}
	public void setDataCreacioInicial(Date dataCreacioInicial) {
		this.dataCreacioInicial = dataCreacioInicial;
	}
	public Date getDataCreacioFinal() {
		return dataCreacioFinal;
	}
	public void setDataCreacioFinal(Date dataCreacioFinal) {
		this.dataCreacioFinal = dataCreacioFinal;
	}
	public Date getDataLimitInicial() {
		return dataLimitInicial;
	}
	public void setDataLimitInicial(Date dataLimitInicial) {
		this.dataLimitInicial = dataLimitInicial;
	}
	public Date getDataLimitFinal() {
		return dataLimitFinal;
	}
	public void setDataLimitFinal(Date dataLimitFinal) {
		this.dataLimitFinal = dataLimitFinal;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getConsultaTramitacioMassivaTascaId() {
		return consultaTramitacioMassivaTascaId;
	}
	public void setConsultaTramitacioMassivaTascaId(String consultaTramitacioMassivaTascaId) {
		this.consultaTramitacioMassivaTascaId = consultaTramitacioMassivaTascaId;
	}
	@AssertTrue
	public boolean isValidRangDataCreacio() {
		if (dataCreacioInicial == null || dataCreacioFinal == null)
			return true;
		return dataCreacioFinal.compareTo(dataCreacioInicial) >= 0;
	}
	@AssertTrue
	public boolean isValidRangDataLimit() {
		if (dataLimitInicial == null || dataLimitFinal == null)
			return true;
		return dataLimitFinal.compareTo(dataLimitInicial) >= 0;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
