/**
 * 
 */
package net.conselldemallorca.helium.ws.backoffice;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cim.ws.backoffice.v1.model.backoffice.ObtenerDominioRequest;
import es.cim.ws.backoffice.v1.model.backoffice.ObtenerDominioResponse;
import es.cim.ws.backoffice.v1.model.backoffice.ProcesarEntradaBandejaRequest;
import es.cim.ws.backoffice.v1.model.backoffice.ProcesarEntradaBandejaResponse;
import es.cim.ws.backoffice.v1.model.backoffice.RealizarConsultaRequest;
import es.cim.ws.backoffice.v1.model.backoffice.RealizarConsultaResponse;
import es.cim.ws.backoffice.v1.model.backoffice.TypeResultadoProcesamiento;
import es.cim.ws.backoffice.v1.model.datosdocumentotelematico.FirmaWS;
import es.cim.ws.backoffice.v1.model.documentoentrada.DocumentoEntrada;
import es.cim.ws.backoffice.v1.model.entrada.Entrada;
import es.cim.ws.backoffice.v1.services.Backoffice;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto.TramitDocumentSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto.TramitAutenticacioTipusDto;

/**
 * Backoffice per a gestionar la recepció de tramits provinents
 * del ESB
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(endpointInterface = "es.cim.ws.backoffice.v1.services.Backoffice")
public class EsbCimBackoffice extends BaseBackoffice implements Backoffice {



	public ProcesarEntradaBandejaResponse procesarEntradaBandeja(
			ProcesarEntradaBandejaRequest request) {
		ProcesarEntradaBandejaResponse response = new ProcesarEntradaBandejaResponse();
		Entrada entrada = request.getTramite();
		if (entrada != null) {
			boolean error = false;
			TramitDto dadesTramit = toDadesTramit(entrada);
			try {
				int numExpedients = processarTramit(dadesTramit);
				logger.info("El tramit [" + dadesTramit.getNumero() + ", " + dadesTramit.getClauAcces() + "] ha creat " + numExpedients + " expedients");
			} catch (Exception ex) {
				logger.error("Error a l'hora de processar el tramit [" + dadesTramit.getNumero() + ", " + dadesTramit.getClauAcces() + "]", ex);
				error = true;
			}
			if (!error)
				response.setResultadoProcesamiento(TypeResultadoProcesamiento.PROCESADA);
			else
				response.setResultadoProcesamiento(TypeResultadoProcesamiento.PROCESADA_ERROR);
		} else {
			response.setResultadoProcesamiento(TypeResultadoProcesamiento.PROCESADA);
		}
		return response;
	}

	public ObtenerDominioResponse obtenerDominio(ObtenerDominioRequest arg0) {
		return null;
	}

	public RealizarConsultaResponse realizarConsulta(
			RealizarConsultaRequest arg0) {
		return null;
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
			return pluginHelper.tramitacioObtenirVistaDocument(request);
		} catch (Exception ex) {
			return null;
		}
	}



	private TramitDto toDadesTramit(Entrada entrada) {
		TramitDto tramit = new TramitDto();
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
				tramit.setAutenticacioTipus(TramitAutenticacioTipusDto.ANONIMA);
			if ("U".equalsIgnoreCase(entrada.getNivelAutenticacion()))
				tramit.setAutenticacioTipus(TramitAutenticacioTipusDto.USUARI);
			if ("C".equalsIgnoreCase(entrada.getNivelAutenticacion()))
				tramit.setAutenticacioTipus(TramitAutenticacioTipusDto.CERTIFICAT);
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
			List<TramitDocumentDto> documents = new ArrayList<TramitDocumentDto>();
			for (DocumentoEntrada documento: entrada.getDocumentos().getDocumento()) {
				TramitDocumentDto document = new TramitDocumentDto();
				document.setIdentificador(documento.getIdentificador());
				document.setNom(documento.getNombre());
				document.setInstanciaNumero(documento.getNumeroInstancia());
				if (documento.getPresentacionPresencial() != null && documento.getPresentacionPresencial().getValue() != null) {
					document.setPresencial(true);
					document.setPresencialDocumentCompulsar(documento.getPresentacionPresencial().getValue().getCompulsarDocumento());
					document.setPresencialSignatura(documento.getPresentacionPresencial().getValue().getFirma());
					document.setPresencialFotocopia(documento.getPresentacionPresencial().getValue().getFotocopia());
					document.setPresencialTipus(documento.getPresentacionPresencial().getValue().getTipoDocumento().value());
				}
				if (documento.getPresentacionTelematica() != null && documento.getPresentacionTelematica().getValue() != null) {
					if (documento.getPresentacionTelematica().getValue().getReferenciaGestorDocumental() != null)
						document.setTelematic(true);
					if (documento.getPresentacionTelematica().getValue().getExtension() != null)
						document.setTelematicArxiuExtensio(
								documento.getPresentacionTelematica().getValue().getExtension().getValue());
					if (documento.getPresentacionTelematica().getValue().getContent() != null) {
						document.setTelematicArxiuNom(
								documento.getNombre());
						document.setTelematicArxiuContingut(
								documento.getPresentacionTelematica().getValue().getContent().getValue());
					}
					if (documento.getPresentacionTelematica().getValue().getFirmas() != null && documento.getPresentacionTelematica().getValue().getFirmas().getValue() != null) {
						List<TramitDocumentSignaturaDto> signatures = new ArrayList<TramitDocumentSignaturaDto>();
						for (FirmaWS firma: documento.getPresentacionTelematica().getValue().getFirmas().getValue().getFirma()) {
							TramitDocumentSignaturaDto signatura = 
									document.newDocumentSignatura(
											firma.getFirma(),
											firma.getFormato());
							signatures.add(signatura);
						}
						document.setTelematicSignatures(signatures);
					}
				}
				documents.add(document);
			}
			tramit.setDocuments(documents);
		}
		return tramit;
	}

	private static final Log logger = LogFactory.getLog(EsbCimBackoffice.class);

}
