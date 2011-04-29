/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentEvent;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentEventTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Event;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a afegir un event a un expedient de la zona
 * personal del ciutadà.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ZonaperExpedientEventHandler extends AbstractHeliumActionHandler {

	private String titol;
	private String varTitol;
	private String text;
	private String varText;
	private String textSms;
	private String varTextSms;
	private String enllasConsulta;
	private String varEnllasConsulta;
	private Date data;
	private String varData;
	private String documentCodi;
	private String varDocumentCodi;
	private String redoseModel;
	private String varRedoseModel;
	private String redoseVersio;
	private String varRedoseVersio;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciant.getExpedient();
		String dc = (String)getValorOVariable(executionContext, documentCodi, varDocumentCodi);
		String docTitol = null;
		String docArxiuNom = null;
		byte[] docArxiuContingut = null;
		if (dc != null) {
			DocumentInfo document = getDocumentInfo(executionContext, dc);
			docTitol = document.getTitol();
			docArxiuNom = document.getArxiuNom();
			docArxiuContingut = document.getArxiuContingut();
		}
		String model = (String)getValorOVariable(executionContext, redoseModel, varRedoseModel);
		Integer versio = getValorOVariableInteger(executionContext, redoseVersio, varRedoseVersio);
		if (ex != null) {
			getExpedientService().publicarEvent(
					getPublicarEventRequest(
							ex,
							(String)getValorOVariable(executionContext, titol, varTitol),
							(String)getValorOVariable(executionContext, text, varText),
							(String)getValorOVariable(executionContext, textSms, varTextSms),
							(String)getValorOVariable(executionContext, enllasConsulta, varEnllasConsulta),
							(Date)getValorOVariable(executionContext, data, varData),
							docTitol,
							docArxiuNom,
							docArxiuContingut,
							model,
							versio));
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			getExpedientService().publicarEvent(
					getPublicarEventRequest(
							expedient,
							(String)getValorOVariable(executionContext, titol, varTitol),
							(String)getValorOVariable(executionContext, text, varText),
							(String)getValorOVariable(executionContext, textSms, varTextSms),
							(String)getValorOVariable(executionContext, enllasConsulta, varEnllasConsulta),
							(Date)getValorOVariable(executionContext, data, varData),
							docTitol,
							docArxiuNom,
							docArxiuContingut,
							model,
							versio));
		}
	}

	public void setTitol(String titol) {
		this.titol = titol;
	}
	public void setVarTitol(String varTitol) {
		this.varTitol = varTitol;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setVarText(String varText) {
		this.varText = varText;
	}
	public void setTextSms(String textSms) {
		this.textSms = textSms;
	}
	public void setVarTextSms(String varTextSms) {
		this.varTextSms = varTextSms;
	}
	public void setEnllasConsulta(String enllasConsulta) {
		this.enllasConsulta = enllasConsulta;
	}
	public void setVarEnllasConsulta(String varEnllasConsulta) {
		this.varEnllasConsulta = varEnllasConsulta;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public void setVarDocumentCodi(String varDocumentCodi) {
		this.varDocumentCodi = varDocumentCodi;
	}
	public void setRedoseModel(String redoseModel) {
		this.redoseModel = redoseModel;
	}
	public void setVarRedoseModel(String varRedoseModel) {
		this.varRedoseModel = varRedoseModel;
	}
	public void setRedoseVersio(String redoseVersio) {
		this.redoseVersio = redoseVersio;
	}
	public void setVarRedoseVersio(String varRedoseVersio) {
		this.varRedoseVersio = varRedoseVersio;
	}



	private PublicarEventRequest getPublicarEventRequest(
			Expedient expedient,
			String titol,
			String text,
			String textSms,
			String enllasConsulta,
			Date data,
			String adjuntTitol,
			String adjuntArxiuNom,
			byte[] adjuntArxiuContingut,
			String adjuntModel,
			Integer adjuntVersio) {
		PublicarEventRequest requestEvent = new PublicarEventRequest();
		requestEvent.setExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		requestEvent.setExpedientClau(expedient.getTramitExpedientClau());
		requestEvent.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		Event event = new Event();
		event.setTitol(titol);
		event.setText(text);
		event.setTextSMS(textSms);
		event.setEnllasConsulta(enllasConsulta);
		List<DocumentEvent> documents = new ArrayList<DocumentEvent>();
		DocumentEvent document = new DocumentEvent();
		document.setNom(adjuntTitol);
		document.setArxiuNom(adjuntArxiuNom);
		document.setArxiuContingut(adjuntArxiuContingut);
		document.setTipus(DocumentEventTipus.ARXIU);
		documents.add(document);
		event.setDocuments(documents);
		requestEvent.setEvent(event);
		return requestEvent;
	}

}
