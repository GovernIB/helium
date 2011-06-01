package net.conselldemallorca.helium.integracio.plugins.registre;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cim.ws.registro.v1.model.acuserecibo.AcuseRecibo;
import es.cim.ws.registro.v1.model.aviso.Aviso;
import es.cim.ws.registro.v1.model.datosexpediente.DatosExpediente;
import es.cim.ws.registro.v1.model.datosinteresado.DatosInteresado;
import es.cim.ws.registro.v1.model.datosnotificacion.DatosNotificacion;
import es.cim.ws.registro.v1.model.datosregistrosalida.DatosRegistroSalida;
import es.cim.ws.registro.v1.model.datosrepresentado.DatosRepresentado;
import es.cim.ws.registro.v1.model.documento.Documento;
import es.cim.ws.registro.v1.model.documento.Documentos;
import es.cim.ws.registro.v1.model.documento.TypeDocumentoRegistro;
import es.cim.ws.registro.v1.model.oficinaregistral.OficinaRegistral;
import es.cim.ws.registro.v1.model.oficioremision.OficioRemision;
import es.cim.ws.registro.v1.model.oficioremision.ParametroTramite;
import es.cim.ws.registro.v1.model.oficioremision.OficioRemision.TramiteSubsanacion;
import es.cim.ws.registro.v1.model.oficioremision.OficioRemision.TramiteSubsanacion.ParametrosTramite;
import es.cim.ws.registro.v1.model.registro.RegistroSalida;
import es.cim.ws.registro.v1.model.registro.RegistroSalidaResponse;
import es.cim.ws.registro.v1.model.registro.TypeCodigoError;
import es.cim.ws.registro.v1.services.ServicioRegistroPortType;
import es.cim.ws.tramitacion.v1.services.ServicioTramitacionPortType;


/**
 * Implementació del plugin de registre per al ESB del Consell
 * de Mallorca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginEsbCim implements RegistrePlugin {

	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException {
		throw new RegistrePluginException("Mètode no implementat en aquest plugin");
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
		throw new RegistrePluginException("Mètode no implementat en aquest plugin");
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
			RegistroSalida registroSalida = new RegistroSalida();
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
				datosRegistroSalida.setDatosRepresentado(
						new JAXBElement<DatosRepresentado>(
									new QName("datosRepresentado"),
									DatosRepresentado.class,
									datosRepresentado));
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
				datosRegistroSalida.setDatosNotificacion(datosNotificacion);
			}
			if (registreNotificacio.getDocuments() != null) {
				Documentos documentos = new Documentos();
				for (DocumentRegistre document: registreNotificacio.getDocuments()) {
					Documento documento = new Documento();
					documento.setNombreDocumento(document.getNom());
					documento.setTipoDocumento(TypeDocumentoRegistro.BIN);
					documento.setNombreFichero(
						new JAXBElement<String>(
									new QName("nombreFichero"),
									String.class,
									document.getArxiuNom()));
					documento.setContenidoFichero(
							new JAXBElement<byte[]>(
									new QName("contenidoFichero"),
									byte[].class,
									document.getArxiuContingut()));
					documentos.getDocumento().add(documento);
				}
				datosRegistroSalida.setDocumentos(
						new JAXBElement<Documentos>(
								new QName("documentos"),
								Documentos.class,
								documentos));
			}
			registroSalida.setNotificacion(datosRegistroSalida);
			RegistroSalidaResponse response = getRegistroClient().crearRegistroSalida(
					registroSalida);
			if (TypeCodigoError.OK.equals(response.getCodigoError())) {
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				if (response.getResultadoRegistro() != null) {
					resposta.setNumero(
							response.getResultadoRegistro().getNumeroRegistro());
					if (response.getResultadoRegistro().getFechaRegistro() != null)
						resposta.setData(
								response.getResultadoRegistro().getFechaRegistro().toGregorianCalendar().getTime());
				}
			} else {
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(response.getDescripcionError());
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
			Holder<TypeCodigoError> codigoError = new Holder<TypeCodigoError>();
			Holder<String> descripcionError = new Holder<String>();
			Holder<AcuseRecibo> acuseRecibo = new Holder<AcuseRecibo>();
			getRegistroClient().obtenerAcuseRecibo(
					numeroRegistre,
					codigoError,
					descripcionError,
					acuseRecibo);
			if (TypeCodigoError.OK.equals(codigoError.value)) {
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
				if (acuseRecibo.value != null && acuseRecibo.value.getFechaAcuseRecibo() != null) {
					resposta.setData(
							acuseRecibo.value.getFechaAcuseRecibo().toGregorianCalendar().getTime());
				}
			} else {
				resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_ERROR);
				resposta.setErrorDescripcio(descripcionError.value);
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir el justificant de recepció", ex);
			throw new RegistrePluginException("Error al obtenir el justificant de recepció", ex);
		}
	}

	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException {
		return "Registre General";
	}



	private ServicioRegistroPortType getRegistroClient() {
		String url = GlobalProperties.getInstance().getProperty("app.registre.plugin.url");
		String userName = GlobalProperties.getInstance().getProperty("app.registre.plugin.username");
		String password = GlobalProperties.getInstance().getProperty("app.registre.plugin.password");
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				ServicioTramitacionPortType.class,
				url,
				userName,
				password);
		return (ServicioRegistroPortType)wsClientProxy;
	}

	private static final Log logger = LogFactory.getLog(RegistrePluginEsbCim.class);

}
