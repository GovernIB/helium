/**
 * 
 */
package es.caib.helium;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

/**
 * Aplicació base amb Spring Boot de HELIUM.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class HeliumApp extends SpringBootServletInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		try {
			Manifest manifest = new Manifest(servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"));
			Attributes attributes = manifest.getMainAttributes();
			String version = attributes.getValue("Implementation-Version");
			String buildTimestamp = attributes.getValue("Build-Timestamp");
			log.info("Carregant l'aplicació HELIUM versió " + version + " generada en data " + buildTimestamp);
			/*
			Implementation-SCM-Revision: 
			Implementation-Title: emiserv-back-war
			Build-Timestamp: 2021-04-05T04:01:49Z
			Implementation-Version: 2.0.1
			Implementation-SCM-Branch: 
			Build-Jdk-Spec: 1.8
			Created-By: Maven WAR Plugin 3.3.1
			Manifest-Version: 1.0
			Implementation-Vendor: Límit Tecnologies
			*/
		} catch (IOException ex) {
			throw new ServletException("Couldn't read MANIFEST.MF", ex);
		}
		super.onStartup(servletContext);
	}

}
