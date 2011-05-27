/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreSortida;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.service.TascaService;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class RegistreSortidaHandler extends AbstractHeliumActionHandler {

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

	public void execute(ExecutionContext executionContext) throws Exception {
		if (!getPluginRegistreDao().isRegistreActiu())
			throw new JbpmException("El plugin de registre no està configurat");
		if (varDocument == null || varDocument.length() == 0)
			throw new JbpmException("És obligatori especificar un document per registrar");
		try {
			String orgCodi = 
				(String)getValorOVariable(executionContext, remitentCodiEntitat, varRemitentCodiEntitat);
			String ofiCodi =
				(String)getValorOVariable(executionContext, oficina, varOficina) + "-" +
				(String)getValorOVariable(executionContext, oficinaFisica, varOficinaFisica);
			RegistreSortida registreSortida = new RegistreSortida();
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(orgCodi);
			dadesOficina.setOficinaCodi(ofiCodi);
			registreSortida.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			dadesInteressat.setAutenticat(true);
			dadesInteressat.setEntitatCodi(
					(String)getValorOVariable(executionContext, destinatariCodiEntitat, varDestinatariCodiEntitat));
			dadesInteressat.setNomAmbCognoms(
					(String)getValorOVariable(executionContext, destinatariNomEntitat, varDestinatariNomEntitat));
			/*dadesInteressat.setPaisCodi(null);
			dadesInteressat.setPaisNom(null);
			dadesInteressat.setProvinciaCodi(null);
			dadesInteressat.setProvinciaNom(null);*/
			dadesInteressat.setMunicipiCodi((String)getValorOVariable(executionContext, destinatariCodiGeografic, varDestinatariCodiGeografic));
			dadesInteressat.setMunicipiNom((String)getValorOVariable(executionContext, destinatariNomGeografic, varDestinatariNomGeografic));
			registreSortida.setDadesInteressat(dadesInteressat);
			/*DadesRepresentat dadesRepresentat = new DadesRepresentat();
			dadesRepresentat.setNif(null);
			dadesRepresentat.setNomAmbCognoms(null);
			registreSortida.setDadesRepresentat(dadesRepresentat);*/
			DadesAssumpte dadesAssumpte = new DadesAssumpte();
			String idiomaExtracte = (String)getValorOVariable(executionContext, documentIdiomaExtracte, varDocumentIdiomaExtracte);
			dadesAssumpte.setIdiomaCodi(
					(idiomaExtracte != null) ? idiomaExtracte : "ca");
			dadesAssumpte.setTipus(
					(String)getValorOVariable(executionContext, documentTipus, varDocumentTipus));
			registreSortida.setDadesAssumpte(dadesAssumpte);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			DocumentRegistre document = new DocumentRegistre();
			DocumentInfo documentInfo = getDocumentInfo(executionContext, varDocument);
			if (documentInfo == null)
				throw new JbpmException("No s'ha trobat la informació del document '" + varDocument + "'");
			InstanciaProcesDto instanciaProces = getExpedientService().getInstanciaProcesById(
					new Long(executionContext.getProcessInstance().getId()).toString(),
					false);
			document.setNom(documentInfo.getTitol());
			dadesAssumpte.setAssumpte(instanciaProces.getExpedient().getIdentificador() + ": " + documentInfo.getTitol());
			String idiomaDocument = (String)getValorOVariable(executionContext, documentIdiomaDocument, varDocumentIdiomaDocument);
			document.setIdiomaCodi(
					(idiomaDocument != null) ? idiomaDocument : "ca");
			document.setData(documentInfo.getDataDocument());
			document.setArxiuNom(documentInfo.getArxiuNom());
			document.setArxiuContingut(documentInfo.getArxiuContingut());
			documents.add(document);
			registreSortida.setDocuments(documents);
			RespostaAnotacioRegistre resposta = getPluginRegistreDao().registrarSortida(registreSortida);
			if (resposta.isOk()) {
				DaoProxy.getInstance().getDocumentStoreDao().updateRegistreSortida(
						documentInfo.getId(),
						resposta.getData(),
						resposta.getNumero(),
						orgCodi,
						ofiCodi,
						getPluginRegistreDao().obtenirNomOficina(ofiCodi));
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
			} else {
				throw new JbpmException("No s'ha pogut registrar la sortida: " + resposta.getErrorDescripcio());
			}
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut registrar la sortida", ex);
		}
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



	private Long getDocumentId(ExecutionContext executionContext) {
		String varCodi = TascaService.PREFIX_DOCUMENT + varDocument;
		Object valor = executionContext.getVariable(varCodi);
		if (valor instanceof Long)
			return (Long)valor;
		return null;
	}

}
