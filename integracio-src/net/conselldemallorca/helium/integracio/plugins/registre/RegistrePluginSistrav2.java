package net.conselldemallorca.helium.integracio.plugins.registre;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.conselldemallorca.helium.util.GlobalProperties;
import net.conselldemallorca.helium.util.ws.WsClientUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.regtel.ws.v2.model.acuserecibo.AcuseRecibo;
import es.caib.regtel.ws.v2.model.aviso.Aviso;
import es.caib.regtel.ws.v2.model.datosasunto.DatosAsunto;
import es.caib.regtel.ws.v2.model.datosexpediente.DatosExpediente;
import es.caib.regtel.ws.v2.model.datosinteresado.DatosInteresado;
import es.caib.regtel.ws.v2.model.datosnotificacion.DatosNotificacion;
import es.caib.regtel.ws.v2.model.datosregistroentrada.DatosRegistroEntrada;
import es.caib.regtel.ws.v2.model.datosregistrosalida.DatosRegistroSalida;
import es.caib.regtel.ws.v2.model.datosrepresentado.DatosRepresentado;
import es.caib.regtel.ws.v2.model.documento.Documento;
import es.caib.regtel.ws.v2.model.documento.Documentos;
import es.caib.regtel.ws.v2.model.oficinaregistral.OficinaRegistral;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision;
import es.caib.regtel.ws.v2.model.oficioremision.ParametroTramite;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision.TramiteSubsanacion;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision.TramiteSubsanacion.ParametrosTramite;
import es.caib.regtel.ws.v2.model.resultadoregistro.ResultadoRegistro;
import es.caib.regtel.ws.v2.services.BackofficeFacade;
import es.caib.regtel.ws.v2.services.BackofficeFacadeException;


