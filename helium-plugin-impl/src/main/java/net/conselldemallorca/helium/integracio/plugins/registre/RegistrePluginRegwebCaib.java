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

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.regweb.RegistroEntrada;
import es.caib.regweb.RegistroEntradaHome;
import es.caib.regweb.RegistroSalida;
import es.caib.regweb.RegistroSalidaHome;
import es.caib.regweb.Valores;
import es.caib.regweb.ValoresHome;


/**
 * Implementació del plugin de registre per a la interficie ejb del
 * registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegwebCaib implements RegistrePlugin {

	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";

	@SuppressWarnings("unchecked")
	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException {
		try {
			RegistroEntrada registroEntrada = getRegistreEntradaService();
			registroEntrada.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			Date ara = new Date();
			registroEntrada.setdataentrada(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			registroEntrada.sethora(new SimpleDateFormat("HH:mm").format(ara));
			if (registreEntrada.getDadesOficina() != null) {
				String oficinaCodi = registreEntrada.getDadesOficina().getOficinaCodi();
				if (oficinaCodi != null) {
					int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						registroEntrada.setoficina(oficinaCodi.substring(0, indexBarra));
						registroEntrada.setoficinafisica(oficinaCodi.substring(indexBarra + 1));
					}
				}
				if (registreEntrada.getDadesOficina().getOrganCodi() != null)
					registroEntrada.setdestinatari(
							registreEntrada.getDadesOficina().getOrganCodi());
			}
			if (registreEntrada.getDadesInteressat() != null) {
				String entitatCodi = registreEntrada.getDadesInteressat().getEntitatCodi();
				if (entitatCodi != null) {
					int indexBarra = entitatCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						registroEntrada.setentidad1(entitatCodi.substring(0, indexBarra));
						registroEntrada.setentidad2(entitatCodi.substring(indexBarra + 1));
					}
				}
				if (registreEntrada.getDadesInteressat().getNomAmbCognoms() != null)
					registroEntrada.setaltres(
							registreEntrada.getDadesInteressat().getNomAmbCognoms());
				if (registreEntrada.getDadesInteressat().getMunicipiCodi() != null)
					registroEntrada.setbalears(
							registreEntrada.getDadesInteressat().getMunicipiCodi());
				if (registreEntrada.getDadesInteressat().getMunicipiNom() != null)
					registroEntrada.setfora(
							registreEntrada.getDadesInteressat().getMunicipiNom());
			}
			if (registreEntrada.getDadesAssumpte() != null) {
				if (registreEntrada.getDadesAssumpte().getTipus() != null)
					registroEntrada.settipo(
							registreEntrada.getDadesAssumpte().getTipus());
				if (registreEntrada.getDadesAssumpte().getRegistreNumero() != null) {
					registroEntrada.setsalida1(
							registreEntrada.getDadesAssumpte().getRegistreNumero());
					registroEntrada.setsalida2(
							registreEntrada.getDadesAssumpte().getRegistreAny());
				}
				if (registreEntrada.getDadesAssumpte().getIdiomaCodi() != null)
					registroEntrada.setidioex(
							convertirIdioma(registreEntrada.getDadesAssumpte().getIdiomaCodi()));
				if (registreEntrada.getDadesAssumpte().getAssumpte() != null)
					registroEntrada.setcomentario(
							registreEntrada.getDadesAssumpte().getAssumpte());
			}
			if (registreEntrada.getDocuments() != null && registreEntrada.getDocuments().size() > 0) {
				if (registreEntrada.getDocuments().size() == 1) {
					DocumentRegistre document = registreEntrada.getDocuments().get(0);
					registroEntrada.setdata(
							new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					registroEntrada.setidioma(
							convertirIdioma(document.getIdiomaCodi()));
				} else {
					throw new RegistrePluginException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistrePluginException("S'ha d'especificar algun document per registrar");
			}
			if (registroEntrada.validar()) {
				registroEntrada.grabar();
				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
				if (registroEntrada.getGrabado()) {
					resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
					resposta.setNumero(
							registroEntrada.getNumeroEntrada() +
							SEPARADOR_NUMERO +
							registroEntrada.getAnoEntrada());
					resposta.setData(ara);
					return resposta;
				} else {
					throw new RegistrePluginException("No s'ha pogut guardar l'entrada");
				}
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Errors de validació:\n");
				Map<String, String> errors = registroEntrada.getErrores();
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
			RegistroEntrada registroEntrada = getRegistreEntradaService();
			registroEntrada.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			registroEntrada.setoficina(organCodi);
			registroEntrada.setoficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			registroEntrada.setNumeroEntrada(registreNumero.substring(0, index));
			registroEntrada.setAnoEntrada(registreNumero.substring(index + 1));
			registroEntrada.leer();
			if (registroEntrada.getLeido()) {
				RespostaConsulta resposta = new RespostaConsulta();
				resposta.setRegistreNumero(registreNumero);
				resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(registroEntrada.getDataEntrada() + " " + registroEntrada.getHora()));
				DadesOficina dadesOficina = new DadesOficina();
				dadesOficina.setOrganCodi(registroEntrada.getDestinatari());
				dadesOficina.setOficinaCodi(registroEntrada.getOficina() + SEPARADOR_ENTITAT + registroEntrada.getOficinafisica());
				resposta.setDadesOficina(dadesOficina);
				DadesInteressat dadesInteressat = new DadesInteressat();
				if (registroEntrada.getEntidad1() != null && !"".equals(registroEntrada.getEntidad1()))
					dadesInteressat.setEntitatCodi(
							registroEntrada.getEntidad1() + SEPARADOR_ENTITAT + registroEntrada.getEntidad2());
				dadesInteressat.setNomAmbCognoms(registroEntrada.getAltres());
				dadesInteressat.setMunicipiCodi(registroEntrada.getBalears());
				dadesInteressat.setMunicipiNom(registroEntrada.getFora());
				resposta.setDadesInteressat(dadesInteressat);
				DadesAssumpte dadesAssumpte = new DadesAssumpte();
				dadesAssumpte.setIdiomaCodi(registroEntrada.getIdioex());
				dadesAssumpte.setTipus(registroEntrada.getTipo());
				dadesAssumpte.setAssumpte(registroEntrada.getComentario());
				resposta.setDadesAssumpte(dadesAssumpte);
				List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
				DocumentRegistre document = new DocumentRegistre();
				document.setIdiomaCodi(registroEntrada.getIdioma());
				if (registroEntrada.getData() != null)
					document.setData(new SimpleDateFormat("dd/MM/yyyy").parse(registroEntrada.getData()));
				documents.add(document);
				resposta.setDocuments(documents);
				return resposta;
			} else {
				throw new RegistrePluginException("No s'ha trobat l'entrada " + registreNumero);
			}
		} catch (Exception ex) {
			logger.error("Error al consultar l'entrada", ex);
			throw new RegistrePluginException("Error al consultar l'entrada", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public RespostaAnotacioRegistre registrarSortida(
			RegistreSortida registreSortida) throws RegistrePluginException {
		try {
			RegistroSalida registroSalida = getRegistreSortidaService();
			registroSalida.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			registroSalida.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			Date ara = new Date();
			registroSalida.setdatasalida(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			registroSalida.sethora(new SimpleDateFormat("HH:mm").format(ara));
			if (registreSortida.getDadesOficina() != null) {
				String oficinaCodi = registreSortida.getDadesOficina().getOficinaCodi();
				if (oficinaCodi != null) {
					int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						registroSalida.setoficina(oficinaCodi.substring(0, indexBarra));
						registroSalida.setoficinafisica(oficinaCodi.substring(indexBarra + 1));
					}
				}
				if (registreSortida.getDadesOficina().getOrganCodi() != null)
					registroSalida.setremitent(
							registreSortida.getDadesOficina().getOrganCodi());
			}
			if (registreSortida.getDadesInteressat() != null) {
				String entitatCodi = registreSortida.getDadesInteressat().getEntitatCodi();
				if (entitatCodi != null) {
					int indexBarra = entitatCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						registroSalida.setentidad1(entitatCodi.substring(0, indexBarra));
						registroSalida.setentidad2(entitatCodi.substring(indexBarra + 1));
					}
				}
				if (registreSortida.getDadesInteressat().getNomAmbCognoms() != null)
					registroSalida.setaltres(
							registreSortida.getDadesInteressat().getNomAmbCognoms());
				if (registreSortida.getDadesInteressat().getMunicipiCodi() != null)
					registroSalida.setbalears(
							registreSortida.getDadesInteressat().getMunicipiCodi());
				if (registreSortida.getDadesInteressat().getMunicipiNom() != null)
					registroSalida.setfora(
							registreSortida.getDadesInteressat().getMunicipiNom());
				
			}
			if (registreSortida.getDadesAssumpte() != null) {
				if (registreSortida.getDadesAssumpte().getTipus() != null)
					registroSalida.settipo(
							registreSortida.getDadesAssumpte().getTipus());
				if (registreSortida.getDadesAssumpte().getRegistreNumero() != null) {
					registroSalida.setentrada1(
							registreSortida.getDadesAssumpte().getRegistreNumero());
					registroSalida.setentrada2(
							registreSortida.getDadesAssumpte().getRegistreAny());
				}
				if (registreSortida.getDadesAssumpte().getIdiomaCodi() != null)
					registroSalida.setidioex(
							convertirIdioma(registreSortida.getDadesAssumpte().getIdiomaCodi()));
				if (registreSortida.getDadesAssumpte().getAssumpte() != null)
					registroSalida.setcomentario(
							registreSortida.getDadesAssumpte().getAssumpte());
			}
			if (registreSortida.getDocuments() != null && registreSortida.getDocuments().size() > 0) {
				if (registreSortida.getDocuments().size() == 1) {
					DocumentRegistre document = registreSortida.getDocuments().get(0);
					registroSalida.setdata(
							new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					registroSalida.setidioma(
							convertirIdioma(document.getIdiomaCodi()));
				} else {
					throw new RegistrePluginException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistrePluginException("S'ha d'especificar algun document per registrar");
			}
			if (registroSalida.validar()) {
				registroSalida.grabar();
				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
				if (registroSalida.getGrabado()) {
					resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
					resposta.setNumero(
							registroSalida.getNumeroSalida() +
							SEPARADOR_NUMERO +
							registroSalida.getAnoSalida());
					resposta.setData(ara);
					return resposta;
				} else {
					throw new RegistrePluginException("No s'ha pogut guardar la sortida");
				}
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Errors de validació:\n");
				Map<String, String> errors = registroSalida.getErrores();
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
			RegistroSalida registroSalida = getRegistreSortidaService();
			registroSalida.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			registroSalida.setoficina(organCodi);
			registroSalida.setoficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			registroSalida.setNumeroSalida(registreNumero.substring(0, index));
			registroSalida.setAnoSalida(registreNumero.substring(index + 1));
			registroSalida.leer();
			if (registroSalida.getLeido()) {
				RespostaConsulta resposta = new RespostaConsulta();
				resposta.setRegistreNumero(registreNumero);
				resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(registroSalida.getDataSalida() + " " + registroSalida.getHora()));
				DadesOficina dadesOficina = new DadesOficina();
				dadesOficina.setOrganCodi(registroSalida.getRemitent());
				dadesOficina.setOficinaCodi(registroSalida.getOficina() + SEPARADOR_ENTITAT + registroSalida.getOficinafisica());
				resposta.setDadesOficina(dadesOficina);
				DadesInteressat dadesInteressat = new DadesInteressat();
				if (registroSalida.getEntidad1() != null && !"".equals(registroSalida.getEntidad1()))
					dadesInteressat.setEntitatCodi(
							registroSalida.getEntidad1() + SEPARADOR_ENTITAT + registroSalida.getEntidad2());
				dadesInteressat.setNomAmbCognoms(registroSalida.getAltres());
				dadesInteressat.setMunicipiCodi(registroSalida.getBalears());
				dadesInteressat.setMunicipiNom(registroSalida.getFora());
				resposta.setDadesInteressat(dadesInteressat);
				DadesAssumpte dadesAssumpte = new DadesAssumpte();
				dadesAssumpte.setIdiomaCodi(registroSalida.getIdioex());
				dadesAssumpte.setTipus(registroSalida.getTipo());
				dadesAssumpte.setAssumpte(registroSalida.getComentario());
				resposta.setDadesAssumpte(dadesAssumpte);
				List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
				DocumentRegistre document = new DocumentRegistre();
				document.setIdiomaCodi(registroSalida.getIdioma());
				if (registroSalida.getData() != null)
					document.setData(new SimpleDateFormat("dd/MM/yyyy").parse(registroSalida.getData()));
				documents.add(document);
				resposta.setDocuments(documents);
				return resposta;
			} else {
				throw new RegistrePluginException("No s'ha trobat l'entrada " + registreNumero);
			}
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

	@SuppressWarnings({"unused", "rawtypes"})
	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException {
		try {
			if (oficinaCodi != null) {
				int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
				if (indexBarra != -1) {
					Vector v = getValoresService().BuscarOficinasFisicasDescripcion("tots", "totes");
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



	private RegistroEntrada getRegistreEntradaService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.RegistroEntradaHome");
		RegistroEntradaHome home = (RegistroEntradaHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				RegistroEntradaHome.class);
		ctx.close();
		//if (false)
		//	newLogin();
		return home.create();
	}
	private RegistroSalida getRegistreSortidaService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.RegistroSalidaHome");
		RegistroSalidaHome home = (RegistroSalidaHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				RegistroSalidaHome.class);
		ctx.close();
		//if (false)
		//	newLogin();
		return home.create();
	}
	private Valores getValoresService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.ValoresHome");
		ValoresHome home = (ValoresHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				ValoresHome.class);
		ctx.close();
		//if (false)
		//	newLogin();
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
		//if (true) {
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
		//}
		return new InitialContext(props);
	}
	/*private void newLogin() throws Exception {
		String principal = GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal");
		String credentials = GlobalProperties.getInstance().getProperty("app.registre.plugin.security.credentials");
		org.jboss.security.auth.callback.UsernamePasswordHandler handler = new org.jboss.security.auth.callback.UsernamePasswordHandler(
				principal,
				credentials.toCharArray());
		javax.security.auth.login.LoginContext lc = new javax.security.auth.login.LoginContext("client-login", handler);
		lc.login();
	}*/

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
		return "1";
	}

	private static final Log logger = LogFactory.getLog(RegistrePluginRegwebCaib.class);

}
