/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreEntrada;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreSortida;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentDisseny;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Tramit;

import org.jbpm.JbpmException;
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

	public List<ParellaCodiValor> consultaEnumeracio(
			ExecutionContext executionContext,
			String codiEnumeracio) {
		return new ArrayList<ParellaCodiValor>();
	}
	
	public String enumeracioGetValor(
			ExecutionContext executionContext,
			String codiEnumeracio,
			String codi) {			
		return null;
	}
	
	public void enumeracioSetValor(
			ExecutionContext executionContext,
			String codiEnumeracio,
			String codi,
			String valor) {
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
	public RespostaRegistre registreSortida(
			ExecutionContext executionContext,
			DadesRegistreSortida dadesSortida,
			List<DocumentInfo> documentsSortida) {
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

	public Object getVariableValor(
			ExecutionContext executionContext,
			String varCodi) {
		return null;
	}

	public String getVariableText(
			ExecutionContext executionContext,
			String varCodi) {
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

	public Tramit consultaTramit(
			String numero,
			String clau) {
		return null;
	}

	public void expedientRelacionar(
			ExecutionContext executionContext,
			long expedientId) {}

	public void tokenRedirigir(long tokenId, String nodeName, boolean cancelarTasques) {}

	public void guardarParametresPerRetrocedir(
			ExecutionContext executionContext,
			List<String> parametres) {}

	public void instanciaProcesReindexar(ExecutionContext executionContext) {}
	
	public boolean instanciaProcesReindexar(long processInstanceId) { 
		return false;
	}

	public boolean tokenActivar(long tokenId, boolean activar) {
		return false;
	}

	public void reprendreExpedient(String processInstanceId) throws Exception {}

	public byte[] obtenirArxiuGestorDocumental(String id) {
		return null;
	}

	public void documentGuardar(
			ExecutionContext executionContext,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {}

	public void adjuntGuardar(
            ExecutionContext executionContext,
            String nomDocument,
            Date data,
            String arxiuNom,
            byte[] arxiuContingut) {}

	public Object getVariableGlobal(
			ExecutionContext executionContext,
			String varCodi) {
		return null;
	}
	
	public void setVariableGlobal(
			ExecutionContext executionContext,
			String varCodi,
			Object varValor) {}
	
	public Object getVariableGlobalValor(
			ExecutionContext executionContext,
			String varCodi) {
		return null;
	}
	
	public void desarInformacioExecucio(
			ExecutionContext executionContext,
			String missatge) throws Exception {
	}
	
	public void altaNotificacio(
			DadesNotificacio dadesNotificacio,
			Long expedientId) throws JbpmException {
	}
	
	
	public void interessatCrear(
			Interessat interessat) {
	}
	
	
	public void interessatModificar(
			Interessat interessat) {	
	}
	
	
	public void interessatEliminar(
			String codi,
			Long expedientId) {
	}
	
	static final long serialVersionUID = 1L;

}
