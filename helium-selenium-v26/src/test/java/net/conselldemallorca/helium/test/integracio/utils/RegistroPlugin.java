package net.conselldemallorca.helium.test.integracio.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreEntrada;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePlugin;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePluginException;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreSortida;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaConsulta;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacioParametre;

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
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision.TramiteSubsanacion;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision.TramiteSubsanacion.ParametrosTramite;
import es.caib.regtel.ws.v2.model.oficioremision.ParametroTramite;
import es.caib.regtel.ws.v2.model.resultadoregistro.ResultadoRegistro;
import es.caib.regweb.logic.helper.ParametrosRegistroEntrada;
import es.caib.regweb.logic.helper.ParametrosRegistroSalida;

/**
 * Implementació Mock del plugin de registre.
 * 
 * @author Tomeu Domenge <tomeud@limit.es>
 */
public class RegistroPlugin implements RegistrePlugin {

	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";

	public RespostaAnotacioRegistre registrarEntrada(RegistreEntrada registreEntrada) throws RegistrePluginException {
		try {
			Date ara = new Date();
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			resposta.setNumero("0" + SEPARADOR_NUMERO + "2012");
			resposta.setData(ara);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar l'entrada", ex);
			throw new RegistrePluginException("Error al registrar l'entrada", ex);
		}
	}

	public RespostaAnotacioRegistre registrarSortida(RegistreSortida registreSortida) throws RegistrePluginException {
		try {
			Date ara = new Date();
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			resposta.setNumero("0" + SEPARADOR_NUMERO + "2012");
			resposta.setData(ara);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar la sortida", ex);
			throw new RegistrePluginException("Error al registrar la sortida", ex);
		}
	}

	public RespostaConsulta consultarEntrada(String organCodi, String oficinaCodi, String registreNumero) throws RegistrePluginException {
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

			ParametrosRegistroEntrada llegit = getParametrosRegistroEntrada(params);
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(llegit.getDataEntrada() + " " + llegit.getHora()));
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(llegit.getDestinatari());
			dadesOficina.setOficinaCodi(llegit.getOficina() + SEPARADOR_ENTITAT + llegit.getOficinafisica());
			resposta.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			if (llegit.getEntidad1() != null && !"".equals(llegit.getEntidad1()))
				dadesInteressat.setEntitatCodi(llegit.getEntidad1() + SEPARADOR_ENTITAT + llegit.getEntidad2());
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

