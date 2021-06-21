package es.caib.helium.integracio.service.tramitacio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.redose.ws.v2.model.referenciards.ReferenciaRDS;
import es.caib.regtel.ws.v2.services.BackofficeFacadeException;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentoExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentosExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventoExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventosExpediente;
import es.caib.zonaper.ws.v2.model.expediente.Expediente;
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
	@Autowired
	private MonitorIntegracionsService monitor;
	
	public boolean crearExpedientZonaPersonal(PublicarExpedientRequest request, Long entornId) throws TramitacioException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Crear expedient zona personal";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("expedientIdentificador", request.getExpedientIdentificador()));	
		parametres.add(new Parametre("expedientClau", request.getExpedientClau()));	
		parametres.add(new Parametre("unitatAdministrativa", request.getUnitatAdministrativa() + ""));	
		parametres.add(new Parametre("descripcio", request.getDescripcio()));	
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
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.TRAMITACIO_SISTRA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());

			log.debug("###===> Nou expedient creat a la zona personal del ciutadà " + request.getRepresentatNif() + ": [" + request.getExpedientIdentificador() + ", " + request.getExpedientClau() + "]");
			return true;
			
		} catch (Exception ex) {
			var error = "Error al crear expedient a la zona personal";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.TRAMITACIO_SISTRA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new TramitacioException(error, ex);
		}
	}
	
	@Override
	public boolean crearEventZonaPersonal(PublicarEventRequest request, Long entornId) throws TramitacioException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Crear event zona personal";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("expedientIdentificador", request.getExpedientIdentificador()));	
		parametres.add(new Parametre("expedientClau", request.getExpedientClau()));	
		parametres.add(new Parametre("unitatAdministrativa", request.getUnitatAdministrativa() + ""));	
		parametres.add(new Parametre("interessatNif", request.getRepresentatNif()));	
		parametres.add(new Parametre("eventTitol", request.getEvent().getTitol()));	
		parametres.add(new Parametre("eventText", request.getEvent().getText()));	
		try {
			crearZonaPers(request.getRepresentatNif(), 
					request.getRepresentatNom(), 
					request.getRepresentatApe1(), 
					request.getRepresentatApe2());
			
			zonaPerClient.altaEventoExpediente(request.getUnitatAdministrativa(),
					request.getExpedientIdentificador(),
					request.getExpedientClau(),
					toEvento(request.getEvent()));
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.TRAMITACIO_SISTRA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.info("Nou event creat a la zona personal del ciutadà per a l'expedient: [" + request.getExpedientIdentificador() + ", " + request.getExpedientClau() + "]");
			return true;
				
		} catch (Exception ex) {
			var error = "Error crear event a la zona personal";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.TRAMITACIO_SISTRA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new TramitacioException(error, ex);
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
	public boolean comunicarResultatProcesTramit(ResultatProcesTramitRequest request, Long entornId) throws TramitacioException {
	
		var t0  = System.currentTimeMillis();
		var descripcio = "Comunicar resultat procés";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("numero", request.getNumeroEntrada()));	
		parametres.add(new Parametre("clau", request.getClauAcces()));	
		parametres.add(new Parametre("resultatProces", request.getResultatProces() + ""));	
		parametres.add(new Parametre("errorDescripcio", request.getErrorDescripcio()));	
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
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.TRAMITACIO_SISTRA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Resultat del proces tramit comunicat correctament");
			return true;
			
		} catch (Exception ex) {
			var error = "Error al comunicar el resultat de processar el tràmit";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.TRAMITACIO_SISTRA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new TramitacioException(error, ex);
		}
	}
	
	@Override
	public boolean existeixExpedient(Long unidadAdministrativa, String identificadorExpediente, Long entornId) throws TramitacioException {

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
	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio, Long entornId) throws TramitacioException {
		/*
		try {
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			var datosRegistroSalida = new DatosRegistroSalida();
			if (registreNotificacio.getDadesOficina() != null) {
				var oficinaRegistral = new OficinaRegistral();
				oficinaRegistral.setCodigoOrgano(registreNotificacio.getDadesOficina().getOrganCodi());
				oficinaRegistral.setCodigoOficina(registreNotificacio.getDadesOficina().getOficinaCodi());
				datosRegistroSalida.setOficinaRegistral(oficinaRegistral);
			}
			if (registreNotificacio.getDadesInteressat() != null) {
				DatosInteresado datosInteresado = new DatosInteresado();
				datosInteresado.setAutenticado(
						new JAXBElement<Boolean>(
								new QName("autenticado"),
								Boolean.class,
								registreNotificacio.getDadesInteressat().isAutenticat()));
				
				IdentificacionInteresadoDesglosada interessatDadesDesglosat = new IdentificacionInteresadoDesglosada();
				interessatDadesDesglosat.setNombre(registreNotificacio.getDadesInteressat().getNom());
				interessatDadesDesglosat.setApellido1(registreNotificacio.getDadesInteressat().getCognom1());
				interessatDadesDesglosat.setApellido2(registreNotificacio.getDadesInteressat().getCognom2());
				
				datosInteresado.setNombreApellidos(
						interessatDadesDesglosat.getNombre() + " " +
						interessatDadesDesglosat.getApellido1() + " " +
						interessatDadesDesglosat.getApellido2());
				
				datosInteresado.setNombreApellidosDesglosado(interessatDadesDesglosat);
				
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
				
				IdentificacionRepresentadoDesglosada representatInfoDesglosada = new IdentificacionRepresentadoDesglosada();
				representatInfoDesglosada.setNombre(registreNotificacio.getDadesInteressat().getNom());
				representatInfoDesglosada.setApellido1(registreNotificacio.getDadesRepresentat().getCognom1());
				representatInfoDesglosada.setApellido2(registreNotificacio.getDadesRepresentat().getCognom2());
				
				datosRepresentado.setNombreApellidos(
						representatInfoDesglosada.getNombre() + " " +
						representatInfoDesglosada.getNombre() + " " +
						representatInfoDesglosada.getNombre());
				
				datosRepresentado.setNombreApellidosDesglosado(representatInfoDesglosada);
				
				datosRegistroSalida.setDatosRepresentado(datosRepresentado);
			}
			if (registreNotificacio.getDadesExpedient() != null) {
				DatosExpediente datosExpediente = new DatosExpediente();
				if (registreNotificacio.getDadesExpedient().getUnitatAdministrativa() != null) {
					try {
					datosExpediente.setUnidadAdministrativa(Long.parseLong(registreNotificacio.getDadesExpedient().getUnitatAdministrativa()));
					} catch (NumberFormatException ex) {
						throw new TramitacioPluginException("La unitat administrativa ha de ser un valor numèric", ex);
					}
				}
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
				logger.info("###===> Notificacio. Comprovar zona personal");
				if (registreNotificacio.getDadesInteressat().getNom() != null)
					crearZonaPers(
							registreNotificacio.getDadesInteressat().getNif(), 
							registreNotificacio.getDadesInteressat().getNom(),
							registreNotificacio.getDadesInteressat().getCognom1(),
							registreNotificacio.getDadesInteressat().getCognom2());
				else
					crearZonaPers(
							registreNotificacio.getDadesInteressat().getNif(), 
							registreNotificacio.getDadesInteressat().getNomAmbCognoms());
				ResultadoRegistro resultado = null;
				
				logger.info("###===> Cridem al servei per registrar notificació");
				resultado = getRegtelClient().registroSalida(datosRegistroSalida);

				logger.info("###===> S'ha cridat al servei correctament");
				
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
		*/
		return null;
	}

	@Override
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre, Long entornId) throws TramitacioException {

		var t0  = System.currentTimeMillis();
		var descripcio = "Crear expedient zona personal";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("numeroRegistre", numeroRegistre));	
		var resposta = new RespostaJustificantRecepcio();
		try {
			var acuseRecibo = regtelClient.obtenerAcuseRecibo(numeroRegistre);
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			if (acuseRecibo != null && acuseRecibo.getFechaAcuseRecibo() != null) {
				resposta.setData(acuseRecibo.getFechaAcuseRecibo().toGregorianCalendar().getTime());
			}
		} catch (BackofficeFacadeException ex) {
			log.error("Error al regtelClient", ex);
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
			resposta.setErrorDescripcio(ex.getMessage());
		} catch (Exception ex) {
			var error = "Error al obtenir el justificant de recepció";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.TRAMITACIO_SISTRA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new TramitacioException(error, ex);
		}
		
		monitor.enviarEvent(IntegracioEvent.builder()
				.codi(CodiIntegracio.TRAMITACIO_SISTRA)
				.entornId(entornId)
				.descripcio(descripcio)
				.data(new Date())
				.tipus(TipusAccio.ENVIAMENT)
				.estat(EstatAccio.OK)
				.parametres(parametres)
				.tempsResposta(System.currentTimeMillis() - t0).build());
		
		log.debug("Justificant recepcio obtingut correctament");
		return resposta;
	}
	
	@Override
	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(String numeroRegistre, Long entornId) throws TramitacioException {
		
			var t0  = System.currentTimeMillis();
			var descripcio = "Crear expedient zona personal";
			List<Parametre> parametres = new ArrayList<>();
			parametres.add(new Parametre("numeroRegistre", numeroRegistre));	
			var resposta = new RespostaJustificantDetallRecepcio();
			try {
				var acuseRecibo = regtelClient.obtenerDetalleAcuseRecibo(numeroRegistre);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				if (acuseRecibo.getFechaAcuseRecibo() == null) {
					
					monitor.enviarEvent(IntegracioEvent.builder()
							.codi(CodiIntegracio.TRAMITACIO_SISTRA)
							.entornId(entornId)
							.descripcio(descripcio)
							.data(new Date())
							.tipus(TipusAccio.ENVIAMENT)
							.estat(EstatAccio.OK)
							.parametres(parametres)
							.tempsResposta(System.currentTimeMillis() - t0).build());
					
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
				var error = "Error al obtenir els detalls del justificant de recepció";
				log.error(error, ex);
				monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.TRAMITACIO_SISTRA) 
						.entornId(entornId) 
						.descripcio(descripcio)
						.tipus(TipusAccio.ENVIAMENT)
						.estat(EstatAccio.ERROR)
						.parametres(parametres)
						.tempsResposta(System.currentTimeMillis() - t0)
						.errorDescripcio(error)
						.excepcioMessage(ex.getMessage())
						.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
				throw new TramitacioException(error, ex);
			}
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.TRAMITACIO_SISTRA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Justificant recepcio detall obtingut correctament");
			return resposta;
	}
	
	@Override
	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request, Long entornId) throws TramitacioException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Crear expedient zona personal";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("referenciaCodi", request.getReferenciaCodi() + ""));
		parametres.add(new Parametre("referenciaClau", request.getReferenciaClau()));
		parametres.add(new Parametre("plantillaTipus", request.getPlantillaTipus()));
		parametres.add(new Parametre("idioma", request.getIdioma()));
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
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.TRAMITACIO_SISTRA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Vista document obtinguda correctament");
			return resposta;
			
		} catch (Exception ex) {
			var error = "Error al obtenir la vista del document";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.TRAMITACIO_SISTRA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new TramitacioException(error, ex);
		}
	}
	
	@Override
	public DadesTramit obtenirDadesTramit(String numero, String clau, Long entornId) throws TramitacioException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Crear expedient zona personal";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("numero", numero));
		parametres.add(new Parametre("clau", clau));
		try {
			ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
			referenciaEntrada.setNumeroEntrada(numero);
			referenciaEntrada.setClaveAcceso(clau);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.TRAMITACIO_SISTRA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Dades tramit obtingudes correctament");
			return toDadesTramit(bantelClient.obtenerEntrada(referenciaEntrada));
			
		} catch (Exception ex) {
			var error = "Error al obtenir dades del tràmit";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.TRAMITACIO_SISTRA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new TramitacioException(error, ex);
		}
	}
	
	private DadesTramit toDadesTramit(TramiteBTE entrada) {
		
		DadesTramit tramit = new DadesTramit();
		tramit.setNumero(entrada.getNumeroEntrada());
		tramit.setClauAcces(entrada.getCodigoEntrada());
		tramit.setIdentificador(entrada.getIdentificadorTramite());
		tramit.setUnitatAdministrativa(entrada.getUnidadAdministrativa());
		tramit.setVersio(entrada.getVersionTramite());
		if (entrada.getFecha() != null) {
			tramit.setData(entrada.getFecha().toGregorianCalendar().getTime());
		}
		tramit.setIdioma(entrada.getIdioma());
		tramit.setRegistreNumero(entrada.getNumeroRegistro());
		if (entrada.getFechaRegistro() != null) {
			tramit.setRegistreData(entrada.getFechaRegistro().toGregorianCalendar().getTime());
		}
		if (entrada.getTipoConfirmacionPreregistro() != null) {
			tramit.setPreregistreTipusConfirmacio(entrada.getTipoConfirmacionPreregistro().getValue());
		}
		if (entrada.getNumeroPreregistro() != null) {
			tramit.setPreregistreNumero(entrada.getNumeroPreregistro().getValue());
		}
		if (entrada.getFechaPreregistro() != null && entrada.getFechaPreregistro().getValue() != null) {
			tramit.setPreregistreData(entrada.getFechaPreregistro().getValue().toGregorianCalendar().getTime());
		}
		if (entrada.getNivelAutenticacion() != null) {
			if ("A".equalsIgnoreCase(entrada.getNivelAutenticacion())) {
				tramit.setAutenticacioTipus(AutenticacioTipus.ANONIMA);
			}
			if ("U".equalsIgnoreCase(entrada.getNivelAutenticacion())) {
				tramit.setAutenticacioTipus(AutenticacioTipus.USUARI);
			}
			if ("C".equalsIgnoreCase(entrada.getNivelAutenticacion())) {
				tramit.setAutenticacioTipus(AutenticacioTipus.CERTIFICAT);
			}
		}
		if (entrada.getUsuarioNif() != null) {
			tramit.setTramitadorNif(entrada.getUsuarioNif().getValue());
		}
		if (entrada.getUsuarioNombre() != null) {
			tramit.setTramitadorNom(entrada.getUsuarioNombre().getValue());
		}
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
		if (entrada.getUsuarioNif() != null) {
			tramit.setRepresentantNif(entrada.getUsuarioNif().getValue());
		}
		if (entrada.getUsuarioNombre() != null) {
			tramit.setRepresentantNom(entrada.getUsuarioNombre().getValue());
		}
		tramit.setSignat(entrada.isFirmadaDigitalmente());
		if (entrada.getHabilitarAvisos() != null) {
			tramit.setAvisosHabilitats("S".equalsIgnoreCase(entrada.getHabilitarAvisos().getValue()));
		}
		if (entrada.getAvisoSMS() != null) {
			tramit.setAvisosSms(entrada.getAvisoSMS().getValue());
		}
		if (entrada.getAvisoEmail() != null) {
			tramit.setAvisosEmail(entrada.getAvisoEmail().getValue());
		}
		if (entrada.getHabilitarNotificacionTelematica() != null) {
			tramit.setNotificacioTelematicaHabilitada("S".equalsIgnoreCase(entrada.getHabilitarNotificacionTelematica().getValue()));
		}
		if (entrada.getDocumentos() != null) {
			List<DocumentTramit> documents = new ArrayList<DocumentTramit>();
			for (DocumentoBTE documento: entrada.getDocumentos().getDocumento()) {
				DocumentTramit document = new DocumentTramit();
				document.setNom(documento.getNombre());
				document.setIdentificador(documento.getIdentificador());
				document.setInstanciaNumero(documento.getNumeroInstancia());
				if (documento.getPresentacionPresencial() != null && documento.getPresentacionPresencial().getValue() != null) {
					DocumentPresencial documentPresencial = new DocumentPresencial();
					documentPresencial.setDocumentCompulsar(documento.getPresentacionPresencial().getValue().getCompulsarDocumento());
					documentPresencial.setSignatura(documento.getPresentacionPresencial().getValue().getFirma());
					documentPresencial.setFotocopia(documento.getPresentacionPresencial().getValue().getFotocopia());
					documentPresencial.setTipus(documento.getPresentacionPresencial().getValue().getTipoDocumento());
					document.setDocumentPresencial(documentPresencial);
				}
				if (documento.getPresentacionTelematica() != null && documento.getPresentacionTelematica().getValue() != null) {
					DocumentTelematic documentTelematic = new DocumentTelematic();
					documentTelematic.setArxiuNom(documento.getPresentacionTelematica().getValue().getNombre());
					documentTelematic.setArxiuExtensio(documento.getPresentacionTelematica().getValue().getExtension());
					documentTelematic.setArxiuContingut(documento.getPresentacionTelematica().getValue().getContent());
					documentTelematic.setReferenciaCodi(documento.getPresentacionTelematica().getValue().getCodigoReferenciaRds());
					documentTelematic.setReferenciaClau(documento.getPresentacionTelematica().getValue().getClaveReferenciaRds());
					
					if (documento.getPresentacionTelematica().getValue().getFirmas() != null 
							&& documento.getPresentacionTelematica().getValue().getFirmas() != null) {
						
						List<Signatura> signatures = new ArrayList<Signatura>();
						for (FirmaWS firma: documento.getPresentacionTelematica().getValue().getFirmas().getFirmas()) {
							Signatura signatura = new Signatura();
							if (firma.getFormato() != null) {
								signatura.setFormat(firma.getFormato().getValue());
							}
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
		evento.setFecha(new JAXBElement<String>(new QName("fecha"), String.class, new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
		evento.setTexto(event.getText());
		evento.setTextoSMS(new JAXBElement<String>(new QName("textoSMS"), String.class, event.getTextSMS()));
		evento.setEnlaceConsulta(new JAXBElement<String>(new QName("enlaceConsulta"), String.class, event.getEnllasConsulta()));
		if (event.getDocuments() != null) {
			var documentos = new DocumentosExpediente();
			for (var document: event.getDocuments()) {
				var documento = new DocumentoExpediente();
				documento.setTitulo(new JAXBElement<String>(new QName("titulo"), String.class, document.getNom()));
				documento.setNombre(new JAXBElement<String>(new QName("nombre"), String.class, document.getArxiuNom()));
				documento.setContenidoFichero(new JAXBElement<byte[]>(new QName("contenidoFichero"), byte[].class, document.getArxiuContingut()));
				documento.setEstructurado(new JAXBElement<Boolean>(new QName("estructurado"), Boolean.class, false));
				documentos.getDocumento().add(documento);
			}
			evento.setDocumentos(new JAXBElement<DocumentosExpediente>(new QName("documentos"), DocumentosExpediente.class, documentos));
		}
		return evento;
	}
}
