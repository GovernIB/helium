/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Classe per a imprimir l'estat del pool de connexions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DataSourceStatus {

	public static void print() {
		try {
			javax.naming.Context initCtx = new javax.naming.InitialContext();
			javax.naming.Context envCtx = (javax.naming.Context)initCtx.lookup("java:comp/env");
			Object heliumDs = envCtx.lookup("jdbc/HeliumDS");
			logger.debug(">>> Class: " + heliumDs.getClass().getName());
			if (heliumDs.getClass().getName().equals("org.apache.tomcat.dbcp.dbcp.BasicDataSource")) {
				logger.debug(">>> Actives: " + PropertyUtils.getProperty(heliumDs, "numActive"));
				logger.debug(">>> En espera: " + PropertyUtils.getProperty(heliumDs, "numIdle"));
				logger.debug(">>> Max actives: " + PropertyUtils.getProperty(heliumDs, "maxActive"));
				logger.debug(">>> Max en espera: " + PropertyUtils.getProperty(heliumDs, "maxIdle"));
			};
		} catch (Exception ex) {
			logger.error("Error al imprimir l'estat del datasource", ex);
		}
	}

	private static final Log logger = LogFactory.getLog(DataSourceStatus.class);

}
