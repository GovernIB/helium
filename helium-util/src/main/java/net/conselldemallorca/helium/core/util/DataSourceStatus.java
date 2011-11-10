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
			System.out.println(">>> Class: " + heliumDs.getClass().getName());
			if (heliumDs.getClass().getName().equals("org.apache.tomcat.dbcp.dbcp.BasicDataSource")) {
				System.out.println(">>> Actives: " + PropertyUtils.getProperty(heliumDs, "numActive"));
				System.out.println(">>> En espera: " + PropertyUtils.getProperty(heliumDs, "numIdle"));
				System.out.println(">>> Max actives: " + PropertyUtils.getProperty(heliumDs, "maxActive"));
				System.out.println(">>> Max en espera: " + PropertyUtils.getProperty(heliumDs, "maxIdle"));
			};
		} catch (Exception ex) {
			logger.error("Error al imprimir l'estat del datasource", ex);
		}
	}

	private static final Log logger = LogFactory.getLog(DataSourceStatus.class);

}
