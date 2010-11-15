/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.conselldemallorca.helium.integracio.plugins.registre.RegistreDocument;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreFont;
import net.conselldemallorca.helium.integracio.plugins.registre.SeientRegistral;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreDocument.IdiomaRegistre;
import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.service.TascaService;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler base per a interactuar amb el registre.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public abstract class RegistreHandler extends AbstractHeliumActionHandler {

	private String data;
	private String varData;
	private String hora;
	private String varHora;
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
	private String varAnyRegistre;
	private String varNumeroAnyRegistre;

	public abstract void execute(ExecutionContext executionContext) throws Exception;

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getVarData() {
		return varData;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public String getVarHora() {
		return varHora;
	}
	public void setVarHora(String varHora) {
		this.varHora = varHora;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getVarOficina() {
		return varOficina;
	}
	public void setVarOficina(String varOficina) {
		this.varOficina = varOficina;
	}
	public String getOficinaFisica() {
		return oficinaFisica;
	}
	public void setOficinaFisica(String oficinaFisica) {
		this.oficinaFisica = oficinaFisica;
	}
	public String getVarOficinaFisica() {
		return varOficinaFisica;
	}
	public void setVarOficinaFisica(String varOficinaFisica) {
		this.varOficinaFisica = varOficinaFisica;
	}
	public String getRemitentCodiEntitat() {
		return remitentCodiEntitat;
	}
	public void setRemitentCodiEntitat(String remitentCodiEntitat) {
		this.remitentCodiEntitat = remitentCodiEntitat;
	}
	public String getVarRemitentCodiEntitat() {
		return varRemitentCodiEntitat;
	}
	public void setVarRemitentCodiEntitat(String varRemitentCodiEntitat) {
		this.varRemitentCodiEntitat = varRemitentCodiEntitat;
	}
	public String getRemitentNomEntitat() {
		return remitentNomEntitat;
	}
	public void setRemitentNomEntitat(String remitentNomEntitat) {
		this.remitentNomEntitat = remitentNomEntitat;
	}
	public String getVarRemitentNomEntitat() {
		return varRemitentNomEntitat;
	}
	public void setVarRemitentNomEntitat(String varRemitentNomEntitat) {
		this.varRemitentNomEntitat = varRemitentNomEntitat;
	}
	public String getRemitentCodiGeografic() {
		return remitentCodiGeografic;
	}
	public void setRemitentCodiGeografic(String remitentCodiGeografic) {
		this.remitentCodiGeografic = remitentCodiGeografic;
	}
	public String getVarRemitentCodiGeografic() {
		return varRemitentCodiGeografic;
	}
	public void setVarRemitentCodiGeografic(String varRemitentCodiGeografic) {
		this.varRemitentCodiGeografic = varRemitentCodiGeografic;
	}
	public String getRemitentNomGeografic() {
		return remitentNomGeografic;
	}
	public void setRemitentNomGeografic(String remitentNomGeografic) {
		this.remitentNomGeografic = remitentNomGeografic;
	}
	public String getVarRemitentNomGeografic() {
		return varRemitentNomGeografic;
	}
	public void setVarRemitentNomGeografic(String varRemitentNomGeografic) {
		this.varRemitentNomGeografic = varRemitentNomGeografic;
	}
	public String getRemitentRegistreNumero() {
		return remitentRegistreNumero;
	}
	public void setRemitentRegistreNumero(String remitentRegistreNumero) {
		this.remitentRegistreNumero = remitentRegistreNumero;
	}
	public String getVarRemitentRegistreNumero() {
		return varRemitentRegistreNumero;
	}
	public void setVarRemitentRegistreNumero(String varRemitentRegistreNumero) {
		this.varRemitentRegistreNumero = varRemitentRegistreNumero;
	}
	public String getRemitentRegistreAny() {
		return remitentRegistreAny;
	}
	public void setRemitentRegistreAny(String remitentRegistreAny) {
		this.remitentRegistreAny = remitentRegistreAny;
	}
	public String getVarRemitentRegistreAny() {
		return varRemitentRegistreAny;
	}
	public void setVarRemitentRegistreAny(String varRemitentRegistreAny) {
		this.varRemitentRegistreAny = varRemitentRegistreAny;
	}
	public String getDestinatariCodiEntitat() {
		return destinatariCodiEntitat;
	}
	public void setDestinatariCodiEntitat(String destinatariCodiEntitat) {
		this.destinatariCodiEntitat = destinatariCodiEntitat;
	}
	public String getVarDestinatariCodiEntitat() {
		return varDestinatariCodiEntitat;
	}
	public void setVarDestinatariCodiEntitat(String varDestinatariCodiEntitat) {
		this.varDestinatariCodiEntitat = varDestinatariCodiEntitat;
	}
	public String getDestinatariNomEntitat() {
		return destinatariNomEntitat;
	}
	public void setDestinatariNomEntitat(String destinatariNomEntitat) {
		this.destinatariNomEntitat = destinatariNomEntitat;
	}
	public String getVarDestinatariNomEntitat() {
		return varDestinatariNomEntitat;
	}
	public void setVarDestinatariNomEntitat(String varDestinatariNomEntitat) {
		this.varDestinatariNomEntitat = varDestinatariNomEntitat;
	}
	public String getDestinatariCodiGeografic() {
		return destinatariCodiGeografic;
	}
	public void setDestinatariCodiGeografic(String destinatariCodiGeografic) {
		this.destinatariCodiGeografic = destinatariCodiGeografic;
	}
	public String getVarDestinatariCodiGeografic() {
		return varDestinatariCodiGeografic;
	}
	public void setVarDestinatariCodiGeografic(String varDestinatariCodiGeografic) {
		this.varDestinatariCodiGeografic = varDestinatariCodiGeografic;
	}
	public String getDestinatariNomGeografic() {
		return destinatariNomGeografic;
	}
	public void setDestinatariNomGeografic(String destinatariNomGeografic) {
		this.destinatariNomGeografic = destinatariNomGeografic;
	}
	public String getVarDestinatariNomGeografic() {
		return varDestinatariNomGeografic;
	}
	public void setVarDestinatariNomGeografic(String varDestinatariNomGeografic) {
		this.varDestinatariNomGeografic = varDestinatariNomGeografic;
	}
	public String getDestinatariRegistreNumero() {
		return destinatariRegistreNumero;
	}
	public void setDestinatariRegistreNumero(String destinatariRegistreNumero) {
		this.destinatariRegistreNumero = destinatariRegistreNumero;
	}
	public String getVarDestinatariRegistreNumero() {
		return varDestinatariRegistreNumero;
	}
	public void setVarDestinatariRegistreNumero(String varDestinatariRegistreNumero) {
		this.varDestinatariRegistreNumero = varDestinatariRegistreNumero;
	}
	public String getDestinatariRegistreAny() {
		return destinatariRegistreAny;
	}
	public void setDestinatariRegistreAny(String destinatariRegistreAny) {
		this.destinatariRegistreAny = destinatariRegistreAny;
	}
	public String getVarDestinatariRegistreAny() {
		return varDestinatariRegistreAny;
	}
	public void setVarDestinatariRegistreAny(String varDestinatariRegistreAny) {
		this.varDestinatariRegistreAny = varDestinatariRegistreAny;
	}
	public String getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(String documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getVarDocumentTipus() {
		return varDocumentTipus;
	}
	public void setVarDocumentTipus(String varDocumentTipus) {
		this.varDocumentTipus = varDocumentTipus;
	}
	public String getDocumentIdiomaDocument() {
		return documentIdiomaDocument;
	}
	public void setDocumentIdiomaDocument(String documentIdiomaDocument) {
		this.documentIdiomaDocument = documentIdiomaDocument;
	}
	public String getVarDocumentIdiomaDocument() {
		return varDocumentIdiomaDocument;
	}
	public void setVarDocumentIdiomaDocument(String varDocumentIdiomaDocument) {
		this.varDocumentIdiomaDocument = varDocumentIdiomaDocument;
	}
	public String getDocumentIdiomaExtracte() {
		return documentIdiomaExtracte;
	}
	public void setDocumentIdiomaExtracte(String documentIdiomaExtracte) {
		this.documentIdiomaExtracte = documentIdiomaExtracte;
	}
	public String getVarDocumentIdiomaExtracte() {
		return varDocumentIdiomaExtracte;
	}
	public void setVarDocumentIdiomaExtracte(String varDocumentIdiomaExtracte) {
		this.varDocumentIdiomaExtracte = varDocumentIdiomaExtracte;
	}
	public String getVarDocument() {
		return varDocument;
	}
	public void setVarDocument(String varDocument) {
		this.varDocument = varDocument;
	}
	public String getVarNumeroRegistre() {
		return varNumeroRegistre;
	}
	public void setVarNumeroRegistre(String varNumeroRegistre) {
		this.varNumeroRegistre = varNumeroRegistre;
	}
	public String getVarAnyRegistre() {
		return varAnyRegistre;
	}
	public void setVarAnyRegistre(String varAnyRegistre) {
		this.varAnyRegistre = varAnyRegistre;
	}
	public String getVarNumeroAnyRegistre() {
		return varNumeroAnyRegistre;
	}
	public void setVarNumeroAnyRegistre(String varNumeroAnyRegistre) {
		this.varNumeroAnyRegistre = varNumeroAnyRegistre;
	}



	protected SeientRegistral getDadesRegistre(
			ExecutionContext executionContext) {
		SeientRegistral dades = new SeientRegistral();
		Date ara = new Date();
		DateFormat dfData = new SimpleDateFormat("dd/MM/yyyy");
		if (	(data == null || "".equals(data)) &&
				(varData == null || "".equals(varData))) {
			dades.setData(dfData.format(ara));
		} else {
			Object valor = getValorOVariable(executionContext, data, varData);
			if (valor instanceof String)
				dades.setData((String)valor);
			else if (valor instanceof Date)
				dades.setData(dfData.format((Date)valor));
			else
				dades.setData(dfData.format(ara));
		}
		if (	(hora == null || "".equals(hora)) &&
				(varHora == null || "".equals(varHora))) {
			DateFormat df = new SimpleDateFormat("HH:mm");
			dades.setHora(df.format(ara));
		} else {
			dades.setHora((String)getValorOVariable(executionContext, hora, varHora));
		}
		dades.setOficina((String)getValorOVariable(executionContext, oficina, varOficina));
		dades.setOficinaFisica((String)getValorOVariable(executionContext, oficinaFisica, varOficinaFisica));
		RegistreFont remitent = new RegistreFont();
		remitent.setCodiEntitat((String)getValorOVariable(executionContext, remitentCodiEntitat, varRemitentCodiEntitat));
		remitent.setNomEntitat((String)getValorOVariable(executionContext, remitentNomEntitat, varRemitentNomEntitat));
		remitent.setCodiGeografic((String)getValorOVariable(executionContext, remitentCodiGeografic, varRemitentCodiGeografic));
		remitent.setNomGeografic((String)getValorOVariable(executionContext, remitentNomGeografic, varRemitentNomGeografic));
		remitent.setNumeroRegistre((String)getValorOVariable(executionContext, remitentRegistreNumero, varRemitentRegistreNumero));
		remitent.setAnyRegistre((String)getValorOVariable(executionContext, remitentRegistreAny, varRemitentRegistreAny));
		dades.setRemitent(remitent);
		RegistreFont destinatari = new RegistreFont();
		destinatari.setCodiEntitat((String)getValorOVariable(executionContext, destinatariCodiEntitat, varDestinatariCodiEntitat));
		destinatari.setNomEntitat((String)getValorOVariable(executionContext, destinatariNomEntitat, varDestinatariNomEntitat));
		destinatari.setCodiGeografic((String)getValorOVariable(executionContext, destinatariCodiGeografic, varDestinatariCodiGeografic));
		destinatari.setNomGeografic((String)getValorOVariable(executionContext, destinatariNomGeografic, varDestinatariNomGeografic));
		destinatari.setNumeroRegistre((String)getValorOVariable(executionContext, destinatariRegistreNumero, varDestinatariRegistreNumero));
		destinatari.setAnyRegistre((String)getValorOVariable(executionContext, destinatariRegistreAny, varDestinatariRegistreAny));
		dades.setDestinatari(destinatari);
		dades.setDocument(getRegistreDocument(executionContext));
		return dades;
	}
	protected void guardarInfoRegistre(
			ExecutionContext executionContext,
			String data,
			String hora,
			String oficina,
			String numero,
			String any,
			boolean entrada) {
		if (varNumeroRegistre != null && !"".equals(varNumeroRegistre))
			executionContext.setVariable(varNumeroRegistre, numero);
		if (varAnyRegistre != null && !"".equals(varAnyRegistre))
			executionContext.setVariable(varAnyRegistre, any);
		if (varNumeroAnyRegistre != null && !"".equals(varNumeroAnyRegistre))
			executionContext.setVariable(varNumeroAnyRegistre, numero + "/" + any);
		Long documentId = getDocumentId(executionContext);
		if (documentId == null)
			throw new JbpmException("No s'ha trobat el document '" + varDocument + "'");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date dataRegistre = null;
		try {
			dataRegistre = df.parse(data + " " + hora);
		} catch (Exception ex) {
			dataRegistre = new Date();
		}
		if (entrada)
			DaoProxy.getInstance().getDocumentStoreDao().updateRegistreEntrada(
					documentId,
					dataRegistre,
					numero,
					any,
					oficina,
					getPluginRegistreService().getNomOficina(oficina));
		else
			DaoProxy.getInstance().getDocumentStoreDao().updateRegistreSortida(
					documentId,
					dataRegistre,
					numero,
					any,
					oficina,
					getPluginRegistreService().getNomOficina(oficina));
	}

	private RegistreDocument getRegistreDocument(ExecutionContext executionContext) {
		RegistreDocument resposta = new RegistreDocument();
		resposta.setTipus((String)getValorOVariable(executionContext, documentTipus, varDocumentTipus));
		resposta.setIdiomaDocument(getIdiomaRegistre((String)getValorOVariable(executionContext, documentIdiomaDocument, varDocumentIdiomaDocument)));
		resposta.setIdiomaExtracte(getIdiomaRegistre((String)getValorOVariable(executionContext, documentIdiomaExtracte, varDocumentIdiomaExtracte)));
		if (varDocument != null && varDocument.length() > 0) {
			Long documentId = getDocumentId(executionContext);
			if (documentId == null)
				throw new JbpmException("No s'ha trobat el document '" + varDocument + "'");
			DocumentStore docStore = DaoProxy.getInstance().getDocumentStoreDao().getById(
					documentId,
					false);
			if (docStore == null)
				throw new JbpmException("No s'ha trobat la informació del document '" + varDocument + "'");
			InstanciaProcesDto instanciaProces = getExpedientService().getInstanciaProcesById(
					new Long(executionContext.getProcessInstance().getId()).toString(),
					false);
			if (docStore.isAdjunt()) {
				resposta.setExtracte(instanciaProces.getExpedient().getIdentificador() + ": " + docStore.getAdjuntTitol());
			} else {
				Document document = getDissenyService().findDocumentAmbDefinicioProcesICodi(
						instanciaProces.getDefinicioProces().getId(),
						docStore.getCodiDocument());
				resposta.setExtracte(instanciaProces.getExpedient().getIdentificador() + ": " + document.getNom());
			}
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			resposta.setData(df.format(docStore.getDataDocument()));
		} else {
			throw new JbpmException("No s'ha especificat el document per registrar");
		}
		return resposta;
	}
	private Long getDocumentId(ExecutionContext executionContext) {
		String varCodi = TascaService.PREFIX_DOCUMENT + varDocument;
		Object valor = executionContext.getVariable(varCodi);
		if (valor instanceof Long)
			return (Long)valor;
		return null;
	}
	private IdiomaRegistre getIdiomaRegistre(String idioma) {
		if ("es".equalsIgnoreCase(idioma))
			return IdiomaRegistre.ES;
		return IdiomaRegistre.CA;
	}

}
