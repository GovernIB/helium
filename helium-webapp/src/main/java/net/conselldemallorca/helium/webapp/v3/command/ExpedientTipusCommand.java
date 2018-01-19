/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipus;

/**
 * Command per editar la informació dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipus(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusCommand {

	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	private boolean ambInfoPropia;
	private boolean heretable;
	private Long expedientTipusPareId;
	private boolean teTitol;
	private boolean teNumero;
	private boolean demanaTitol;
	private boolean demanaNumero;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String expressioNumero;
	private long sequencia = 1;
	private long sequenciaDefault = 1;
	private boolean reiniciarCadaAny;
	private int anyActual = 0;
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	private String responsableDefecteCodi;
	private boolean restringirPerGrup;
	private boolean tramitacioMassiva;
	private boolean seleccionarAny;
	private boolean ambRetroaccio;
	private boolean reindexacioAsincrona;
	private String diesNoLaborables;

	private List<String> sequenciesAny = new ArrayList<String>();
	private List<String> sequenciesValor = new ArrayList<String>();

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
	public boolean isAmbInfoPropia() {
		return ambInfoPropia;
	}
	public void setAmbInfoPropia(boolean ambInfoPropia) {
		this.ambInfoPropia = ambInfoPropia;
	}
	public boolean isHeretable() {
		return heretable;
	}
	public void setHeretable(boolean heretable) {
		this.heretable = heretable;
	}
	public Long getExpedientTipusPareId() {
		return expedientTipusPareId;
	}
	public void setExpedientTipusPareId(Long expedientTipusPareId) {
		this.expedientTipusPareId = expedientTipusPareId;
	}
	public boolean isTeNumero() {
		return teNumero;
	}
	public void setTeNumero(boolean teNumero) {
		this.teNumero = teNumero;
	}
	public boolean isTeTitol() {
		return teTitol;
	}
	public void setTeTitol(boolean teTitol) {
		this.teTitol = teTitol;
	}
	public boolean isDemanaNumero() {
		return demanaNumero;
	}
	public void setDemanaNumero(boolean demanaNumero) {
		this.demanaNumero = demanaNumero;
	}
	public boolean isDemanaTitol() {
		return demanaTitol;
	}
	public void setDemanaTitol(boolean demanaTitol) {
		this.demanaTitol = demanaTitol;
	}
	public String getExpressioNumero() {
		return expressioNumero;
	}
	public void setExpressioNumero(String expressioNumero) {
		this.expressioNumero = expressioNumero;
	}
	public long getSequencia() {
		return sequencia;
	}
	public void setSequencia(long sequencia) {
		this.sequencia = sequencia;
	}
	public long getSequenciaDefault() {
		return sequenciaDefault;
	}
	public void setSequenciaDefault(long sequenciaDefault) {
		this.sequenciaDefault = sequenciaDefault;
	}
	public boolean isReiniciarCadaAny() {
		return reiniciarCadaAny;
	}
	public void setReiniciarCadaAny(boolean reiniciarCadaAny) {
		this.reiniciarCadaAny = reiniciarCadaAny;
	}
	public int getAnyActual() {
		return anyActual;
	}
	public void setAnyActual(int anyActual) {
		this.anyActual = anyActual;
	}
	public String getResponsableDefecteCodi() {
		return responsableDefecteCodi;
	}
	public void setResponsableDefecteCodi(String responsableDefecteCodi) {
		this.responsableDefecteCodi = responsableDefecteCodi;
	}
	public boolean isRestringirPerGrup() {
		return restringirPerGrup;
	}
	public void setRestringirPerGrup(boolean restringirPerGrup) {
		this.restringirPerGrup = restringirPerGrup;
	}
	public boolean isTramitacioMassiva() {
		return tramitacioMassiva;
	}
	public void setTramitacioMassiva(boolean tramitacioMassiva) {
		this.tramitacioMassiva = tramitacioMassiva;
	}
	public boolean isSeleccionarAny() {
		return seleccionarAny;
	}
	public void setSeleccionarAny(boolean seleccionarAny) {
		this.seleccionarAny = seleccionarAny;
	}
	public boolean isAmbRetroaccio() {
		return ambRetroaccio;
	}
	public void setAmbRetroaccio(boolean ambRetroaccio) {
		this.ambRetroaccio = ambRetroaccio;
	}

	public boolean isReindexacioAsincrona() {
		return reindexacioAsincrona;
	}
	public void setReindexacioAsincrona(boolean reindexacioAsincrona) {
		this.reindexacioAsincrona = reindexacioAsincrona;
	}
	public String getDiesNoLaborables() {
		return diesNoLaborables;
	}
	public void setDiesNoLaborables(String diesNoLaborables) {
		this.diesNoLaborables = diesNoLaborables;
	}
	public List<String> getSequenciesAny() {
		return sequenciesAny;
	}
	public void setSequenciesAny(List<String> sequenciesAny) {
		this.sequenciesAny = sequenciesAny;
	}

	public List<String> getSequenciesValor() {
		return sequenciesValor;
	}
	public void setSequenciesValor(List<String> sequenciesValor) {
		this.sequenciesValor = sequenciesValor;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