/**
 * Implementació del plugin de registre accedint a les
 * funcions del SISTRA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginSistrav2 implements RegistrePlugin {

	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException {
		try {
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			DatosRegistroEntrada datosRegistroEntrada = new DatosRegistroEntrada();
			if (registreEntrada.getDadesOficina() != null) {
				OficinaRegistral oficinaRegistral = new OficinaRegistral();
				oficinaRegistral.setCodigoOrgano(
						registreEntrada.getDadesOficina().getOrganCodi());
				oficinaRegistral.setCodigoOficina(
						registreEntrada.getDadesOficina().getOficinaCodi());
				datosRegistroEntrada.setOficinaRegistral(oficinaRegistral);
			}
			if (registreEntrada.getDadesInteressat() != null) {
				DatosInteresado datosInteresado = new DatosInteresado();
				datosInteresado.setAutenticado(
						new JAXBElement<Boolean>(
								new QName("autenticado"),
								Boolean.class,
								new Boolean(registreEntrada.getDadesInteressat().isAutenticat())));
				datosInteresado.setNombreApellidos(
						registreEntrada.getDadesInteressat().getNomAmbCognoms());
				datosInteresado.setNif(
						registreEntrada.getDadesInteressat().getNif());
				if (registreEntrada.getDadesInteressat().getPaisCodi() != null)
					datosInteresado.setCodigoPais(
							new JAXBElement<String>(
									new QName("codigoPais"),
									String.class,
									registreEntrada.getDadesInteressat().getPaisCodi()));
				if (registreEntrada.getDadesInteressat().getPaisNom() != null)
					datosInteresado.setNombrePais(
							new JAXBElement<String>(
									new QName("nombrePais"),
									String.class,
									registreEntrada.getDadesInteressat().getPaisNom()));
				if (registreEntrada.getDadesInteressat().getProvinciaCodi() != null)
					datosInteresado.setCodigoProvincia(
							new JAXBElement<String>(
									new QName("codigoProvincia"),
									String.class,
									registreEntrada.getDadesInteressat().getProvinciaCodi()));
				if (registreEntrada.getDadesInteressat().getProvinciaNom() != null)
					datosInteresado.setNombreProvincia(
							new JAXBElement<String>(
									new QName("nombreProvincia"),
									String.class,
									registreEntrada.getDadesInteressat().getProvinciaNom()));
				if (registreEntrada.getDadesInteressat().getMunicipiCodi() != null)
					datosInteresado.setCodigoLocalidad(
							new JAXBElement<String>(
									new QName("codigoLocalidad"),
									String.class,
									registreEntrada.getDadesInteressat().getMunicipiCodi()));
				if (registreEntrada.getDadesInteressat().getMunicipiNom() != null)
					datosInteresado.setNombreLocalidad(
							new JAXBElement<String>(
									new QName("nombreLocalidad"),
									String.class,
									registreEntrada.getDadesInteressat().getMunicipiNom()));
				datosRegistroEntrada.setDatosInteresado(datosInteresado);
			}
			if (registreEntrada.getDadesRepresentat() != null) {
				DatosRepresentado datosRepresentado = new DatosRepresentado();
				datosRepresentado.setNif(registreEntrada.getDadesRepresentat().getNif());
				datosRepresentado.setNombreApellidos(registreEntrada.getDadesRepresentat().getNomAmbCognoms());
				datosRegistroEntrada.setDatosRepresentado(datosRepresentado);
			}
			if (registreEntrada.getDadesAssumpte() != null) {
				DatosAsunto datosAsunto = new DatosAsunto();
				if (registreEntrada.getDadesAssumpte().getUnitatAdministrativa() != null)
					datosAsunto.setCodigoUnidadAdministrativa(
							Long.parseLong(registreEntrada.getDadesAssumpte().getUnitatAdministrativa()));
				datosAsunto.setIdioma(
						registreEntrada.getDadesAssumpte().getIdiomaCodi());
				datosAsunto.setTipoAsunto(
						registreEntrada.getDadesAssumpte().getTipus());
				datosAsunto.setAsunto(
						registreEntrada.getDadesAssumpte().getAssumpte());
				datosRegistroEntrada.setDatosAsunto(datosAsunto);
			}
			if (registreEntrada.getDocuments() != null) {
				Documentos documentos = new Documentos();
				for (DocumentRegistre document: registreEntrada.getDocuments()) {
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
					documento.setNombre(
						new JAXBElement<String>(
									new QName("nombre"),
									String.class,
									document.getArxiuNom()));
					int indexPunt = document.getArxiuNom().indexOf(".");
					if (indexPunt != -1 && ! document.getArxiuNom().endsWith(".")) {
						documento.setExtension(
							new JAXBElement<String>(
										new QName("extension"),
										String.class,
										document.getArxiuNom().substring(indexPunt + 1)));
					} else {
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
				datosRegistroEntrada.setDocumentos(
						new JAXBElement<Documentos>(
								new QName("documentos"),
								Documentos.class,
								documentos));
			}
			try {
				ResultadoRegistro resultado = getRegtelClient().registroEntrada(datosRegistroEntrada);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				resposta.setNumero(
						resultado.getNumeroRegistro());
				resposta.setData(
						resultado.getFechaRegistro().toGregorianCalendar().getTime());
			} catch (BackofficeFacadeException ex) {
				logger.error("Error al registrar l'entrada", ex);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(ex.getMessage());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar l'entrada", ex);
			throw new RegistrePluginException("Error al registrar l'entrada", ex);
		}
	}
	public RespostaConsulta consultarEntrada(
			String organCodi,
			String oficinaCodi,
			String numeroRegistre)
			throws RegistrePluginException {
		throw new RegistrePluginException("Mètode no implementat en aquest plugin");
	}

	public RespostaAnotacioRegistre registrarSortida(
			RegistreSortida registreSortida) throws RegistrePluginException {
		try {
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			DatosRegistroSalida datosRegistroSalida = new DatosRegistroSalida();
			try {
				ResultadoRegistro resultado = getRegtelClient().registroSalida(datosRegistroSalida);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				resposta.setNumero(
						resultado.getNumeroRegistro());
				resposta.setData(
						resultado.getFechaRegistro().toGregorianCalendar().getTime());
			} catch (BackofficeFacadeException ex) {
				logger.error("Error al registrar la sortida", ex);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(ex.getMessage());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar la sortida", ex);
			throw new RegistrePluginException("Error al registrar la sortida", ex);
		}
	}
	public RespostaConsulta consultarSortida(
			String organCodi,
			String oficinaCodi,
			String numeroRegistre)
			throws RegistrePluginException {
		throw new RegistrePluginException("Mètode no implementat en aquest plugin");
	}

	public RespostaAnotacioRegistre registrarNotificacio(
			RegistreNotificacio registreNotificacio) throws RegistrePluginException {
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
				datosExpediente.setUnidadAdministrativa(
						Long.parseLong(registreNotificacio.getDadesExpedient().getUnitatAdministrativa()));
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
				oficioRemision.setTitulo(
						registreNotificacio.getDadesNotificacio().getOficiTitol());
				oficioRemision.setTexto(
						registreNotificacio.getDadesNotificacio().getOficiText());
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
				ResultadoRegistro resultado = getRegtelClient().registroSalida(datosRegistroSalida);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				resposta.setNumero(
						resultado.getNumeroRegistro());
				resposta.setData(
						resultado.getFechaRegistro().toGregorianCalendar().getTime());
			} catch (BackofficeFacadeException ex) {
				logger.error("Error al registrar la sortida", ex);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(ex.getMessage());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar la sortida", ex);
			throw new RegistrePluginException("Error al registrar la sortida", ex);
		}
	}
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(
			String numeroRegistre) throws RegistrePluginException {
		try {
			RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
			try {
				AcuseRecibo acuseRecibo = getRegtelClient().obtenerAcuseRecibo(
						numeroRegistre);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				if (acuseRecibo.getFechaAcuseRecibo() != null) {
					resposta.setData(
							acuseRecibo.getFechaAcuseRecibo().toGregorianCalendar().getTime());
				}
			} catch (BackofficeFacadeException ex) {
				logger.error("Error al obtenir el justificant de recepció", ex);
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(ex.getMessage());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir el justificant de recepció", ex);
			throw new RegistrePluginException("Error al obtenir el justificant de recepció", ex);
		}
	}



	private BackofficeFacade getRegtelClient() {
		String url = GlobalProperties.getInstance().getProperty("app.registre.plugin.url");
		String userName = GlobalProperties.getInstance().getProperty("app.registre.plugin.username");
		String password = GlobalProperties.getInstance().getProperty("app.registre.plugin.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				BackofficeFacade.class,
				url,
				userName,
				password);
		return (BackofficeFacade)wsClientProxy;
	}

	private String getModelo() {
		return GlobalProperties.getInstance().getProperty("app.registre.plugin.rds.model");
	}
	private Integer getVersion() {
		return new Integer(GlobalProperties.getInstance().getProperty("app.registre.plugin.rds.versio"));
	}

	private static final Log logger = LogFactory.getLog(RegistrePluginSistrav2.class);

}
