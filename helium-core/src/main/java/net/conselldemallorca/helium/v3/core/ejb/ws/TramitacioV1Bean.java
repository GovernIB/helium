/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb.ws;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.ArxiuProces;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.CampProces;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.CampTasca;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.DocumentProces;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.DocumentTasca;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.ExpedientInfo;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.ParellaCodiValor;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.TascaTramitacio;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.TramitacioException;
import net.conselldemallorca.helium.v3.core.service.ws.tramitaciov1.TramitacioService;

/**
 * EJB pel servei de callback de portafirmes amb interfície MCGD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "TramitacioV1",
		serviceName = "TramitacioServiceImplService",
		portName = "TramitacioServiceImplPort",
		targetNamespace = "http://conselldemallorca.net/helium/ws/tramitacio/v1")
@WebContext(
		contextRoot = "/helium/ws/v1",
		urlPattern = "/Tramitacio",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TramitacioV1Bean implements TramitacioService {

	@Autowired
	private TramitacioService delegate;

	@Override
	public String iniciExpedient(String entornCodi, String expedientTipusCodi, String numero, String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
		return delegate.iniciExpedient(entornCodi, expedientTipusCodi, numero, titol, valorsFormulari);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonals(String entornCodi) throws TramitacioException {
		return delegate.consultaTasquesPersonals(entornCodi);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrup(String entornCodi) throws TramitacioException {
		return delegate.consultaTasquesGrup(entornCodi);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(String entornCodi, String expedientNumero)
			throws TramitacioException {
		return delegate.consultaTasquesPersonalsByCodi(entornCodi, expedientNumero);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrupByCodi(String entornCodi, String expedientNumero)
			throws TramitacioException {
		return delegate.consultaTasquesGrupByCodi(entornCodi, expedientNumero);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonalsByProces(String entornCodi, String processInstanceId)
			throws TramitacioException {
		return delegate.consultaTasquesPersonalsByProces(entornCodi, processInstanceId);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrupByProces(String entornCodi, String processInstanceId)
			throws TramitacioException {
		return delegate.consultaTasquesGrupByProces(entornCodi, processInstanceId);
	}

	@Override
	public void agafarTasca(String entornCodi, String tascaId) throws TramitacioException {
		delegate.agafarTasca(entornCodi, tascaId);
	}

	@Override
	public void alliberarTasca(String entornCodi, String tascaId) throws TramitacioException {
		delegate.alliberarTasca(entornCodi, tascaId);
	}

	@Override
	public List<CampTasca> consultaFormulariTasca(String entornCodi, String tascaId) throws TramitacioException {
		return delegate.consultaFormulariTasca(entornCodi, tascaId);
	}

	@Override
	public void setDadesFormulariTasca(String entornCodi, String tascaId, List<ParellaCodiValor> valors)
			throws TramitacioException {
		delegate.setDadesFormulariTasca(entornCodi, tascaId, valors);
	}

	@Override
	public List<DocumentTasca> consultaDocumentsTasca(String entornCodi, String tascaId) throws TramitacioException {
		return delegate.consultaDocumentsTasca(entornCodi, tascaId);
	}

	@Override
	public void setDocumentTasca(String entornCodi, String tascaId, String document, String nom, Date data,
			byte[] contingut) throws TramitacioException {
		delegate.setDocumentTasca(entornCodi, tascaId, document, nom, data, contingut);
	}

	@Override
	public void esborrarDocumentTasca(String entornCodi, String tascaId, String document) throws TramitacioException {
		delegate.esborrarDocumentTasca(entornCodi, tascaId, document);
	}

	@Override
	public void finalitzarTasca(String entornCodi, String tascaId, String transicio) throws TramitacioException {
		delegate.finalitzarTasca(entornCodi, tascaId, transicio);
	}

	@Override
	public ExpedientInfo getExpedientInfo(String entornCodiCodi, String processInstanceId) throws TramitacioException {
		return delegate.getExpedientInfo(entornCodiCodi, processInstanceId);
	}

	@Override
	public List<CampProces> consultarVariablesProces(String entornCodi, String processInstanceId)
			throws TramitacioException {
		return delegate.consultarVariablesProces(entornCodi, processInstanceId);
	}

	@Override
	public void setVariableProces(String entornCodi, String processInstanceId, String varCodi, Object valor)
			throws TramitacioException {
		delegate.setVariableProces(entornCodi, processInstanceId, varCodi, valor);
	}

	@Override
	public void esborrarVariableProces(String entornCodi, String processInstanceId, String varCodi)
			throws TramitacioException {
		delegate.esborrarVariableProces(entornCodi, processInstanceId, varCodi);
	}

	@Override
	public List<DocumentProces> consultarDocumentsProces(String entornCodi, String processInstanceId)
			throws TramitacioException {
		return delegate.consultarDocumentsProces(entornCodi, processInstanceId);
	}

	@Override
	public ArxiuProces getArxiuProces(Long documentId) throws TramitacioException {
		return delegate.getArxiuProces(documentId);
	}

	@Override
	public Long setDocumentProces(String entornCodi, String processInstanceId, String documentCodi, String arxiu,
			Date data, byte[] contingut) throws TramitacioException {
		return delegate.setDocumentProces(entornCodi, processInstanceId, documentCodi, arxiu, data, contingut);
	}

	@Override
	public void esborrarDocumentProces(String entornCodi, String processInstanceId, Long documentId)
			throws TramitacioException {
		delegate.esborrarDocumentProces(entornCodi, processInstanceId, documentId);
	}

	@Override
	public void executarAccioProces(String entornCodi, String processInstanceId, String accio)
			throws TramitacioException {
		delegate.executarAccioProces(entornCodi, processInstanceId, accio);
	}

	@Override
	public void executarScriptProces(String entornCodi, String processInstanceId, String script)
			throws TramitacioException {
		delegate.executarScriptProces(entornCodi, processInstanceId, script);
	}

	@Override
	public void aturarExpedient(String entornCodi, String processInstanceId, String motiu) throws TramitacioException {
		delegate.aturarExpedient(entornCodi, processInstanceId, motiu);
	}

	@Override
	public void reprendreExpedient(String entornCodi, String processInstanceId) throws TramitacioException {
		delegate.reprendreExpedient(entornCodi, processInstanceId);
	}

	@Override
	public List<ExpedientInfo> consultaExpedients(String entornCodi, String titol, String numero, Date dataInici1,
			Date dataInici2, String expedientTipusCodi, String estatCodi, boolean iniciat, boolean finalitzat,
			Double geoPosX, Double geoPosY, String geoReferencia) throws TramitacioException {
		return delegate.consultaExpedients(entornCodi, titol, numero, dataInici1, dataInici2, expedientTipusCodi, estatCodi, iniciat, finalitzat, geoPosX, geoPosY, geoReferencia);
	}

	@Override
	public void deleteExpedient(String entornCodi, String processInstanceId) throws TramitacioException {
		delegate.deleteExpedient(entornCodi, processInstanceId);
	}

}
