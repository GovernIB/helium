/**
 * 
 */
package net.conselldemallorca.helium.wsintegraciones;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsServerUtils;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Servlet per a publicar tots els serveis de integraciones de Selenium que proporciona Helium a través de CXF.
 * 
 * @author Limit Tecnologies
 */
public class EndpointPublisherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String CONTEXT_ATTRIBUTE_NAME = "CXFServlet";
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		String selenium = GlobalProperties.getInstance().getProperty("app.selenium.ws.integracion");

		if (selenium != null && "true".equals(selenium)) {
			CXFServlet cxfServlet = (CXFServlet)servletConfig.getServletContext().getAttribute(CONTEXT_ATTRIBUTE_NAME);
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
			Bus bus = cxfServlet.getBus();
			BusFactory.setDefaultBus(bus); 
			
			String user = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.username");
			String pass = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.password");
			String auth = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.auth");
			String ts = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.generate.timestamp");
			String log = GlobalProperties.getInstance().getProperty("app.tramitacio.servei.log.calls");
			
			WsServerUtils.publish(
					"/portafirmas",
					context.getBean("cwsService"),
					user != null ? user : "",
					pass != null ? pass : "",
					auth != null ? auth : "",
					"true".equalsIgnoreCase(ts) ? true : false,
					"true".equalsIgnoreCase(log) ? true : false);
			
			WsServerUtils.publish(
					"/CustodiaDocumentos",
					context.getBean("custodiaService"),
					user != null ? user : "",
					pass != null ? pass : "",
					auth != null ? auth : "",
					"true".equalsIgnoreCase(ts) ? true : false,
					"true".equalsIgnoreCase(log) ? true : false);
			
			WsServerUtils.publish(
					"/IniciFormulari",
					context.getBean("formsService"),
					user != null ? user : "",
					pass != null ? pass : "",
					auth != null ? auth : "",
					"true".equalsIgnoreCase(ts) ? true : false,
					"true".equalsIgnoreCase(log) ? true : false);
			WsServerUtils.publish(
					"/NotificacioEntradaV3",
					context.getBean("bantelV3Backoffice"),
					user != null ? user : "",
					pass != null ? pass : "",
					auth != null ? auth : "",
					"true".equalsIgnoreCase(ts) ? true : false,
					"true".equalsIgnoreCase(log) ? true : false);			
		}
    }
}
