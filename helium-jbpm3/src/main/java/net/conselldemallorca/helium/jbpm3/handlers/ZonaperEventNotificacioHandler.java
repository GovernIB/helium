package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreSortida;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;

import org.dom4j.DocumentHelper;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class ZonaperEventNotificacioHandler extends BasicActionHandler implements RegistreSortidaHandlerInterface {
	
	private String oficina;
	private String varOficina;
	private String oficinaFisica;
	private String varOficinaFisica;
	private String remitentCodiEntitat;
	private String varRemitentCodiEntitat;
	private String remitentNomEntitat;
	private String varRemitentNomEntitat;
	private String remitentCodiGeografic;
	private String varRemitentCodiGeografic;
	private String remitentNomGeografic;
	private String varRemitentNomGeografic;
	private String remitentRegistreNumero;
	private String varRemitentRegistreNumero;
	private String remitentRegistreAny;
	private String varRemitentRegistreAny;
	private String destinatariCodiEntitat;
	private String varDestinatariCodiEntitat;
	private String destinatariNomEntitat;
	private String varDestinatariNomEntitat;
	private String destinatariCodiGeografic;
	private String varDestinatariCodiGeografic;
	private String destinatariNomGeografic;
	private String varDestinatariNomGeografic;
	private String destinatariRegistreNumero;
	private String varDestinatariRegistreNumero;
	private String destinatariRegistreAny;
	private String varDestinatariRegistreAny;
	private String documentTipus;
	private String varDocumentTipus;
	private String documentIdiomaDocument;
	private String varDocumentIdiomaDocument;
	private String documentIdiomaExtracte;
	private String varDocumentIdiomaExtracte;
	private String varDocument;
	private String varNumeroRegistre;
	private String varNumeroAnyRegistre;
	private String varDataRegistre;
	private String varData;
	private String varReferenciaRDSJustificanteClave;
	private String varReferenciaRDSJustificanteCodigo;

	private String notificacioOficiTitol;
	private String varNotificacioOficiTitol;
	private String notificacioOficiText;
	private String varNotificacioOficiText;
	
	private String notificacioSubsanacioTramitIdentificador;
	private String varNotificacioSubsanacioTramitIdentificador;
	private int notificacioSubsanacioTramitVersio;
	private String varNotificacioSubsanacioTramitVersio;
	private String notificacioSubsanacioTramitDescripcio;
	private String varNotificacioSubsanacioTramitDescripcio;
	
	public void execute(ExecutionContext executionContext) throws Exception {
		if (!Jbpm3HeliumBridge.getInstanceService().isRegistreActiu())
			throw new JbpmException("El plugin de registre no està configurat");
		if (varDocument == null || varDocument.length() == 0)
			throw new JbpmException("És obligatori especificar un document per registrar");
		DadesRegistreNotificacio anotacio = new DadesRegistreNotificacio();
		anotacio.setOrganCodi((String)getValorOVariable(
				executionContext,
				remitentCodiEntitat,
				varRemitentCodiEntitat));
		anotacio.setOficinaCodi((String)getValorOVariable(
				executionContext,
				oficina,
				varOficina) + "-" + (String)getValorOVariable(
						executionContext,
						oficinaFisica,
						varOficinaFisica));
		anotacio.setInteressatEntitatCodi(
				(String)getValorOVariable(
						executionContext,
						destinatariCodiEntitat,
						varDestinatariCodiEntitat));
		anotacio.setInteressatNomAmbCognoms(
				(String)getValorOVariable(
						executionContext,
						destinatariNomEntitat,
						varDestinatariNomEntitat));
		anotacio.setInteressatMunicipiCodi(
				(String)getValorOVariable(
						executionContext,
						destinatariCodiGeografic,
						varDestinatariCodiGeografic));
		anotacio.setInteressatMunicipiNom(
				(String)getValorOVariable(
						executionContext,
						destinatariNomGeografic,
						varDestinatariNomGeografic));
		String idiomaExtracte = (String)getValorOVariable(
				executionContext,
				documentIdiomaExtracte,
				varDocumentIdiomaExtracte);
		anotacio.setAnotacioIdiomaCodi(
				(idiomaExtracte != null) ? idiomaExtracte : "ca");
		anotacio.setAnotacioTipusAssumpte(
				(String)getValorOVariable(
						executionContext,
						documentTipus,
						varDocumentTipus));
		
		DocumentInfo documentInfo = null;
		List<DocumentInfo> annexos = new ArrayList<DocumentInfo>();
		if (varDocument != null && !varDocument.isEmpty()) {
			documentInfo = getDocumentInfo(
				executionContext,
				varDocument,
				true);
			ExpedientDto expedient = getExpedientActual(executionContext);
			anotacio.setAnotacioAssumpte(
					expedient.getIdentificador() + ": " + documentInfo.getTitol());
			annexos.add(documentInfo);
		}
		
		anotacio.setNotificacioOficiText(
				(String)getValorOVariable(
						executionContext,
						notificacioOficiText,
						varNotificacioOficiText));
		
		anotacio.setNotificacioOficiTitol(
				(String)getValorOVariable(
						executionContext,
						notificacioOficiTitol,
						varNotificacioOficiTitol));
		
		if (varNotificacioSubsanacioTramitIdentificador != null && !varNotificacioSubsanacioTramitIdentificador.isEmpty()) {
			anotacio.setNotificacioSubsanacioTramitIdentificador(
					(String)getValorOVariable(
							executionContext,
							notificacioSubsanacioTramitIdentificador,
							varNotificacioSubsanacioTramitIdentificador));
			
			anotacio.setNotificacioSubsanacioTramitVersio(
					getValorOVariableInteger(
							executionContext,
							notificacioSubsanacioTramitVersio,
							varNotificacioSubsanacioTramitVersio));
			
			anotacio.setNotificacioSubsanacioTramitDescripcio(
					(String)getValorOVariable(
							executionContext,
							notificacioSubsanacioTramitDescripcio,
							varNotificacioSubsanacioTramitDescripcio));
		}
		
		RespostaRegistre resposta = registreNotificacio(
				executionContext,
				anotacio,
				annexos);
		if (documentInfo != null) {
			Jbpm3HeliumBridge.getInstanceService().documentExpedientGuardarDadesRegistre(
					documentInfo.getId(),
					resposta.getNumero(),
					resposta.getData(),
					anotacio.getOficinaCodi(),
					Jbpm3HeliumBridge.getInstanceService().getRegistreOficinaNom(anotacio.getOficinaCodi()),
					false);
		}
		if (varNumeroAnyRegistre != null)
			executionContext.setVariable(
					varNumeroAnyRegistre,
					resposta.getNumero());
		if (varNumeroRegistre != null)
			executionContext.setVariable(
					varNumeroRegistre,
					resposta.getNumero());
		if (varDataRegistre != null)
			executionContext.setVariable(
					varDataRegistre,
					resposta.getData());
		if (varData != null)
			executionContext.setVariable(
					varData,
					resposta.getData());				
		if (varReferenciaRDSJustificanteClave != null)
			executionContext.setVariable(
					varReferenciaRDSJustificanteClave,
					resposta.getReferenciaRDSJustificante().getClave());
		if (varReferenciaRDSJustificanteCodigo != null)
			executionContext.setVariable(
					varReferenciaRDSJustificanteCodigo,
					resposta.getReferenciaRDSJustificante().getCodigo());
	}

	public void setNotificacioOficiTitol(String notificacioOficiTitol) {
		this.notificacioOficiTitol = notificacioOficiTitol;
	}
	public void setVarNotificacioOficiTitol(String varNotificacioOficiTitol) {
		this.varNotificacioOficiTitol = varNotificacioOficiTitol;
	}
	public void setNotificacioOficiText(String notificacioOficiText) {
		this.notificacioOficiText = notificacioOficiText;
	}
	public void setVarNotificacioOficiText(String varNotificacioOficiText) {
		this.varNotificacioOficiText = varNotificacioOficiText;
	}
	public void setNotificacioSubsanacioTramitIdentificador(String notificacioSubsanacioTramitIdentificador) {
		this.notificacioSubsanacioTramitIdentificador = notificacioSubsanacioTramitIdentificador;
	}
	public void setVarNotificacioSubsanacioTramitIdentificador(String varNotificacioSubsanacioTramitIdentificador) {
		this.varNotificacioSubsanacioTramitIdentificador = varNotificacioSubsanacioTramitIdentificador;
	}
	public void setNotificacioSubsanacioTramitVersio(int notificacioSubsanacioTramitVersio) {
		this.notificacioSubsanacioTramitVersio = notificacioSubsanacioTramitVersio;
	}
	public void setVarNotificacioSubsanacioTramitVersio(String varNotificacioSubsanacioTramitVersio) {
		this.varNotificacioSubsanacioTramitVersio = varNotificacioSubsanacioTramitVersio;
	}
	public void setNotificacioSubsanacioTramitDescripcio(String notificacioSubsanacioTramitDescripcio) {
		this.notificacioSubsanacioTramitDescripcio = notificacioSubsanacioTramitDescripcio;
	}
	public void setVarNotificacioSubsanacioTramitDescripcio(String varNotificacioSubsanacioTramitDescripcio) {
		this.varNotificacioSubsanacioTramitDescripcio = varNotificacioSubsanacioTramitDescripcio;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public void setVarOficina(String varOficina) {
		this.varOficina = varOficina;
	}
	public void setOficinaFisica(String oficinaFisica) {
		this.oficinaFisica = oficinaFisica;
	}
	public void setVarOficinaFisica(String varOficinaFisica) {
		this.varOficinaFisica = varOficinaFisica;
	}
	public void setRemitentCodiEntitat(String remitentCodiEntitat) {
		this.remitentCodiEntitat = remitentCodiEntitat;
	}
	public void setVarRemitentCodiEntitat(String varRemitentCodiEntitat) {
		this.varRemitentCodiEntitat = varRemitentCodiEntitat;
	}
	public void setRemitentNomEntitat(String remitentNomEntitat) {
		this.remitentNomEntitat = remitentNomEntitat;
	}
	public void setVarRemitentNomEntitat(String varRemitentNomEntitat) {
		this.varRemitentNomEntitat = varRemitentNomEntitat;
	}
	public void setRemitentCodiGeografic(String remitentCodiGeografic) {
		this.remitentCodiGeografic = remitentCodiGeografic;
	}
	public void setVarRemitentCodiGeografic(String varRemitentCodiGeografic) {
		this.varRemitentCodiGeografic = varRemitentCodiGeografic;
	}
	public void setRemitentNomGeografic(String remitentNomGeografic) {
		this.remitentNomGeografic = remitentNomGeografic;
	}
	public void setVarRemitentNomGeografic(String varRemitentNomGeografic) {
		this.varRemitentNomGeografic = varRemitentNomGeografic;
	}
	public void setRemitentRegistreNumero(String remitentRegistreNumero) {
		this.remitentRegistreNumero = remitentRegistreNumero;
	}
	public void setVarRemitentRegistreNumero(String varRemitentRegistreNumero) {
		this.varRemitentRegistreNumero = varRemitentRegistreNumero;
	}
	public void setRemitentRegistreAny(String remitentRegistreAny) {
		this.remitentRegistreAny = remitentRegistreAny;
	}
	public void setVarRemitentRegistreAny(String varRemitentRegistreAny) {
		this.varRemitentRegistreAny = varRemitentRegistreAny;
	}
	public void setDestinatariCodiEntitat(String destinatariCodiEntitat) {
		this.destinatariCodiEntitat = destinatariCodiEntitat;
	}
	public void setVarDestinatariCodiEntitat(String varDestinatariCodiEntitat) {
		this.varDestinatariCodiEntitat = varDestinatariCodiEntitat;
	}
	public void setDestinatariNomEntitat(String destinatariNomEntitat) {
		this.destinatariNomEntitat = destinatariNomEntitat;
	}
	public void setVarDestinatariNomEntitat(String varDestinatariNomEntitat) {
		this.varDestinatariNomEntitat = varDestinatariNomEntitat;
	}
	public void setDestinatariCodiGeografic(String destinatariCodiGeografic) {
		this.destinatariCodiGeografic = destinatariCodiGeografic;
	}
	public void setVarDestinatariCodiGeografic(String varDestinatariCodiGeografic) {
		this.varDestinatariCodiGeografic = varDestinatariCodiGeografic;
	}
	public void setDestinatariNomGeografic(String destinatariNomGeografic) {
		this.destinatariNomGeografic = destinatariNomGeografic;
	}
	public void setVarDestinatariNomGeografic(String varDestinatariNomGeografic) {
		this.varDestinatariNomGeografic = varDestinatariNomGeografic;
	}
	public void setDestinatariRegistreNumero(String destinatariRegistreNumero) {
		this.destinatariRegistreNumero = destinatariRegistreNumero;
	}
	public void setVarDestinatariRegistreNumero(String varDestinatariRegistreNumero) {
		this.varDestinatariRegistreNumero = varDestinatariRegistreNumero;
	}
	public void setDestinatariRegistreAny(String destinatariRegistreAny) {
		this.destinatariRegistreAny = destinatariRegistreAny;
	}
	public void setVarDestinatariRegistreAny(String varDestinatariRegistreAny) {
		this.varDestinatariRegistreAny = varDestinatariRegistreAny;
	}
	public void setDocumentTipus(String documentTipus) {
		this.documentTipus = documentTipus;
	}
	public void setVarDocumentTipus(String varDocumentTipus) {
		this.varDocumentTipus = varDocumentTipus;
	}
	public void setDocumentIdiomaDocument(String documentIdiomaDocument) {
		this.documentIdiomaDocument = documentIdiomaDocument;
	}
	public void setVarDocumentIdiomaDocument(String varDocumentIdiomaDocument) {
		this.varDocumentIdiomaDocument = varDocumentIdiomaDocument;
	}
	public void setDocumentIdiomaExtracte(String documentIdiomaExtracte) {
		this.documentIdiomaExtracte = documentIdiomaExtracte;
	}
	public void setVarDocumentIdiomaExtracte(String varDocumentIdiomaExtracte) {
		this.varDocumentIdiomaExtracte = varDocumentIdiomaExtracte;
	}
	public void setVarDocument(String varDocument) {
		this.varDocument = varDocument;
	}
	public void setVarNumeroRegistre(String varNumeroRegistre) {
		this.varNumeroRegistre = varNumeroRegistre;
	}
	public void setVarNumeroAnyRegistre(String varNumeroAnyRegistre) {
		this.varNumeroAnyRegistre = varNumeroAnyRegistre;
	}
	public void setVarDataRegistre(String varDataRegistre) {
		this.varDataRegistre = varDataRegistre;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setVarReferenciaRDSJustificanteClave(String varReferenciaRDSJustificanteClave) {
		this.varReferenciaRDSJustificanteClave = varReferenciaRDSJustificanteClave;
	}
	public void setVarReferenciaRDSJustificanteCodigo(String varReferenciaRDSJustificanteCodigo) {
		this.varReferenciaRDSJustificanteCodigo = varReferenciaRDSJustificanteCodigo;
	}
}
