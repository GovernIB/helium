/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.bantel.ws.v2.model.documentobte.DocumentoBTE;
import es.caib.bantel.ws.v2.model.firmaws.FirmaWS;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;
import es.caib.bantel.ws.v2.model.tramitebte.TramiteBTE;
import es.caib.redose.ws.v2.model.documentords.DocumentoRDS;
import es.caib.redose.ws.v2.model.referenciards.ReferenciaRDS;
import es.caib.regtel.ws.v2.model.acuserecibo.AcuseRecibo;
import es.caib.regtel.ws.v2.model.aviso.Aviso;
import es.caib.regtel.ws.v2.model.datosexpediente.DatosExpediente;
import es.caib.regtel.ws.v2.model.datosinteresado.DatosInteresado;
import es.caib.regtel.ws.v2.model.datosnotificacion.DatosNotificacion;
import es.caib.regtel.ws.v2.model.datosregistrosalida.DatosRegistroSalida;
import es.caib.regtel.ws.v2.model.datosrepresentado.DatosRepresentado;
import es.caib.regtel.ws.v2.model.detalleacuserecibo.DetalleAcuseRecibo;
import es.caib.regtel.ws.v2.model.detalleacuserecibo.DetalleAviso;
import es.caib.regtel.ws.v2.model.documento.Documento;
import es.caib.regtel.ws.v2.model.documento.Documentos;
import es.caib.regtel.ws.v2.model.oficinaregistral.OficinaRegistral;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision.TramiteSubsanacion;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision.TramiteSubsanacion.ParametrosTramite;
import es.caib.regtel.ws.v2.model.oficioremision.ParametroTramite;
import es.caib.regtel.ws.v2.model.resultadoregistro.ResultadoRegistro;
import es.caib.regtel.ws.v2.services.BackofficeFacade;
import es.caib.regtel.ws.v2.services.BackofficeFacadeException;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentoExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentosExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventoExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventosExpediente;
import es.caib.zonaper.ws.v2.model.expediente.Expediente;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.ReferenciaRDSJustificante;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacioParametre;

/**
 * Implementació del plugin de tramitacio accedint a la v2
 * dels ws de SISTRA
 * 
 * @author Limit Tecnologies
 */
