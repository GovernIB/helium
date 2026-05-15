package es.caib.helium.ejb.base;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

/**
 * Singleton utilitzat per a forçar la inicialització del context de Spring.
 *
 * @author Límit Tecnologies
 */
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class EjbContextStartup {

	@PostConstruct
	private void startup() {
		EjbContextConfig.getApplicationContext();
	}

}
