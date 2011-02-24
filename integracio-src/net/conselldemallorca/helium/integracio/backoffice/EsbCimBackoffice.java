/**
 * 
 */
package net.conselldemallorca.helium.integracio.backoffice;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentPresencial;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTelematic;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Signatura;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPlugin;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginEsbCim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cim.ws.backoffice.v1.model.backoffice.ParametrosDominio;
import es.cim.ws.backoffice.v1.model.backoffice.PeticionProcesarEntrada;
import es.cim.ws.backoffice.v1.model.backoffice.RespuestaProcesarEntrada;
import es.cim.ws.backoffice.v1.model.backoffice.TypeCodigoError;
import es.cim.ws.backoffice.v1.model.backoffice.TypeResultadoProcesamiento;
import es.cim.ws.backoffice.v1.model.datosdocumentotelematico.DatosDocumentoTelematico;
import es.cim.ws.backoffice.v1.model.documentoconsulta.DocumentosConsulta;
import es.cim.ws.backoffice.v1.model.documentoentrada.DocumentoEntrada;
import es.cim.ws.backoffice.v1.model.entrada.Entrada;
import es.cim.ws.backoffice.v1.model.formularioconsulta.FormulariosConsulta;
import es.cim.ws.backoffice.v1.model.valoresdominio.ValoresDominio;
import es.cim.ws.backoffice.v1.services.Backoffice;
import es.cim.ws.backoffice.v1.model.datosdocumentotelematico.FirmaWS;

