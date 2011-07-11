/**
 * 
 */
package net.conselldemallorca.helium.ws;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.Endpoint;

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
		Endpoint bantelV1Endpoint = Endpoint.publish("/NotificacioEntrada", context.getBean("bantelV1Backoffice"));
		Endpoint bantelV2Endpoint = Endpoint.publish("/NotificacioEntradaV2", context.getBean("bantelV2Backoffice"));
		Endpoint bantelEsbEndpoint = Endpoint.publish("/NotificacioEntradaEsbCim", context.getBean("bantelEsbCimBackoffice"));
		Endpoint dominiInternEndpoint = Endpoint.publish("/DominiIntern", context.getBean("dominiIntern"));
		Endpoint formExternEndpoint = Endpoint.publish("/FormulariExtern", context.getBean("formulariExtern"));
		Endpoint tramitacioEndpoint = Endpoint.publish("/TramitacioService", context.getBean("tramitacioService"));
		/*Endpoint tramitacio2Endpoint = WsServerUtils.publish(
				"/NotEnt2",
				context.getBean("bantelV1Backoffice"),
				"username",
				"pass",
				"USERNAMETOKEN",
				true,
				true);*/
    }

}
