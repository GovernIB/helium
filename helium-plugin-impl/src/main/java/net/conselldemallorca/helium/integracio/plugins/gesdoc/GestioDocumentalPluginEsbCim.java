/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.gesdoc;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cim.ws.documentos.v1.model.gestordocumental.EliminarDocumentoRequest;
import es.cim.ws.documentos.v1.model.gestordocumental.EliminarDocumentoResponse;
import es.cim.ws.documentos.v1.model.gestordocumental.InsertarDocumentoExpedienteRequest;
import es.cim.ws.documentos.v1.model.gestordocumental.ObtenerDocumentoRequest;
import es.cim.ws.documentos.v1.model.gestordocumental.ObtenerDocumentoResponse;
import es.cim.ws.documentos.v1.model.gestordocumental.TypeCodigoError;
import es.cim.ws.documentos.v1.model.gestordocumental.TypeRespuestaRefDocumento;
import es.cim.ws.documentos.v1.services.ServicioGestorDocumentalPortType;

/**
 * Implementació del plugin de gestió documental emprant el
 * servei de gestió documental del ESB del Consell de Mallorca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GestioDocumentalPluginEsbCim implements GestioDocumentalPlugin {

	public String createDocument(
			String expedientNumero,
			String expedientTipus,
			String documentCodi,
			String documentDescripcio,
			Date documentData,
			String documentArxiuNom,
			byte[] documentArxiuContingut) throws GestioDocumentalPluginException {
		try {
			InsertarDocumentoExpedienteRequest request = new InsertarDocumentoExpedienteRequest();
			request.setCodigoDocumento(documentCodi);
			request.setDescripcionDocumento(documentDescripcio);
			request.setNombreDocumento(documentArxiuNom);
			request.setContenidoDocumento(documentArxiuContingut);
			request.setNumeroExpediente(expedientNumero);
			request.setTipoProcedimiento(expedientTipus);
			request.setAnyo(new SimpleDateFormat("yyyy").format(new Date()));
			TypeRespuestaRefDocumento response = getGestorDocumentalClient().insertarDocumentoExpediente(request);
			if (TypeCodigoError.OK.equals(response.getCodigoError())) {
				return response.getReferenciaDocumento();
			} else {
				throw new GestioDocumentalPluginException("Error al guardar l'arxiu dins el gestor documental: " + response.getDescripcionError());
			}
		} catch (Exception ex) {
			logger.error("Error al guardar l'arxiu dins el gestor documental", ex);
			throw new GestioDocumentalPluginException("Error al guardar l'arxiu dins el gestor documental", ex);
		}
	}

	public byte[] retrieveDocument(String documentId) throws GestioDocumentalPluginException {
		try {
			ObtenerDocumentoRequest request = new ObtenerDocumentoRequest();
			request.setReferenciaDocumento(documentId);
			ObtenerDocumentoResponse response = getGestorDocumentalClient().obtenerDocumento(request);
			if (TypeCodigoError.OK.equals(response.getCodigoError())) {
				return response.getContenidoDocumento();
			} else {
				throw new GestioDocumentalPluginException("Error al guardar l'arxiu " + documentId + " dins el gestor documental: " + response.getDescripcionError());
			}
		} catch (Exception ex) {
			logger.error("Error al llegir l'arxiu " + documentId + " del gestor documental", ex);
			throw new GestioDocumentalPluginException("Error al llegir l'arxiu " + documentId + " del gestor documental", ex);
		}
	}

	public void deleteDocument(String documentId) throws GestioDocumentalPluginException {
		try {
			EliminarDocumentoRequest request = new EliminarDocumentoRequest();
			request.setReferenciaDocumento(documentId);
			EliminarDocumentoResponse response = getGestorDocumentalClient().eliminarDocumento(request);
			if (TypeCodigoError.ERROR.equals(response.getCodigoError())) {
				throw new GestioDocumentalPluginException("Error al esborrar l'arxiu " + documentId + " del gestor documental: " + response.getDescripcionError());
			}
		} catch (Exception ex) {
			logger.error("Error al esborrar l'arxiu " + documentId + " del gestor documental", ex);
			throw new GestioDocumentalPluginException("Error al esborrar l'arxiu " + documentId + " del gestor documental", ex);
		}
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
				isWsClientDisableCnCheck(),
				null);
		return (ServicioGestorDocumentalPortType)wsClientProxy;
	}

	private String getWsClientAuthType() {
		String authType = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.ws.client.auth");
		if (authType == null)
			authType = GlobalProperties.getInstance().getProperty("app.ws.client.auth");
		return authType;
	}
	private boolean isWsClientGenerateTimestamp() {
		String authType = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.ws.client.generate.timestamp");
		if (authType == null)
			authType = GlobalProperties.getInstance().getProperty("app.ws.client.generate.timestamp");
		return "true".equalsIgnoreCase(authType);
	}
	private boolean isWsClientLogCalls() {
		String logCalls = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.ws.client.log.calls");
		if (logCalls == null)
			logCalls = GlobalProperties.getInstance().getProperty("app.ws.client.log.calls");
		return "true".equalsIgnoreCase(logCalls);
	}
	private boolean isWsClientDisableCnCheck() {
		String disableCnCheck = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.ws.client.disable.cn.check");
		if (disableCnCheck == null)
			disableCnCheck = GlobalProperties.getInstance().getProperty("app.ws.client.disable.cn.check");
		return "true".equalsIgnoreCase(disableCnCheck);
	}

	private static final Log logger = LogFactory.getLog(GestioDocumentalPluginEsbCim.class);

}
