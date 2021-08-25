/**
 * 
 */
package es.caib.helium.logic.intf.dto;

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
	private MotorTipusEnum motorTipus;
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
	private boolean reindexacioAsincrona;
	private boolean ambInfoPropia;
	private boolean heretable;
	private Long expedientTipusPareId;
	private boolean ambHerencia;
	private String diesNoLaborables;
	private boolean notificacionsActivades;
	private String notificacioOrganCodi;
	private String notificacioOficinaCodi;
	private String notificacioUnitatAdministrativa;
	private String notibSeuCodiProcediment;
	private String notificacioCodiProcediment;
	private String notificacioAvisTitol;
	private String notificacioAvisText;
	private String notificacioAvisTextSms;
	private String notificacioOficiTitol;
	private String notificacioOficiText;

	private EntornDto entorn;
	private List<EstatDto> estats = new ArrayList<EstatDto>();
	private List<ConsultaDto> consultes;
	private Map<Integer, SequenciaAnyDto> sequenciaAny = new TreeMap<Integer, SequenciaAnyDto>();
	private Map<Integer, SequenciaDefaultAnyDto> sequenciaDefaultAny = new TreeMap<Integer, SequenciaDefaultAnyDto>();

	private int permisCount = 0;

	// integració amb forms
	private String formextUrl;
	private String formextUsuari;
	private String formextContrasenya;
	// Integració amb tràmits Sistra
	private String sistraTramitCodi;
	
	private boolean ntiActiu;
	private String ntiOrgano;
	private String ntiClasificacion;
	private String ntiSerieDocumental;

	private boolean arxiuActiu;
	
	// Integració NOTIB
	private Boolean notibActiu;
	private String notibEmisor;
	private String notibCodiProcediment;

	// Integració DISTRIBUCIO
	private boolean distribucioActiu;
	private String distribucioCodiProcediment;
	private String distribucioCodiAssumpte;
	private boolean distribucioProcesAuto;
	private boolean distribucioSistra;


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
	public MotorTipusEnum getMotorTipus() {
		return motorTipus;
	}
	public void setMotorTipus(MotorTipusEnum motorTipus) {
		this.motorTipus = motorTipus;
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
	public boolean isReindexacioAsincrona() {
		return reindexacioAsincrona;
	}
	public void setReindexacioAsincrona(boolean reindexacioAsincrona) {
		this.reindexacioAsincrona = reindexacioAsincrona;
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
	public boolean isAmbHerencia() {
		return ambHerencia;
	}
	public void setAmbHerencia(boolean ambHerencia) {
		this.ambHerencia = ambHerencia;
	}
	public String getDiesNoLaborables() {
		return diesNoLaborables;
	}
	public void setDiesNoLaborables(String diesNoLaborables) {
		this.diesNoLaborables = diesNoLaborables;
	}
	public boolean isNotificacionsActivades() {
		return notificacionsActivades;
	}
	public void setNotificacionsActivades(boolean notificacionsActivades) {
		this.notificacionsActivades = notificacionsActivades;
	}
	public String getNotificacioOrganCodi() {
		return notificacioOrganCodi;
	}
	public void setNotificacioOrganCodi(String notificacioOrganCodi) {
		this.notificacioOrganCodi = notificacioOrganCodi;
	}
	public String getNotificacioOficinaCodi() {
		return notificacioOficinaCodi;
	}
	public void setNotificacioOficinaCodi(String notificacioOficinaCodi) {
		this.notificacioOficinaCodi = notificacioOficinaCodi;
	}
	public String getNotificacioUnitatAdministrativa() {
		return notificacioUnitatAdministrativa;
	}
	public void setNotificacioUnitatAdministrativa(String notificacioUnitatAdministrativa) {
		this.notificacioUnitatAdministrativa = notificacioUnitatAdministrativa;
	}
	public String getNotificacioCodiProcediment() {
		return notificacioCodiProcediment;
	}
	public void setNotificacioCodiProcediment(String notificacioCodiProcediment) {
		this.notificacioCodiProcediment = notificacioCodiProcediment;
	}
	public String getNotificacioAvisTitol() {
		return notificacioAvisTitol;
	}
	public void setNotificacioAvisTitol(String notificacioAvisTitol) {
		this.notificacioAvisTitol = notificacioAvisTitol;
	}
	public String getNotificacioAvisText() {
		return notificacioAvisText;
	}
	public void setNotificacioAvisText(String notificacioAvisText) {
		this.notificacioAvisText = notificacioAvisText;
	}
	public String getNotificacioAvisTextSms() {
		return notificacioAvisTextSms;
	}
	public void setNotificacioAvisTextSms(String notificacioAvisTextSms) {
		this.notificacioAvisTextSms = notificacioAvisTextSms;
	}
	public String getNotificacioOficiTitol() {
		return notificacioOficiTitol;
	}
	public void setNotificacioOficiTitol(String notificacioOficiTitol) {
		this.notificacioOficiTitol = notificacioOficiTitol;
	}
	public String getNotificacioOficiText() {
		return notificacioOficiText;
	}
	public void setNotificacioOficiText(String notificacioOficiText) {
		this.notificacioOficiText = notificacioOficiText;
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

	public String getFormextUrl() {
		return formextUrl;
	}
	public void setFormextUrl(String formextUrl) {
		this.formextUrl = formextUrl;
	}
	public String getFormextUsuari() {
		return formextUsuari;
	}
	public void setFormextUsuari(String formextUsuari) {
		this.formextUsuari = formextUsuari;
	}
	public String getFormextContrasenya() {
		return formextContrasenya;
	}
	public void setFormextContrasenya(String formextContrasenya) {
		this.formextContrasenya = formextContrasenya;
	}
	public String getSistraTramitCodi() {
		return sistraTramitCodi;
	}
	public void setSistraTramitCodi(String sistraTramitCodi) {
		this.sistraTramitCodi = sistraTramitCodi;
	}
	public boolean isNtiActiu() {
		return ntiActiu;
	}
	public void setNtiActiu(boolean ntiActiu) {
		this.ntiActiu = ntiActiu;
	}
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public void setNtiOrgano(String ntiOrgano) {
		this.ntiOrgano = ntiOrgano;
	}
	public String getNtiClasificacion() {
		return ntiClasificacion;
	}
	public void setNtiClasificacion(String ntiClasificacion) {
		this.ntiClasificacion = ntiClasificacion;
	}
	public String getNtiSerieDocumental() {
		return ntiSerieDocumental;
	}
	public void setNtiSerieDocumental(String ntiSerieDocumental) {
		this.ntiSerieDocumental = ntiSerieDocumental;
	}
	public boolean isArxiuActiu() {
		return arxiuActiu;
	}
	public void setArxiuActiu(boolean arxiuActiu) {
		this.arxiuActiu = arxiuActiu;
	}

	public Boolean getNotibActiu() {
		return notibActiu;
	}
	public void setNotibActiu(Boolean notibActiu) {
		this.notibActiu = notibActiu;
	}
	public String getNotibEmisor() {
		return notibEmisor;
	}
	public void setNotibEmisor(String notibEmisor) {
		this.notibEmisor = notibEmisor;
	}
	public String getNotibCodiProcediment() {
		return notibCodiProcediment;
	}
	public void setNotibCodiProcediment(String notibCodiProcediment) {
		this.notibCodiProcediment = notibCodiProcediment;
	}
	public String getNotibSeuCodiProcediment() {
		return notibSeuCodiProcediment;
	}
	public void setNotibSeuCodiProcediment(String notibSeuCodiProcediment) {
		this.notibSeuCodiProcediment = notibSeuCodiProcediment;
	}
	public boolean isDistribucioActiu() {
		return distribucioActiu;
	}
	public void setDistribucioActiu(boolean distribucioActiu) {
		this.distribucioActiu = distribucioActiu;
	}
	public String getDistribucioCodiProcediment() {
		return distribucioCodiProcediment;
	}
	public void setDistribucioCodiProcediment(String distribucioCodiProcediment) {
		this.distribucioCodiProcediment = distribucioCodiProcediment;
	}
	
	public String getDistribucioCodiAssumpte() {
		return distribucioCodiAssumpte;
	}
	public void setDistribucioCodiAssumpte(String distribucioCodiAssumpte) {
		this.distribucioCodiAssumpte = distribucioCodiAssumpte;
	}
	public boolean isDistribucioProcesAuto() {
		return distribucioProcesAuto;
	}
	public void setDistribucioProcesAuto(boolean distribucioProcesAuto) {
		this.distribucioProcesAuto = distribucioProcesAuto;
	}
	public boolean isDistribucioSistra() {
		return distribucioSistra;
	}
	public void setDistribucioSistra(boolean distribucioSistra) {
		this.distribucioSistra = distribucioSistra;
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
