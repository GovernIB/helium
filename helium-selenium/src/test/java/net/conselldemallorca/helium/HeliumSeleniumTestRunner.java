package net.conselldemallorca.helium;

import java.io.IOException;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import net.conselldemallorca.helium.suites.IntegracioSuite;

/**
 * Classe per executar els tests del projecte des de la línia de comandes amb el propi .jar.
 * Els tests que s'executen són:
 * <ul>
 * 	<li>{@link IntegracioSuite}</li
 * </ul>
 * 
 * @author limit@limit.es
 */
public class HeliumSeleniumTestRunner {

	public static void main(String[] args) throws IOException {
		
		// Proves d'integració
		System.out.println("--- Inici de les proves d'integració ...");
		 Result result = JUnitCore.runClasses(IntegracioSuite.class);
	      for (Failure failure : result.getFailures()) {
	         System.out.println(failure.toString());
	      }
	      System.out.println("Resultat: ");
	      System.out.println("\t" + result.getRunCount() + " tests");
	      System.out.println("\t" + result.getFailureCount() + " errors");
	      System.out.println("\t" + result.getIgnoreCount() + " ignorats");
	      System.out.println("\t" + result.getRunTime() / 1000 + " s");	 
	      
		System.out.println("--- Fi de les proves d'integració.");
	}

}
