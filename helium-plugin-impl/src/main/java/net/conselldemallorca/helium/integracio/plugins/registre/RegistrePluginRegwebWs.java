package net.conselldemallorca.helium.integracio.plugins.registre;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import es.caib.regweb.ws.model.ErrorEntrada;
import es.caib.regweb.ws.model.ErrorSalida;
import es.caib.regweb.ws.model.ListaResultados;
import es.caib.regweb.ws.model.ParametrosRegistroEntradaWS;
import es.caib.regweb.ws.model.ParametrosRegistroSalidaWS;
import es.caib.regweb.ws.services.regwebfacade.RegwebFacadeServiceLocator;
import es.caib.regweb.ws.services.regwebfacade.RegwebFacade_PortType;


/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegwebWs implements RegistrePlugin {

	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";

	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException {
		try {
			Date ara = new Date();
			ParametrosRegistroEntradaWS paramsws = new ParametrosRegistroEntradaWS();
			paramsws.setUsuarioConexion(getUsuarioConexion());
			paramsws.setPassword(getPassword());
			paramsws.setUsuarioRegistro(getUsuariRegistre());
			paramsws.setDataentrada(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			paramsws.setHora(new SimpleDateFormat("HH:mm").format(ara));
			if (registreEntrada.getDadesOficina() != null) {
				String oficinaCodi = registreEntrada.getDadesOficina().getOficinaCodi();
				if (oficinaCodi != null) {
					int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						paramsws.setOficina(oficinaCodi.substring(0, indexBarra));
						paramsws.setOficinafisica(oficinaCodi.substring(indexBarra + 1));
					}
				}
				if (registreEntrada.getDadesOficina().getOrganCodi() != null)
					paramsws.setDestinatari(
							registreEntrada.getDadesOficina().getOrganCodi());
			}
			if (registreEntrada.getDadesInteressat() != null) {
				String entitatCodi = registreEntrada.getDadesInteressat().getEntitatCodi();
				if (entitatCodi != null) {
					int indexBarra = entitatCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						paramsws.setEntidad1(entitatCodi.substring(0, indexBarra));
						paramsws.setEntidad2(entitatCodi.substring(indexBarra + 1));
						paramsws.setEntidadCastellano(paramsws.getEntidad1());
					}
				}
				if (registreEntrada.getDadesInteressat().getNomAmbCognoms() != null)
					paramsws.setAltres(
							registreEntrada.getDadesInteressat().getNomAmbCognoms());
				if (registreEntrada.getDadesInteressat().getMunicipiCodi() != null)
					paramsws.setBalears(
							registreEntrada.getDadesInteressat().getMunicipiCodi());
				if (registreEntrada.getDadesInteressat().getMunicipiNom() != null)
					paramsws.setFora(
							registreEntrada.getDadesInteressat().getMunicipiNom());
			}
			if (registreEntrada.getDadesAssumpte() != null) {
				if (registreEntrada.getDadesAssumpte().getTipus() != null)
					paramsws.setTipo(
							registreEntrada.getDadesAssumpte().getTipus());
				if (registreEntrada.getDadesAssumpte().getRegistreNumero() != null) {
					paramsws.setSalida1(
							registreEntrada.getDadesAssumpte().getRegistreNumero());
					paramsws.setSalida2(
							registreEntrada.getDadesAssumpte().getRegistreAny());
				}
				if (registreEntrada.getDadesAssumpte().getIdiomaCodi() != null)
					paramsws.setIdioex(
							convertirIdioma(registreEntrada.getDadesAssumpte().getIdiomaCodi()));
				if (registreEntrada.getDadesAssumpte().getAssumpte() != null)
					paramsws.setComentario(
							registreEntrada.getDadesAssumpte().getAssumpte());
			}
			if (registreEntrada.getDocuments() != null && registreEntrada.getDocuments().size() > 0) {
				if (registreEntrada.getDocuments().size() == 1) {
					DocumentRegistre document = registreEntrada.getDocuments().get(0);
					paramsws.setData(
							new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					paramsws.setIdioma(
							convertirIdioma(document.getIdiomaCodi()));
				} else {
					throw new RegistrePluginException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistrePluginException("S'ha d'especificar algun document per registrar");
			}
			ParametrosRegistroEntradaWS respostaValidacio = getRegistreService().validarEntrada(paramsws);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroEntradaWS respostaGrabacio = getRegistreService().grabarEntrada(paramsws);
				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
				if (respostaGrabacio.getRegistroGrabado()) {
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
				if (respostaValidacio.getErrores() != null) {
					for (ErrorEntrada error: respostaValidacio.getErrores().getErrores()) {
						sb.append(" | [" + error.getCodigo() + "] " + error.getDescripcion());	
					}
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
			ParametrosRegistroEntradaWS paramsws = new ParametrosRegistroEntradaWS();
			paramsws.setUsuarioConexion(getUsuarioConexion());
			paramsws.setPassword(getPassword());
			paramsws.setUsuarioRegistro(getUsuariRegistre());
			paramsws.setOficina(organCodi);
			paramsws.setOficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			paramsws.setNumeroEntrada(registreNumero.substring(0, index));
			paramsws.setAnoEntrada(registreNumero.substring(index + 1));
			ParametrosRegistroEntradaWS llegit = getRegistreService().leerEntrada(paramsws);
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(llegit.getDataentrada() + " " + llegit.getHora()));
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
			dadesAssumpte.setIdiomaCodi(llegit.getIdioex());
			dadesAssumpte.setTipus(llegit.getTipo());
			dadesAssumpte.setAssumpte(llegit.getComentario());
			resposta.setDadesAssumpte(dadesAssumpte);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			DocumentRegistre document = new DocumentRegistre();
			document.setIdiomaCodi(llegit.getIdioma());
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

	public RespostaAnotacioRegistre registrarSortida(
			RegistreSortida registreSortida) throws RegistrePluginException {
		try {
			Date ara = new Date();
			ParametrosRegistroSalidaWS paramsws = new ParametrosRegistroSalidaWS();
			paramsws.setUsuarioConexion(getUsuarioConexion());
			paramsws.setPassword(getPassword());
			paramsws.setUsuarioRegistro(getUsuariRegistre());
			paramsws.setDatasalida(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			paramsws.setHora(new SimpleDateFormat("HH:mm").format(ara));
			if (registreSortida.getDadesOficina() != null) {
				String oficinaCodi = registreSortida.getDadesOficina().getOficinaCodi();
				if (oficinaCodi != null) {
					int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						paramsws.setOficina(oficinaCodi.substring(0, indexBarra));
						paramsws.setOficinafisica(oficinaCodi.substring(indexBarra + 1));
					}
				}
				if (registreSortida.getDadesOficina().getOrganCodi() != null)
					paramsws.setRemitent(
							registreSortida.getDadesOficina().getOrganCodi());
			}
			if (registreSortida.getDadesInteressat() != null) {
				String entitatCodi = registreSortida.getDadesInteressat().getEntitatCodi();
				if (entitatCodi != null) {
					int indexBarra = entitatCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						paramsws.setEntidad1(entitatCodi.substring(0, indexBarra));
						paramsws.setEntidad2(entitatCodi.substring(indexBarra + 1));
						paramsws.setEntidadCastellano(paramsws.getEntidad1());
					}
				}
				if (registreSortida.getDadesInteressat().getNomAmbCognoms() != null)
					paramsws.setAltres(
							registreSortida.getDadesInteressat().getNomAmbCognoms());
				if (registreSortida.getDadesInteressat().getMunicipiCodi() != null)
					paramsws.setBalears(
							registreSortida.getDadesInteressat().getMunicipiCodi());
				if (registreSortida.getDadesInteressat().getMunicipiNom() != null)
					paramsws.setFora(
							registreSortida.getDadesInteressat().getMunicipiNom());
			}
			if (registreSortida.getDadesAssumpte() != null) {
				if (registreSortida.getDadesAssumpte().getTipus() != null)
					paramsws.setTipo(
							registreSortida.getDadesAssumpte().getTipus());
				if (registreSortida.getDadesAssumpte().getRegistreNumero() != null) {
					paramsws.setEntrada1(
							registreSortida.getDadesAssumpte().getRegistreNumero());
					paramsws.setEntrada2(
							registreSortida.getDadesAssumpte().getRegistreAny());
				}
				if (registreSortida.getDadesAssumpte().getIdiomaCodi() != null)
					paramsws.setIdioex(
							convertirIdioma(registreSortida.getDadesAssumpte().getIdiomaCodi()));
				if (registreSortida.getDadesAssumpte().getAssumpte() != null)
					paramsws.setComentario(
							registreSortida.getDadesAssumpte().getAssumpte());
			}
			if (registreSortida.getDocuments() != null && registreSortida.getDocuments().size() > 0) {
				if (registreSortida.getDocuments().size() == 1) {
					DocumentRegistre document = registreSortida.getDocuments().get(0);
					paramsws.setData(
							new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					paramsws.setIdioma(
							convertirIdioma(document.getIdiomaCodi()));
				} else {
					throw new RegistrePluginException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistrePluginException("S'ha d'especificar algun document per registrar");
			}
			ParametrosRegistroSalidaWS respostaValidacio = getRegistreService().validarSalida(paramsws);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroSalidaWS respostaGrabacio = getRegistreService().grabarSalida(paramsws);
				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
				if (respostaGrabacio.getRegistroSalidaGrabado()) {
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
				if (respostaValidacio.getErrores() != null) {
					for (ErrorSalida error: respostaValidacio.getErrores().getErrores()) {
						sb.append(" | [" + error.getCodigo() + "] " + error.getDescripcion());	
					}
				}
				throw new RegistrePluginException("S'han produit errors de validació de la sortida: " + sb.toString());
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
			ParametrosRegistroSalidaWS paramsws = new ParametrosRegistroSalidaWS();
			paramsws.setUsuarioConexion(getUsuarioConexion());
			paramsws.setPassword(getPassword());
			paramsws.setUsuarioRegistro(getUsuariRegistre());
			paramsws.setOficina(organCodi);
			paramsws.setOficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			paramsws.setNumeroSalida(registreNumero.substring(0, index));
			paramsws.setAnoSalida(registreNumero.substring(index + 1));
			ParametrosRegistroSalidaWS llegit = getRegistreService().leerSalida(paramsws);
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(llegit.getDatasalida() + " " + llegit.getHora()));
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
			dadesAssumpte.setIdiomaCodi(llegit.getIdioex());
			dadesAssumpte.setTipus(llegit.getTipo());
			dadesAssumpte.setAssumpte(llegit.getComentario());
			resposta.setDadesAssumpte(dadesAssumpte);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			DocumentRegistre document = new DocumentRegistre();
			document.setIdiomaCodi(llegit.getIdioma());
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
		throw new RegistrePluginException("Mètode no implementat en aquest plugin");
	}
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(
			String numeroRegistre) throws RegistrePluginException {
		throw new RegistrePluginException("Mètode no implementat en aquest plugin");
	}

	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException {
		try {
			if (oficinaCodi != null) {
				int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
				if (indexBarra != -1) {
					ListaResultados lr = getRegistreService().buscarOficinasFisicasDescripcion(
							getUsuarioConexion(),
							getPassword(),
							"tots",
							"totes");
					Iterator<String> it = Arrays.asList(lr.getResultado()).iterator();
					while (it.hasNext()) {
						String codiOficina = it.next();
						String codiOficinaFisica = it.next();
						@SuppressWarnings("unused")
						String nomOficinaFisica = it.next();
						String nomOficina = it.next();
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



	private RegwebFacade_PortType getRegistreService() throws Exception {
		String url = GlobalProperties.getInstance().getProperty("app.registre.plugin.ws.url") + "?wsdl";
	    RegwebFacadeServiceLocator service = new RegwebFacadeServiceLocator();
	    service.setRegwebFacadeEndpointAddress(url);
	    return service.getRegwebFacade();
	}

	private String getUsuariRegistre() {
		String usuari = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null)
			usuari = auth.getName();
		return usuari;
		/*if (usuari != null)
			return usuari;
		else
			return getUsuarioConexion();*/
	}
	private String getUsuarioConexion() {
		return GlobalProperties.getInstance().getProperty("app.registre.plugin.ws.usuari");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty("app.registre.plugin.ws.password");
	}

	private String convertirIdioma(String iso6391) {
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
		return "2";
	}

	private static final Log logger = LogFactory.getLog(RegistrePluginRegwebWs.class);

}
