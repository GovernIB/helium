/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ZonaperDocumentDto;
import es.caib.helium.logic.intf.dto.ZonaperEventDto;
import es.caib.helium.logic.intf.dto.ZonaperDocumentDto.DocumentEventTipus;

/**
 * Handler per a afegir un event a un expedient de la zona
 * personal del ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ZonaperExpedientEventHandler extends AbstractHeliumActionHandler implements ZonaperExpedientEventHandlerInterface {

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
		ExpedientDto expedient = getExpedientActual(executionContext);
		String dc = (String)getValorOVariable(
				executionContext,
				documentCodi,
				varDocumentCodi);
		String docTitol = null;
		String docArxiuNom = null;
		byte[] docArxiuContingut = null;
		if (dc != null) {
			DocumentInfo document = getDocumentInfo(
					executionContext,
					dc,
					true);
			if (document == null)
				throw new JbpmException("No s'ha pogut obtenir el document de l'expedient amb el codi " + dc);
			docTitol = document.getTitol();
			docArxiuNom = document.getArxiuNom();
			docArxiuContingut = document.getArxiuContingut();
		}
		String model = (String)getValorOVariable(
				executionContext,
				redoseModel,
				varRedoseModel);
		Integer versio = getValorOVariableInteger(
				executionContext,
				redoseVersio,
				varRedoseVersio);
		Jbpm3HeliumBridge.getInstanceService().zonaperEventCrear(
				getProcessInstanceId(executionContext),
				construirEvent(
						expedient,
						(String)getValorOVariable(executionContext, titol, varTitol),
						(String)getValorOVariable(executionContext, text, varText),
						(String)getValorOVariable(executionContext, textSms, varTextSms),
						(String)getValorOVariable(executionContext, enllasConsulta, varEnllasConsulta),
						(Date)getValorOVariable(executionContext, data, varData),
						dc != null,
						docTitol,
						docArxiuNom,
						docArxiuContingut,
						model,
						versio));
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



	private ZonaperEventDto construirEvent(
			ExpedientDto expedient,
			String titol,
			String text,
			String textSms,
			String enllasConsulta,
			Date data,
			boolean hiHaDocument,
			String adjuntTitol,
			String adjuntArxiuNom,
			byte[] adjuntArxiuContingut,
			String adjuntModel,
			Integer adjuntVersio) {
		ZonaperEventDto zonaperEvent = new ZonaperEventDto();
		zonaperEvent.setTitol(titol);
		zonaperEvent.setText(text);
		zonaperEvent.setTextSMS(textSms);
		zonaperEvent.setEnllasConsulta(enllasConsulta);
		if (hiHaDocument) {
			List<ZonaperDocumentDto> documents = new ArrayList<ZonaperDocumentDto>();
			ZonaperDocumentDto document = new ZonaperDocumentDto();
			document.setNom(adjuntTitol);
			document.setArxiuNom(adjuntArxiuNom);
			document.setArxiuContingut(adjuntArxiuContingut);
			document.setTipus(DocumentEventTipus.ARXIU);
			documents.add(document);
			zonaperEvent.setDocuments(documents);
		}
		return zonaperEvent;
	}

}
