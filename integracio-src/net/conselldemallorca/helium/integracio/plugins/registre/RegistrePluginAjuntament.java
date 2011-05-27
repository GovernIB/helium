package net.conselldemallorca.helium.integracio.plugins.registre;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.conselldemallorca.helium.util.GlobalProperties;
import net.conselldemallorca.helium.util.ws.WsClientUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.regtel.ws.v2.model.acuserecibo.AcuseRecibo;
import es.caib.regtel.ws.v2.model.aviso.Aviso;
import es.caib.regtel.ws.v2.model.datosexpediente.DatosExpediente;
import es.caib.regtel.ws.v2.model.datosinteresado.DatosInteresado;
import es.caib.regtel.ws.v2.model.datosnotificacion.DatosNotificacion;
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
import es.caib.regweb.logic.helper.ParametrosRegistroEntrada;
import es.caib.regweb.logic.helper.ParametrosRegistroSalida;
import es.caib.regweb.logic.interfaces.RegistroEntradaFacade;
import es.caib.regweb.logic.interfaces.RegistroEntradaFacadeHome;
import es.caib.regweb.logic.interfaces.RegistroSalidaFacade;
import es.caib.regweb.logic.interfaces.RegistroSalidaFacadeHome;
import es.caib.regweb.logic.interfaces.ValoresFacade;
import es.caib.regweb.logic.interfaces.ValoresFacadeHome;


/**
 * Implementació del plugin de registre per a la interficie logic del
 * registre de la CAIB.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */

public class RegistrePluginAjuntament implements RegistrePlugin {

	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";

