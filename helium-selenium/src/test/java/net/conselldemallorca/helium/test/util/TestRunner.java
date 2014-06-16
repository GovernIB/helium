package net.conselldemallorca.helium.test.util;

import java.io.IOException;
import java.util.Properties;

import net.conselldemallorca.helium.test.suites.ConfiguracioDefinicioProcesSuite;
import net.conselldemallorca.helium.test.suites.ConfiguracioEntornSuite;

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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
