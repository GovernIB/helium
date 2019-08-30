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
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.ArxiuProces;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.CampProces;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.CampTasca;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.DocumentProces;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.DocumentTasca;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.ExpedientInfo;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.ParellaCodiValor;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.TascaTramitacio;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.TramitacioException;
import net.conselldemallorca.helium.v3.core.service.ws.tramitacio.TramitacioService;

/**
 * EJB pel servei de callback de portafirmes amb interfície MCGD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "Tramitacio",
		serviceName = "TramitacioService",
		portName = "TramitacioPort",
		targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/")
@WebContext(
		contextRoot = "/helium/ws",
		urlPattern = "/TramitacioService",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TramitacioBean implements TramitacioService {

	@Autowired
	private TramitacioService delegate;

	@Override
	public String iniciExpedient(String entornCodi, String usuariCodi, String expedientTipusCodi, String numero,
			String titol, List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
		return delegate.iniciExpedient(entornCodi, usuariCodi, expedientTipusCodi, numero, titol, valorsFormulari);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonals(String entornCodi, String usuari) throws TramitacioException {
		return delegate.consultaTasquesPersonals(entornCodi, usuari);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrup(String entornCodi, String usuari) throws TramitacioException {
		return delegate.consultaTasquesGrup(entornCodi, usuari);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(String entornCodi, String usuariCodi,
			String expedientNumero) throws TramitacioException {
		return delegate.consultaTasquesPersonalsByCodi(entornCodi, usuariCodi, expedientNumero);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrupByCodi(String entornCodi, String usuariCodi, String expedientNumero)
			throws TramitacioException {
		return delegate.consultaTasquesGrupByCodi(entornCodi, usuariCodi, expedientNumero);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonalsByProces(String entornCodi, String usuariCodi,
			String processInstanceId) throws TramitacioException {
		return delegate.consultaTasquesPersonalsByProces(entornCodi, usuariCodi, processInstanceId);
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrupByProces(String entornCodi, String usuariCodi,
			String processInstanceId) throws TramitacioException {
		return delegate.consultaTasquesGrupByProces(entornCodi, usuariCodi, processInstanceId);
	}

	@Override
	public void agafarTasca(String entornCodi, String usuariCodi, String tascaId) throws TramitacioException {
		delegate.agafarTasca(entornCodi, usuariCodi, tascaId);
	}

	@Override
	public void alliberarTasca(String entornCodi, String usuariCodi, String tascaId) throws TramitacioException {
		delegate.alliberarTasca(entornCodi, usuariCodi, tascaId);
	}

	@Override
	public List<CampTasca> consultaFormulariTasca(String entornCodi, String usuariCodi, String tascaId)
			throws TramitacioException {
		return delegate.consultaFormulariTasca(entornCodi, usuariCodi, tascaId);
	}

	@Override
	public void setDadesFormulariTasca(String entornCodi, String usuariCodi, String tascaId,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
		delegate.setDadesFormulariTasca(entornCodi, usuariCodi, tascaId, valorsFormulari);
	}

	@Override
	public List<DocumentTasca> consultaDocumentsTasca(String entornCodi, String usuariCodi, String tascaId)
			throws TramitacioException {
		return delegate.consultaDocumentsTasca(entornCodi, usuariCodi, tascaId);
	}

	@Override
	public void setDocumentTasca(String entornCodi, String usuariCodi, String tascaId, String documentCodi,
			String arxiuNom, Date documentData, byte[] arxiuContingut) throws TramitacioException {
		delegate.setDocumentTasca(entornCodi, usuariCodi, tascaId, documentCodi, arxiuNom, documentData, arxiuContingut);
	}

	@Override
	public void esborrarDocumentTasca(String entornCodi, String usuariCodi, String tascaId, String documentCodi)
			throws TramitacioException {
		delegate.esborrarDocumentTasca(entornCodi, usuariCodi, tascaId, documentCodi);
	}

	@Override
	public void finalitzarTasca(String entornCodi, String usuariCodi, String tascaId, String transicio)
			throws TramitacioException {
		delegate.finalitzarTasca(entornCodi, usuariCodi, tascaId, transicio);		
	}

	@Override
	public ExpedientInfo getExpedientInfo(String entornCodi, String usuariCodi, String processInstanceId)
			throws TramitacioException {
		return delegate.getExpedientInfo(entornCodi, usuariCodi, processInstanceId);
	}

	@Override
	public List<CampProces> consultarVariablesProces(String entornCodi, String usuariCodi, String processInstanceId)
			throws TramitacioException {
		return delegate.consultarVariablesProces(entornCodi, usuariCodi, processInstanceId);
	}

	@Override
	public void setVariableProces(String entornCodi, String usuariCodi, String processInstanceId, String varCodi,
			Object varValor) throws TramitacioException {
		delegate.setVariableProces(entornCodi, usuariCodi, processInstanceId, varCodi, varValor);
	}

	@Override
	public void esborrarVariableProces(String entornCodi, String usuariCodi, String processInstanceId, String varCodi)
			throws TramitacioException {
		delegate.esborrarVariableProces(entornCodi, usuariCodi, processInstanceId, varCodi);
	}

	@Override
	public List<DocumentProces> consultarDocumentsProces(String entornCodi, String usuariCodi, String processInstanceId)
			throws TramitacioException {
		return delegate.consultarDocumentsProces(entornCodi, usuariCodi, processInstanceId);
	}

	@Override
	public ArxiuProces getArxiuProces(Long documentId) throws TramitacioException {
		return delegate.getArxiuProces(documentId);
	}

	@Override
	public Long setDocumentProces(String entornCodi, String usuariCodi, String processInstanceId, String documentCodi,
			String arxiuNom, Date documentData, byte[] arxiuContingut) throws TramitacioException {
		return delegate.setDocumentProces(entornCodi, usuariCodi, processInstanceId, documentCodi, arxiuNom, documentData, arxiuContingut);
	}

	@Override
	public void esborrarDocumentProces(String entornCodi, String usuariCodi, String processInstanceId, Long documentId)
			throws TramitacioException {
		delegate.esborrarDocumentProces(entornCodi, usuariCodi, processInstanceId, documentId);
	}

	@Override
	public void executarAccioProces(String entornCodi, String usuariCodi, String processInstanceId, String accio)
			throws TramitacioException {
		delegate.executarAccioProces(entornCodi, usuariCodi, processInstanceId, accio);
	}

	@Override
	public void executarScriptProces(String entornCodi, String usuariCodi, String processInstanceId, String script)
			throws TramitacioException {
		delegate.executarScriptProces(entornCodi, usuariCodi, processInstanceId, script);
	}

	@Override
	public void aturarExpedient(String entornCodi, String usuariCodi, String processInstanceId, String motiu)
			throws TramitacioException {
		delegate.aturarExpedient(entornCodi, usuariCodi, processInstanceId, motiu);
	}

	@Override
	public void reprendreExpedient(String entornCodi, String usuariCodi, String processInstanceId)
			throws TramitacioException {
		delegate.reprendreExpedient(entornCodi, usuariCodi, processInstanceId);
	}

	@Override
	public List<ExpedientInfo> consultaExpedients(String entornCodi, String usuariCodi, String titol, String numero,
			Date dataInici1, Date dataInici2, String expedientTipusCodi, String estatCodi, boolean iniciat,
			boolean finalitzat, Double geoPosX, Double geoPosY, String geoReferencia) throws TramitacioException {
		return delegate.consultaExpedients(entornCodi, usuariCodi, titol, numero, dataInici1, dataInici2,
				expedientTipusCodi, estatCodi, iniciat, finalitzat, geoPosX, geoPosY, geoReferencia);
	}

	@Override
	public void deleteExpedient(String entornCodi, String usuariCodi, String processInstanceId)
			throws TramitacioException {
		delegate.deleteExpedient(entornCodi, usuariCodi, processInstanceId);
	}

}
