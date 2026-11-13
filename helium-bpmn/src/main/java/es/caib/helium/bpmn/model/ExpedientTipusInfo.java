package es.caib.helium.bpmn.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpedientTipusInfo {
	private Long id;
	private String codi;
	private String nom;
	private String bpmProcessDefinitionKey;
	private boolean teNumero;
	private boolean teTitol;
	private boolean demanaNumero;
	private boolean demanaTitol;
	private String expressioNumero;
	private long sequencia;
	private long sequenciaDefault;
	private boolean reiniciarCadaAny;
	private int anyActual;
	private String responsableDefecteCodi;
	private boolean restringirPerGrup;
	private boolean tramitacioMassiva;
	private boolean seleccionarAny;
	private boolean ambRetroaccio;
	private String tipus;
	private boolean ambInfoPropia;
	private boolean heretable;
	private boolean ambHerencia;
	private String diesNoLaborables;

	// Integració NOTIB
	private Boolean notibActiu;
	private String notibEmisor;
	private String notibCodiProcediment;

	private boolean notificacionsActivades;
	private String notificacioOrganCodi;
	private String notificacioOficinaCodi;
	private String notificacioUnitatAdministrativa;
	private String notificacioCodiProcediment;
	private String notificacioAvisTitol;
	private String notificacioAvisText;
	private String notificacioAvisTextSms;
	private String notificacioOficiTitol;
	private String notificacioOficiText;
	private boolean enviarCorreuAnotacions;
}
