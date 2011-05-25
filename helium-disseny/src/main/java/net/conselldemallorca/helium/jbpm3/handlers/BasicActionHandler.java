/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreEntrada;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreSortida;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentDisseny;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler que pot servir com a base per als handlers que s'hagin
 * d'implementar a dins les definicions de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
	
	public RespostaRegistre registreEntrada(
			ExecutionContext executionContext,
			DadesRegistreEntrada dadesEntrada,
			List<DocumentInfo> documentsEntrada) {
		return null;
	}
	public DadesRegistreEntrada registreConsultarEntrada(
			ExecutionContext executionContext,
			String organCodi,
			String oficinaCodi,
			String numero) {
		return null;
	}
	public RespostaRegistre registreSortida(
			ExecutionContext executionContext,
			DadesRegistreSortida dadesSortida,
			List<DocumentInfo> documentsSortida) {
		return null;
	}
	public DadesRegistreSortida registreConsultarSortida(
			ExecutionContext executionContext,
			String organCodi,
			String oficinaCodi,
			String numero) {
		return null;
	}
	public RespostaRegistre registreNotificacio(
			ExecutionContext executionContext,
			DadesRegistreNotificacio dadesNotificacio,
			List<DocumentInfo> documentsNotificacio) {
		return null;
	}
	public Date registreObtenirJustificantRecepcio(String registreNumero) {
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

	public byte[] obtenirArxiuGestorDocumental(String id) {
		return null;
	}

	public void documentGuardar(
			ExecutionContext executionContext,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {}

	static final long serialVersionUID = 1L;

}
