package es.caib.helium.integracio.service.tramitacio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Service;

import com.netflix.servo.util.Strings;

import es.caib.bantel.ws.v2.model.documentobte.DocumentoBTE;
import es.caib.bantel.ws.v2.model.firmaws.FirmaWS;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;
import es.caib.bantel.ws.v2.model.tramitebte.TramiteBTE;
import es.caib.bantel.ws.v2.services.BackofficeFacade;
import es.caib.helium.integracio.domini.registre.ReferenciaRDSJustificante;
import es.caib.helium.integracio.domini.registre.RegistreNotificacio;
import es.caib.helium.integracio.domini.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.domini.tramitacio.DadesTramit;
import es.caib.helium.integracio.domini.tramitacio.DadesVistaDocument;
import es.caib.helium.integracio.domini.tramitacio.DocumentPresencial;
import es.caib.helium.integracio.domini.tramitacio.DocumentTelematic;
import es.caib.helium.integracio.domini.tramitacio.DocumentTramit;
import es.caib.helium.integracio.domini.tramitacio.Event;
import es.caib.helium.integracio.domini.tramitacio.ObtenirVistaDocumentRequest;
import es.caib.helium.integracio.domini.tramitacio.PublicarEventRequest;
import es.caib.helium.integracio.domini.tramitacio.PublicarExpedientRequest;
import es.caib.helium.integracio.domini.tramitacio.RespostaJustificantDetallRecepcio;
import es.caib.helium.integracio.domini.tramitacio.RespostaJustificantRecepcio;
import es.caib.helium.integracio.domini.tramitacio.ResultatProcesTramitRequest;
import es.caib.helium.integracio.domini.tramitacio.Signatura;
import es.caib.helium.integracio.enums.tramitacio.AutenticacioTipus;
import es.caib.helium.integracio.enums.tramitacio.ResultatProcesTipus;
import es.caib.helium.integracio.excepcions.tramitacio.TramitacioException;
import es.caib.redose.ws.v2.model.referenciards.ReferenciaRDS;
import es.caib.regtel.ws.v2.model.acuserecibo.AcuseRecibo;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentoExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentosExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventoExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventosExpediente;
import es.caib.zonaper.ws.v2.model.expediente.Expediente;
import es.caib.regtel.ws.v2.services.BackofficeFacadeException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TramitacioServiceSistraImpl implements TramitacioService {
	
	@Setter
	private BackofficeFacade bantelClient;
	@Setter
	es.caib.regtel.ws.v2.services.BackofficeFacade regtelClient;
	@Setter
	es.caib.zonaper.ws.v2.services.BackofficeFacade zonaPerClient;
	@Setter
	es.caib.redose.ws.v2.services.BackofficeFacade redoseClient;
	
	public boolean crearExpedientZonaPersonal(PublicarExpedientRequest request) throws TramitacioException {
		
		try {
			var expediente = new Expediente();
			expediente.setIdentificadorExpediente(request.getExpedientIdentificador());
			expediente.setUnidadAdministrativa(request.getUnitatAdministrativa());
			expediente.setClaveExpediente(request.getExpedientClau());
			expediente.setIdioma(request.getIdioma());
			expediente.setDescripcion(request.getDescripcio());
			expediente.setAutenticado(request.isAutenticat());
			
			/*
			- Amb delegat
			BANTEL			HELIUM			ZONAPER
			Usuario			Tramitador		-
			Representado	Interessat		Representado
			Delegado		Representant	Representante
			- Sense delegat
			BANTEL			HELIUM			ZONAPER
			Usuario			Tramitador		-
			Representado	Interessat		Representado
			Usuario			Representant	Representante
			 */
			if (request.getRepresentantNif() != null) {
				expediente.setNifRepresentante(new JAXBElement<String>(new QName("nifRepresentante"), String.class, request.getRepresentantNif()));
			}
			if (request.getRepresentatNif() != null) {
				expediente.setNifRepresentado(new JAXBElement<String>(new QName("nifRepresentado"), String.class, request.getRepresentatNif()));
			}
			if (request.getRepresentatNom() != null) {
				expediente.setNombreRepresentado(new JAXBElement<String>(new QName("nombreRepresentado"), String.class, request.getRepresentatNom()));
			}
			if (request.getTramitNumero() != null) {
				expediente.setNumeroEntradaBTE(new JAXBElement<String>(new QName("numeroEntradaBTE"), String.class, request.getTramitNumero()));
			}
			var configuracionAvisos = new ConfiguracionAvisosExpediente();
			configuracionAvisos.setHabilitarAvisos(new JAXBElement<Boolean>(new QName("habilitarAvisos"),Boolean.class,request.isAvisosHabilitat()));
			configuracionAvisos.setAvisoEmail(new JAXBElement<String>(new QName("avisoEmail"), String.class, request.getAvisosEmail()));
			configuracionAvisos.setAvisoSMS(new JAXBElement<String>(new QName("avisoSMS"), String.class, request.getAvisosSMS()));
			configuracionAvisos.setHabilitarAvisos(new JAXBElement<Boolean>(new QName("habilitarAvisos"), Boolean.class, request.isAvisosHabilitat()));
			
			expediente.setIdentificadorProcedimiento(new JAXBElement<String>(new QName("identificadorProcedimiento"), String.class, request.getCodiProcediment()));
			expediente.setConfiguracionAvisos(new JAXBElement<ConfiguracionAvisosExpediente>(new QName("configuracionAvisos"), 
					ConfiguracionAvisosExpediente.class, configuracionAvisos));
			
			if (request.getEvents() != null) {
				var eventosExpediente = new EventosExpediente();
				for (Event event: request.getEvents()) {
					eventosExpediente.getEvento().add(toEvento(event));
				}
				expediente.setEventos(new JAXBElement<EventosExpediente>(new QName("eventos"), EventosExpediente.class, eventosExpediente));
			}
			
			crearZonaPers(request.getRepresentatNif(), request.getRepresentatNom(), request.getRepresentatApe1(), request.getRepresentatApe2());
			
			zonaPerClient.altaExpediente(expediente);
			log.info("###===> Nou expedient creat a la zona personal del ciutadà " + request.getRepresentatNif() + ": [" + request.getExpedientIdentificador() + ", " + request.getExpedientClau() + "]");
			return true;
		} catch (Exception ex) {
			log.error("Error al crear expedient a la zona personal: " + request, ex);
			throw new TramitacioException("Error al crear expedient a la zona personal", ex);
		}
	}
	
	@Override
	public boolean crearEventZonaPersonal(PublicarEventRequest request) throws TramitacioException {
		
		try {
			crearZonaPers(request.getRepresentatNif(), 
					request.getRepresentatNom(), 
					request.getRepresentatApe1(), 
					request.getRepresentatApe2());
			
			zonaPerClient.altaEventoExpediente(request.getUnitatAdministrativa(),
					request.getExpedientIdentificador(),
					request.getExpedientClau(),
					toEvento(request.getEvent()));
			
			log.info("Nou event creat a la zona personal del ciutadà per a l'expedient: [" + request.getExpedientIdentificador() + ", " + request.getExpedientClau() + "]");
			return true;
				
		} catch (Exception ex) {
			log.error("Error crear event a la zona personal", ex);
			throw new TramitacioException("Error crear event a la zona personal", ex);
		}
	}
	
	private void crearZonaPers(String nif, String nom, String cognom1, String cognom2) 
			throws es.caib.zonaper.ws.v2.services.BackofficeFacadeException, TramitacioException {
		
		if (!zonaPerClient.existeZonaPersonalUsuario(nif) && !zonaPerClient.existeZonaPersonalUsuario(nif.toUpperCase())) {
			if (zonaPerClient.altaZonaPersonalUsuario(nif.toUpperCase(), nom == null ? "" : nom, cognom1, cognom2) == null) {
				log.error("registrarNotificacio >> Error al crear la zona personal: " + nif);
				throw new TramitacioException("registrarNotificacio >> Error al crear la zona personal: " + nif);
			}
		}
	}
	
	@Override
	public boolean comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioException {
	
		try {
			ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
			referenciaEntrada.setNumeroEntrada(request.getNumeroEntrada());
			referenciaEntrada.setClaveAcceso(request.getClauAcces());
			String resultat = null;
			if (ResultatProcesTipus.PROCESSAT.equals(request.getResultatProces())) {
				resultat = "S";
			} else if (ResultatProcesTipus.NO_PROCESSAT.equals(request.getResultatProces())) {
				resultat = "N";
			} else if (ResultatProcesTipus.ERROR.equals(request.getResultatProces())) {
				resultat = "X";
			}
			bantelClient.establecerResultadoProceso(
					referenciaEntrada,
					resultat,
					request.getErrorDescripcio());
			return true;
		} catch (Exception ex) {
			log.error("Error al comunicar el resultat de processar el tràmit", ex);
			throw new TramitacioException("Error al obtenir dades del tràmit", ex);
		}
	}
	
	@Override
	public boolean existeixExpedient(Long unidadAdministrativa, String identificadorExpediente) throws TramitacioException {

		try {
			throw new Exception("metode no implementat");
			// TODO zonaPerClient.existeExpedient no existeix??? es.caib.zonaper.ws.v2.service -> classes autogenerades?
//			return zonaPerClient.existeExpediente(unidadAdministrativa, identificadorExpediente);
//			return false;
		} catch (Exception ex) {
			log.error("Error al comprovar exitència d'expedient en zona personal. Unitat administrativa: " + unidadAdministrativa + 
					", Identificador Expedient: " + identificadorExpediente, ex);
			throw new TramitacioException("Error al comprovar exitència d'expedient en zona personal. Unitat administrativa: " + unidadAdministrativa + 
					", Identificador Expedient: " + identificadorExpediente, ex);
		}
	}
	

	@Override
	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio) throws TramitacioException {
		
		// TODO IMPLEMENTAR
		return null;
	}
	
	private EventoExpediente toEvento(Event event) {
		
		var evento = new EventoExpediente();
		evento.setTitulo(event.getTitol());
		evento.setFecha(new JAXBElement<String>(new QName("fecha"), String.class, new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
		evento.setTexto(event.getText());
		evento.setTextoSMS(new JAXBElement<String>(new QName("textoSMS"), String.class, event.getTextSMS()));
		evento.setEnlaceConsulta(new JAXBElement<String>(new QName("enlaceConsulta"), String.class,	event.getEnllasConsulta()));
		
		if (event.getDocuments() == null || event.getDocuments().isEmpty()) {
			return evento;
		}
		
		var documentos = new DocumentosExpediente();
		for (var document : event.getDocuments()) {
			var documento = new DocumentoExpediente();
			documento.setTitulo(new JAXBElement<String>(new QName("titulo"), String.class,	document.getNom()));
			documento.setNombre(new JAXBElement<String>(new QName("nombre"), String.class,  document.getArxiuNom()));
			documento.setContenidoFichero(new JAXBElement<byte[]>(new QName("contenidoFichero"), byte[].class, document.getArxiuContingut()));
			documento.setEstructurado(new JAXBElement<Boolean>(new QName("estructurado"), Boolean.class, false));
			documentos.getDocumento().add(documento);
		}
		evento.setDocumentos(new JAXBElement<DocumentosExpediente>(new QName("documentos"), DocumentosExpediente.class,	documentos));
		
		return evento;
	}

	@Override
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws TramitacioException {

		RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
		try {
			AcuseRecibo acuseRecibo = regtelClient.obtenerAcuseRecibo(numeroRegistre);
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			if (acuseRecibo != null && acuseRecibo.getFechaAcuseRecibo() != null) {
				resposta.setData(acuseRecibo.getFechaAcuseRecibo().toGregorianCalendar().getTime());
			}
		} catch (BackofficeFacadeException ex) {
			log.error("Error al regtelClient", ex);
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
			resposta.setErrorDescripcio(ex.getMessage());
		} catch (Exception ex) {
			var msg = "Error al obtenir el justificant de recepció";
			log.error(msg);
			throw new TramitacioException(msg, ex);
		}
		return resposta;
	}
	
	@Override
	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(String numeroRegistre) throws TramitacioException {
		
			var resposta = new RespostaJustificantDetallRecepcio();
			try {
				var acuseRecibo = regtelClient.obtenerDetalleAcuseRecibo(numeroRegistre);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				if (acuseRecibo.getFechaAcuseRecibo() == null) {
					return resposta;
				}
				resposta.setData(acuseRecibo.getFechaAcuseRecibo().getValue().toGregorianCalendar().getTime());
				resposta.setFechaAcuseRecibo(acuseRecibo.getFechaAcuseRecibo().getValue());					
				var referenciaRDSJustificante = new ReferenciaRDSJustificante();
				referenciaRDSJustificante.setCodigo(acuseRecibo.getFicheroAcuseRecibo().getValue().getCodigo());
				referenciaRDSJustificante.setClave(acuseRecibo.getFicheroAcuseRecibo().getValue().getClave());
				resposta.setFicheroAcuseRecibo(referenciaRDSJustificante);
				for (var aviso : acuseRecibo.getAvisos().getValue().getAviso()) {
					var detalle = new es.caib.helium.integracio.domini.tramitacio.DetalleAviso();
					detalle.setConfirmarEnvio(aviso.isConfirmarEnvio());
					detalle.setDestinatario(aviso.getDestinatario());
					detalle.setFechaEnvio(aviso.getFechaEnvio().getValue());

					if (aviso.getTipo().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoAviso.EMAIL)) {
						detalle.setTipo(es.caib.helium.integracio.enums.tramitacio.TipoAviso.EMAIL);
					} else if (aviso.getTipo().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoAviso.EMAIL)) {
						detalle.setTipo(es.caib.helium.integracio.enums.tramitacio.TipoAviso.EMAIL);
					}
						
					if (aviso.getConfirmadoEnvio().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoConfirmacionAviso.DESCONOCIDO)) {
						detalle.setConfirmadoEnvio(es.caib.helium.integracio.enums.tramitacio.TipoConfirmacionAviso.DESCONOCIDO);
					} else if (aviso.getConfirmadoEnvio().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoConfirmacionAviso.ENVIADO)) {
						detalle.setConfirmadoEnvio(es.caib.helium.integracio.enums.tramitacio.TipoConfirmacionAviso.ENVIADO);
					} else if (aviso.getConfirmadoEnvio().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoConfirmacionAviso.NO_ENVIADO)) {
						detalle.setConfirmadoEnvio(es.caib.helium.integracio.enums.tramitacio.TipoConfirmacionAviso.NO_ENVIADO);
					}
					resposta.getAvisos().getAviso().add(detalle);
				}
				if (acuseRecibo.getEstado().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoEstadoNotificacion.ENTREGADA)) {
					resposta.setEstado(es.caib.helium.integracio.enums.tramitacio.TipoEstadoNotificacion.ENTREGADA);
				} else if (acuseRecibo.getEstado().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoEstadoNotificacion.PENDIENTE)) {
					resposta.setEstado(es.caib.helium.integracio.enums.tramitacio.TipoEstadoNotificacion.PENDIENTE);
				} else if (acuseRecibo.getEstado().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoEstadoNotificacion.RECHAZADA)) {
					resposta.setEstado(es.caib.helium.integracio.enums.tramitacio.TipoEstadoNotificacion.RECHAZADA);
				}
			} catch (BackofficeFacadeException ex) {
				log.error("Error al obtenir els detalls del justificant de recepció", ex);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(ex.getMessage());
			} catch (Exception ex) {
				throw new TramitacioException("Error al obtenir els detalls del justificant de recepció", ex);
			}
			return resposta;
	}
	
	@Override
	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioException {
		
		try {
			var referencia = new ReferenciaRDS();
			referencia.setCodigo(request.getReferenciaCodi());
			referencia.setClave(request.getReferenciaClau());
			var documento = redoseClient.consultarDocumentoFormateado(
					referencia,
					request.getPlantillaTipus(),
					request.getIdioma());
			DadesVistaDocument resposta = new DadesVistaDocument();
			resposta.setNom(documento.getNombreFichero());
			resposta.setArxiuNom(documento.getNombreFichero());
			resposta.setArxiuContingut(documento.getDatosFichero());
			return resposta;
		} catch (Exception ex) {
			log.error("Error al obtenir la vista del document " + request, ex);
			throw new TramitacioException("Error al obtenir la vista del document " + request, ex);
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
			return toDadesTramit(bantelClient.obtenerEntrada(referenciaEntrada));
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

}
