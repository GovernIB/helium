/**
 * 
 */
package es.caib.helium.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

/**
 * @author limit
 *
 */
@EnableWs
@Configuration
public class WebServiceConfig {

	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
			ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean<MessageDispatcherServlet>(servlet, "/ws/*");
	}

//	@Bean(name = "EmiservBackoffice")
//	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema emiservBackofficeSchema) {
//		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
//		wsdl11Definition.setPortTypeName(EmiservBackoffice.SERVICE_NAME + "Port");
//		wsdl11Definition.setLocationUri("/ws");
//		wsdl11Definition.setTargetNamespace(EmiservBackoffice.NAMESPACE_URI);
//		wsdl11Definition.setSchema(emiservBackofficeSchema);
//		return wsdl11Definition;
//	}
//
//	@Bean
//	public XsdSchema emiservBackofficeSchema() {
//		return new SimpleXsdSchema(new ClassPathResource("EmiservBackoffice.xsd"));
//	}

}
