/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
@Getter @Setter
@EqualsAndHashCode(of = {"codi"})
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


	public List<ConsultaDto> getConsultes() {
		if (consultes == null)
			consultes = new ArrayList<ConsultaDto>();
		return consultes;
	}

	public List<ConsultaDto> getConsultesSort() {
		if (consultes == null)
			consultes = new ArrayList<ConsultaDto>();
		Collections.sort(consultes);
		return consultes;
	}

	private static final long serialVersionUID = 4990928454645567913L;
}
