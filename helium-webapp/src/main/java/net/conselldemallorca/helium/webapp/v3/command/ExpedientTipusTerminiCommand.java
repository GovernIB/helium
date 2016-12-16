package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusTerminiCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusTerminiCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Codi;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusTermini;

@ExpedientTipusTermini(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusTerminiCommand {

	private Long expedientTipusId;
	private Long definicioProcesId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	@Codi(groups = {Creacio.class, Modificacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String descripcio;
	private boolean duradaPredefinida;
	private int anys;
	private int mesos;
	private int dies;
	private boolean laborable;
	private boolean manual = true;
	private Integer diesPrevisAvis;
	private boolean alertaPrevia;
	private boolean alertaFinal;
	private boolean alertaCompletat;

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public boolean isDuradaPredefinida() {
		return duradaPredefinida;
	}
	public void setDuradaPredefinida(boolean duradaPredefinida) {
		this.duradaPredefinida = duradaPredefinida;
	}

	public int getAnys() {
		return anys;
	}
	public void setAnys(int anys) {
		this.anys = anys;
	}

	public int getMesos() {
		return mesos;
	}
	public void setMesos(int mesos) {
		this.mesos = mesos;
	}

	public int getDies() {
		return dies;
	}
	public void setDies(int dies) {
		this.dies = dies;
	}

	public boolean isLaborable() {
		return laborable;
	}
	public void setLaborable(boolean laborable) {
		this.laborable = laborable;
	}

	public boolean isManual() {
		return manual;
	}
	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public Integer getDiesPrevisAvis() {
		return diesPrevisAvis;
	}
	public void setDiesPrevisAvis(Integer diesPrevisAvis) {
		this.diesPrevisAvis = diesPrevisAvis;
	}

	public boolean isAlertaPrevia() {
		return alertaPrevia;
	}
	public void setAlertaPrevia(boolean alertaPrevia) {
		this.alertaPrevia = alertaPrevia;
	}

	public boolean isAlertaFinal() {
		return alertaFinal;
	}
	public void setAlertaFinal(boolean alertaFinal) {
		this.alertaFinal = alertaFinal;
	}

	public boolean isAlertaCompletat() {
		return alertaCompletat;
	}
	public void setAlertaCompletat(boolean alertaCompletat) {
		this.alertaCompletat = alertaCompletat;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
