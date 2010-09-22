/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.util;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import es.caib.regweb.RegistroEntrada;
import es.caib.regweb.RegistroEntradaHome;

/**
 * @author josepg
 *
 */
public class Regtest {

	@SuppressWarnings("unchecked")
	public void test() {
		try {
			Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.HttpNamingContextFactory");
            props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
            props.put(Context.PROVIDER_URL, "https://proves.caib.es/invoker-regweb/ReadOnlyJNDIFactory");
            Context ctx = new InitialContext(props);
            Object objRef = ctx.lookup("es.caib.regweb.RegistroEntradaHome");
            RegistroEntradaHome home = (RegistroEntradaHome)javax.rmi.PortableRemoteObject.narrow(
                       objRef,
                       RegistroEntradaHome.class);
            RegistroEntrada registroEntrada = home.create();
			registroEntrada.fijaUsuario("usuari");
			registroEntrada.setdataentrada("02/07/2010");
			registroEntrada.sethora("14:00");
			registroEntrada.setoficina("1");
			registroEntrada.setoficinafisica("2001");
			registroEntrada.setdata("02/07/2010");
			registroEntrada.settipo("1");
			registroEntrada.setidioma("2");
			registroEntrada.setaltres("altres");
			registroEntrada.setfora("fora");
			registroEntrada.setdestinatari("1");
			registroEntrada.setidioex("2");
			registroEntrada.setcomentario("com");
			boolean grabado = false;
			boolean validado = registroEntrada.validar();
			if (validado) {
				System.out.println(">>> Validat");
				registroEntrada.grabar();
				grabado = registroEntrada.getGrabado();
				if (grabado)
					System.out.println(">>> Gravat");
				else
					System.out.println(">>> No gravat");
			} else {
				System.out.println(">>> Errors de validació");
				Map<String, String> errors = registroEntrada.getErrores();
				for (String camp: errors.keySet()) {
					System.out.println(">>> [" + camp + "]: " + errors.get(camp));
				}
			}
	        ctx.close();
	    } catch (Exception e){
	      e.printStackTrace();
	    }
	}

}
