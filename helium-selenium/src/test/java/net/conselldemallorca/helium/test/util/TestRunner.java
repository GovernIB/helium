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

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
	public static void main(String[] args) {
		try {
			Properties properties = new Properties();
			properties.load(BaseTest.class.getResourceAsStream("test.properties"));
			System.out.println("--- TestRunner");
			if ("true".equals(properties.getProperty("test.executar.suite.configuracio.entorn"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(ConfiguracioEntornSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.configuracio.defpro"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(ConfiguracioDefinicioProcesSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.configuracio.aplicacio"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(ConfiguracioAplicacioSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.consulta.expedient"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(ConsultaExpedientSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.execucio.massiva"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(ExecucioMassivaSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}	
			if ("true".equals(properties.getProperty("test.executar.suite.informes"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(InformesSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.integracio"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(IntegracioSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.test"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(TestSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.tipus.expedient"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(TipusExpedientSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}
			if ("true".equals(properties.getProperty("test.executar.suite.tramitacio"))) {
				System.out.println("--- TestSuit");
				Result result = JUnitCore.runClasses(TramitacioSuite.class);
				for (Failure failure : result.getFailures()) {
					System.out.println(failure.toString());
				}
				System.out.println(result.wasSuccessful() ? "Els tests s'han executat correctament" : "S'han produit errors en els tests.");
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
