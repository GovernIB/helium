package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

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
			params.fijaUsuario("admin");
			params.setdataentrada(dadesRegistre.getDataEntrada());
			params.sethora(dadesRegistre.getHoraEntrada());
			params.setoficina(dadesRegistre.getOficina());
			params.setoficinafisica(dadesRegistre.getOficinaFisica());
			params.setdata(dadesRegistre.getData());
			params.settipo(dadesRegistre.getTipus());
			params.setidioma(dadesRegistre.getIdioma());
			params.setentidad1(dadesRegistre.getEntitat1());
			params.setentidad2(dadesRegistre.getEntitat2());
			params.setaltres(dadesRegistre.getEntitatAltres());
			params.setbalears(dadesRegistre.getProcedenciaBalears());
			params.setfora(dadesRegistre.getProcedenciaFora());
			params.setsalida1(dadesRegistre.getSortida1());
			params.setsalida2(dadesRegistre.getSortida2());
			params.setdestinatari(dadesRegistre.getDestinatari());
			params.setidioex(dadesRegistre.getIdiomaExtracte());
			params.setcomentario(dadesRegistre.getExtracte());
			RegistroEntradaFacade registroEntrada = getRegistreEntradaService();
			ParametrosRegistroEntrada respostaValidacio = registroEntrada.validar(params);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroEntrada respostaGrabacio = registroEntrada.grabar(params);
				if (respostaGrabacio.getGrabado()) {
					return new String[] {respostaGrabacio.getNumeroEntrada() + "/" + respostaGrabacio.getAnoEntrada()};
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
			params.settipo(dadesRegistre.getTipus());
			params.setidioma(dadesRegistre.getIdioma());
			params.setentidad1(dadesRegistre.getEntitat1());
			params.setentidad2(dadesRegistre.getEntitat2());
			params.setaltres(dadesRegistre.getEntitatAltres());
			params.setbalears(dadesRegistre.getProcedenciaBalears());
			params.setfora(dadesRegistre.getProcedenciaFora());
			params.setentrada1(dadesRegistre.getEntrada1());
			params.setentrada2(dadesRegistre.getEntrada2());
			params.setremitent(dadesRegistre.getRemitent());
			params.setidioex(dadesRegistre.getIdiomaExtracte());
			params.setcomentario(dadesRegistre.getExtracte());
			RegistroSalidaFacade registroSalida = getRegistreSortidaService();
			ParametrosRegistroSalida respostaValidacio = registroSalida.validar(params);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroSalida respostaGrabacio = registroSalida.grabar(params);
				if (respostaGrabacio.getGrabado()) {
					return new String[] {respostaGrabacio.getNumeroSalida() + "/" + respostaGrabacio.getAnoSalida()};
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
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.security.jndi.JndiLoginInitialContextFactory");
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		props.put(Context.PROVIDER_URL, "jnp://10.35.3.88:1099");
		props.put(Context.SECURITY_PRINCIPAL, "admin");
		props.put(Context.SECURITY_CREDENTIALS, "admin");
		Context ctx = new InitialContext(props);
		Object objRef = ctx.lookup("es.caib.regweb.logic.RegistroEntradaFacade");
		RegistroEntradaFacadeHome home = (RegistroEntradaFacadeHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				RegistroEntradaFacadeHome.class);
		ctx.close();
		return home.create();
	}

	private RegistroSalidaFacade getRegistreSortidaService() throws Exception {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.security.jndi.JndiLoginInitialContextFactory");
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		props.put(Context.PROVIDER_URL, "jnp://10.35.3.88:1099");
		props.put(Context.SECURITY_PRINCIPAL, "admin");
		props.put(Context.SECURITY_CREDENTIALS, "admin");
		Context ctx = new InitialContext(props);
		Object objRef = ctx.lookup("es.caib.regweb.logic.RegistroSalidaFacade");
		RegistroSalidaFacadeHome home = (RegistroSalidaFacadeHome)javax.rmi.PortableRemoteObject.narrow(
				objRef,
				RegistroSalidaFacadeHome.class);
		ctx.close();
		return home.create();
	}

}
