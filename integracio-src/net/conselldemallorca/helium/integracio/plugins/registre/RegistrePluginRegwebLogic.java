package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;

import net.conselldemallorca.helium.integracio.plugins.registre.RegistreDocument.IdiomaRegistre;
import net.conselldemallorca.helium.util.GlobalProperties;
import es.caib.regweb.logic.helper.ParametrosRegistroEntrada;
import es.caib.regweb.logic.helper.ParametrosRegistroSalida;
import es.caib.regweb.logic.interfaces.AdminFacade;
import es.caib.regweb.logic.interfaces.AdminFacadeHome;
import es.caib.regweb.logic.interfaces.RegistroEntradaFacade;
import es.caib.regweb.logic.interfaces.RegistroEntradaFacadeHome;
import es.caib.regweb.logic.interfaces.RegistroSalidaFacade;
import es.caib.regweb.logic.interfaces.RegistroSalidaFacadeHome;


/**
 * Implementació del plugin de registre per a la interficie logic del
 * registre de la CAIB.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */

public class RegistrePluginRegwebLogic implements RegistrePlugin {

	@SuppressWarnings("unchecked")
	public String[] registrarEntrada(
			SeientRegistral dadesRegistre) throws RegistrePluginException {
		try {
			ParametrosRegistroEntrada params = new ParametrosRegistroEntrada();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			params.setdataentrada(dadesRegistre.getData());
			params.sethora(dadesRegistre.getHora());
			params.setoficina(dadesRegistre.getOficina());
			params.setoficinafisica(dadesRegistre.getOficinaFisica());
			if (dadesRegistre.getRemitent().getCodiEntitat() != null) {
				String codiEntitat = dadesRegistre.getRemitent().getCodiEntitat();
				int indexBarra = codiEntitat.indexOf("/");
				params.setentidad1(codiEntitat.substring(0, indexBarra));
				params.setentidad2(codiEntitat.substring(indexBarra + 1));
			}
			if (dadesRegistre.getRemitent().getNomEntitat() != null)
				params.setaltres(dadesRegistre.getRemitent().getNomEntitat());
			if (dadesRegistre.getRemitent().getCodiGeografic() != null)
				params.setbalears(dadesRegistre.getRemitent().getCodiGeografic());
			if (dadesRegistre.getRemitent().getNomGeografic() != null)
				params.setfora(dadesRegistre.getRemitent().getNomGeografic());
			if (dadesRegistre.getRemitent().getNumeroRegistre() != null)
				params.setsalida1(dadesRegistre.getRemitent().getNumeroRegistre());
			if (dadesRegistre.getRemitent().getAnyRegistre() != null)
				params.setsalida2(dadesRegistre.getRemitent().getAnyRegistre());
			if (dadesRegistre.getDestinatari().getCodiEntitat() != null)
				params.setdestinatari(dadesRegistre.getDestinatari().getCodiEntitat());
			params.setdata(dadesRegistre.getDocument().getData());
			if (dadesRegistre.getDocument().getTipus() != null)
				params.settipo(dadesRegistre.getDocument().getTipus());
			params.setidioma(idioma2Str(dadesRegistre.getDocument().getIdiomaDocument()));
			params.setidioex(idioma2Str(dadesRegistre.getDocument().getIdiomaExtracte()));
			params.setcomentario(dadesRegistre.getDocument().getExtracte());
			RegistroEntradaFacade registroEntrada = getRegistreEntradaService();
			ParametrosRegistroEntrada respostaValidacio = registroEntrada.validar(params);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroEntrada respostaGrabacio = registroEntrada.grabar(params);
				if (respostaGrabacio.getGrabado()) {
					return new String[] {respostaGrabacio.getNumeroEntrada(), respostaGrabacio.getAnoEntrada()};
				} else {
					throw new RegistrePluginException("No s'ha pogut guardar l'entrada");
				}
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Errors de validació:\n");
				Map<String, String> errors = respostaValidacio.getErrores();
				for (String camp: errors.keySet()) {
					sb.append("  " + errors.get(camp));
				}
				throw new RegistrePluginException(sb.toString());
			}
		} catch (Exception ex) {
			throw new RegistrePluginException("Error creant registre d'entrada", ex);
		}
	}

	public SeientRegistral consultarEntrada(
			String oficina,
			String numero,
			String any) throws RegistrePluginException {
		try {
			ParametrosRegistroEntrada params = new ParametrosRegistroEntrada();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			params.setoficina(oficina);
			params.setNumeroEntrada(numero);
			params.setAnoEntrada(any);
			RegistroEntradaFacade registroEntrada = getRegistreEntradaService();
			ParametrosRegistroEntrada llegit = registroEntrada.leer(params);
			SeientRegistral resposta = new SeientRegistral();
			resposta.setData(llegit.getDataEntrada());
			resposta.setHora(llegit.getHora());
			resposta.setOficina(llegit.getOficina());
			resposta.setOficinaFisica(llegit.getOficinafisica());
			RegistreFont remitent = new RegistreFont();
			if (llegit.getEntidad1() != null && !"".equals(llegit.getEntidad1()))
				remitent.setCodiEntitat(llegit.getEntidad1() + "/" + llegit.getEntidad2());
			remitent.setNomEntitat(llegit.getAltres());
			remitent.setCodiGeografic(llegit.getBalears());
			remitent.setNomGeografic(llegit.getFora());
			remitent.setNumeroRegistre(llegit.getSalida1());
			remitent.setAnyRegistre(llegit.getSalida2());
			resposta.setRemitent(remitent);
			RegistreFont destinatari = new RegistreFont();
			destinatari.setCodiEntitat(llegit.getDestinatari());
			resposta.setDestinatari(destinatari);
			RegistreDocument document = new RegistreDocument();
			document.setData(llegit.getData());
			document.setTipus(llegit.getTipo());
			document.setIdiomaDocument(str2Idioma(llegit.getIdioma()));
			document.setIdiomaExtracte(str2Idioma(llegit.getIdioex()));
			document.setExtracte(llegit.getComentario());
			resposta.setDocument(document);
			return resposta;
		} catch (Exception ex) {
			throw new RegistrePluginException("Error al consultar registre d'entrada", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public String[] registrarSortida(
			SeientRegistral dadesRegistre) throws RegistrePluginException {
		try {
			ParametrosRegistroSalida params = new ParametrosRegistroSalida();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			params.setdatasalida(dadesRegistre.getData());
			params.sethora(dadesRegistre.getHora());
			params.setoficina(dadesRegistre.getOficina());
			params.setoficinafisica(dadesRegistre.getOficinaFisica());
			if (dadesRegistre.getDestinatari().getCodiEntitat() != null) {
				String codiEntitat = dadesRegistre.getDestinatari().getCodiEntitat();
				int indexBarra = codiEntitat.indexOf("/");
				params.setentidad1(codiEntitat.substring(0, indexBarra));
				params.setentidad2(codiEntitat.substring(indexBarra + 1));
			}
			if (dadesRegistre.getDestinatari().getNomEntitat() != null)
				params.setaltres(dadesRegistre.getDestinatari().getNomEntitat());
			if (dadesRegistre.getDestinatari().getCodiGeografic() != null)
				params.setbalears(dadesRegistre.getDestinatari().getCodiGeografic());
			if (dadesRegistre.getDestinatari().getNomGeografic() != null)
				params.setfora(dadesRegistre.getDestinatari().getNomGeografic());
			if (dadesRegistre.getDestinatari().getNumeroRegistre() != null)
				params.setentrada1(dadesRegistre.getDestinatari().getNumeroRegistre());
			if (dadesRegistre.getDestinatari().getAnyRegistre() != null)
				params.setentrada2(dadesRegistre.getDestinatari().getAnyRegistre());
			if (dadesRegistre.getRemitent().getCodiEntitat() != null)
				params.setremitent(dadesRegistre.getRemitent().getCodiEntitat());
			params.setdata(dadesRegistre.getDocument().getData());
			if (dadesRegistre.getDocument().getTipus() != null)
				params.settipo(dadesRegistre.getDocument().getTipus());
			params.setidioma(idioma2Str(dadesRegistre.getDocument().getIdiomaDocument()));
			params.setidioex(idioma2Str(dadesRegistre.getDocument().getIdiomaExtracte()));
			params.setcomentario(dadesRegistre.getDocument().getExtracte());
			RegistroSalidaFacade registroSalida = getRegistreSortidaService();
			ParametrosRegistroSalida respostaValidacio = registroSalida.validar(params);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroSalida respostaGrabacio = registroSalida.grabar(params);
				if (respostaGrabacio.getGrabado()) {
					return new String[] {respostaGrabacio.getNumeroSalida(), respostaGrabacio.getAnoSalida()};
				} else {
					throw new RegistrePluginException("No s'ha pogut guardar la sortida");
				}
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Errors de validació:\n");
				Map<String, String> errors = respostaValidacio.getErrores();
				for (String camp: errors.keySet()) {
					sb.append("  " + errors.get(camp));
				}
				throw new RegistrePluginException(sb.toString());
			}
		} catch (Exception ex) {
			throw new RegistrePluginException("Error creant registre de sortida", ex);
		}
	}

	public SeientRegistral consultarSortida(
			String oficina,
			String numero,
			String any) throws RegistrePluginException {
		try {
			ParametrosRegistroSalida params = new ParametrosRegistroSalida();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			params.setoficina(oficina);
			params.setNumeroSalida(numero);
			params.setAnoSalida(any);
			RegistroSalidaFacade registroSalida = getRegistreSortidaService();
			ParametrosRegistroSalida llegit = registroSalida.leer(params);
			SeientRegistral resposta = new SeientRegistral();
			resposta.setData(llegit.getDataSalida());
			resposta.setHora(llegit.getHora());
			resposta.setOficina(llegit.getOficina());
			resposta.setOficinaFisica(llegit.getOficinafisica());
			RegistreFont destinatari = new RegistreFont();
			if (llegit.getEntidad1() != null && !"".equals(llegit.getEntidad1()))
				destinatari.setCodiEntitat(llegit.getEntidad1() + "/" + llegit.getEntidad2());
			destinatari.setNomEntitat(llegit.getAltres());
			destinatari.setCodiGeografic(llegit.getBalears());
			destinatari.setNomGeografic(llegit.getFora());
			destinatari.setNumeroRegistre(llegit.getEntrada1());
			destinatari.setAnyRegistre(llegit.getEntrada2());
			resposta.setDestinatari(destinatari);
			RegistreFont remitent = new RegistreFont();
			remitent.setCodiEntitat(llegit.getRemitent());
			resposta.setRemitent(remitent);
			RegistreDocument document = new RegistreDocument();
			document.setData(llegit.getData());
			document.setTipus(llegit.getTipo());
			document.setIdiomaDocument(str2Idioma(llegit.getIdioma()));
			document.setIdiomaExtracte(str2Idioma(llegit.getIdioex()));
			document.setExtracte(llegit.getComentario());
			resposta.setDocument(document);
			return resposta;
		} catch (Exception ex) {
			throw new RegistrePluginException("Error al consultar registre d'entrada", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public String getNomOficina(String codi) throws RegistrePluginException {
		try {
			Vector resposta = getAdminService().getOficina(codi);
			return (String)resposta.get(1);
		} catch (Exception ex) {
			throw new RegistrePluginException("Error consultant el nom de l'oficina", ex);
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
	private AdminFacade getAdminService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.logic.AdminFacade");
		AdminFacadeHome home = (AdminFacadeHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				AdminFacadeHome.class);
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

	private String idioma2Str(IdiomaRegistre idioma) {
		if (idioma.equals(IdiomaRegistre.CA))
			return "1";
		else
			return "2";
	}
	private IdiomaRegistre str2Idioma(String str) {
		if (str.equals("1"))
			return IdiomaRegistre.CA;
		else
			return IdiomaRegistre.ES;
	}

}