/**
 * Backoffice per a gestionar la recepció de tramits provinents
 * del ESB
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(endpointInterface = "es.cim.ws.backoffice.v1.services.Backoffice")
public class EsbCimBackoffice extends BaseBackoffice implements Backoffice {

	TramitacioPlugin tramitacioPlugin = new TramitacioPluginEsbCim();



	public RespuestaProcesarEntrada procesarEntrada(PeticionProcesarEntrada peticion) {
		RespuestaProcesarEntrada respuesta = new RespuestaProcesarEntrada();
		Entrada entrada = peticion.getTramite();
		if (entrada != null) {
			boolean error = false;
			DadesTramit dadesTramit = toDadesTramit(entrada);
			try {
				int numExpedients = processarTramit(dadesTramit);
				logger.info("El tramit [" + dadesTramit.getNumero() + ", " + dadesTramit.getClauAcces() + "] ha creat " + numExpedients + " expedients");
			} catch (Exception ex) {
				logger.error("Error a l'hora de processar el tramit [" + dadesTramit.getNumero() + ", " + dadesTramit.getClauAcces() + "]", ex);
				error = true;
			}
			if (!error)
				respuesta.setResultadoProcesamiento(TypeResultadoProcesamiento.PROCESADA);
			else
				respuesta.setResultadoProcesamiento(TypeResultadoProcesamiento.PROCESADA_ERROR);
		} else {
			respuesta.setResultadoProcesamiento(TypeResultadoProcesamiento.PROCESADA);
		}
		return respuesta;
	}

	public void realizarConsulta(
			String arg0,
			FormulariosConsulta arg1,
			Holder<TypeCodigoError> arg2,
			Holder<String> arg3,
			Holder<DocumentosConsulta> arg4) {
		
	}

	public void obtenerDominio(
			String arg0,
			ParametrosDominio arg1,
			Holder<TypeCodigoError> arg2,
			Holder<String> arg3,
			Holder<ValoresDominio> arg4) {
		
	}

	protected DadesVistaDocument getVistaDocumentTramit(
			long referenciaCodi,
			String referenciaClau,
			String plantillaTipus,
			String idioma) {
		ObtenirVistaDocumentRequest request = new ObtenirVistaDocumentRequest();
		request.setReferenciaCodi(referenciaCodi);
		request.setReferenciaClau(referenciaClau);
		request.setPlantillaTipus(plantillaTipus);
		request.setIdioma(idioma);
		try {
			return tramitacioPlugin.obtenirVistaDocument(request);
		} catch (Exception ex) {
			return null;
		}
	}



	private DadesTramit toDadesTramit(Entrada entrada) {
		DadesTramit tramit = new DadesTramit();
		tramit.setNumero(entrada.getNumeroEntrada());
		tramit.setIdentificador(entrada.getIdentificadorTramite());
		tramit.setVersio(entrada.getVersionTramite());
		tramit.setIdioma(entrada.getIdioma());
		tramit.setRegistreNumero(entrada.getNumeroRegistro());
		if (entrada.getFechaRegistro() != null)
			tramit.setRegistreData(
					entrada.getFechaRegistro().toGregorianCalendar().getTime());
		if (entrada.getTipoConfirmacionPreregistro() != null)
			tramit.setPreregistreTipusConfirmacio(entrada.getTipoConfirmacionPreregistro().getValue());
		if (entrada.getNumeroPreregistro() != null)
			tramit.setPreregistreNumero(entrada.getNumeroPreregistro().getValue());
		if (entrada.getFechaPreregistro() != null && entrada.getFechaPreregistro().getValue() != null)
			tramit.setPreregistreData(
					entrada.getFechaPreregistro().getValue().toGregorianCalendar().getTime());
		if (entrada.getNivelAutenticacion() != null) {
			if ("A".equalsIgnoreCase(entrada.getNivelAutenticacion()))
				tramit.setAutenticacioTipus(AutenticacioTipus.ANONIMA);
			if ("U".equalsIgnoreCase(entrada.getNivelAutenticacion()))
				tramit.setAutenticacioTipus(AutenticacioTipus.USUARI);
			if ("C".equalsIgnoreCase(entrada.getNivelAutenticacion()))
				tramit.setAutenticacioTipus(AutenticacioTipus.CERTIFICAT);
		}
		if (entrada.getUsuarioNif() != null)
			tramit.setTramitadorNif(entrada.getUsuarioNif().getValue());
		if (entrada.getUsuarioNombre() != null)
			tramit.setTramitadorNom(entrada.getUsuarioNombre().getValue());
		if (entrada.getRepresentadoNif() != null)
			tramit.setInteressatNif(entrada.getRepresentadoNif().getValue());
		if (entrada.getRepresentadoNombre() != null)
			tramit.setInteressatNom(entrada.getRepresentadoNombre().getValue());
		if (entrada.getDelegadoNif() != null)
			tramit.setRepresentantNif(entrada.getDelegadoNif().getValue());
		if (entrada.getDelegadoNombre() != null)
			tramit.setRepresentantNom(entrada.getDelegadoNombre().getValue());
		tramit.setSignat(entrada.isFirmadaDigitalmente());
		if (entrada.getHabilitarAvisos() != null)
			tramit.setAvisosHabilitats(
					"S".equalsIgnoreCase(entrada.getHabilitarAvisos().getValue()));
		if (entrada.getAvisoSMS() != null)
			tramit.setAvisosSms(entrada.getAvisoSMS().getValue());
		if (entrada.getAvisoEmail() != null)
			tramit.setAvisosEmail(entrada.getAvisoEmail().getValue());
		if (entrada.getHabilitarNotificacionTelematica() != null)
			tramit.setNotificacioTelematicaHabilitada(
					"S".equalsIgnoreCase(entrada.getHabilitarNotificacionTelematica().getValue()));
		if (entrada.getDocumentos() != null) {
			List<DocumentTramit> documents = new ArrayList<DocumentTramit>();
			for (DocumentoEntrada documento: entrada.getDocumentos().getDocumento()) {
				DocumentTramit document = new DocumentTramit();
				document.setNom(documento.getNombre());
				document.setIdentificador(documento.getIdentificador());
				document.setInstanciaNumero(documento.getNumeroInstancia());
				if (documento.getPresentacionPresencial() != null && documento.getPresentacionPresencial().getValue() != null) {
					DocumentPresencial documentPresencial = new DocumentPresencial();
					documentPresencial.setDocumentCompulsar(
							documento.getPresentacionPresencial().getValue().getCompulsarDocumento());
					documentPresencial.setSignatura(
							documento.getPresentacionPresencial().getValue().getFirma());
					documentPresencial.setFotocopia(
							documento.getPresentacionPresencial().getValue().getFotocopia());
					documentPresencial.setTipus(
							documento.getPresentacionPresencial().getValue().getTipoDocumento());
					document.setDocumentPresencial(documentPresencial);
				}
				if (documento.getPresentacionTelematica() != null && documento.getPresentacionTelematica().getValue() != null) {
					DatosDocumentoTelematico documentoTelematico = documento.getPresentacionTelematica().getValue();
					DocumentTelematic documentTelematic = new DocumentTelematic();
					if (documentoTelematico.getExtension() != null)
						documentTelematic.setArxiuExtensio(
								documentoTelematico.getExtension().getValue());
					if (documentoTelematico.getContent() != null)
						documentTelematic.setArxiuContingut(
								documentoTelematico.getContent().getValue());
					if (documentoTelematico.getReferenciaGestorDocumental() != null)
						documentTelematic.setReferenciaGestorDocumental(
								documentoTelematico.getReferenciaGestorDocumental().getValue());
					if (documentoTelematico.getFirmas() != null && documentoTelematico.getFirmas().getValue() != null) {
						List<Signatura> signatures = new ArrayList<Signatura>();
						for (FirmaWS firma: documentoTelematico.getFirmas().getValue().getFirmas()) {
							Signatura signatura = new Signatura();
							signatura.setFormat(firma.getFormato());
							signatura.setSignatura(firma.getFirma());
							signatures.add(signatura);
						}
						documentTelematic.setSignatures(signatures);
					}
					document.setDocumentTelematic(documentTelematic);
				}
				documents.add(document);
			}
			tramit.setDocuments(documents);
		}
		return tramit;
	}

	private static final Log logger = LogFactory.getLog(EsbCimBackoffice.class);

}
