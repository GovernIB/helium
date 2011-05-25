/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.util.Base64;

import es.cim.ws.firma.v1.model.Certificado;
import es.cim.ws.firma.v1.model.PeticionObtenerDatosCertificado;
import es.cim.ws.firma.v1.model.PeticionValidarFirma;
import es.cim.ws.firma.v1.model.RespuestaObtenerDatosCertificado;
import es.cim.ws.firma.v1.model.RespuestaValidarFirma;
import es.cim.ws.firma.v1.model.TypeCodigoError;
import es.cim.ws.firma.v1.model.TypeFormatoFirma;
import es.cim.ws.firma.v1.services.ServicioFirmaPortType;


/**
 * Implementació del plugin de signatura emprant els
 * serveis de @Firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaPluginEsbCim implements SignaturaPlugin {

	public RespostaValidacioSignatura verificarSignatura(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws SignaturaPluginException {
		try {
			RespostaValidacioSignatura resposta = new RespostaValidacioSignatura();
			PeticionValidarFirma peticion = new PeticionValidarFirma();
			peticion.setDatos(Base64.encode(document).getBytes());
			peticion.setFirma(Base64.encode(signatura).getBytes());
			peticion.setFormatoFirma(TypeFormatoFirma.valueOf(getFormatSignatura()));
			RespuestaValidarFirma respuesta = getFirmaClient().validarFirma(peticion);
			if (TypeCodigoError.OK.equals(respuesta.getCodigoError())) {
				PeticionObtenerDatosCertificado peticionDatos = new PeticionObtenerDatosCertificado();
				peticionDatos.setCertificado(respuesta.getCertificado());
				RespuestaObtenerDatosCertificado respuestaDatos = getFirmaClient().obtenerDatosCertificado(peticionDatos);
				if (TypeCodigoError.OK.equals(respuestaDatos.getCodigoError())) {
					Certificado dc = respuestaDatos.getCertificado();
					List<DadesCertificat> dadesCertificats = new ArrayList<DadesCertificat>();
					DadesCertificat dadesCertificat = new DadesCertificat();
					dadesCertificat.setTipoCertificado(dc.getTipoCertificado());
					dadesCertificat.setSubject(dc.getSubject());
					dadesCertificat.setNombreResponsable(dc.getNombre());
					dadesCertificat.setPrimerApellidoResponsable(dc.getPrimerApellido());
					dadesCertificat.setSegundoApellidoResponsable(dc.getSegundoApellido());
					dadesCertificat.setNifResponsable(dc.getNifResponsable());
					dadesCertificat.setIdEmisor(dc.getIdEmisor());
					dadesCertificat.setNifCif(dc.getCif());
					dadesCertificat.setEmail(dc.getEmail());
					dadesCertificat.setFechaNacimiento(dc.getFechaNacimiento());
					dadesCertificat.setRazonSocial(dc.getRazonSocial());
					dadesCertificat.setClasificacion(dc.getClasificacion());
					dadesCertificat.setNumeroSerie(dc.getNumeroSerie());
					dadesCertificats.add(dadesCertificat);
					resposta.setDadesCertificat(dadesCertificats);
				} else {
					resposta.setEstat(RespostaValidacioSignatura.ESTAT_ERROR);
					resposta.setErrorDescripcio(respuestaDatos.getDescripcionError());
					logger.error("Error obtenint les dades del certificat: " + respuestaDatos.getDescripcionError());
				}
			} else {
				resposta.setEstat(RespostaValidacioSignatura.ESTAT_ERROR);
				resposta.setErrorDescripcio(respuesta.getDescripcionError());
				logger.error("Error verificant la signatura: " + respuesta.getDescripcionError());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error verificant la signatura", ex);
			throw new SignaturaPluginException("Error verificant la signatura", ex);
		}
	}



	private ServicioFirmaPortType getFirmaClient() {
		String url = GlobalProperties.getInstance().getProperty("app.signatura.plugin.url");
		String userName = GlobalProperties.getInstance().getProperty("app.signatura.plugin.username");
		String password = GlobalProperties.getInstance().getProperty("app.signatura.plugin.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				ServicioFirmaPortType.class,
				url,
				userName,
				password);
		return (ServicioFirmaPortType)wsClientProxy;
	}
	private String getFormatSignatura() {
		return GlobalProperties.getInstance().getProperty("app.signatura.plugin.format");
	}

	private static final Log logger = LogFactory.getLog(SignaturaPluginEsbCim.class);

}
