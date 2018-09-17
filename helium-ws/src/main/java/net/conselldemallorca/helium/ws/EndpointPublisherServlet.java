/**
 * 
 */
package net.conselldemallorca.helium.ws;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.Endpoint;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsServerUtils;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Servlet per a publicar tots els serveis que proporciona Helium
 * a través de CXF.
 * 
 * @author Limit Tecnologies
 */
public class EndpointPublisherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		CXFServlet cxfServlet = (CXFServlet)servletConfig.getServletContext().getAttribute(HeliumCXFServlet.CONTEXT_ATTRIBUTE_NAME);
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		Bus bus = cxfServlet.getBus();
		BusFactory.setDefaultBus(bus); 
		String user = GlobalProperties.getInstance().getProperty("app.bantel.avisos.username");
		String pass = GlobalProperties.getInstance().getProperty("app.bantel.avisos.password");
		String auth = GlobalProperties.getInstance().getProperty("app.bantel.avisos.auth");
		String ts = GlobalProperties.getInstance().getProperty("app.bantel.avisos.generate.timestamp");
		String log = GlobalProperties.getInstance().getProperty("app.bantel.avisos.log.calls");
		String versio = GlobalProperties.getInstance().getProperty("app.bantel.avisos.versio");
		if ("1".equalsIgnoreCase(versio)){
			Endpoint bantelV1Endpoint = WsServerUtils.publish(
					"/NotificacioEntrada",
					context.getBean("bantelV1Backoffice"),
					user != null ? user : "",
					pass != null ? pass : "",
					auth != null ? auth : "",
					"true".equalsIgnoreCase(ts) ? true : false,
					"true".equalsIgnoreCase(log) ? true : false);
		} else if ("esbcim".equalsIgnoreCase(versio)){
			Endpoint bantelEsbEndpoint = WsServerUtils.publish(
					"/NotificacioEntradaEsbCim",
					context.getBean("bantelEsbCimBackoffice"),
					user != null ? user : "",
					pass != null ? pass : "",
					auth != null ? auth : "",
					"true".equalsIgnoreCase(ts) ? true : false,
					"true".equalsIgnoreCase(log) ? true : false);
		} else {
			Endpoint bantelV2Endpoint = WsServerUtils.publish(
					"/NotificacioEntradaV2",
					context.getBean("bantelV2Backoffice"),
					user != null ? user : "",
					pass != null ? pass : "",
					auth != null ? auth : "",
					"true".equalsIgnoreCase(ts) ? true : false,
					"true".equalsIgnoreCase(log) ? true : false);
		}

		user = GlobalProperties.getInstance().getProperty("app.domini.intern.username");
		pass = GlobalProperties.getInstance().getProperty("app.domini.intern.password");
		auth = GlobalProperties.getInstance().getProperty("app.domini.intern.auth");
		ts = GlobalProperties.getInstance().getProperty("app.domini.intern.generate.timestamp");
		log = GlobalProperties.getInstance().getProperty("app.domini.intern.log.calls");
		Endpoint dominiInternEndpoint = WsServerUtils.publish(
				"/DominiIntern",
				context.getBean("dominiIntern"),
				user != null ? user : "",
				pass != null ? pass : "",
				auth != null ? auth : "",
				"true".equalsIgnoreCase(ts) ? true : false,
				"true".equalsIgnoreCase(log) ? true : false);

		user = GlobalProperties.getInstance().getProperty("app.form.guardar.username");
		pass = GlobalProperties.getInstance().getProperty("app.form.guardar.password");
		auth = GlobalProperties.getInstance().getProperty("app.form.guardar.auth");
		ts = GlobalProperties.getInstance().getProperty("app.form.guardar.generate.timestamp");
		log = GlobalProperties.getInstance().getProperty("app.form.guardar.log.calls");
		Endpoint formExternEndpoint = WsServerUtils.publish(
				"/FormulariExtern",
				context.getBean("formulariExtern"),
				user != null ? user : "",
				pass != null ? pass : "",
				auth != null ? auth : "",
				"true".equalsIgnoreCase(ts) ? true : false,
				"true".equalsIgnoreCase(log) ? true : false);

		user = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.username");
		pass = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.password");
		auth = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.auth");
		ts = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.generate.timestamp");
		log = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.log.calls");
		Endpoint tramitacioEndpoint = WsServerUtils.publish(
				"/TramitacioService",
				context.getBean("tramitacioService"),
				user != null ? user : "",
				pass != null ? pass : "",
				auth != null ? auth : "",
				"true".equalsIgnoreCase(ts) ? true : false,
				"true".equalsIgnoreCase(log) ? true : false);
		Endpoint tramitacioSeguraEndpoint = WsServerUtils.publish(
				"/v1/Tramitacio",
				context.getBean("tramitacioV1"),
				user != null ? user : "",
				pass != null ? pass : "",
				auth != null ? auth : "",
				"true".equalsIgnoreCase(ts) ? true : false,
				"true".equalsIgnoreCase(log) ? true : false);
    }

}