	public RespostaConsulta consultarSortida(String organCodi, String oficinaCodi, String registreNumero) throws RegistrePluginException {
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

			ParametrosRegistroSalida llegit = getParametrosRegistroSalida(params);
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(llegit.getDataSalida() + " " + llegit.getHora()));
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(llegit.getRemitent());
			dadesOficina.setOficinaCodi(llegit.getOficina() + SEPARADOR_ENTITAT + llegit.getOficinafisica());
			resposta.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			if (llegit.getEntidad1() != null && !"".equals(llegit.getEntidad1()))
				dadesInteressat.setEntitatCodi(llegit.getEntidad1() + SEPARADOR_ENTITAT + llegit.getEntidad2());
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

	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio) throws RegistrePluginException {
		try {
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			DatosRegistroSalida datosRegistroSalida = new DatosRegistroSalida();
			if (registreNotificacio.getDadesOficina() != null) {
				OficinaRegistral oficinaRegistral = new OficinaRegistral();
				oficinaRegistral.setCodigoOrgano(registreNotificacio.getDadesOficina().getOrganCodi());
				oficinaRegistral.setCodigoOficina(registreNotificacio.getDadesOficina().getOficinaCodi());
				datosRegistroSalida.setOficinaRegistral(oficinaRegistral);
			}
			if (registreNotificacio.getDadesInteressat() != null) {
				DatosInteresado datosInteresado = new DatosInteresado();
				datosInteresado.setAutenticado(new JAXBElement<Boolean>(new QName("autenticado"), Boolean.class, new Boolean(registreNotificacio.getDadesInteressat().isAutenticat())));
				datosInteresado.setNombreApellidos(registreNotificacio.getDadesInteressat().getNomAmbCognoms());
				datosInteresado.setNif(registreNotificacio.getDadesInteressat().getNif());
				if (registreNotificacio.getDadesInteressat().getPaisCodi() != null)
					datosInteresado.setCodigoPais(new JAXBElement<String>(new QName("codigoPais"), String.class, registreNotificacio.getDadesInteressat().getPaisCodi()));
				if (registreNotificacio.getDadesInteressat().getPaisNom() != null)
					datosInteresado.setNombrePais(new JAXBElement<String>(new QName("nombrePais"), String.class, registreNotificacio.getDadesInteressat().getPaisNom()));
				if (registreNotificacio.getDadesInteressat().getProvinciaCodi() != null)
					datosInteresado.setCodigoProvincia(new JAXBElement<String>(new QName("codigoProvincia"), String.class, registreNotificacio.getDadesInteressat().getProvinciaCodi()));
				if (registreNotificacio.getDadesInteressat().getProvinciaNom() != null)
					datosInteresado.setNombreProvincia(new JAXBElement<String>(new QName("nombreProvincia"), String.class, registreNotificacio.getDadesInteressat().getProvinciaNom()));
				if (registreNotificacio.getDadesInteressat().getMunicipiCodi() != null)
					datosInteresado.setCodigoLocalidad(new JAXBElement<String>(new QName("codigoLocalidad"), String.class, registreNotificacio.getDadesInteressat().getMunicipiCodi()));
				if (registreNotificacio.getDadesInteressat().getMunicipiNom() != null)
					datosInteresado.setNombreLocalidad(new JAXBElement<String>(new QName("nombreLocalidad"), String.class, registreNotificacio.getDadesInteressat().getMunicipiNom()));
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
				datosExpediente.setUnidadAdministrativa(Long.parseLong(registreNotificacio.getDadesExpedient().getUnitatAdministrativa()));
				datosExpediente.setIdentificadorExpediente(registreNotificacio.getDadesExpedient().getIdentificador());
				datosExpediente.setClaveExpediente(registreNotificacio.getDadesExpedient().getClau());
				datosRegistroSalida.setDatosExpediente(datosExpediente);
			}
			if (registreNotificacio.getDadesNotificacio() != null) {
				DatosNotificacion datosNotificacion = new DatosNotificacion();
				datosNotificacion.setIdioma(registreNotificacio.getDadesNotificacio().getIdiomaCodi());
				datosNotificacion.setTipoAsunto(registreNotificacio.getDadesNotificacio().getTipus());
				datosNotificacion.setAcuseRecibo(registreNotificacio.getDadesNotificacio().isJustificantRecepcio());
				OficioRemision oficioRemision = new OficioRemision();
				oficioRemision.setTitulo(registreNotificacio.getDadesNotificacio().getOficiTitol());
				oficioRemision.setTexto(registreNotificacio.getDadesNotificacio().getOficiText());
				if (registreNotificacio.getDadesNotificacio().getOficiTramitSubsanacio() != null) {
					TramitSubsanacio tramitSubsanacio = registreNotificacio.getDadesNotificacio().getOficiTramitSubsanacio();
					TramiteSubsanacion tramiteSubsanacion = new TramiteSubsanacion();
					tramiteSubsanacion.setIdentificadorTramite(tramitSubsanacio.getIdentificador());
					tramiteSubsanacion.setVersionTramite(tramitSubsanacio.getVersio());
					tramiteSubsanacion.setDescripcionTramite(tramitSubsanacio.getDescripcio());
					if (tramitSubsanacio.getParametres() != null) {
						ParametrosTramite parametrosTramite = new ParametrosTramite();
						for (TramitSubsanacioParametre parametre : tramitSubsanacio.getParametres()) {
							ParametroTramite parametro = new ParametroTramite();
							parametro.setParametro(parametre.getParametre());
							parametro.setValor(parametre.getValor());
							parametrosTramite.getParametroTramite().add(parametro);
						}
						tramiteSubsanacion.setParametrosTramite(new JAXBElement<ParametrosTramite>(new QName("parametrosTramite"), ParametrosTramite.class, parametrosTramite));
					}
					oficioRemision.setTramiteSubsanacion(new JAXBElement<TramiteSubsanacion>(new QName("tramiteSubsanacion"), TramiteSubsanacion.class, tramiteSubsanacion));
				}
				datosNotificacion.setOficioRemision(oficioRemision);
				if (registreNotificacio.getDadesNotificacio().getAvisTitol() != null) {
					Aviso aviso = new Aviso();
					aviso.setTitulo(registreNotificacio.getDadesNotificacio().getAvisTitol());
					aviso.setTexto(registreNotificacio.getDadesNotificacio().getAvisText());
					aviso.setTextoSMS(new JAXBElement<String>(new QName("textoSMS"), String.class, registreNotificacio.getDadesNotificacio().getAvisTextSms()));
					datosNotificacion.setAviso(aviso);
				}
				datosRegistroSalida.setDatosNotificacion(datosNotificacion);
			}
			if (registreNotificacio.getDocuments() != null) {
				Documentos documentos = new Documentos();
				for (DocumentRegistre document : registreNotificacio.getDocuments()) {
					Documento documento = new Documento();
					documento.setModelo(new JAXBElement<String>(new QName("modelo"), String.class, getModelo()));
					documento.setVersion(new JAXBElement<Integer>(new QName("version"), Integer.class, getVersion()));
					int indexPunt = document.getArxiuNom().indexOf(".");
					if (indexPunt != -1 && !document.getArxiuNom().endsWith(".")) {
						documento.setNombre(new JAXBElement<String>(new QName("nombre"), String.class, document.getArxiuNom().substring(0, indexPunt)));
						documento.setExtension(new JAXBElement<String>(new QName("extension"), String.class, document.getArxiuNom().substring(indexPunt + 1)));
					} else {
						documento.setNombre(new JAXBElement<String>(new QName("nombre"), String.class, document.getArxiuNom()));
						documento.setExtension(new JAXBElement<String>(new QName("extension"), String.class, ""));
					}
					documento.setDatosFichero(new JAXBElement<byte[]>(new QName("datosFichero"), byte[].class, document.getArxiuContingut()));
					documentos.getDocumentos().add(documento);
				}
				datosRegistroSalida.setDocumentos(new JAXBElement<Documentos>(new QName("documentos"), Documentos.class, documentos));
			}

			ResultadoRegistro resultado = getResultadoRegistro(datosRegistroSalida);
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			resposta.setNumero(resultado.getNumeroRegistro());
			resposta.setData(resultado.getFechaRegistro().toGregorianCalendar().getTime());

			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar la sortida", ex);
			throw new RegistrePluginException("Error al registrar la sortida", ex);
		}
	}

	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws RegistrePluginException {
		try {
			RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
			AcuseRecibo acuseRecibo = getAcuseRecibo(numeroRegistre);
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			if (acuseRecibo.getFechaAcuseRecibo() != null) {
				resposta.setData(acuseRecibo.getFechaAcuseRecibo().toGregorianCalendar().getTime());
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir el justificant de recepció", ex);
			throw new RegistrePluginException("Error al obtenir el justificant de recepció", ex);
		}
	}

	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException {
		try {
			if (oficinaCodi != null) {
				return "oficina codi";
			}
			return null;
		} catch (Exception ex) {
			logger.error("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
			throw new RegistrePluginException("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
		}
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

	private String getModelo() {
		return "GE0005ANEXGEN";
	}

	private Integer getVersion() {
		return 1;
	}

	private ParametrosRegistroSalida getParametrosRegistroSalida(ParametrosRegistroSalida params) {
		ParametrosRegistroSalida llegit = new ParametrosRegistroSalida();

		llegit.setdatasalida("06/11/2013 19:45");
		llegit.sethora("19:45");
		llegit.setremitent("remitent");
		llegit.setoficina("3-1");
		llegit.setoficinafisica("la oficina");
		llegit.setentidad1("Entidad 1");
		llegit.setentidad2("Entidad 2");
		llegit.setaltres("Altres");
		llegit.setbalears("balears");
		llegit.setfora("fora");
		llegit.setidioex("idioex");
		llegit.settipo("tipo");
		llegit.setcomentario("comentario");
		llegit.setidioma("1");
		llegit.setdata("data");

		return llegit;
	}

	private ParametrosRegistroEntrada getParametrosRegistroEntrada(ParametrosRegistroEntrada params) {
		ParametrosRegistroEntrada llegit = new ParametrosRegistroEntrada();

		llegit.setdataentrada("05/11/2013 18:40");
		llegit.sethora("18:40");
		llegit.setdestinatari("destinatari");
		llegit.setoficina("3-1");
		llegit.setoficinafisica("la oficina");
		llegit.setentidad1("Entidad 1");
		llegit.setentidad2("Entidad 2");
		llegit.setaltres("Altres");
		llegit.setbalears("balears");
		llegit.setfora("fora");
		llegit.setidioex("idioex");
		llegit.settipo("tipo");
		llegit.setcomentario("comentario");
		llegit.setidioma("1");
		llegit.setdata("data");

		return llegit;
	}

	private AcuseRecibo getAcuseRecibo(String numeroRegistre) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		AcuseRecibo acuseRecibo = new AcuseRecibo();
		acuseRecibo.setFechaAcuseRecibo(date);
		return acuseRecibo;
	}

	private ResultadoRegistro getResultadoRegistro(DatosRegistroSalida datosRegistroSalida) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		ResultadoRegistro res = new ResultadoRegistro();
		res.setNumeroRegistro("El número de registro");
		res.setFechaRegistro(date);

		return res;
	}

	private static final Log logger = LogFactory.getLog(RegistrePlugin.class);
}
