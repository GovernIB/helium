/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentDisseny;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo.IniciadorTipus;
import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.model.dao.DominiDao;
import net.conselldemallorca.helium.model.dao.EntornDao;
import net.conselldemallorca.helium.model.dao.MailDao;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.Domini;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.ServiceProxy;
import net.conselldemallorca.helium.model.service.TascaService;
import net.conselldemallorca.helium.util.ExpedientIniciant;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

/**
 * Handler que pot servir com a base per als handlers que s'hagin
 * d'implementar a dins les definicions de procés.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public abstract class BasicActionHandler implements ActionHandler {

	public abstract void execute(ExecutionContext executionContext) throws Exception;



	/**
	 * Llança un error de validació
	 * 
	 * @param error descripció de l'error
	 */
	public void errorValidacio(String error) {
		throw new ValidationException(error);
	}

	/**
	 * Realitza una consulta a un domini
	 * 
	 * @param executionContext
	 * @param codiDomini
	 * @param parametres
	 * @return
	 */
	public List<FilaResultat> consultaDomini(
			ExecutionContext executionContext,
			String codiDomini,
			String id,
			Map<String, Object> parametres) {
		ExpedientInfo expedient = getExpedient(executionContext);
		if (expedient == null)
			throw new JbpmException("No s'ha trobat cap expedient que correspongui amb aquesta instància de procés (" + executionContext.getProcessInstance().getId() + ")");
		Entorn entorn = getEntornDao().findAmbCodi(expedient.getEntornCodi());
		Domini domini = getDominiDao().findAmbEntornICodi(
				entorn.getId(),
				codiDomini);
		if (domini == null)
			throw new JbpmException("No s'ha trobat el domini amb el codi '" + codiDomini + "'");
		try {
			List<net.conselldemallorca.helium.integracio.domini.FilaResultat> resultatConsulta = getDominiDao().consultar(domini.getId(), id, parametres);
			List<FilaResultat> resposta = new ArrayList<FilaResultat>();
			for (net.conselldemallorca.helium.integracio.domini.FilaResultat fila: resultatConsulta) {
				FilaResultat fres = new FilaResultat();
				for (net.conselldemallorca.helium.integracio.domini.ParellaCodiValor parella: fila.getColumnes()) {
					fres.addColumna(new ParellaCodiValor(
							parella.getCodi(),
							parella.getValor()));
				}
				resposta.add(fres);
			}
			return resposta;
		} catch (Exception ex) {
			throw new JbpmException("Error en la consulta del domini amb el codi '" + codiDomini + "'", ex);
		}
	}

	/**
	 * Obté el document al que fa referència una variable
	 * 
	 * @param executionContext
	 * @param varDocument
	 * @return
	 */
	public DocumentInfo getDocument(
			ExecutionContext executionContext,
			String varDocument) {
		String varCodi = TascaService.PREFIX_DOCUMENT + varDocument;
		Object valor = executionContext.getVariable(varCodi);
		if (valor == null)
			return null;
		if (valor instanceof Long) {
			Long id = (Long)valor;
			DocumentStore docStore = getDocumentStoreDao().getById(id, false);
			DocumentInfo resposta = new DocumentInfo();
			resposta.setId(docStore.getId());
			if (docStore.isAdjunt()) {
				resposta.setTitol(docStore.getAdjuntTitol());
			} else {
				InstanciaProcesDto instanciaProces = getExpedientService().getInstanciaProcesById(
						new Long(executionContext.getProcessInstance().getId()).toString(),
						false);
				Document document = getDissenyService().findDocumentAmbDefinicioProcesICodi(
						instanciaProces.getDefinicioProces().getId(),
						docStore.getCodiDocument());
				resposta.setTitol(document.getNom());
			}
			resposta.setArxiuNom(docStore.getArxiuNom());
			resposta.setArxiuContingut(docStore.getArxiuContingut());
			resposta.setDataCreacio(docStore.getDataCreacio());
			resposta.setDataDocument(docStore.getDataDocument());
			resposta.setSignat(docStore.isSignat());
			return resposta;
		} else {
			throw new JbpmException("La variable \"" + varCodi + "\" no es del tipus correcte");
		}
	}

	/**
	 * Obté la informació de disseny del document
	 * 
	 * @param executionContext
	 * @param codiDocument
	 * @return
	 */
	public DocumentDisseny getDocumentDisseny(
			ExecutionContext executionContext,
			String codiDocument) {
		InstanciaProcesDto instanciaProces = getExpedientService().getInstanciaProcesById(
				new Long(executionContext.getProcessInstance().getId()).toString(),
				false);
		Document document = getDissenyService().findDocumentAmbDefinicioProcesICodi(
				instanciaProces.getDefinicioProces().getId(),
				codiDocument);
		if (document == null)
			return null;
		DocumentDisseny resposta = new DocumentDisseny();
		resposta.setId(document.getId());
		resposta.setCodi(document.getCodi());
		resposta.setNom(document.getNom());
		resposta.setDescripcio(document.getDescripcio());
		resposta.setPlantilla(document.isPlantilla());
		resposta.setContentType(document.getContentType());
		resposta.setCustodiaCodi(document.getCustodiaCodi());
		resposta.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
		return resposta;
	}

	/**
	 * Enllaça un document d'una instancia de procés pare. Si el document no existeix no
	 * el copia i no produeix cap error.
	 * 
	 * @param executionContext
	 * @param codiDocument
	 */
	public void crearReferenciaDocumentInstanciaProcesPare(
			ExecutionContext executionContext,
			String varDocument) {
		Token tokenPare = executionContext.getProcessInstance().getRootToken().getParent();
		if (tokenPare != null) {
			String varCodi = TascaService.PREFIX_DOCUMENT + varDocument;
			Object valor = tokenPare.getProcessInstance().getContextInstance().getVariable(varCodi);
			if (valor != null) {
				if (valor instanceof Long) {
					long lv = ((Long)valor).longValue();
					executionContext.setVariable(varCodi, new Long(-lv));
				} else {
					throw new JbpmException("El contingut del document '" + varDocument + "' no és del tipus correcte");
				}
			}
		} else {
			throw new JbpmException("Aquesta instància de procés no té pare");
		}
	}

	/**
	 * Retorna l'expedient associat al procés actual
	 * 
	 * @param executionContext
	 * @return
	 */
	public ExpedientInfo getExpedient(ExecutionContext executionContext) {
		Expedient ex = ExpedientIniciant.getExpedient();
		if (ex != null) {
			ExpedientInfo resposta = new ExpedientInfo();
			resposta.setTitol(ex.getTitol());
			resposta.setNumero(ex.getNumero());
			resposta.setNumeroDefault(ex.getNumeroDefault());
			resposta.setDataInici(ex.getDataInici());
			resposta.setDataFi(ex.getDataFi());
			resposta.setComentari(ex.getComentari());
			resposta.setInfoAturat(ex.getInfoAturat());
			if (ex.getIniciadorTipus().equals(net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus.INTERN))
				resposta.setIniciadorTipus(IniciadorTipus.INTERN);
			else if (ex.getIniciadorTipus().equals(net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus.SISTRA))
				resposta.setIniciadorTipus(IniciadorTipus.SISTRA);
			resposta.setIniciadorCodi(ex.getIniciadorCodi());
			resposta.setResponsableCodi(ex.getResponsableCodi());
			resposta.setExpedientTipusCodi(ex.getTipus().getCodi());
			resposta.setEntornCodi(ex.getEntorn().getCodi());
			if (ex.getEstat() != null)
				resposta.setEstatCodi(ex.getEstat().getCodi());
			return resposta;
		} else {
			ExpedientDto expedient = getExpedientService().findExpedientAmbProcessInstanceId(
					getProcessInstanceId(executionContext));
			if (expedient != null) {
				ExpedientInfo resposta = new ExpedientInfo();
				resposta.setTitol(expedient.getTitol());
				resposta.setNumero(expedient.getNumero());
				resposta.setNumeroDefault(expedient.getNumeroDefault());
				resposta.setDataInici(expedient.getDataInici());
				resposta.setDataFi(expedient.getDataFi());
				resposta.setComentari(expedient.getComentari());
				resposta.setInfoAturat(expedient.getInfoAturat());
				if (expedient.getIniciadorTipus().equals(net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus.INTERN))
					resposta.setIniciadorTipus(IniciadorTipus.INTERN);
				else if (expedient.getIniciadorTipus().equals(net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus.SISTRA))
					resposta.setIniciadorTipus(IniciadorTipus.SISTRA);
				resposta.setIniciadorCodi(expedient.getIniciadorCodi());
				resposta.setResponsableCodi(expedient.getResponsableCodi());
				resposta.setExpedientTipusCodi(expedient.getTipus().getCodi());
				resposta.setEntornCodi(expedient.getEntorn().getCodi());
				if (expedient.getEstat() != null)
					resposta.setEstatCodi(expedient.getEstat().getCodi());
				return resposta;
			}
		}
		return null;
	}

	/**
	 * Envia un email amb possibilitat d'adjuntar documents de l'expedient.
	 * 
	 * @param recipients
	 * @param ccRecipients
	 * @param bccRecipients
	 * @param subject
	 * @param text
	 * @param attachments
	 */
	public void enviarEmail(
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<Long> attachments) {
		try {
			getMailDao().send(
					GlobalProperties.getInstance().getProperty("app.correu.remitent"),
					recipients,
					ccRecipients,
					bccRecipients,
					subject,
					text,
					attachments);
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut enviar el missatge", ex);
		}
	}



	private String getProcessInstanceId(ExecutionContext executionContext) {
		return new Long(executionContext.getProcessInstance().getId()).toString();
	}
	private EntornDao getEntornDao() {
		return DaoProxy.getInstance().getEntornDao();
	}
	private DominiDao getDominiDao() {
		return DaoProxy.getInstance().getDominiDao();
	}
	private DocumentStoreDao getDocumentStoreDao() {
		return DaoProxy.getInstance().getDocumentStoreDao();
	}
	private MailDao getMailDao() {
		return DaoProxy.getInstance().getMailDao();
	}
	private ExpedientService getExpedientService() {
		return ServiceProxy.getInstance().getExpedientService();
	}
	private DissenyService getDissenyService() {
		return ServiceProxy.getInstance().getDissenyService();
	}

	static final long serialVersionUID = 1L;

}
