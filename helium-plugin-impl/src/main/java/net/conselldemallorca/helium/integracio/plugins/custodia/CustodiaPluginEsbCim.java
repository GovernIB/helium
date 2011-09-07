/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.gesdoc.GestioDocumentalPluginException;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cim.ws.documentos.v1.model.gestordocumental.AdjuntarFirmaDocumentoRequest;
import es.cim.ws.documentos.v1.model.gestordocumental.AdjuntarFirmaDocumentoResponse;
import es.cim.ws.documentos.v1.model.gestordocumental.ObtenerFirmasDocumentoRequest;
import es.cim.ws.documentos.v1.model.gestordocumental.ObtenerFirmasDocumentoResponse;
import es.cim.ws.documentos.v1.model.gestordocumental.TypeCodigoError;
import es.cim.ws.documentos.v1.model.gestordocumental.TypeFirma;
import es.cim.ws.documentos.v1.model.gestordocumental.TypeFormatoFirma;
import es.cim.ws.documentos.v1.model.gestordocumental.TypeListaFirmas;
import es.cim.ws.documentos.v1.services.ServicioGestorDocumentalPortType;

/**
 * Implementació del plugin de custodia documental que guarda
 * les signatures dins la gestió documental del ESB del Consell
 * de Mallorca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginEsbCim implements CustodiaPlugin {

	public String addSignature(
			String id,
			String gesdocId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura) throws CustodiaPluginException {
		try {
			AdjuntarFirmaDocumentoRequest request = new AdjuntarFirmaDocumentoRequest();
			request.setReferenciaDocumento(gesdocId);
			request.setTipoFirma(getTipoFirma());
			request.setFirmaElectronica(signatura);
			AdjuntarFirmaDocumentoResponse response = getGestorDocumentalClient().adjuntarFirmaDocumento(request);
			if (TypeCodigoError.ERROR.equals(response.getCodigoError())) {
				throw new GestioDocumentalPluginException("Error al custodiar la signatura: " + response.getDescripcionError());
			}
			return gesdocId;
		} catch (Exception ex) {
			logger.error("No s'ha pogut custodiar la signatura", ex);
			throw new CustodiaPluginException("No s'ha pogut custodiar la signatura", ex);
		}
	}

	public List<byte[]> getSignatures(String id) throws CustodiaPluginException {
		try {
			ObtenerFirmasDocumentoRequest request = new ObtenerFirmasDocumentoRequest();
			request.setReferenciaDocumento(id);
			ObtenerFirmasDocumentoResponse response = getGestorDocumentalClient().obtenerFirmasDocumento(request);
			if (TypeCodigoError.OK.equals(response.getCodigoError())) {
				List<byte[]> signatures = null;
				TypeListaFirmas firmas = response.getFirmas();
				if (firmas != null) {
					signatures = new ArrayList<byte[]>();
					for (TypeFirma firma: firmas.getFirma())
						signatures.add(firma.getFirma());
				}
				return signatures;
			} else {
				throw new GestioDocumentalPluginException("Error al obtenir les signatures: " + response.getDescripcionError());
			}
		} catch (Exception ex) {
			logger.error("No s'han pogut obtenir les signatures", ex);
			throw new CustodiaPluginException("No s'han pogut obtenir les signatures", ex);
		}
	}

	public byte[] getSignaturesAmbArxiu(String id) throws CustodiaPluginException {
		throw new CustodiaPluginException("Aquest plugin no suporta les signatures adjuntades a dins un arxiu");
	}

	public void deleteSignatures(String id) throws CustodiaPluginException {
		throw new CustodiaPluginException("Aquest plugin no suporta la funcionalitat d'eliminar signatures");
	}

	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id) throws CustodiaPluginException {
		throw new CustodiaPluginException("Aquest plugin no suporta la funcionalitat de validació de signatures");
	}

	public boolean potObtenirInfoSignatures() {
		return false;
	}
	public boolean isValidacioImplicita() {
		return false;
	}

	public String getUrlComprovacioSignatura(
			String id) throws CustodiaPluginException {
		return null;
	}



	private TypeFormatoFirma getTipoFirma() {
		return TypeFormatoFirma.fromValue(
				GlobalProperties.getInstance().getProperty("app.custodia.plugin.esbcim.tipo.firma"));
	}
	private ServicioGestorDocumentalPortType getGestorDocumentalClient() {
		String url = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.url");
		String userName = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.user");
		String password = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.pass");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				ServicioGestorDocumentalPortType.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck());
		return (ServicioGestorDocumentalPortType)wsClientProxy;
	}

	private String getWsClientAuthType() {
		String authType = GlobalProperties.getInstance().getProperty("app.custodia.plugin.ws.client.auth");
		if (authType == null)
			authType = GlobalProperties.getInstance().getProperty("app.ws.client.auth");
		return authType;
	}
	private boolean isWsClientGenerateTimestamp() {
		String authType = GlobalProperties.getInstance().getProperty("app.custodia.plugin.ws.client.generate.timestamp");
		if (authType == null)
			authType = GlobalProperties.getInstance().getProperty("app.ws.client.generate.timestamp");
		return "true".equalsIgnoreCase(authType);
	}
	private boolean isWsClientLogCalls() {
		String logCalls = GlobalProperties.getInstance().getProperty("app.custodia.plugin.ws.client.log.calls");
		if (logCalls == null)
			logCalls = GlobalProperties.getInstance().getProperty("app.ws.client.log.calls");
		return "true".equalsIgnoreCase(logCalls);
	}
	private boolean isWsClientDisableCnCheck() {
		String disableCnCheck = GlobalProperties.getInstance().getProperty("app.custodia.plugin.ws.client.disable.cn.check");
		if (disableCnCheck == null)
			disableCnCheck = GlobalProperties.getInstance().getProperty("app.ws.client.disable.cn.check");
		return "true".equalsIgnoreCase(disableCnCheck);
	}

	private static final Log logger = LogFactory.getLog(CustodiaPluginEsbCim.class);

}
