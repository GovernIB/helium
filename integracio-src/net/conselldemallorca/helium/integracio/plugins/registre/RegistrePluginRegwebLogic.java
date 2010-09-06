package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import net.conselldemallorca.helium.util.GlobalProperties;

import es.caib.regweb.logic.helper.ParametrosRegistroEntrada;
import es.caib.regweb.logic.helper.ParametrosRegistroSalida;
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
			DadesRegistre dadesRegistre) throws RegistrePluginException {
		try {
			ParametrosRegistroEntrada params = new ParametrosRegistroEntrada();
			params.fijaUsuario(GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
			params.setdataentrada(dadesRegistre.getDataEntrada());
			params.sethora(dadesRegistre.getHoraEntrada());
			params.setoficina(dadesRegistre.getOficina());
			params.setoficinafisica(dadesRegistre.getOficinaFisica());
			params.setdata(dadesRegistre.getData());
			if (dadesRegistre.getTipus() != null)
				params.settipo(dadesRegistre.getTipus());
			params.setidioma(dadesRegistre.getIdioma());
			if (dadesRegistre.getRemitentEntitat1() != null)
				params.setentidad1(dadesRegistre.getRemitentEntitat1());
			if (dadesRegistre.getRemitentEntitat2() != null)
				params.setentidad2(dadesRegistre.getRemitentEntitat2());
			if (dadesRegistre.getRemitentAltres() != null)
				params.setaltres(dadesRegistre.getRemitentAltres());
			if (dadesRegistre.getProcedenciaBalears() != null)
				params.setbalears(dadesRegistre.getProcedenciaBalears());
			if (dadesRegistre.getProcedenciaFora() != null)
				params.setfora(dadesRegistre.getProcedenciaFora());
			if (dadesRegistre.getSortida1() != null)
				params.setsalida1(dadesRegistre.getSortida1());
			if (dadesRegistre.getSortida2() != null)
				params.setsalida2(dadesRegistre.getSortida2());
			if (dadesRegistre.getDestinatari() != null)
				params.setdestinatari(dadesRegistre.getDestinatari());
			params.setidioex(dadesRegistre.getIdiomaExtracte());
			params.setcomentario(dadesRegistre.getExtracte());
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

	public DadesRegistre consultarEntrada(
			String numero,
			String any) throws RegistrePluginException {
		return null;
	}

	@SuppressWarnings("unchecked")
	public String[] registrarSortida(
			DadesRegistre dadesRegistre) throws RegistrePluginException {
		try {
			ParametrosRegistroSalida params = new ParametrosRegistroSalida();
			params.fijaUsuario("admin");
			params.setdatasalida(dadesRegistre.getDataSortida());
			params.sethora(dadesRegistre.getHoraSortida());
			params.setoficina(dadesRegistre.getOficina());
			params.setoficinafisica(dadesRegistre.getOficinaFisica());
			params.setdata(dadesRegistre.getData());
			if (dadesRegistre.getTipus() != null)
				params.settipo(dadesRegistre.getTipus());
			params.setidioma(dadesRegistre.getIdioma());
			if (dadesRegistre.getDestinatariEntitat1() != null)
				params.setentidad1(dadesRegistre.getDestinatariEntitat1());
			if (dadesRegistre.getDestinatariEntitat2() != null)
				params.setentidad2(dadesRegistre.getDestinatariEntitat2());
			if (dadesRegistre.getDestinatariAltres() != null)
				params.setaltres(dadesRegistre.getDestinatariAltres());
			if (dadesRegistre.getDestiBalears() != null)
				params.setbalears(dadesRegistre.getDestiBalears());
			if (dadesRegistre.getDestiFora() != null)
				params.setfora(dadesRegistre.getDestiFora());
			if (dadesRegistre.getEntrada1() != null)
				params.setentrada1(dadesRegistre.getEntrada1());
			if (dadesRegistre.getEntrada2() != null)
				params.setentrada2(dadesRegistre.getEntrada2());
			if (dadesRegistre.getRemitent() != null)
				params.setremitent(dadesRegistre.getRemitent());
			params.setidioex(dadesRegistre.getIdiomaExtracte());
			params.setcomentario(dadesRegistre.getExtracte());
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

	public DadesRegistre consultarSortida(
			String numero,
			String any) throws RegistrePluginException {
		return null;
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
		props.put(
				Context.SECURITY_PRINCIPAL,
				GlobalProperties.getInstance().getProperty("app.registre.plugin.security.principal"));
		props.put(
				Context.SECURITY_CREDENTIALS,
				GlobalProperties.getInstance().getProperty("app.registre.plugin.security.credentials"));
		return new InitialContext(props);
	}

}
