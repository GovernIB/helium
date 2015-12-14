package net.conselldemallorca.helium.ws.backoffice.plugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.AutenticacioTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentEvent;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTelematic;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Event;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTipus;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;
import es.caib.bantel.ws.v2.model.tramitebte.TramiteBTE;
import es.caib.redose.ws.v2.model.documentords.DocumentoRDS;
import es.caib.redose.ws.v2.model.referenciards.ReferenciaRDS;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentoExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentosExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventoExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventosExpediente;
import es.caib.zonaper.ws.v2.model.expediente.Expediente;

/**
 * Implementació del plugin de tramitacio accedint a la v2
 * dels ws de SISTRA
 * 
 * @author Limit Tecnologies
 */
public class TramitacioPluginSistrav3 implements TramitacioSeleniumPlugin {

	public void publicarExpedient(
			PublicarExpedientRequest request) throws TramitacioPluginException {
		try {
			Expediente expediente = new Expediente();
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
			if (request.getRepresentantNif() != null)
				expediente.setNifRepresentante(
						new JAXBElement<String>(
								new QName("nifRepresentante"),
								String.class,
								request.getRepresentantNif()));
			if (request.getRepresentatNif() != null)
				expediente.setNifRepresentado(
						new JAXBElement<String>(
								new QName("nifRepresentado"),
								String.class,
								request.getRepresentatNif()));
			if (request.getRepresentatNom() != null)
				expediente.setNombreRepresentado(
						new JAXBElement<String>(
								new QName("nombreRepresentado"),
								String.class,
								request.getRepresentatNom()));
			if (request.getTramitNumero() != null)
				expediente.setNumeroEntradaBTE(
						new JAXBElement<String>(
								new QName("numeroEntradaBTE"),
								String.class,
								request.getTramitNumero()));
			ConfiguracionAvisosExpediente configuracionAvisos = new ConfiguracionAvisosExpediente();
			configuracionAvisos.setHabilitarAvisos(
					new JAXBElement<Boolean>(
							new QName("habilitarAvisos"),
							Boolean.class,
							new Boolean(request.isAvisosHabilitat())));
			configuracionAvisos.setAvisoEmail(
					new JAXBElement<String>(
							new QName("avisoEmail"),
							String.class,
							request.getAvisosEmail()));
			configuracionAvisos.setAvisoSMS(
					new JAXBElement<String>(
							new QName("avisoSMS"),
							String.class,
							request.getAvisosSMS()));
			expediente.setConfiguracionAvisos(
					new JAXBElement<ConfiguracionAvisosExpediente>(
							new QName("configuracionAvisos"),
							ConfiguracionAvisosExpediente.class,
							configuracionAvisos));
			if (request.getEvents() != null) {
				EventosExpediente eventosExpediente = new EventosExpediente();
				for (Event event: request.getEvents())
					eventosExpediente.getEvento().add(toEvento(event));
				expediente.setEventos(
						new JAXBElement<EventosExpediente>(
								new QName("eventos"),
								EventosExpediente.class,
								eventosExpediente));
			}

			logger.debug("ZPers: TramitacioPluginSistrav2 expedient nom : " + expediente.getNombreRepresentado());
			logger.debug("ZPers: TramitacioPluginSistrav2 expedient nif : " + expediente.getNifRepresentado());

			logger.debug("ZPers: TramitacioPluginSistrav2 request : " + request);
			String nifZonaPersonal = request.getRepresentatNif() == null ? request.getRepresentantNif() : request.getRepresentatNif();
			
			if (!getZonaperClient().existeZonaPersonalUsuario(nifZonaPersonal.toUpperCase()) && !getZonaperClient().existeZonaPersonalUsuario(nifZonaPersonal)) {
				if (getZonaperClient().altaZonaPersonalUsuario(
						nifZonaPersonal.toUpperCase(), 
						request.getRepresentatNom() == null ? "" : request.getRepresentatNom(), 
						request.getRepresentatApe1(), 
						request.getRepresentatApe2()) == null) {
					logger.error("Error al crear la zona personal: " + request + " - " + request.getRepresentantNif());
					throw new TramitacioPluginException("Error al crear la zona personal: " + request.getRepresentantNif());
				}
			}
			
			getZonaperClient().altaExpediente(expediente);
			logger.info("Nou expedient creat a la zona personal del ciutadà " + request.getRepresentatNif() + ": [" + request.getExpedientIdentificador() + ", " + request.getExpedientClau() + "]");
		} catch (Exception ex) {
			logger.error("Error al crear expedient a la zona personal: " + request, ex);
			throw new TramitacioPluginException("Error al crear expedient a la zona personal", ex);
		}
	}

	public void publicarEvent(
			PublicarEventRequest request) throws TramitacioPluginException {
		try {
			Event event = request.getEvent();
			if (event != null) {
				if (!getZonaperClient().existeZonaPersonalUsuario(request.getRepresentatNif().toUpperCase())) {
					if (getZonaperClient().altaZonaPersonalUsuario(
							request.getRepresentatNif().toUpperCase(), 
							request.getRepresentatNom() == null ? "" : request.getRepresentatNom(), 
							request.getRepresentatApe1(), 
							request.getRepresentatApe2()) == null) {
						logger.error("Error al crear la zona personal: " + request.getRepresentatNif());
						throw new TramitacioPluginException("Error al crear la zona personal: " + request.getRepresentatNif());
					}
				}
				getZonaperClient().altaEventoExpediente(
						request.getUnitatAdministrativa(),
						request.getExpedientIdentificador(),
						request.getExpedientClau(),
						toEvento(event));
				logger.info("Nou event creat a la zona personal del ciutadà per a l'expedient: [" + request.getExpedientIdentificador() + ", " + request.getExpedientClau() + "]");
			} else {
				throw new TramitacioPluginException("Error crear event: l'event es null");
			}
		} catch (Exception ex) {
			logger.error("Error crear event a la zona personal", ex);
			throw new TramitacioPluginException("Error crear event a la zona personal", ex);
		}
	}

