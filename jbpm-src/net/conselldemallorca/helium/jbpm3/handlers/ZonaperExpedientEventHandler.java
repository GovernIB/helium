/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.JbpmException;
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
			String varCodi = TascaService.PREFIX_DOCUMENT + dc;
			Object valor = executionContext.getVariable(varCodi);
			if (valor == null || !(valor instanceof Long))
				throw new JbpmException("El document especificat (" + dc + ") no existeix");
			Long id = (Long)valor;
			DocumentStore docStore = getDocumentStoreDao().getById(id, false);
			if (docStore == null)
				throw new JbpmException("No s'ha trobat el contingut del document especificat(" + dc + ")");
			InstanciaProcesDto instanciaProces = getExpedientService().getInstanciaProcesById(
					new Long(executionContext.getProcessInstance().getId()).toString(),
					false);
			Document document = getDissenyService().findDocumentAmbDefinicioProcesICodi(
					instanciaProces.getDefinicioProces().getId(),
					docStore.getCodiDocument());
			docTitol = document.getNom();
			docArxiuNom = docStore.getArxiuNom();
			docArxiuContingut = docStore.getArxiuContingut();
		}
		String model = (String)getValorOVariable(executionContext, redoseModel, varRedoseModel);
		Integer versio = getValorOVariableInteger(executionContext, redoseVersio, varRedoseVersio);
		if (ex != null) {
			getSistraService().zonaperExpedientEvent(
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
					versio);
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			getSistraService().zonaperExpedientEvent(
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
					versio);
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

}
