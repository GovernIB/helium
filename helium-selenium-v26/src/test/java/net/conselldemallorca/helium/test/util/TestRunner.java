package net.conselldemallorca.helium.test.util;

import java.io.IOException;
import java.util.Properties;

import net.conselldemallorca.helium.test.suites.ConfiguracioAplicacioSuite;
import net.conselldemallorca.helium.test.suites.ConfiguracioDefinicioProcesSuite;
import net.conselldemallorca.helium.test.suites.ConfiguracioEntornSuite;
import net.conselldemallorca.helium.test.suites.ConsultaExpedientSuite;
import net.conselldemallorca.helium.test.suites.ExecucioMassivaSuite;
import net.conselldemallorca.helium.test.suites.InformesSuite;
import net.conselldemallorca.helium.test.suites.IntegracioSuite;
import net.conselldemallorca.helium.test.suites.TestSuite;
import net.conselldemallorca.helium.test.suites.TipusExpedientSuite;
import net.conselldemallorca.helium.test.suites.TramitacioSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
	public static void main(String[] args) {
		try {
			Properties properties = new Properties();
			properties.load(BaseTest.class.getResourceAsStream("test.properties"));
			logger.debug("--- TestRunner");
			if ("true".equals(properties.getProperty("test.executar.suite.configuracio.entorn"))) {
				logger.debug("--- TestSuit Configuració - Entorn");
				Result result = JUnitCore.runClasses(ConfiguracioEntornSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.configuracio.defpro"))) {
				logger.debug("--- TestSuit Configuració - Definició de procés");
				Result result = JUnitCore.runClasses(ConfiguracioDefinicioProcesSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.configuracio.aplicacio"))) {
				logger.debug("--- TestSuit Configuració - Aplicació");
				Result result = JUnitCore.runClasses(ConfiguracioAplicacioSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.consulta.expedient"))) {
				logger.debug("--- TestSuit Consulta expedient");
				Result result = JUnitCore.runClasses(ConsultaExpedientSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.execucio.massiva"))) {
				logger.debug("--- TestSuit Execució massiva");
				Result result = JUnitCore.runClasses(ExecucioMassivaSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}	
			if ("true".equals(properties.getProperty("test.executar.suite.informes"))) {
				logger.debug("--- TestSuit Informes");
				Result result = JUnitCore.runClasses(InformesSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.integracio"))) {
				logger.debug("--- TestSuit Integració");
				Result result = JUnitCore.runClasses(IntegracioSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.test"))) {
				logger.debug("--- TestSuit Test");
				Result result = JUnitCore.runClasses(TestSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.tipus.expedient"))) {
				logger.debug("--- TestSuit Tipus expedient");
				Result result = JUnitCore.runClasses(TipusExpedientSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.tramitacio"))) {
				logger.debug("--- TestSuit Tramitació");
				Result result = JUnitCore.runClasses(TramitacioSuite.class);
				for (Failure failure : result.getFailures()) {
					logger.debug(failure.toString());
				}
				logger.debug(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final Log logger = LogFactory.getLog(TestRunner.class);
}