	public DadesTramit obtenirDadesTramit(
			ObtenirDadesTramitRequest request) throws TramitacioPluginException {
		try {
			ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
			referenciaEntrada.setNumeroEntrada(request.getNumero());
			referenciaEntrada.setClaveAcceso(request.getClau());
			//return toDadesTramit(getBantelClient().obtenerEntrada(referenciaEntrada));
			return toDadesTramit(null);
		} catch (Exception ex) {
			logger.error("Error al obtenir dades del tràmit", ex);
			throw new TramitacioPluginException("Error al obtenir dades del tràmit", ex);
		}
	}

	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioPluginException {
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
			getBantelClient().establecerResultadoProceso(
					referenciaEntrada,
					resultat,
					request.getErrorDescripcio());
		} catch (Exception ex) {
			logger.error("Error al comunicar el resultat de processar el tràmit", ex);
			throw new TramitacioPluginException("Error al obtenir dades del tràmit", ex);
		}
	}

	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioPluginException {
		try {
			ReferenciaRDS referencia = new ReferenciaRDS();
			referencia.setCodigo(request.getReferenciaCodi());
			referencia.setClave(request.getReferenciaClau());
			DocumentoRDS documento = getRedoseClient().consultarDocumentoFormateado(
					referencia,
					request.getPlantillaTipus(),
					request.getIdioma());
			DadesVistaDocument resposta = new DadesVistaDocument();
			resposta.setNom(documento.getNombreFichero());
			resposta.setArxiuNom(documento.getNombreFichero());
			resposta.setArxiuContingut(documento.getDatosFichero());
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir la vista del document " + request, ex);
			throw new TramitacioPluginException("Error al obtenir la vista del document " + request, ex);
		}
	}



	private DadesTramit toDadesTramit(TramiteBTE entrada) {
		
		DadesTramit tramit = new DadesTramit();
		tramit.setIdentificador("IntegraSistra");
		tramit.setNumero("1");
		tramit.setRegistreNumero("PRE/B/837/2014");
		tramit.setRegistreData(new Date(2014, 10, 01, 8, 40, 35));
		tramit.setData(new Date());
		tramit.setUnitatAdministrativa(272l);
		tramit.setIdioma("ca");
		tramit.setAutenticacioTipus(AutenticacioTipus.ANONIMA);
		tramit.setTramitadorNif("10346059S");
		tramit.setTramitadorNom("rwrwer werttrt");
		tramit.setInteressatNif(null);
		tramit.setInteressatNom(null);
		tramit.setRepresentantNif("10346059S");
		tramit.setRepresentantNom("rwrwer werttrt");
		tramit.setAvisosHabilitats(false);
		tramit.setAvisosEmail(null);
		tramit.setAvisosSms(null);
		tramit.setNotificacioTelematicaHabilitada(false);
		
		
		
		List<DocumentTramit> documents = new ArrayList<DocumentTramit>();
		
		DocumentTramit document = new DocumentTramit();
		document.setNom("Cod Doc Sistra");
		document.setIdentificador("Cod Doc Sistra");
		document.setInstanciaNumero(1);
			DocumentTelematic documentTelematic = new DocumentTelematic();
			documentTelematic.setEstructurat(true);
			documentTelematic.setArxiuNom("DatosPropios.xml");
			documentTelematic.setArxiuContingut(new byte[] {60, 63, 120, 109, 108, 32, 118, 101, 114, 115, 105, 111, 110, 61, 34, 49, 46, 48, 34, 32, 101, 110, 99, 111, 100, 105, 110, 103, 61, 34, 85, 84, 70, 45, 56, 34, 63, 62, 10, 60, 70, 79, 82, 77, 85, 76, 65, 82, 73, 79, 32, 109, 111, 100, 101, 108, 111, 61, 34, 69, 72, 48, 48, 53, 56, 67, 65, 65, 69, 49, 34, 32, 118, 101, 114, 115, 105, 111, 110, 61, 34, 49, 34, 32, 62, 10, 32, 32, 32, 32, 60, 68, 65, 84, 79, 83, 95, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 76, 68, 69, 69, 78, 84, 73, 68, 65, 68, 32, 105, 110, 100, 105, 99, 101, 61, 34, 49, 34, 62, 85, 110, 105, 118, 101, 114, 115, 105, 116, 97, 116, 32, 100, 101, 32, 108, 101, 115, 32, 73, 108, 108, 101, 115, 32, 66, 97, 108, 101, 97, 114, 115, 60, 47, 76, 68, 69, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 78, 79, 77, 66, 82, 69, 69, 78, 84, 73, 68, 65, 68, 62, 85, 110, 105, 118, 101, 114, 115, 105, 116, 97, 116, 32, 100, 101, 32, 108, 101, 115, 32, 73, 108, 108, 101, 115, 32, 66, 97, 108, 101, 97, 114, 115, 60, 47, 67, 68, 84, 78, 79, 77, 66, 82, 69, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 73, 70, 69, 78, 84, 73, 68, 65, 68, 62, 81, 48, 55, 49, 56, 48, 48, 49, 65, 60, 47, 67, 68, 84, 67, 73, 70, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 69, 78, 84, 73, 68, 65, 68, 62, 76, 108, 111, 114, 101, 110, -61, -89, 32, 72, 117, 103, 104, 101, 116, 60, 47, 67, 68, 84, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 68, 78, 73, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 48, 48, 48, 48, 48, 48, 48, 48, 84, 60, 47, 67, 68, 84, 68, 78, 73, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 65, 82, 71, 79, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 82, 101, 99, 116, 111, 114, 60, 47, 67, 68, 84, 67, 65, 82, 71, 79, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 68, 73, 82, 69, 67, 67, 73, 79, 78, 69, 78, 84, 73, 68, 65, 68, 62, 67, 116, 114, 97, 46, 32, 100, 101, 32, 86, 97, 108, 108, 100, 101, 109, 111, 115, 115, 97, 44, 32, 107, 109, 32, 55, 44, 53, 60, 47, 67, 68, 84, 68, 73, 82, 69, 67, 67, 73, 79, 78, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 80, 69, 78, 84, 73, 68, 65, 68, 62, 48, 55, 49, 50, 50, 60, 47, 67, 68, 84, 67, 80, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 80, 79, 66, 76, 65, 67, 73, 79, 78, 69, 78, 84, 73, 68, 65, 68, 62, 80, 97, 108, 109, 97, 60, 47, 67, 68, 84, 80, 79, 66, 76, 65, 67, 73, 79, 78, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 83, 85, 69, 78, 84, 73, 68, 65, 68, 80, 85, 66, 76, 73, 67, 65, 32, 105, 110, 100, 105, 99, 101, 61, 34, 116, 34, 62, 83, 105, 60, 47, 67, 83, 85, 69, 78, 84, 73, 68, 65, 68, 80, 85, 66, 76, 73, 67, 65, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 69, 78, 84, 73, 68, 65, 68, 80, 85, 66, 76, 73, 67, 65, 83, 73, 79, 67, 85, 76, 84, 79, 62, 88, 60, 47, 67, 68, 84, 69, 78, 84, 73, 68, 65, 68, 80, 85, 66, 76, 73, 67, 65, 83, 73, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 69, 78, 84, 73, 68, 65, 68, 80, 85, 66, 76, 73, 67, 65, 78, 79, 79, 67, 85, 76, 84, 79, 47, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 79, 68, 73, 71, 79, 69, 78, 84, 73, 68, 65, 68, 80, 85, 66, 76, 73, 67, 65, 79, 67, 85, 76, 84, 79, 62, 83, 105, 60, 47, 67, 68, 84, 67, 79, 68, 73, 71, 79, 69, 78, 84, 73, 68, 65, 68, 80, 85, 66, 76, 73, 67, 65, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 60, 47, 68, 65, 84, 79, 83, 95, 69, 78, 84, 73, 68, 65, 68, 62, 10, 32, 32, 32, 32, 60, 68, 65, 84, 79, 83, 95, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 95, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 78, 79, 77, 66, 82, 69, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 114, 119, 114, 119, 101, 114, 60, 47, 67, 68, 84, 78, 79, 77, 66, 82, 69, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 80, 82, 73, 77, 69, 82, 65, 80, 69, 76, 76, 73, 68, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 119, 101, 114, 116, 116, 114, 116, 60, 47, 67, 68, 84, 80, 82, 73, 77, 69, 82, 65, 80, 69, 76, 76, 73, 68, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 83, 69, 71, 85, 78, 68, 79, 65, 80, 69, 76, 76, 73, 68, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 119, 101, 113, 119, 101, 114, 113, 101, 114, 119, 60, 47, 67, 68, 84, 83, 69, 71, 85, 78, 68, 79, 65, 80, 69, 76, 76, 73, 68, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 68, 78, 73, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 49, 48, 51, 52, 54, 48, 53, 57, 83, 60, 47, 67, 68, 84, 68, 78, 73, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 83, 85, 83, 69, 88, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 32, 105, 110, 100, 105, 99, 101, 61, 34, 104, 34, 62, 72, 111, 109, 101, 60, 47, 67, 83, 85, 83, 69, 88, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 79, 68, 73, 71, 79, 83, 69, 88, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 72, 111, 109, 98, 114, 101, 60, 47, 67, 68, 84, 67, 79, 68, 73, 71, 79, 83, 69, 88, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 70, 69, 67, 72, 65, 78, 65, 67, 73, 77, 73, 69, 78, 84, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 49, 50, 47, 49, 50, 47, 49, 57, 56, 57, 60, 47, 67, 68, 84, 70, 69, 67, 72, 65, 78, 65, 67, 73, 77, 73, 69, 78, 84, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 68, 73, 82, 69, 67, 67, 73, 79, 78, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 115, 100, 102, 115, 100, 102, 60, 47, 67, 68, 84, 68, 73, 82, 69, 67, 67, 73, 79, 78, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 80, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 51, 52, 51, 52, 51, 60, 47, 67, 68, 84, 67, 80, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 80, 79, 66, 76, 65, 67, 73, 79, 78, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 115, 100, 103, 116, 114, 116, 119, 101, 114, 60, 47, 67, 68, 84, 80, 79, 66, 76, 65, 67, 73, 79, 78, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 84, 69, 76, 69, 70, 79, 78, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 51, 52, 51, 52, 51, 52, 51, 52, 51, 60, 47, 67, 68, 84, 84, 69, 76, 69, 70, 79, 78, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 77, 79, 86, 73, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 51, 52, 51, 52, 51, 52, 51, 52, 51, 60, 47, 67, 68, 84, 77, 79, 86, 73, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 70, 65, 88, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 51, 52, 51, 52, 51, 52, 51, 52, 51, 60, 47, 67, 68, 84, 70, 65, 88, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 69, 77, 65, 73, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 115, 100, 102, 115, 100, 115, 100, 100, 102, 64, 100, 115, 102, 115, 100, 46, 99, 111, 109, 60, 47, 67, 68, 84, 69, 77, 65, 73, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 68, 69, 80, 65, 82, 84, 65, 77, 69, 78, 84, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 113, 119, 101, 114, 114, 119, 114, 60, 47, 67, 68, 84, 68, 69, 80, 65, 82, 84, 65, 77, 69, 78, 84, 79, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 84, 73, 84, 85, 76, 65, 67, 73, 79, 78, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 119, 101, 114, 102, 103, 102, 102, 100, 115, 102, 115, 60, 47, 67, 68, 84, 84, 73, 84, 85, 76, 65, 67, 73, 79, 78, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 83, 85, 69, 83, 68, 79, 67, 84, 79, 82, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 32, 105, 110, 100, 105, 99, 101, 61, 34, 116, 34, 62, 83, 105, 60, 47, 67, 83, 85, 69, 83, 68, 79, 67, 84, 79, 82, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 79, 68, 73, 71, 79, 68, 79, 67, 84, 79, 82, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 83, 105, 60, 47, 67, 68, 84, 67, 79, 68, 73, 71, 79, 68, 79, 67, 84, 79, 82, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 83, 73, 68, 79, 67, 84, 79, 82, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 88, 60, 47, 67, 68, 84, 83, 73, 68, 79, 67, 84, 79, 82, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 78, 79, 68, 79, 67, 84, 79, 82, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 47, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 65, 84, 69, 71, 79, 82, 73, 65, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 114, 101, 116, 116, 114, 113, 116, 113, 119, 116, 114, 114, 119, 101, 116, 114, 60, 47, 67, 68, 84, 67, 65, 84, 69, 71, 79, 82, 73, 65, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 83, 85, 82, 69, 76, 65, 67, 73, 79, 78, 76, 65, 66, 79, 82, 65, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 32, 105, 110, 100, 105, 99, 101, 61, 34, 116, 34, 62, 83, 105, 60, 47, 67, 83, 85, 82, 69, 76, 65, 67, 73, 79, 78, 76, 65, 66, 79, 82, 65, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 67, 79, 68, 73, 71, 79, 82, 69, 76, 65, 67, 73, 79, 78, 76, 65, 66, 79, 82, 65, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 83, 105, 60, 47, 67, 68, 84, 67, 79, 68, 73, 71, 79, 82, 69, 76, 65, 67, 73, 79, 78, 76, 65, 66, 79, 82, 65, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 83, 73, 82, 69, 76, 65, 67, 73, 79, 78, 76, 65, 66, 79, 82, 65, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 88, 60, 47, 67, 68, 84, 83, 73, 82, 69, 76, 65, 67, 73, 79, 78, 76, 65, 66, 79, 82, 65, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 78, 79, 82, 69, 76, 65, 67, 73, 79, 78, 76, 65, 66, 79, 82, 65, 76, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 79, 67, 85, 76, 84, 79, 47, 62, 10, 32, 32, 32, 32, 60, 47, 68, 65, 84, 79, 83, 95, 73, 78, 86, 69, 83, 84, 73, 71, 65, 68, 79, 82, 95, 82, 69, 83, 80, 79, 78, 83, 65, 66, 76, 69, 62, 10, 32, 32, 32, 32, 60, 68, 65, 84, 79, 83, 95, 65, 67, 67, 73, 79, 78, 95, 69, 83, 80, 69, 67, 73, 65, 76, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 84, 73, 84, 85, 76, 79, 65, 67, 67, 73, 79, 78, 69, 83, 80, 69, 67, 73, 65, 76, 62, 119, 101, 114, 116, 101, 114, 116, 121, 119, 114, 101, 116, 119, 101, 114, 116, 60, 47, 67, 68, 84, 84, 73, 84, 85, 76, 79, 65, 67, 67, 73, 79, 78, 69, 83, 80, 69, 67, 73, 65, 76, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 84, 69, 77, 65, 84, 73, 67, 65, 65, 67, 67, 73, 79, 78, 69, 83, 80, 69, 67, 73, 65, 76, 62, 103, 114, 116, 119, 116, 121, 119, 101, 116, 121, 60, 47, 67, 68, 84, 84, 69, 77, 65, 84, 73, 67, 65, 65, 67, 67, 73, 79, 78, 69, 83, 80, 69, 67, 73, 65, 76, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 70, 69, 67, 72, 65, 73, 78, 73, 67, 73, 79, 62, 49, 50, 47, 49, 50, 47, 50, 48, 49, 52, 60, 47, 67, 68, 84, 70, 69, 67, 72, 65, 73, 78, 73, 67, 73, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 70, 69, 67, 72, 65, 70, 73, 78, 65, 76, 62, 49, 50, 47, 49, 50, 47, 50, 48, 49, 53, 60, 47, 67, 68, 84, 70, 69, 67, 72, 65, 70, 73, 78, 65, 76, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 83, 73, 78, 73, 86, 65, 62, 50, 52, 51, 52, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 83, 73, 78, 73, 86, 65, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 73, 86, 65, 62, 50, 52, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 73, 86, 65, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 84, 79, 84, 65, 76, 62, 50, 52, 53, 56, 44, 48, 48, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 84, 79, 84, 65, 76, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 84, 79, 84, 65, 76, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 79, 67, 85, 76, 84, 79, 62, 50, 52, 53, 56, 44, 48, 48, 32, -30, -126, -84, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 84, 79, 84, 65, 76, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 79, 67, 85, 76, 84, 79, 62, 40, 50, 52, 51, 52, 32, 43, 32, 50, 52, 41, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 83, 79, 76, 73, 67, 73, 84, 65, 68, 79, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 67, 79, 70, 83, 73, 78, 73, 86, 65, 62, 50, 51, 52, 50, 51, 52, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 67, 79, 70, 83, 73, 78, 73, 86, 65, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 67, 79, 70, 73, 86, 65, 62, 50, 51, 52, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 67, 79, 70, 73, 86, 65, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 67, 79, 70, 84, 79, 84, 65, 76, 62, 50, 51, 52, 52, 54, 56, 44, 48, 48, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 67, 79, 70, 84, 79, 84, 65, 76, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 84, 79, 84, 65, 76, 79, 67, 85, 76, 84, 79, 62, 50, 51, 52, 52, 54, 56, 44, 48, 48, 32, -30, -126, -84, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 84, 79, 84, 65, 76, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 79, 67, 85, 76, 84, 79, 62, 40, 50, 51, 52, 50, 51, 52, 32, 43, 32, 50, 51, 52, 41, 60, 47, 67, 68, 84, 73, 77, 80, 79, 82, 84, 69, 65, 89, 85, 68, 65, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 83, 85, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 32, 105, 110, 100, 105, 99, 101, 61, 34, 102, 34, 62, 79, 114, 103, 97, 110, 105, 116, 122, 97, 99, 105, -61, -77, 32, 100, 101, 32, 99, 111, 110, 103, 114, 101, 115, 115, 111, 115, 44, 32, 115, 101, 109, 105, 110, 97, 114, 105, 115, 44, 32, 106, 111, 114, 110, 97, 100, 101, 115, 32, 105, 44, 32, 101, 110, 32, 103, 101, 110, 101, 114, 97, 108, 44, 32, 114, 101, 117, 110, 105, 111, 110, 115, 32, 100, 101, 32, 116, 101, 109, -61, -96, 116, 105, 99, 97, 32, 99, 105, 101, 110, 116, -61, -83, 102, 105, 99, 97, 32, 105, 32, 116, 101, 99, 110, 111, 108, -61, -78, 103, 105, 99, 97, 46, 60, 47, 67, 83, 85, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 95, 65, 95, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 47, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 95, 66, 95, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 47, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 95, 67, 95, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 47, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 95, 68, 95, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 47, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 95, 69, 95, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 47, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 95, 70, 95, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 62, 88, 60, 47, 67, 68, 84, 95, 70, 95, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 60, 67, 68, 84, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 62, 79, 114, 103, 97, 110, 105, 122, 97, 99, 105, -61, -77, 110, 32, 100, 101, 32, 99, 111, 110, 103, 114, 101, 115, 111, 115, 44, 32, 115, 101, 109, 105, 110, 97, 114, 105, 111, 115, 44, 32, 106, 111, 114, 110, 97, 100, 97, 115, 32, 121, 44, 32, 101, 110, 32, 103, 101, 110, 101, 114, 97, 108, 44, 32, 114, 101, 117, 110, 105, 111, 110, 101, 115, 32, 100, 101, 32, 116, 101, 109, -61, -95, 116, 105, 99, 97, 32, 99, 105, 101, 110, 116, -61, -83, 102, 105, 99, 97, 32, 121, 32, 116, 101, 99, 110, 111, 108, -61, -77, 103, 105, 99, 97, 60, 47, 67, 68, 84, 84, 73, 80, 79, 65, 67, 67, 73, 79, 78, 79, 67, 85, 76, 84, 79, 62, 10, 32, 32, 32, 32, 60, 47, 68, 65, 84, 79, 83, 95, 65, 67, 67, 73, 79, 78, 95, 69, 83, 80, 69, 67, 73, 65, 76, 62, 10, 60, 47, 70, 79, 82, 77, 85, 76, 65, 82, 73, 79, 62, 10});
		document.setDocumentTelematic(documentTelematic);
		documents.add(document);
		
		/*DocumentTramit document2 = new DocumentTramit();
		document2.setNom("SitraCodiAdjunt");
		document2.setIdentificador("SitraCodiAdjunt");
		document2.setInstanciaNumero(1);
			DocumentPresencial documentPresencial = new DocumentPresencial();
			documentPresencial.setTipus("A");
			documentPresencial.setSignatura("N");
			documentPresencial.setFotocopia("N");
			documentPresencial.setDocumentCompulsar("N");
		document2.setDocumentPresencial(documentPresencial);*/
		
		DocumentTramit document2 = new DocumentTramit();
		document2.setNom("SitraCodiAdjunt");
		document2.setIdentificador("SitraCodiAdjunt");
		document2.setInstanciaNumero(1);
			DocumentTelematic documentTelematic2 = new DocumentTelematic();
			documentTelematic2.setEstructurat(true);
			documentTelematic2.setArxiuNom("DatosPropios2.xml");
			documentTelematic2.setArxiuContingut(new byte[] {60, 63, 120, 109, 108, 32, 118, 101, 114, 115, 105, 111, 110, 61, 34, 49, 46, 48, 34, 32, 101, 110, 99, 111, 100, 105, 110, 103, 61, 34, 85, 84, 70, 45, 56, 34, 32, 115, 116, 97, 110, 100, 97, 108, 111, 110, 101, 61, 34, 121, 101, 115, 34, 63, 62, 60, 68, 65, 84, 79, 83, 95, 80, 82, 79, 80, 73, 79, 83, 62, 60, 73, 78, 83, 84, 82, 85, 67, 67, 73, 79, 78, 69, 83, 62, 60, 84, 69, 88, 84, 79, 95, 73, 78, 83, 84, 82, 85, 67, 67, 73, 79, 78, 69, 83, 62, 80, 101, 114, 113, 117, -61, -88, 32, 108, 97, 32, 118, 111, 115, 116, 114, 97, 32, 115, 111, 108, -62, -73, 108, 105, 99, 105, 116, 117, 100, 32, 115, 105, 103, 117, 105, 32, 99, 111, 109, 112, 108, 101, 116, 97, 109, 101, 110, 116, 32, 118, -61, -96, 108, 105, 100, 97, 32, 114, 101, 118, 105, 115, 97, 117, 32, 108, 97, 32, 100, 111, 99, 117, 109, 101, 110, 116, 97, 99, 105, -61, -77, 32, 97, 32, 97, 112, 111, 114, 116, 97, 114, 32, 105, 32, 112, 114, 101, 115, 101, 110, 116, 97, 117, 45, 108, 97, 32, 101, 110, 32, 101, 108, 115, 32, 112, 117, 110, 116, 115, 32, 100, 101, 32, 108, 108, 105, 117, 114, 97, 109, 101, 110, 116, 32, 105, 110, 100, 105, 99, 97, 116, 115, 46, 32, 80, 111, 100, 101, 117, 32, 114, 101, 97, 108, 105, 116, 122, 97, 114, 32, 101, 108, 32, 115, 101, 103, 117, 105, 109, 101, 110, 116, 32, 100, 101, 32, 108, 39, 101, 115, 116, 97, 116, 32, 100, 101, 32, 108, 97, 32, 118, 111, 115, 116, 114, 97, 32, 115, 111, 108, -62, -73, 108, 105, 99, 105, 116, 117, 100, 32, 97, 32, 116, 114, 97, 118, -61, -87, 115, 32, 100, 101, 32, 39, 69, 108, 32, 109, 101, 117, 32, 112, 111, 114, 116, 97, 108, 39, 32, 97, 109, 98, 32, 108, 97, 32, 115, 101, 118, 97, 32, 99, 108, 97, 117, 32, 100, 101, 32, 116, 114, 97, 109, 105, 116, 97, 99, 105, -61, -77, 58, 32, 50, 73, 88, 67, 80, 78, 49, 77, 45, 85, 88, 66, 79, 67, 69, 84, 56, 45, 84, 56, 76, 75, 85, 84, 86, 66, 46, 32, 60, 47, 84, 69, 88, 84, 79, 95, 73, 78, 83, 84, 82, 85, 67, 67, 73, 79, 78, 69, 83, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 83, 95, 69, 78, 84, 82, 69, 71, 65, 82, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 73, 82, 77, 65, 82, 61, 34, 116, 114, 117, 101, 34, 32, 84, 73, 80, 79, 61, 34, 71, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 70, 111, 114, 109, 117, 108, 97, 114, 105, 32, 103, 101, 110, 101, 114, 97, 108, 32, 100, 101, 32, 115, 111, 108, -62, -73, 108, 105, 99, 105, 116, 117, 100, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 70, 79, 82, 77, 49, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 77, 101, 109, -61, -78, 114, 105, 97, 32, 100, 101, 115, 99, 114, 105, 112, 116, 105, 118, 97, 32, 40, 105, 109, 112, 114, -61, -88, 115, 32, 49, 41, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 77, 69, 77, 68, 50, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 80, 114, 101, 115, 115, 117, 112, 111, 115, 116, 32, 40, 105, 109, 112, 114, -61, -88, 115, 32, 50, 41, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 80, 82, 83, 83, 50, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 82, 101, 115, 117, 109, 32, 67, 86, 32, 100, 101, 108, 32, 114, 101, 115, 112, 111, 110, 115, 97, 98, 108, 101, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 82, 69, 83, 67, 86, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 70, 111, 116, 111, 99, -61, -78, 112, 105, 97, 32, 100, 101, 108, 32, 68, 78, 73, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 70, 84, 68, 78, 73, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 68, 101, 99, 108, 97, 114, 97, 99, 105, -61, -77, 32, 101, 120, 112, 114, 101, 115, 115, 97, 32, 100, 39, 97, 106, 117, 116, 115, 32, 115, 111, 108, -62, -73, 108, 105, 99, 105, 116, 97, 116, 115, 32, 105, 47, 111, 32, 111, 98, 116, 105, 110, 103, 117, 116, 115, 32, 40, 105, 109, 112, 114, -61, -88, 115, 32, 51, 41, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 68, 69, 67, 76, 82, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 68, 101, 99, 108, 97, 114, 97, 99, 105, -61, -77, 32, 97, 106, 117, 100, 101, 115, 47, 109, -61, -83, 110, 105, 109, 115, 32, 40, 105, 109, 112, 114, -61, -88, 115, 32, 52, 41, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 77, 73, 78, 73, 77, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 68, 111, 99, 117, 109, 101, 110, 116, 32, 99, 111, 110, 115, 116, 105, 116, 117, 116, 105, 117, 32, 100, 101, 32, 108, 39, 101, 110, 116, 105, 116, 97, 116, 32, 105, 32, 97, 99, 114, 101, 100, 105, 116, 97, 99, 105, -61, -77, 32, 100, 101, 32, 108, 97, 32, 114, 101, 112, 114, 101, 115, 101, 110, 116, 97, 99, 105, -61, -77, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 67, 79, 78, 83, 84, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 69, 120, 105, 115, 116, -61, -88, 110, 99, 105, 97, 32, 100, 101, 108, 32, 99, 111, 109, 112, 116, 101, 32, 98, 97, 110, 99, 97, 114, 105, 32, 40, 105, 109, 112, 114, -61, -88, 115, 32, 53, 41, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 67, 79, 77, 80, 84, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 68, 101, 99, 108, 97, 114, 97, 99, 105, -61, -77, 32, 100, 101, 32, 110, 111, 32, 105, 110, 99, -61, -78, 114, 114, 101, 114, 32, 101, 110, 32, 99, 97, 112, 32, 99, 97, 117, 115, 97, 32, 100, 39, 105, 110, 99, 111, 109, 112, 97, 116, 105, 98, 105, 108, 105, 116, 97, 116, 32, 40, 105, 109, 112, 114, -61, -88, 115, 32, 54, 41, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 73, 78, 67, 79, 77, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 68, 101, 99, 108, 97, 114, 97, 99, 105, -61, -77, 32, 100, 101, 32, 99, 117, 109, 112, 108, 105, 109, 101, 110, 116, 32, 100, 39, 111, 98, 108, 105, 103, 97, 99, 105, 111, 110, 115, 32, 40, 105, 109, 112, 114, -61, -88, 115, 32, 55, 41, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 67, 79, 77, 80, 76, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 68, 79, 67, 85, 77, 69, 78, 84, 79, 32, 70, 79, 84, 79, 67, 79, 80, 73, 65, 61, 34, 102, 97, 108, 115, 101, 34, 32, 67, 79, 77, 80, 85, 76, 83, 65, 82, 61, 34, 102, 97, 108, 115, 101, 34, 32, 84, 73, 80, 79, 61, 34, 65, 34, 62, 60, 84, 73, 84, 85, 76, 79, 62, 68, 101, 99, 108, 97, 114, 97, 99, 105, -61, -77, 32, 100, 39, 101, 115, 116, 97, 114, 32, 97, 108, 32, 99, 111, 114, 114, 101, 110, 116, 32, 100, 101, 108, 32, 112, 97, 103, 97, 109, 101, 110, 116, 32, 40, 105, 109, 112, 114, -61, -88, 115, 32, 56, 41, 60, 47, 84, 73, 84, 85, 76, 79, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 80, 65, 71, 65, 77, 45, 49, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 62, 60, 47, 68, 79, 67, 85, 77, 69, 78, 84, 79, 83, 95, 69, 78, 84, 82, 69, 71, 65, 82, 62, 60, 70, 69, 67, 72, 65, 95, 84, 79, 80, 69, 95, 69, 78, 84, 82, 69, 71, 65, 62, 50, 48, 49, 52, 49, 48, 51, 49, 48, 55, 51, 56, 50, 50, 60, 47, 70, 69, 67, 72, 65, 95, 84, 79, 80, 69, 95, 69, 78, 84, 82, 69, 71, 65, 62, 60, 84, 69, 88, 84, 79, 95, 70, 69, 67, 72, 65, 95, 84, 79, 80, 69, 95, 69, 78, 84, 82, 69, 71, 65, 62, 69, 108, 32, 116, 101, 114, 109, 105, 110, 105, 32, 100, 101, 32, 112, 114, 101, 115, 101, 110, 116, 97, 99, 105, -61, -77, 32, 100, 101, 32, 115, 111, 108, -62, -73, 108, 105, 99, 105, 116, 117, 100, 115, 32, 112, 101, 114, 32, 97, 32, 97, 113, 117, 101, 115, 116, 101, 115, 32, 97, 106, 117, 100, 101, 115, 32, -61, -87, 115, 32, 97, 32, 112, 97, 114, 116, 105, 114, 32, 100, 101, 32, 108, 39, 101, 110, 100, 101, 109, -61, -96, 32, 100, 39, 104, 97, 118, 101, 114, 45, 115, 101, 32, 112, 117, 98, 108, 105, 99, 97, 116, 32, 108, 97, 32, 82, 101, 115, 111, 108, 117, 99, 105, -61, -77, 32, 101, 110, 32, 101, 108, 32, 66, 117, 116, 108, 108, 101, 116, -61, -83, 32, 79, 102, 105, 99, 105, 97, 108, 32, 100, 101, 32, 108, 101, 115, 32, 73, 108, 108, 101, 115, 32, 66, 97, 108, 101, 97, 114, 115, 44, 32, 102, 105, 110, 115, 32, 101, 108, 32, 49, 53, 32, 100, 101, 32, 106, 117, 108, 105, 111, 108, 32, 100, 101, 32, 50, 48, 49, 53, 32, 113, 117, 101, 32, 115, 101, 114, -61, -96, 32, 108, 97, 32, 100, 97, 116, 97, 32, 100, 101, 32, 102, 105, 110, 97, 108, 105, 116, 122, 97, 99, 105, -61, -77, 32, 100, 101, 32, 108, 97, 32, 99, 111, 110, 118, 111, 99, 97, 116, -61, -78, 114, 105, 97, 60, 47, 84, 69, 88, 84, 79, 95, 70, 69, 67, 72, 65, 95, 84, 79, 80, 69, 95, 69, 78, 84, 82, 69, 71, 65, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 95, 80, 69, 82, 83, 73, 83, 84, 69, 78, 67, 73, 65, 62, 50, 73, 88, 67, 80, 78, 49, 77, 45, 85, 88, 66, 79, 67, 69, 84, 56, 45, 84, 56, 76, 75, 85, 84, 86, 66, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 95, 80, 69, 82, 83, 73, 83, 84, 69, 78, 67, 73, 65, 62, 60, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 95, 80, 82, 79, 67, 69, 68, 73, 77, 73, 69, 78, 84, 79, 62, 69, 72, 48, 48, 53, 56, 67, 65, 65, 69, 60, 47, 73, 68, 69, 78, 84, 73, 70, 73, 67, 65, 68, 79, 82, 95, 80, 82, 79, 67, 69, 68, 73, 77, 73, 69, 78, 84, 79, 62, 60, 47, 73, 78, 83, 84, 82, 85, 67, 67, 73, 79, 78, 69, 83, 62, 60, 47, 68, 65, 84, 79, 83, 95, 80, 82, 79, 80, 73, 79, 83, 62});
		document2.setDocumentTelematic(documentTelematic2);
		documents.add(document2);
		
		tramit.setDocuments(documents);
		
		/*
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
		if (entrada.getRepresentadoNif() != null)
			tramit.setInteressatNif(entrada.getRepresentadoNif().getValue());
		if (entrada.getRepresentadoNombre() != null)
			tramit.setInteressatNom(entrada.getRepresentadoNombre().getValue());
			
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
		}*/
		return tramit;
	}
	
	private EventoExpediente toEvento(Event event) {
		EventoExpediente evento = new EventoExpediente();
		evento.setTitulo(event.getTitol());
		evento.setFecha(
				new JAXBElement<String>(
						new QName("fecha"),
						String.class,
						new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
		evento.setTexto(event.getText());
		evento.setTextoSMS(
				new JAXBElement<String>(
						new QName("textoSMS"),
						String.class,
						event.getTextSMS()));
		evento.setEnlaceConsulta(
				new JAXBElement<String>(
						new QName("enlaceConsulta"),
						String.class,
						event.getEnllasConsulta()));
		if (event.getDocuments() != null) {
			DocumentosExpediente documentos = new DocumentosExpediente();
			for (DocumentEvent document: event.getDocuments()) {
				DocumentoExpediente documento = new DocumentoExpediente();
				documento.setTitulo(
						new JAXBElement<String>(
								new QName("titulo"),
								String.class,
								document.getNom()));
				documento.setNombre(
						new JAXBElement<String>(
								new QName("nombre"),
								String.class,
								document.getArxiuNom()));
				documento.setContenidoFichero(
						new JAXBElement<byte[]>(
								new QName("contenidoFichero"),
								byte[].class,
								document.getArxiuContingut()));
				documento.setEstructurado(
						new JAXBElement<Boolean>(
								new QName("estructurado"),
								Boolean.class,
								new Boolean(false)));
				documentos.getDocumento().add(documento);
			}
			evento.setDocumentos(
					new JAXBElement<DocumentosExpediente>(
							new QName("documentos"),
							DocumentosExpediente.class,
							documentos));
		}
		return evento;
	}

	private es.caib.bantel.ws.v2.services.BackofficeFacade getBantelClient() {
		String url = GlobalProperties.getInstance().getProperty("app.bantel.selenium.entrades.url");
		String userName = GlobalProperties.getInstance().getProperty("app.bantel.selenium.entrades.username");
		String password = GlobalProperties.getInstance().getProperty("app.bantel.selenium.entrades.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.bantel.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck());
		return (es.caib.bantel.ws.v2.services.BackofficeFacade)wsClientProxy;
	}

	private es.caib.zonaper.ws.v2.services.BackofficeFacade getZonaperClient() {
		String url = GlobalProperties.getInstance().getProperty("app.zonaper.service.url");
		if (url == null)
			url = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.zonaper.url");
		String userName = GlobalProperties.getInstance().getProperty("app.zonaper.service.username");
		if (userName == null)
			userName = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.zonaper.username");
		String password = GlobalProperties.getInstance().getProperty("app.zonaper.service.password");
		if (password == null)
			password = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.zonaper.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.zonaper.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck());
		return (es.caib.zonaper.ws.v2.services.BackofficeFacade)wsClientProxy;
	}

	private es.caib.redose.ws.v2.services.BackofficeFacade getRedoseClient() {
		String url = GlobalProperties.getInstance().getProperty("app.redose.service.url");
		if (url == null)
			url = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.redose.url");
		String userName = GlobalProperties.getInstance().getProperty("app.redose.service.username");
		if (userName == null)
			userName = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.redose.username");
		String password = GlobalProperties.getInstance().getProperty("app.redose.service.password");
		if (password == null)
			password = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.redose.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.redose.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck());
		return (es.caib.redose.ws.v2.services.BackofficeFacade)wsClientProxy;
	}

	private String getWsClientAuthType() {
		String authType = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.auth");
		if (authType == null)
			authType = GlobalProperties.getInstance().getProperty("app.ws.client.auth");
		return authType;
	}
	private boolean isWsClientGenerateTimestamp() {
		String authType = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.generate.timestamp");
		if (authType == null)
			authType = GlobalProperties.getInstance().getProperty("app.ws.client.generate.timestamp");
		return "true".equalsIgnoreCase(authType);
	}
	private boolean isWsClientLogCalls() {
		String logCalls = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.log.calls");
		if (logCalls == null)
			logCalls = GlobalProperties.getInstance().getProperty("app.ws.client.log.calls");
		return "true".equalsIgnoreCase(logCalls);
	}
	private boolean isWsClientDisableCnCheck() {
		String disableCnCheck = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.disable.cn.check");
		if (disableCnCheck == null)
			disableCnCheck = GlobalProperties.getInstance().getProperty("app.ws.client.disable.cn.check");
		return "true".equalsIgnoreCase(disableCnCheck);
	}

	private static final Log logger = LogFactory.getLog(TramitacioPluginSistrav3.class);

}
