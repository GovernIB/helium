package net.conselldemallorca.helium.integracio.plugins.registre;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

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
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegwebLogicNew implements RegistrePlugin {

	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";

	private EjbUtil ejbUtil;

	public RegistrePluginRegwebLogicNew() {
		ejbUtil = new EjbUtil();
	}

	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException {
		try {
			ParametrosRegistroEntrada params = new ParametrosRegistroEntrada();
			params.fijaUsuario(getUsuariRegistre());
			Date ara = new Date();
			params.setdataentrada(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			params.sethora(new SimpleDateFormat(getTimeFormat()).format(ara));
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
						// Afegit per indicació de la DGTIC
						params.setEntidadCastellano(params.getEntidad1());
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
				if (registreEntrada.getDadesAssumpte().getRegistreNumero() != null) {
					params.setsalida1(
							registreEntrada.getDadesAssumpte().getRegistreNumero());
					params.setsalida2(
							registreEntrada.getDadesAssumpte().getRegistreAny());
				}
				if (registreEntrada.getDadesAssumpte().getIdiomaCodi() != null)
					params.setidioex(
							convertirIdioma(registreEntrada.getDadesAssumpte().getIdiomaCodi()));
				if (registreEntrada.getDadesAssumpte().getAssumpte() != null)
					params.setcomentario(
							registreEntrada.getDadesAssumpte().getAssumpte());
			}
			if (registreEntrada.getDocuments() != null && registreEntrada.getDocuments().size() > 0) {
				if (registreEntrada.getDocuments().size() == 1) {
					DocumentRegistre document = registreEntrada.getDocuments().get(0);
					params.setdata(
							new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					params.setidioma(
							convertirIdioma(document.getIdiomaCodi()));
				} else {
					throw new RegistrePluginException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistrePluginException("S'ha d'especificar algun document per registrar");
			}
			ExecValidarGrabarEntradaThread execThread = new ExecValidarGrabarEntradaThread(
					ara,
					params);
			execThread.start();
			execThread.join();
			if (execThread.getException() != null) {
				throw execThread.getException();
			} else {
				return execThread.getReturned();
			}
		} catch (RegistrePluginException rpex) {
			throw rpex;
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
			params.fijaUsuario(getUsuariRegistre());
			params.setoficina(organCodi);
			params.setoficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			params.setNumeroEntrada(registreNumero.substring(0, index));
			params.setAnoEntrada(registreNumero.substring(index + 1));
			ExecLeerEntradaThread execThread = new ExecLeerEntradaThread(
					params);
			execThread.start();
			execThread.join();
			ParametrosRegistroEntrada llegit = null;
			if (execThread.getException() != null) {
				throw execThread.getException();
			} else {
				llegit = execThread.getReturned();
			}
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy " + getTimeFormat()).parse(llegit.getDataEntrada() + " " + llegit.getHora()));
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
		} catch (RegistrePluginException rpex) {
			throw rpex;
		} catch (Exception ex) {
			logger.error("Error al consultar l'entrada", ex);
			throw new RegistrePluginException("Error al consultar l'entrada", ex);
		}
	}

	public RespostaAnotacioRegistre registrarSortida(
			RegistreSortida registreSortida) throws RegistrePluginException {
		try {
			ParametrosRegistroSalida params = new ParametrosRegistroSalida();
			params.fijaUsuario(getUsuariRegistre());
			Date ara = new Date();
			params.setdatasalida(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			params.sethora(new SimpleDateFormat(getTimeFormat()).format(ara));
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
					if (indexBarra != -1) {
						params.setentidad1(entitatCodi.substring(0, indexBarra));
						params.setentidad2(entitatCodi.substring(indexBarra + 1));
						// Afegit per indicació de la DGTIC
						params.setEntidadCastellano(params.getEntidad1());
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
				if (registreSortida.getDadesAssumpte().getRegistreNumero() != null) {
					params.setentrada1(
							registreSortida.getDadesAssumpte().getRegistreNumero());
					params.setentrada2(
							registreSortida.getDadesAssumpte().getRegistreAny());
				}
				if (registreSortida.getDadesAssumpte().getIdiomaCodi() != null)
					params.setidioex(
							convertirIdioma(registreSortida.getDadesAssumpte().getIdiomaCodi()));
				if (registreSortida.getDadesAssumpte().getAssumpte() != null)
					params.setcomentario(
							registreSortida.getDadesAssumpte().getAssumpte());
			}
			if (registreSortida.getDocuments() != null && registreSortida.getDocuments().size() > 0) {
				if (registreSortida.getDocuments().size() == 1) {
					DocumentRegistre document = registreSortida.getDocuments().get(0);
					params.setdata(
							new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					params.setidioma(
							convertirIdioma(document.getIdiomaCodi()));
				} else {
					throw new RegistrePluginException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistrePluginException("S'ha d'especificar algun document per registrar");
			}
			ExecValidarGrabarSortidaThread execThread = new ExecValidarGrabarSortidaThread(
					ara,
					params);
			execThread.start();
			execThread.join();
			if (execThread.getException() != null) {
				throw execThread.getException();
			} else {
				return execThread.getReturned();
			}
		} catch (RegistrePluginException rpex) {
			throw rpex;
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
			params.fijaUsuario(getUsuariRegistre());
			params.setoficina(organCodi);
			params.setoficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			params.setNumeroSalida(registreNumero.substring(0, index));
			params.setAnoSalida(registreNumero.substring(index + 1));
			ExecLeerSalidaThread execThread = new ExecLeerSalidaThread(
					params);
			execThread.start();
			execThread.join();
			ParametrosRegistroSalida llegit = null;
			if (execThread.getException() != null) {
				throw execThread.getException();
			} else {
				llegit = execThread.getReturned();
			}
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy " + getTimeFormat()).parse(llegit.getDataSalida() + " " + llegit.getHora()));
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
		} catch (RegistrePluginException rpex) {
			throw rpex;
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

	@SuppressWarnings({"rawtypes", "unused"})
	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException {
		try {
			String nomObtingut = null;
			if (oficinaCodi != null) {
				int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
				if (indexBarra != -1) {
					ExecBuscarOficinas execThread = new ExecBuscarOficinas();
					execThread.start();
					execThread.join();
					Vector v = null;
					if (execThread.getException() != null) {
						throw execThread.getException();
					} else {
						v = execThread.getReturned();
					}
					Iterator it = v.iterator();
					while (it.hasNext()) {
						String codiOficina = (String)it.next();
						String codiOficinaFisica = (String)it.next();
						String nomOficinaFisica = (String)it.next();
						String nomOficina = (String)it.next();
						String textComparacio = codiOficina + SEPARADOR_ENTITAT + codiOficinaFisica;
						if (textComparacio.equals(oficinaCodi)) {
							nomObtingut = nomOficina;
							break;
						}
					}
				}
			}
			return nomObtingut;
		} catch (RegistrePluginException rpex) {
			throw rpex;
		} catch (Exception ex) {
			logger.error("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
			throw new RegistrePluginException("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
		}
	}



	private RegistroEntradaFacade getRegistreEntradaService() throws Exception {
		RegistroEntradaFacadeHome home = (RegistroEntradaFacadeHome)lookupHome(
				"es.caib.regweb.logic.RegistroEntradaFacade",
				RegistroEntradaFacadeHome.class);
		return home.create();
	}
	private RegistroSalidaFacade getRegistreSortidaService() throws Exception {
		RegistroSalidaFacadeHome home = (RegistroSalidaFacadeHome)lookupHome(
				"es.caib.regweb.logic.RegistroSalidaFacade",
				RegistroSalidaFacadeHome.class);
		return home.create();
	}
	private ValoresFacade getValoresService() throws Exception {
		ValoresFacadeHome home = (ValoresFacadeHome)lookupHome(
				"es.caib.regweb.logic.ValoresFacade",
				ValoresFacadeHome.class);
		return home.create();
	}

	public String getTimeFormat() {
	  String format = GlobalProperties.getInstance().getProperty("app.registre.plugin.format.time");
	  if (format == null) {
	    return "HH:mm";
	  } else {
	    return format;
	  }
	}
	


	private Object lookupHome(
			String jndi,
			Class<?> narrowTo) throws Exception {
		String principal = GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal");
		if (principal != null && principal.length() != 0) {
			String credentials = GlobalProperties.getInstance().getProperty("app.registre.plugin.security.credentials");
			return ejbUtil.lookupHome(
					jndi,
					false,
					GlobalProperties.getInstance().getProperty("app.registre.plugin.provider.url"),
					narrowTo,
					principal,
					credentials);
		} else {
			return ejbUtil.lookupHome(
					jndi,
					false,
					GlobalProperties.getInstance().getProperty("app.registre.plugin.provider.url"),
					narrowTo);
		}
	}

	private String getUsuariRegistre() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
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

	private class ExecValidarGrabarEntradaThread extends Thread {
		private Date ara;
		private ParametrosRegistroEntrada params;
		private RespostaAnotacioRegistre returned;
		private Exception exception;
		public ExecValidarGrabarEntradaThread(
				Date ara,
				ParametrosRegistroEntrada params) {
			this.ara = ara;
			this.params = params;
		}
		public void run() {
			try {
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
						returned = resposta;
					} else {
						throw new RegistrePluginException("No s'ha pogut guardar l'entrada");
					}
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("Errors de validació:\n");
					@SuppressWarnings("unchecked")
					Map<String, String> errors = respostaValidacio.getErrores();
					for (String camp: errors.keySet()) {
						sb.append(" | " + errors.get(camp));
					}
					exception = new RegistrePluginException("S'han produit errors de validació de l'entrada: " + sb.toString());
				}
			} catch (Exception ex) {
				exception = ex;
			}
		}
		public RespostaAnotacioRegistre getReturned() {
			return returned;
		}
		public Exception getException() {
			return exception;
		}
	}
	private class ExecLeerEntradaThread extends Thread {
		private ParametrosRegistroEntrada params;
		private ParametrosRegistroEntrada returned;
		private Exception exception;
		public ExecLeerEntradaThread(ParametrosRegistroEntrada params) {
			this.params = params;
		}
		public void run() {
			try {
				RegistroEntradaFacade registroEntrada = getRegistreEntradaService();
				returned = registroEntrada.leer(params);
			} catch (Exception ex) {
				exception = ex;
			}
		}
		public ParametrosRegistroEntrada getReturned() {
			return returned;
		}
		public Exception getException() {
			return exception;
		}
	}
	private class ExecValidarGrabarSortidaThread extends Thread {
		private Date ara;
		private ParametrosRegistroSalida params;
		private RespostaAnotacioRegistre returned;
		private Exception exception;
		public ExecValidarGrabarSortidaThread(
				Date ara,
				ParametrosRegistroSalida params) {
			this.ara = ara;
			this.params = params;
		}
		public void run() {
			try {
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
						returned = resposta;
					} else {
						throw new RegistrePluginException("No s'ha pogut guardar la sortida");
					}
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("Errors de validació:\n");
					@SuppressWarnings("unchecked")
					Map<String, String> errors = respostaValidacio.getErrores();
					for (String camp: errors.keySet()) {
						sb.append(" | " + errors.get(camp));
					}
					throw new RegistrePluginException("S'han produit errors de validació de l'entrada: " + sb.toString());
				}
			} catch (Exception ex) {
				exception = ex;
			}
		}
		public RespostaAnotacioRegistre getReturned() {
			return returned;
		}
		public Exception getException() {
			return exception;
		}
	}
	private class ExecLeerSalidaThread extends Thread {
		private ParametrosRegistroSalida params;
		private ParametrosRegistroSalida returned;
		private Exception exception;
		public ExecLeerSalidaThread(ParametrosRegistroSalida params) {
			this.params = params;
		}
		public void run() {
			try {
				RegistroSalidaFacade registroSalida = getRegistreSortidaService();
				returned = registroSalida.leer(params);
			} catch (Exception ex) {
				exception = ex;
			}
		}
		public ParametrosRegistroSalida getReturned() {
			return returned;
		}
		public Exception getException() {
			return exception;
		}
	}
	private class ExecBuscarOficinas extends Thread {
		@SuppressWarnings("rawtypes")
		private Vector returned;
		private Exception exception;
		public ExecBuscarOficinas() {
		}
		public void run() {
			try {
				returned = getValoresService().buscarOficinasFisicasDescripcion(
						"tots",
						"totes");
			} catch (Exception ex) {
				exception = ex;
			}
		}
		@SuppressWarnings("rawtypes")
		public Vector getReturned() {
			return returned;
		}
		public Exception getException() {
			return exception;
		}
	}

	private static final Log logger = LogFactory.getLog(RegistrePluginRegwebLogicNew.class);

}