	@SuppressWarnings("unchecked")
	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException {
		try {
			ParametrosRegistroEntrada params = new ParametrosRegistroEntrada();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			Date ara = new Date();
			params.setdataentrada(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			params.sethora(new SimpleDateFormat("HH:mm").format(ara));
			if (registreEntrada.getDadesOficina() != null) {
				String oficinaCodi = registreEntrada.getDadesOficina().getOficinaCodi();
				if (oficinaCodi != null) {
					int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						params.setoficina(oficinaCodi.substring(0, indexBarra));
						params.setoficinafisica(oficinaCodi.substring(indexBarra + 1));
					}
				}
				if (registreEntrada.getDadesOficina().getOrganCodi() != null)
					params.setdestinatari(
							registreEntrada.getDadesOficina().getOrganCodi());
			}
			if (registreEntrada.getDadesInteressat() != null) {
				String entitatCodi = registreEntrada.getDadesInteressat().getEntitatCodi();
				if (entitatCodi != null) {
					int indexBarra = entitatCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						params.setentidad1(entitatCodi.substring(0, indexBarra));
						params.setentidad2(entitatCodi.substring(indexBarra + 1));
					}
				}
				if (registreEntrada.getDadesInteressat().getNomAmbCognoms() != null)
					params.setaltres(
							registreEntrada.getDadesInteressat().getNomAmbCognoms());
				if (registreEntrada.getDadesInteressat().getMunicipiCodi() != null)
					params.setbalears(
							registreEntrada.getDadesInteressat().getMunicipiCodi());
				if (registreEntrada.getDadesInteressat().getMunicipiNom() != null)
					params.setfora(
							registreEntrada.getDadesInteressat().getMunicipiNom());
			}
			if (registreEntrada.getDadesAssumpte() != null) {
				if (registreEntrada.getDadesAssumpte().getTipus() != null)
					params.settipo(
							registreEntrada.getDadesAssumpte().getTipus());
				if (registreEntrada.getDadesAssumpte().getIdiomaCodi() != null)
					params.setidioex(
							idiomaIso2Regweb(registreEntrada.getDadesAssumpte().getIdiomaCodi()));
				if (registreEntrada.getDadesAssumpte().getAssumpte() != null)
					params.setcomentario(
							registreEntrada.getDadesAssumpte().getAssumpte());
			}
			if (registreEntrada.getDocuments() != null && registreEntrada.getDocuments().size() > 0) {
				if (registreEntrada.getDocuments().size() == 1) {
					DocumentRegistre document = registreEntrada.getDocuments().get(0);
					if (document.getData() != null)
						params.setdata(
								new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					if (document.getIdiomaCodi() != null)
						params.setidioma(
								idiomaIso2Regweb(document.getIdiomaCodi()));
				} else {
					throw new RegistrePluginException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistrePluginException("S'ha d'especificar algun document per registrar");
			}
			RegistroEntradaFacade registroEntrada = getRegistreEntradaService();
			ParametrosRegistroEntrada respostaValidacio = registroEntrada.validar(params);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroEntrada respostaGrabacio = registroEntrada.grabar(params);
				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
				if (respostaGrabacio.getGrabado()) {
					resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
					resposta.setNumero(
							respostaGrabacio.getNumeroEntrada() +
							SEPARADOR_NUMERO +
							respostaGrabacio.getAnoEntrada());
					resposta.setData(ara);
					return resposta;
				} else {
					throw new RegistrePluginException("No s'ha pogut guardar l'entrada");
				}
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Errors de validació:\n");
				Map<String, String> errors = respostaValidacio.getErrores();
				for (String camp: errors.keySet()) {
					sb.append(" | " + errors.get(camp));
				}
				throw new RegistrePluginException("S'han produit errors de validació de l'entrada: " + sb.toString());
			}
		} catch (Exception ex) {
			logger.error("Error al registrar l'entrada", ex);
			throw new RegistrePluginException("Error al registrar l'entrada", ex);
		}
	}
	public RespostaConsulta consultarEntrada(
			String organCodi,
			String oficinaCodi,
			String registreNumero) throws RegistrePluginException {
		try {
			ParametrosRegistroEntrada params = new ParametrosRegistroEntrada();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			params.setoficina(organCodi);
			params.setoficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			params.setNumeroEntrada(registreNumero.substring(0, index));
			params.setAnoEntrada(registreNumero.substring(index + 1));
			RegistroEntradaFacade registroEntrada = getRegistreEntradaService();
			ParametrosRegistroEntrada llegit = registroEntrada.leer(params);
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(llegit.getDataEntrada() + " " + llegit.getHora()));
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(llegit.getDestinatari());
			dadesOficina.setOficinaCodi(llegit.getOficina() + SEPARADOR_ENTITAT + llegit.getOficinafisica());
			resposta.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			if (llegit.getEntidad1() != null && !"".equals(llegit.getEntidad1()))
				dadesInteressat.setEntitatCodi(
						llegit.getEntidad1() + SEPARADOR_ENTITAT + llegit.getEntidad2());
			dadesInteressat.setNomAmbCognoms(llegit.getAltres());
			dadesInteressat.setMunicipiCodi(llegit.getBalears());
			dadesInteressat.setMunicipiNom(llegit.getFora());
			resposta.setDadesInteressat(dadesInteressat);
			DadesAssumpte dadesAssumpte = new DadesAssumpte();
			dadesAssumpte.setIdiomaCodi(idiomaRegweb2iso(llegit.getIdioex()));
			dadesAssumpte.setTipus(llegit.getTipo());
			dadesAssumpte.setAssumpte(llegit.getComentario());
			resposta.setDadesAssumpte(dadesAssumpte);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			DocumentRegistre document = new DocumentRegistre();
			document.setIdiomaCodi(idiomaRegweb2iso(llegit.getIdioma()));
			if (llegit.getData() != null)
				document.setData(new SimpleDateFormat("dd/MM/yyyy").parse(llegit.getData()));
			documents.add(document);
			resposta.setDocuments(documents);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al consultar l'entrada", ex);
			throw new RegistrePluginException("Error al consultar l'entrada", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public RespostaAnotacioRegistre registrarSortida(
			RegistreSortida registreSortida) throws RegistrePluginException {
		try {
			ParametrosRegistroSalida params = new ParametrosRegistroSalida();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			Date ara = new Date();
			params.setdatasalida(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			params.sethora(new SimpleDateFormat("HH:mm").format(ara));
			if (registreSortida.getDadesOficina() != null) {
				String oficinaCodi = registreSortida.getDadesOficina().getOficinaCodi();
				if (oficinaCodi != null) {
					int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						params.setoficina(oficinaCodi.substring(0, indexBarra));
						params.setoficinafisica(oficinaCodi.substring(indexBarra + 1));
					}
				}
				if (registreSortida.getDadesOficina().getOrganCodi() != null)
					params.setremitent(
							registreSortida.getDadesOficina().getOrganCodi());
			}
			if (registreSortida.getDadesInteressat() != null) {
				String entitatCodi = registreSortida.getDadesInteressat().getEntitatCodi();
				if (entitatCodi != null) {
					int indexBarra = entitatCodi.indexOf(SEPARADOR_ENTITAT);
					if (entitatCodi != null && indexBarra != -1) {
						params.setentidad1(entitatCodi.substring(0, indexBarra));
						params.setentidad2(entitatCodi.substring(indexBarra + 1));
					}
				}
				if (registreSortida.getDadesInteressat().getNomAmbCognoms() != null)
					params.setaltres(
							registreSortida.getDadesInteressat().getNomAmbCognoms());
				if (registreSortida.getDadesInteressat().getMunicipiCodi() != null)
					params.setbalears(
							registreSortida.getDadesInteressat().getMunicipiCodi());
				if (registreSortida.getDadesInteressat().getMunicipiNom() != null)
					params.setfora(
							registreSortida.getDadesInteressat().getMunicipiNom());
			}
			if (registreSortida.getDadesAssumpte() != null) {
				if (registreSortida.getDadesAssumpte().getTipus() != null)
					params.settipo(
							registreSortida.getDadesAssumpte().getTipus());
				if (registreSortida.getDadesAssumpte().getIdiomaCodi() != null)
					params.setidioex(
							idiomaIso2Regweb(registreSortida.getDadesAssumpte().getIdiomaCodi()));
				if (registreSortida.getDadesAssumpte().getAssumpte() != null)
					params.setcomentario(
							registreSortida.getDadesAssumpte().getAssumpte());
			}
			if (registreSortida.getDocuments() != null && registreSortida.getDocuments().size() > 0) {
				if (registreSortida.getDocuments().size() == 1) {
					DocumentRegistre document = registreSortida.getDocuments().get(0);
					if (document.getData() != null)
						params.setdata(
								new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					if (document.getIdiomaCodi() != null)
						params.setidioma(
								idiomaIso2Regweb(document.getIdiomaCodi()));
				} else {
					throw new RegistrePluginException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistrePluginException("S'ha d'especificar algun document per registrar");
			}
			RegistroSalidaFacade registroSalida = getRegistreSortidaService();
			ParametrosRegistroSalida respostaValidacio = registroSalida.validar(params);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroSalida respostaGrabacio = registroSalida.grabar(params);
				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
				if (respostaGrabacio.getGrabado()) {
					resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
					resposta.setNumero(
							respostaGrabacio.getNumeroSalida() +
							SEPARADOR_NUMERO +
							respostaGrabacio.getAnoSalida());
					resposta.setData(ara);
					return resposta;
				} else {
					throw new RegistrePluginException("No s'ha pogut guardar la sortida");
				}
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Errors de validació:\n");
				Map<String, String> errors = respostaValidacio.getErrores();
				for (String camp: errors.keySet()) {
					sb.append(" | " + errors.get(camp));
				}
				throw new RegistrePluginException("S'han produit errors de validació de l'entrada: " + sb.toString());
			}
		} catch (Exception ex) {
			logger.error("Error al registrar la sortida", ex);
			throw new RegistrePluginException("Error al registrar la sortida", ex);
		}
	}

	public RespostaConsulta consultarSortida(
			String organCodi,
			String oficinaCodi,
			String registreNumero) throws RegistrePluginException {
		try {
			ParametrosRegistroSalida params = new ParametrosRegistroSalida();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			params.setoficina(organCodi);
			params.setoficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			params.setNumeroSalida(registreNumero.substring(0, index));
			params.setAnoSalida(registreNumero.substring(index + 1));
			RegistroSalidaFacade registroSalida = getRegistreSortidaService();
			ParametrosRegistroSalida llegit = registroSalida.leer(params);
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(llegit.getDataSalida() + " " + llegit.getHora()));
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(llegit.getRemitent());
			dadesOficina.setOficinaCodi(llegit.getOficina() + SEPARADOR_ENTITAT + llegit.getOficinafisica());
			resposta.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			if (llegit.getEntidad1() != null && !"".equals(llegit.getEntidad1()))
				dadesInteressat.setEntitatCodi(
						llegit.getEntidad1() + SEPARADOR_ENTITAT + llegit.getEntidad2());
			dadesInteressat.setNomAmbCognoms(llegit.getAltres());
			dadesInteressat.setMunicipiCodi(llegit.getBalears());
			dadesInteressat.setMunicipiNom(llegit.getFora());
			resposta.setDadesInteressat(dadesInteressat);
			DadesAssumpte dadesAssumpte = new DadesAssumpte();
			dadesAssumpte.setUnitatAdministrativa(llegit.getRemitent());
			dadesAssumpte.setIdiomaCodi(idiomaRegweb2iso(llegit.getIdioex()));
			dadesAssumpte.setTipus(llegit.getTipo());
			dadesAssumpte.setAssumpte(llegit.getComentario());
			resposta.setDadesAssumpte(dadesAssumpte);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			DocumentRegistre document = new DocumentRegistre();
			document.setIdiomaCodi(idiomaRegweb2iso(llegit.getIdioma()));
			if (llegit.getData() != null)
				document.setData(new SimpleDateFormat("dd/MM/yyyy").parse(llegit.getData()));
			documents.add(document);
			resposta.setDocuments(documents);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al consultar la sortida", ex);
			throw new RegistrePluginException("Error al consultar la sortida", ex);
		}
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

	@SuppressWarnings({"unchecked", "unused"})
	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException {
		try {
			if (oficinaCodi != null) {
				int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
				if (indexBarra != -1) {
					Vector v = getValoresService().buscarOficinasFisicasDescripcion("tots", "totes");
					Iterator it = v.iterator();
					while (it.hasNext()) {
						String codiOficina = (String)it.next();
						String codiOficinaFisica = (String)it.next();
						String nomOficinaFisica = (String)it.next();
						String nomOficina = (String)it.next();
						String textComparacio = codiOficina + SEPARADOR_ENTITAT + codiOficinaFisica;
						if (textComparacio.equals(oficinaCodi))
							return nomOficina;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			logger.error("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
			throw new RegistrePluginException("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
		}
	}



	private RegistroEntradaFacade getRegistreEntradaService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.logic.RegistroEntradaFacade");
		RegistroEntradaFacadeHome home = (RegistroEntradaFacadeHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				RegistroEntradaFacadeHome.class);
		ctx.close();
		return home.create();
	}
	private RegistroSalidaFacade getRegistreSortidaService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.logic.RegistroSalidaFacade");
		RegistroSalidaFacadeHome home = (RegistroSalidaFacadeHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				RegistroSalidaFacadeHome.class);
		ctx.close();
		return home.create();
	}
	private ValoresFacade getValoresService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.logic.ValoresFacade");
		ValoresFacadeHome home = (ValoresFacadeHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				ValoresFacadeHome.class);
		ctx.close();
		return home.create();
	}

	private Context getInitialContext() throws Exception {
		Properties props = new Properties();
		props.put(
				Context.INITIAL_CONTEXT_FACTORY,
				GlobalProperties.getInstance().getProperty("app.registre.plugin.initial.context.factory"));
		props.put(
				Context.URL_PKG_PREFIXES,
				GlobalProperties.getInstance().getProperty("app.registre.plugin.url.pkg.prefixes"));
		props.put(
				Context.PROVIDER_URL,
				GlobalProperties.getInstance().getProperty("app.registre.plugin.provider.url"));
		String principal = GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal");
		if (principal != null && principal.length() > 0)
			props.put(
					Context.SECURITY_PRINCIPAL,
					principal);
		String credentials = GlobalProperties.getInstance().getProperty("app.registre.plugin.security.credentials");
		if (credentials != null && credentials.length() > 0)
			props.put(
					Context.SECURITY_CREDENTIALS,
					credentials);
		return new InitialContext(props);
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

	private String idiomaIso2Regweb(String iso6391) {
		if ("es".equalsIgnoreCase(iso6391)) {
			return "1";
		} else if ("ca".equalsIgnoreCase(iso6391)) {
			return "2";
		} else if ("eu".equalsIgnoreCase(iso6391)) {
			return "4";
		} else if ("gl".equalsIgnoreCase(iso6391)) {
			return "5";
		} else if ("as".equalsIgnoreCase(iso6391)) {
			return "6";
		} else if ("de".equalsIgnoreCase(iso6391)) {
			return "C";
		} else if ("en".equalsIgnoreCase(iso6391)) {
			return "A";
		} else if ("fr".equalsIgnoreCase(iso6391)) {
			return "B";
		} else if ("it".equalsIgnoreCase(iso6391)) {
			return "E";
		} else if ("pt".equalsIgnoreCase(iso6391)) {
			return "F";
		}
		return "1";
	}
	private String idiomaRegweb2iso(String regweb) {
		if ("1".equalsIgnoreCase(regweb)) {
			return "es";
		} else if ("2".equalsIgnoreCase(regweb)) {
			return "ca";
		} else if ("4".equalsIgnoreCase(regweb)) {
			return "eu";
		} else if ("5".equalsIgnoreCase(regweb)) {
			return "gl";
		} else if ("6".equalsIgnoreCase(regweb)) {
			return "as";
		} else if ("C".equalsIgnoreCase(regweb)) {
			return "de";
		} else if ("A".equalsIgnoreCase(regweb)) {
			return "en";
		} else if ("B".equalsIgnoreCase(regweb)) {
			return "fr";
		} else if ("E".equalsIgnoreCase(regweb)) {
			return "it";
		} else if ("F".equalsIgnoreCase(regweb)) {
			return "pt";
		}
		return "es";
	}

	private static final Log logger = LogFactory.getLog(RegistrePluginAjuntament.class);

}
