package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import net.conselldemallorca.helium.integracio.plugins.registre.RegistreDocument.IdiomaRegistre;
import net.conselldemallorca.helium.util.GlobalProperties;
import es.caib.regweb.RegistroEntrada;
import es.caib.regweb.RegistroEntradaHome;
import es.caib.regweb.RegistroSalida;
import es.caib.regweb.RegistroSalidaHome;


/**
 * Implementació del plugin de registre per a la interficie ejb del
 * registre de la CAIB.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */

public class RegistrePluginRegwebCaib implements RegistrePlugin {

	private static final String SEPARADOR_ENTITAT = "-";

	@SuppressWarnings("unchecked")
	public String[] registrarEntrada(
			SeientRegistral dadesRegistre) throws RegistrePluginException {
		try {
			RegistroEntrada registroEntrada = getRegistreEntradaService();
			registroEntrada.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			registroEntrada.setdataentrada(dadesRegistre.getData());
			registroEntrada.sethora(dadesRegistre.getHora());
			registroEntrada.setoficina(dadesRegistre.getOficina());
			registroEntrada.setoficinafisica(dadesRegistre.getOficinaFisica());
			if (dadesRegistre.getRemitent().getCodiEntitat() != null) {
				String codiEntitat = dadesRegistre.getRemitent().getCodiEntitat();
				int indexBarra = codiEntitat.indexOf(SEPARADOR_ENTITAT);
				registroEntrada.setentidad1(codiEntitat.substring(0, indexBarra));
				registroEntrada.setentidad2(codiEntitat.substring(indexBarra + 1));
			}
			if (dadesRegistre.getRemitent().getNomEntitat() != null)
				registroEntrada.setaltres(dadesRegistre.getRemitent().getNomEntitat());
			if (dadesRegistre.getRemitent().getCodiGeografic() != null)
				registroEntrada.setbalears(dadesRegistre.getRemitent().getCodiGeografic());
			if (dadesRegistre.getRemitent().getNomGeografic() != null)
				registroEntrada.setfora(dadesRegistre.getRemitent().getNomGeografic());
			if (dadesRegistre.getRemitent().getNumeroRegistre() != null)
				registroEntrada.setsalida1(dadesRegistre.getRemitent().getNumeroRegistre());
			if (dadesRegistre.getRemitent().getAnyRegistre() != null)
				registroEntrada.setsalida2(dadesRegistre.getRemitent().getAnyRegistre());
			if (dadesRegistre.getDestinatari().getCodiEntitat() != null)
				registroEntrada.setdestinatari(dadesRegistre.getDestinatari().getCodiEntitat());
			registroEntrada.setdata(dadesRegistre.getDocument().getData());
			if (dadesRegistre.getDocument().getTipus() != null)
				registroEntrada.settipo(dadesRegistre.getDocument().getTipus());
			registroEntrada.setidioma(idioma2Str(dadesRegistre.getDocument().getIdiomaDocument()));
			registroEntrada.setidioex(idioma2Str(dadesRegistre.getDocument().getIdiomaExtracte()));
			registroEntrada.setcomentario(dadesRegistre.getDocument().getExtracte());
			if (registroEntrada.validar()) {
				registroEntrada.grabar();
				if (registroEntrada.getGrabado()) {
					return new String[] {registroEntrada.getNumeroEntrada(), registroEntrada.getAnoEntrada()};
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
			RegistroEntrada registroEntrada = getRegistreEntradaService();
			registroEntrada.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			registroEntrada.setoficina(oficina);
			registroEntrada.setNumeroEntrada(numero);
			registroEntrada.setAnoEntrada(any);
			registroEntrada.leer();
			if (registroEntrada.getLeido()) {
				SeientRegistral resposta = new SeientRegistral();
				resposta.setData(registroEntrada.getDataEntrada());
				resposta.setHora(registroEntrada.getHora());
				resposta.setOficina(registroEntrada.getOficina());
				resposta.setOficinaFisica(registroEntrada.getOficinafisica());
				RegistreFont remitent = new RegistreFont();
				if (registroEntrada.getEntidad1() != null && !"".equals(registroEntrada.getEntidad1()))
					remitent.setCodiEntitat(registroEntrada.getEntidad1() + SEPARADOR_ENTITAT + registroEntrada.getEntidad2());
				remitent.setNomEntitat(registroEntrada.getAltres());
				remitent.setCodiGeografic(registroEntrada.getBalears());
				remitent.setNomGeografic(registroEntrada.getFora());
				remitent.setNumeroRegistre(registroEntrada.getSalida1());
				remitent.setAnyRegistre(registroEntrada.getSalida2());
				resposta.setRemitent(remitent);
				RegistreFont destinatari = new RegistreFont();
				destinatari.setCodiEntitat(registroEntrada.getDestinatari());
				resposta.setDestinatari(destinatari);
				RegistreDocument document = new RegistreDocument();
				document.setData(registroEntrada.getData());
				document.setTipus(registroEntrada.getTipo());
				document.setIdiomaDocument(str2Idioma(registroEntrada.getIdioma()));
				document.setIdiomaExtracte(str2Idioma(registroEntrada.getIdioex()));
				document.setExtracte(registroEntrada.getComentario());
				resposta.setDocument(document);
				return resposta;
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw new RegistrePluginException("Error al consultar registre d'entrada", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public String[] registrarSortida(
			SeientRegistral dadesRegistre) throws RegistrePluginException {
		try {
			RegistroSalida registroSalida = getRegistreSortidaService();
			registroSalida.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			registroSalida.setdatasalida(dadesRegistre.getData());
			registroSalida.sethora(dadesRegistre.getHora());
			registroSalida.setoficina(dadesRegistre.getOficina());
			registroSalida.setoficinafisica(dadesRegistre.getOficinaFisica());
			if (dadesRegistre.getDestinatari().getCodiEntitat() != null) {
				String codiEntitat = dadesRegistre.getDestinatari().getCodiEntitat();
				int indexBarra = codiEntitat.indexOf(SEPARADOR_ENTITAT);
				registroSalida.setentidad1(codiEntitat.substring(0, indexBarra));
				registroSalida.setentidad2(codiEntitat.substring(indexBarra + 1));
			}
			if (dadesRegistre.getDestinatari().getNomEntitat() != null)
				registroSalida.setaltres(dadesRegistre.getDestinatari().getNomEntitat());
			if (dadesRegistre.getDestinatari().getCodiGeografic() != null)
				registroSalida.setbalears(dadesRegistre.getDestinatari().getCodiGeografic());
			if (dadesRegistre.getDestinatari().getNomGeografic() != null)
				registroSalida.setfora(dadesRegistre.getDestinatari().getNomGeografic());
			if (dadesRegistre.getDestinatari().getNumeroRegistre() != null)
				registroSalida.setentrada1(dadesRegistre.getDestinatari().getNumeroRegistre());
			if (dadesRegistre.getDestinatari().getAnyRegistre() != null)
				registroSalida.setentrada2(dadesRegistre.getDestinatari().getAnyRegistre());
			if (dadesRegistre.getRemitent().getCodiEntitat() != null)
				registroSalida.setremitent(dadesRegistre.getRemitent().getCodiEntitat());
			registroSalida.setdata(dadesRegistre.getDocument().getData());
			if (dadesRegistre.getDocument().getTipus() != null)
				registroSalida.settipo(dadesRegistre.getDocument().getTipus());
			registroSalida.setidioma(idioma2Str(dadesRegistre.getDocument().getIdiomaDocument()));
			registroSalida.setidioex(idioma2Str(dadesRegistre.getDocument().getIdiomaExtracte()));
			registroSalida.setcomentario(dadesRegistre.getDocument().getExtracte());
			if (registroSalida.validar()) {
				registroSalida.grabar();
				if (registroSalida.getGrabado()) {
					return new String[] {registroSalida.getNumeroSalida(), registroSalida.getAnoSalida()};
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
			RegistroSalida registroSalida = getRegistreSortidaService();
			registroSalida.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			registroSalida.setoficina(oficina);
			registroSalida.setNumeroSalida(numero);
			registroSalida.setAnoSalida(any);
			registroSalida.leer();
			if (registroSalida.getLeido()) {
				SeientRegistral resposta = new SeientRegistral();
				resposta.setData(registroSalida.getDataSalida());
				resposta.setHora(registroSalida.getHora());
				resposta.setOficina(registroSalida.getOficina());
				resposta.setOficinaFisica(registroSalida.getOficinafisica());
				RegistreFont destinatari = new RegistreFont();
				if (registroSalida.getEntidad1() != null && !"".equals(registroSalida.getEntidad1()))
					destinatari.setCodiEntitat(registroSalida.getEntidad1() + SEPARADOR_ENTITAT + registroSalida.getEntidad2());
				destinatari.setNomEntitat(registroSalida.getAltres());
				destinatari.setCodiGeografic(registroSalida.getBalears());
				destinatari.setNomGeografic(registroSalida.getFora());
				destinatari.setNumeroRegistre(registroSalida.getEntrada1());
				destinatari.setAnyRegistre(registroSalida.getEntrada2());
				resposta.setDestinatari(destinatari);
				RegistreFont remitent = new RegistreFont();
				remitent.setCodiEntitat(registroSalida.getRemitent());
				resposta.setRemitent(remitent);
				RegistreDocument document = new RegistreDocument();
				document.setData(registroSalida.getData());
				document.setTipus(registroSalida.getTipo());
				document.setIdiomaDocument(str2Idioma(registroSalida.getIdioma()));
				document.setIdiomaExtracte(str2Idioma(registroSalida.getIdioex()));
				document.setExtracte(registroSalida.getComentario());
				resposta.setDocument(document);
				return resposta;
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw new RegistrePluginException("Error al consultar registre d'entrada", ex);
		}
	}

	public String getNomOficina(String codi) throws RegistrePluginException {
		try {
			return codi;
		} catch (Exception ex) {
			throw new RegistrePluginException("Error consultant el nom de l'oficina", ex);
		}
	}



	private RegistroEntrada getRegistreEntradaService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.RegistroEntradaHome");
		RegistroEntradaHome home = (RegistroEntradaHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				RegistroEntradaHome.class);
		ctx.close();
		newLogin();
		return home.create();
	}
	private RegistroSalida getRegistreSortidaService() throws Exception {
		Context ctx = getInitialContext();
		Object objRef = ctx.lookup("es.caib.regweb.RegistroSalidaHome");
		RegistroSalidaHome home = (RegistroSalidaHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				RegistroSalidaHome.class);
		ctx.close();
		newLogin();
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
		return new InitialContext(props);
	}
	private void newLogin() throws Exception {
		String principal = GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal");
		String credentials = GlobalProperties.getInstance().getProperty("app.registre.plugin.security.credentials");
		org.jboss.security.auth.callback.UsernamePasswordHandler handler = new org.jboss.security.auth.callback.UsernamePasswordHandler(
				principal,
				credentials.toCharArray());
		javax.security.auth.login.LoginContext lc = new javax.security.auth.login.LoginContext("client-login", handler);
		lc.login();
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
