package es.caib.helium.integracio.service.tramitacio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.netflix.servo.util.Strings;

import es.caib.bantel.ws.v2.model.documentobte.DocumentoBTE;
import es.caib.bantel.ws.v2.model.firmaws.FirmaWS;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;
import es.caib.bantel.ws.v2.model.tramitebte.TramiteBTE;
import es.caib.bantel.ws.v2.services.BackofficeFacade;
import es.caib.helium.integracio.domini.tramitacio.DadesTramit;
import es.caib.helium.integracio.domini.tramitacio.DocumentPresencial;
import es.caib.helium.integracio.domini.tramitacio.DocumentTelematic;
import es.caib.helium.integracio.domini.tramitacio.DocumentTramit;
import es.caib.helium.integracio.domini.tramitacio.RespostaJustificantRecepcio;
import es.caib.helium.integracio.domini.tramitacio.Signatura;
import es.caib.helium.integracio.enums.tramitacio.AutenticacioTipus;
import es.caib.helium.integracio.excepcions.tramitacio.TramitacioException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TramitacioServiceSistraImpl implements TramitacioService {
	
	private BackofficeFacade wsClientProxy;

	@Override
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws TramitacioException {

		try {
			RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
//			try {
//				AcuseRecibo acuseRecibo = getRegtelClient().obtenerAcuseRecibo(numeroRegistre);
//				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
//				if (acuseRecibo != null && acuseRecibo.getFechaAcuseRecibo() != null) {
//					resposta.setData(acuseRecibo.getFechaAcuseRecibo().toGregorianCalendar().getTime());
//				}
//			} catch (BackofficeFacadeException ex) {
//				logger.error("Error al obtenir el justificant de recepció", ex);
//				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
//				resposta.setErrorDescripcio(ex.getMessage());
//			}
			return resposta;
		} catch (Exception ex) {
			log.error("Error al obtenir el justificant de recepció", ex);
			throw new TramitacioException("Error al obtenir el justificant de recepció", ex);
		}
	}
	
	@Override
	public DadesTramit obtenirDadesTramit(String numero, String clau) throws TramitacioException {
		
		if (Strings.isNullOrEmpty(numero) || Strings.isNullOrEmpty(clau)) {
			return null;
		}
		
		try {
			ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
			referenciaEntrada.setNumeroEntrada(numero);
			referenciaEntrada.setClaveAcceso(clau);
			return toDadesTramit(wsClientProxy.obtenerEntrada(referenciaEntrada));
		} catch (Exception ex) {
			log.error("Error al obtenir dades del tràmit", ex);
			throw new TramitacioException("Error al obtenir dades del tràmit", ex);
		}
	}
	
	private DadesTramit toDadesTramit(TramiteBTE entrada) {
		DadesTramit tramit = new DadesTramit();
		tramit.setNumero(entrada.getNumeroEntrada());
		tramit.setClauAcces(entrada.getCodigoEntrada());
		tramit.setIdentificador(entrada.getIdentificadorTramite());
		tramit.setUnitatAdministrativa(entrada.getUnidadAdministrativa());
		tramit.setVersio(entrada.getVersionTramite());
		if (entrada.getFecha() != null)
			tramit.setData(entrada.getFecha().toGregorianCalendar().getTime());
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
		if (entrada.getRepresentadoNif() != null) {
			tramit.setInteressatNif(entrada.getRepresentadoNif().getValue());
		} else if (entrada.getUsuarioNif() != null) {
			tramit.setInteressatNif(entrada.getUsuarioNif().getValue());
		}
		if (entrada.getRepresentadoNombre() != null) {
			tramit.setInteressatNom(entrada.getRepresentadoNombre().getValue());
		} else if (entrada.getUsuarioNombre() != null) {
			tramit.setInteressatNom(entrada.getUsuarioNombre().getValue());
		}
		if (entrada.getUsuarioNif() != null)
			tramit.setRepresentantNif(entrada.getUsuarioNif().getValue());
		if (entrada.getUsuarioNombre() != null)
			tramit.setRepresentantNom(entrada.getUsuarioNombre().getValue());
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
			for (DocumentoBTE documento: entrada.getDocumentos().getDocumento()) {
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
					DocumentTelematic documentTelematic = new DocumentTelematic();
					documentTelematic.setArxiuNom(
							documento.getPresentacionTelematica().getValue().getNombre());
					documentTelematic.setArxiuExtensio(
							documento.getPresentacionTelematica().getValue().getExtension());
					documentTelematic.setArxiuContingut(
							documento.getPresentacionTelematica().getValue().getContent());
					documentTelematic.setReferenciaCodi(
							documento.getPresentacionTelematica().getValue().getCodigoReferenciaRds());
					documentTelematic.setReferenciaClau(
							documento.getPresentacionTelematica().getValue().getClaveReferenciaRds());
					if (documento.getPresentacionTelematica().getValue().getFirmas() != null && documento.getPresentacionTelematica().getValue().getFirmas() != null) {
						List<Signatura> signatures = new ArrayList<Signatura>();
						for (FirmaWS firma: documento.getPresentacionTelematica().getValue().getFirmas().getFirmas()) {
							Signatura signatura = new Signatura();
							if (firma.getFormato() != null)
								signatura.setFormat(firma.getFormato().getValue());
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
	
	public void crearClientBantel(
			String url, 
			String user, 
			String password, 
			String wsClientAuthType,
			boolean wsClientGenerateTimestamp,
			boolean wsClientLogCalls,
			boolean wsClientDisableCnCheck,
			boolean wsClientChunked) {

		wsClientProxy = (BackofficeFacade) WsClientUtils.getWsClientProxy(
				BackofficeFacade.class,
				url,
				user,
				password,
				wsClientAuthType,
				wsClientGenerateTimestamp,
				wsClientLogCalls,
				wsClientDisableCnCheck,
				null,
				wsClientChunked);
	}
}
