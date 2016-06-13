/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * DTO amb informació d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusDto extends ControlPermisosDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String jbpmProcessDefinitionKey;
	private boolean teNumero;
	private boolean teTitol;
	private boolean demanaNumero;
	private boolean demanaTitol;
	private String expressioNumero;
	private long sequencia = 1;
	private long sequenciaDefault = 1;
	private boolean reiniciarCadaAny;
	private int anyActual = 0;
	private String responsableDefecteCodi;
	private boolean restringirPerGrup;
	private boolean tramitacioMassiva;
	private boolean seleccionarAny;
	private boolean ambRetroaccio;

	private EntornDto entorn;
	private List<EstatDto> estats = new ArrayList<EstatDto>();
	private List<ConsultaDto> consultes;
	private Map<Integer, SequenciaAnyDto> sequenciaAny = new TreeMap<Integer, SequenciaAnyDto>();
	private Map<Integer, SequenciaDefaultAnyDto> sequenciaDefaultAny = new TreeMap<Integer, SequenciaDefaultAnyDto>();

	private int permisCount = 0;



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
	public String getJbpmProcessDefinitionKey() {
		return jbpmProcessDefinitionKey;
	}
	public void setJbpmProcessDefinitionKey(String jbpmProcessDefinitionKey) {
		this.jbpmProcessDefinitionKey = jbpmProcessDefinitionKey;
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
	public EntornDto getEntorn() {
		return entorn;
	}
	public void setEntorn(EntornDto entorn) {
		this.entorn = entorn;
	}
	public List<EstatDto> getEstats() {
		return this.estats;
	}
	public void setEstats(List<EstatDto> estats) {
		this.estats = estats;
	}
	public List<ConsultaDto> getConsultes() {
		if (consultes == null)
			consultes = new ArrayList<ConsultaDto>();
		return consultes;
	}
	public void setConsultes(List<ConsultaDto> consultes) {
		this.consultes = consultes;
	}
	public Map<Integer, SequenciaAnyDto> getSequenciaAny() {
		return sequenciaAny;
	}
	public void setSequenciaAny(Map<Integer, SequenciaAnyDto> sequenciaAny) {
		this.sequenciaAny = sequenciaAny;
	}
	public Map<Integer, SequenciaDefaultAnyDto> getSequenciaDefaultAny() {
		return sequenciaDefaultAny;
	}
	public void setSequenciaDefaultAny(Map<Integer, SequenciaDefaultAnyDto> sequenciaDefaultAny) {
		this.sequenciaDefaultAny = sequenciaDefaultAny;
	}
	public int getPermisCount() {
		return permisCount;
	}
	public void setPermisCount(int permisCount) {
		this.permisCount = permisCount;
	}

	public List<ConsultaDto> getConsultesSort() {
		if (consultes == null)
			consultes = new ArrayList<ConsultaDto>();
		Collections.sort(consultes);
		return consultes;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpedientTipusDto other = (ExpedientTipusDto) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = 4990928454645567913L;

}