public class TramitacioPluginSistrav2 implements TramitacioPlugin {

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
			configuracionAvisos.setHabilitarAvisos(
					new JAXBElement<Boolean>(
							new QName("habilitarAvisos"),
							Boolean.class,
							request.isAvisosHabilitat()));
			expediente.setIdentificadorProcedimiento(
					new JAXBElement<String>(
							new QName("identificadorProcedimiento"),
							String.class,
							request.getCodiProcediment()));
			
			
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
			if (!getZonaperClient().existeZonaPersonalUsuario(request.getRepresentatNif()) && !getZonaperClient().existeZonaPersonalUsuario(request.getRepresentatNif().toUpperCase())) {
				if (getZonaperClient().altaZonaPersonalUsuario(
						request.getRepresentatNif().toUpperCase(), 
						request.getRepresentatNom() == null ? "" : request.getRepresentatNom(), 
						null, 
						null) == null) {
					logger.error("Error al crear la zona personal: " + request.getRepresentantNif());
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
	
	@Override
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws TramitacioPluginException {
		try {
			RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
			try {
				AcuseRecibo acuseRecibo = getRegtelClient().obtenerAcuseRecibo(numeroRegistre);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				if (acuseRecibo != null && acuseRecibo.getFechaAcuseRecibo() != null) {
					resposta.setData(acuseRecibo.getFechaAcuseRecibo().toGregorianCalendar().getTime());
				}
			} catch (BackofficeFacadeException ex) {
				logger.error("Error al obtenir el justificant de recepció", ex);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(ex.getMessage());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir el justificant de recepció", ex);
			throw new TramitacioPluginException("Error al obtenir el justificant de recepció", ex);
		}
	}
	
	@Override
	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(String numeroRegistre) throws TramitacioPluginException {
		try {
			RespostaJustificantDetallRecepcio resposta = new RespostaJustificantDetallRecepcio();
			try {
				DetalleAcuseRecibo acuseRecibo = getRegtelClient().obtenerDetalleAcuseRecibo(numeroRegistre);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				if (acuseRecibo.getFechaAcuseRecibo() != null) {
					resposta.setData(acuseRecibo.getFechaAcuseRecibo().getValue().toGregorianCalendar().getTime());
					resposta.setFechaAcuseRecibo(acuseRecibo.getFechaAcuseRecibo().getValue());					
					ReferenciaRDSJustificante referenciaRDSJustificante = new ReferenciaRDSJustificante();
					referenciaRDSJustificante.setCodigo(acuseRecibo.getFicheroAcuseRecibo().getValue().getCodigo());
					referenciaRDSJustificante.setClave(acuseRecibo.getFicheroAcuseRecibo().getValue().getClave());
					resposta.setFicheroAcuseRecibo(referenciaRDSJustificante);
					for (DetalleAviso aviso : acuseRecibo.getAvisos().getValue().getAviso()) {
						net.conselldemallorca.helium.integracio.plugins.registre.DetalleAviso detalle = new net.conselldemallorca.helium.integracio.plugins.registre.DetalleAviso();
						detalle.setConfirmarEnvio(aviso.isConfirmarEnvio());
						detalle.setDestinatario(aviso.getDestinatario());
						detalle.setFechaEnvio(aviso.getFechaEnvio().getValue());

						if (aviso.getTipo().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoAviso.EMAIL))
							detalle.setTipo(net.conselldemallorca.helium.integracio.plugins.registre.TipoAviso.EMAIL);
						else if (aviso.getTipo().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoAviso.EMAIL))
							detalle.setTipo(net.conselldemallorca.helium.integracio.plugins.registre.TipoAviso.EMAIL);
							
						if (aviso.getConfirmadoEnvio().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoConfirmacionAviso.DESCONOCIDO))
							detalle.setConfirmadoEnvio(net.conselldemallorca.helium.integracio.plugins.registre.TipoConfirmacionAviso.DESCONOCIDO);
						else if (aviso.getConfirmadoEnvio().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoConfirmacionAviso.ENVIADO))
							detalle.setConfirmadoEnvio(net.conselldemallorca.helium.integracio.plugins.registre.TipoConfirmacionAviso.ENVIADO);
						else if (aviso.getConfirmadoEnvio().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoConfirmacionAviso.NO_ENVIADO))
							detalle.setConfirmadoEnvio(net.conselldemallorca.helium.integracio.plugins.registre.TipoConfirmacionAviso.NO_ENVIADO);
						
						resposta.getAvisos().getAviso().add(detalle);
					}
					if (acuseRecibo.getEstado().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoEstadoNotificacion.ENTREGADA))
						resposta.setEstado(net.conselldemallorca.helium.integracio.plugins.registre.TipoEstadoNotificacion.ENTREGADA);
					else if (acuseRecibo.getEstado().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoEstadoNotificacion.PENDIENTE))
						resposta.setEstado(net.conselldemallorca.helium.integracio.plugins.registre.TipoEstadoNotificacion.PENDIENTE);
					else if (acuseRecibo.getEstado().equals(es.caib.regtel.ws.v2.model.detalleacuserecibo.TipoEstadoNotificacion.RECHAZADA))
						resposta.setEstado(net.conselldemallorca.helium.integracio.plugins.registre.TipoEstadoNotificacion.RECHAZADA);
				}
			} catch (BackofficeFacadeException ex) {
				logger.error("Error al obtenir el justificant de recepció", ex);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(ex.getMessage());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir el justificant de recepció", ex);
			throw new TramitacioPluginException("Error al obtenir el justificant de recepció", ex);
		}
	}

	public void publicarEvent(
			PublicarEventRequest request) throws TramitacioPluginException {
		try {
			Event event = request.getEvent();
			if (event != null) {
				if (!getZonaperClient().existeZonaPersonalUsuario(request.getRepresentatNif()) && !getZonaperClient().existeZonaPersonalUsuario(request.getRepresentatNif().toUpperCase())) {
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
			return toDadesTramit(
					getBantelClient().obtenerEntrada(referenciaEntrada));
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

	public RespostaAnotacioRegistre registrarNotificacio(
			RegistreNotificacio registreNotificacio) throws TramitacioPluginException {
		try {
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			DatosRegistroSalida datosRegistroSalida = new DatosRegistroSalida();
			if (registreNotificacio.getDadesOficina() != null) {
				OficinaRegistral oficinaRegistral = new OficinaRegistral();
				oficinaRegistral.setCodigoOrgano(
						registreNotificacio.getDadesOficina().getOrganCodi());
				oficinaRegistral.setCodigoOficina(
						registreNotificacio.getDadesOficina().getOficinaCodi());
				datosRegistroSalida.setOficinaRegistral(oficinaRegistral);
			}
			if (registreNotificacio.getDadesInteressat() != null) {
				DatosInteresado datosInteresado = new DatosInteresado();
				datosInteresado.setAutenticado(
						new JAXBElement<Boolean>(
								new QName("autenticado"),
								Boolean.class,
								new Boolean(registreNotificacio.getDadesInteressat().isAutenticat())));
				datosInteresado.setNombreApellidos(
						registreNotificacio.getDadesInteressat().getNomAmbCognoms());
				datosInteresado.setNif(
						registreNotificacio.getDadesInteressat().getNif());
				if (registreNotificacio.getDadesInteressat().getPaisCodi() != null)
					datosInteresado.setCodigoPais(
							new JAXBElement<String>(
									new QName("codigoPais"),
									String.class,
									registreNotificacio.getDadesInteressat().getPaisCodi()));
				if (registreNotificacio.getDadesInteressat().getPaisNom() != null)
					datosInteresado.setNombrePais(
							new JAXBElement<String>(
									new QName("nombrePais"),
									String.class,
									registreNotificacio.getDadesInteressat().getPaisNom()));
				if (registreNotificacio.getDadesInteressat().getProvinciaCodi() != null)
					datosInteresado.setCodigoProvincia(
							new JAXBElement<String>(
									new QName("codigoProvincia"),
									String.class,
									registreNotificacio.getDadesInteressat().getProvinciaCodi()));
				if (registreNotificacio.getDadesInteressat().getProvinciaNom() != null)
					datosInteresado.setNombreProvincia(
							new JAXBElement<String>(
									new QName("nombreProvincia"),
									String.class,
									registreNotificacio.getDadesInteressat().getProvinciaNom()));
				if (registreNotificacio.getDadesInteressat().getMunicipiCodi() != null)
					datosInteresado.setCodigoLocalidad(
							new JAXBElement<String>(
									new QName("codigoLocalidad"),
									String.class,
									registreNotificacio.getDadesInteressat().getMunicipiCodi()));
				if (registreNotificacio.getDadesInteressat().getMunicipiNom() != null)
					datosInteresado.setNombreLocalidad(
							new JAXBElement<String>(
									new QName("nombreLocalidad"),
									String.class,
									registreNotificacio.getDadesInteressat().getMunicipiNom()));
				
				datosRegistroSalida.setDatosInteresado(datosInteresado);
			}
			if (registreNotificacio.getDadesRepresentat() != null) {
				DatosRepresentado datosRepresentado = new DatosRepresentado();
				datosRepresentado.setNif(registreNotificacio.getDadesRepresentat().getNif());
				datosRepresentado.setNombreApellidos(registreNotificacio.getDadesRepresentat().getNomAmbCognoms());
				datosRegistroSalida.setDatosRepresentado(datosRepresentado);
			}
			if (registreNotificacio.getDadesExpedient() != null) {
				DatosExpediente datosExpediente = new DatosExpediente();
				if (registreNotificacio.getDadesExpedient().getUnitatAdministrativa() != null)
					datosExpediente.setUnidadAdministrativa(Long.parseLong(registreNotificacio.getDadesExpedient().getUnitatAdministrativa()));
				datosExpediente.setIdentificadorExpediente(
						registreNotificacio.getDadesExpedient().getIdentificador());
				datosExpediente.setClaveExpediente(
						registreNotificacio.getDadesExpedient().getClau());
				datosRegistroSalida.setDatosExpediente(datosExpediente);
			}
			if (registreNotificacio.getDadesNotificacio() != null) {				
				DatosNotificacion datosNotificacion = new DatosNotificacion();
				datosNotificacion.setIdioma(
						registreNotificacio.getDadesNotificacio().getIdiomaCodi());
				datosNotificacion.setTipoAsunto(
						registreNotificacio.getDadesNotificacio().getTipus());
				datosNotificacion.setAcuseRecibo(
						registreNotificacio.getDadesNotificacio().isJustificantRecepcio());				
				OficioRemision oficioRemision = new OficioRemision();
				oficioRemision.setTitulo(registreNotificacio.getDadesNotificacio().getOficiTitol());
				oficioRemision.setTexto(registreNotificacio.getDadesNotificacio().getOficiText());
				if (registreNotificacio.getDadesNotificacio().getOficiTramitSubsanacio() != null) {
					TramitSubsanacio tramitSubsanacio = registreNotificacio.getDadesNotificacio().getOficiTramitSubsanacio();
					TramiteSubsanacion tramiteSubsanacion = new TramiteSubsanacion();
					tramiteSubsanacion.setIdentificadorTramite(
							tramitSubsanacio.getIdentificador());
					tramiteSubsanacion.setVersionTramite(
							tramitSubsanacio.getVersio());
					tramiteSubsanacion.setDescripcionTramite(
							tramitSubsanacio.getDescripcio());
					if (tramitSubsanacio.getParametres() != null) {
						ParametrosTramite parametrosTramite = new ParametrosTramite();
						for (TramitSubsanacioParametre parametre: tramitSubsanacio.getParametres()) {
							ParametroTramite parametro = new ParametroTramite();
							parametro.setParametro(parametre.getParametre());
							parametro.setValor(parametre.getValor());
							parametrosTramite.getParametroTramite().add(parametro);
						}
						tramiteSubsanacion.setParametrosTramite(
								new JAXBElement<ParametrosTramite>(
										new QName("parametrosTramite"),
										ParametrosTramite.class,
										parametrosTramite));
					}
					oficioRemision.setTramiteSubsanacion(
							new JAXBElement<TramiteSubsanacion>(
									new QName("tramiteSubsanacion"),
									TramiteSubsanacion.class,
									tramiteSubsanacion));
				}
				datosNotificacion.setOficioRemision(oficioRemision);
				if (registreNotificacio.getDadesNotificacio().getAvisTitol() != null) {
					Aviso aviso = new Aviso();
					aviso.setTitulo(
							registreNotificacio.getDadesNotificacio().getAvisTitol());
					aviso.setTexto(
							registreNotificacio.getDadesNotificacio().getAvisText());
					aviso.setTextoSMS(
							new JAXBElement<String>(
									new QName("textoSMS"),
									String.class,
									registreNotificacio.getDadesNotificacio().getAvisTextSms()));
					datosNotificacion.setAviso(aviso);
				}
				datosRegistroSalida.setDatosNotificacion(datosNotificacion);
			}
			if (registreNotificacio.getDocuments() != null) {
				Documentos documentos = new Documentos();
				for (DocumentRegistre document: registreNotificacio.getDocuments()) {
					Documento documento = new Documento();
					documento.setModelo(
						new JAXBElement<String>(
									new QName("modelo"),
									String.class,
									getModelo()));
					documento.setVersion(
						new JAXBElement<Integer>(
									new QName("version"),
									Integer.class,
									getVersion()));
					int indexPunt = document.getArxiuNom().indexOf(".");
					if (indexPunt != -1 && ! document.getArxiuNom().endsWith(".")) {
						documento.setNombre(
								new JAXBElement<String>(
											new QName("nombre"),
											String.class,
											document.getArxiuNom().substring(0, indexPunt)));
						documento.setExtension(
							new JAXBElement<String>(
										new QName("extension"),
										String.class,
										document.getArxiuNom().substring(indexPunt + 1)));
					} else {
						documento.setNombre(
								new JAXBElement<String>(
											new QName("nombre"),
											String.class,
											document.getArxiuNom()));
						documento.setExtension(
								new JAXBElement<String>(
											new QName("extension"),
											String.class,
											""));
					}
					documento.setDatosFichero(
							new JAXBElement<byte[]>(
									new QName("datosFichero"),
									byte[].class,
									document.getArxiuContingut()));
					documentos.getDocumentos().add(documento);
				}
				datosRegistroSalida.setDocumentos(
						new JAXBElement<Documentos>(
								new QName("documentos"),
								Documentos.class,
								documentos));
			}
			try {
				crearZonaPers(registreNotificacio.getDadesInteressat().getNif(), registreNotificacio.getDadesInteressat().getNomAmbCognoms());
				ResultadoRegistro resultado = null;
				resultado = getRegtelClient().registroSalida(datosRegistroSalida);
				
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				resposta.setNumero(
						resultado.getNumeroRegistro());
				resposta.setData(
						resultado.getFechaRegistro().toGregorianCalendar().getTime());				
				ReferenciaRDSJustificante referenciaRDSJustificante = new ReferenciaRDSJustificante();
				referenciaRDSJustificante.setClave(resultado.getReferenciaRDSJustificante().getClave());
				referenciaRDSJustificante.setCodigo(resultado.getReferenciaRDSJustificante().getCodigo());
				resposta.setReferenciaRDSJustificante(referenciaRDSJustificante);
			} catch (BackofficeFacadeException ex) {
				logger.error("Error al registrar la notificacion electronica", ex);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(ex.getMessage());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar la notificacion electronica", ex);
			throw new TramitacioPluginException("Error al registrar la sortida", ex);
		}
	}

	private void crearZonaPers(String nif, String nom) throws es.caib.zonaper.ws.v2.services.BackofficeFacadeException, TramitacioPluginException {
		if (!getZonaperClient().existeZonaPersonalUsuario(nif) && !getZonaperClient().existeZonaPersonalUsuario(nif.toUpperCase())) {
			if (getZonaperClient().altaZonaPersonalUsuario(
					nif.toUpperCase(), 
					nom == null ? "" : nom, 
					null, 
					null) == null) {
				logger.error("registrarNotificacio >> Error al crear la zona personal: " + nif);
				throw new TramitacioPluginException("registrarNotificacio >> Error al crear la zona personal: " + nif);
			}
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

	private String getModelo() {
		return GlobalProperties.getInstance().getProperty("app.registre.plugin.rds.model");
	}
	private Integer getVersion() {
		return new Integer(GlobalProperties.getInstance().getProperty("app.registre.plugin.rds.versio"));
	}

	private es.caib.bantel.ws.v2.services.BackofficeFacade getBantelClient() {
		String url = GlobalProperties.getInstance().getProperty("app.bantel.entrades.url");
		if (url == null)
			url = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.bantel.url");
		String userName = GlobalProperties.getInstance().getProperty("app.bantel.entrades.username");
		if (userName == null)
			userName = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.bantel.username");
		String password = GlobalProperties.getInstance().getProperty("app.bantel.entrades.password");
		if (password == null)
			password = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.bantel.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.bantel.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck(),
				null);
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
				isWsClientDisableCnCheck(),
				null);
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
				isWsClientDisableCnCheck(),
				null);
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
	
	private BackofficeFacade getRegtelClient() {
		String url = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.regtel.url");
		String userName = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.regtel.username");
		String password = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.sistra.client.regtel.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				BackofficeFacade.class,
				url,
				userName,
				password,
				getWsClientAuthType(),
				isWsClientGenerateTimestamp(),
				isWsClientLogCalls(),
				isWsClientDisableCnCheck(),
				null);
		return (BackofficeFacade)wsClientProxy;
	}

	private static final Log logger = LogFactory.getLog(TramitacioPluginSistrav2.class);
}
