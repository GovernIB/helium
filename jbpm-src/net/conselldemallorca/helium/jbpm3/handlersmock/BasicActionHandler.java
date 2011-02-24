/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistre;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentDisseny;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler que pot servir com a base per als handlers que s'hagin
 * d'implementar a dins les definicions de procés.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public abstract class BasicActionHandler implements ActionHandler {

	public abstract void execute(ExecutionContext executionContext) throws Exception;



	public void errorValidacio(String error) {}

	public List<FilaResultat> consultaDomini(
			ExecutionContext executionContext,
			String codiDomini,
			String id,
			Map<String, Object> parametres) {
		return new ArrayList<FilaResultat>();
	}

	public List<ExpedientInfo> consultaExpedients(
			ExecutionContext executionContext,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			String expedientTipusCodi,
			String estatCodi,
			boolean iniciat,
			boolean finalitzat){
		return new ArrayList<ExpedientInfo>();
	}

	public DocumentInfo getDocument(
			ExecutionContext executionContext,
			String varDocument) {
		return new DocumentInfo();
	}

	public DocumentDisseny getDocumentDisseny(
			ExecutionContext executionContext,
			String codiDocument) {
		return new DocumentDisseny();
	}

	public void crearReferenciaDocumentInstanciaProcesPare(
			ExecutionContext executionContext,
			String varDocument) {
	}

	public ExpedientInfo getExpedient(ExecutionContext executionContext) {
		return new ExpedientInfo();
	}

	public void enviarEmail(
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<Long> attachments) {
	}
	
	public String[] registreEntrada(
			DadesRegistre dadesRegistre,
			ExecutionContext executionContext) {
		return null;
	}
	public DadesRegistre registreConsultarEntrada(
			String varDocument,
			ExecutionContext executionContext) {
		return null;
	}
	public String[] registreSortida(
			DadesRegistre dadesRegistre,
			ExecutionContext executionContext) {
		return null;
	}
	public DadesRegistre registreConsultarSortida(
			String varDocument,
			ExecutionContext executionContext) {
		return null;
	}

	public String getTextPerVariableAmbDomini(
			ExecutionContext executionContext,
			String varCodi) {
		return null;
	}

	public void terminiGuardar(
			ExecutionContext executionContext,
			String varName,
			int anys,
			int mesos,
			int dies) {}

	static final long serialVersionUID = 1L;

}
