package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ExpedientTests extends BaseTest {

	@Override
	protected void runTests() throws InterruptedException {
		super.runTests();

		// Crear i seleccionar entorn
		//InicialitzarTests.testEntornTest();
		EntornTests.testEntornSeleccionar(true);

		/*TipusExpedientTests.crearTipoExpedienteTest();
		TipusExpedientTests.modificarTipExp();
		TipusExpedientTests.assignarPermisosTipExp(true);
		DefinicioProcesTests.importPar(true);
		TipusExpedientTests.procesPrincipal();
		DefinicioProcesTests.crearEnumeracio(false);		
		DefinicioProcesTests.seleccionarDefProc();   // seleccionar def. de procés
		DefinicioProcesTests.crearVariables();
		DefinicioProcesTests.adjuntarDoc();
		DefinicioProcesTests.adjuntarDocPlantilla();	
		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi1")+"/"+getProperty("defproc.variable.nom1"), false, false);
		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi2")+"/"+getProperty("defproc.variable.nom2"), true, false);
		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi3")+"/"+getProperty("defproc.variable.nom3"), false, false);
		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi4")+"/"+getProperty("defproc.variable.nom4"), false, false);
		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi5")+"/"+getProperty("defproc.variable.nom5"), false, false);
		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi6")+"/"+getProperty("defproc.variable.nom6"), false, true);
		DefinicioProcesTests.crearDocTasca(getProperty("defproc.document.codi1")+"/"+getProperty("defproc.document.nom1"));	
		DefinicioProcesTests.crearDocTasca(getProperty("defproc.document.codi2")+"/"+getProperty("defproc.document.nom2"));
		DefinicioProcesTests.modificarDocTasca(getProperty("defproc.document.codi1")+"/"+getProperty("defproc.document.nom1"));*/

		 iniciarExp();
		
		
		
	}

	// TESTS A NIVELL D'EXPEDIENT
	// --------------------------------------------------------------------------------------------------------------

	// iniciar expedient
	public static void iniciarExp() throws InterruptedException {
	 	WebElement menuNouExp = driver.findElement(By.id("menuIniciar"));
				
		actions = new Actions(driver);
		actions.moveToElement(menuNouExp);
		actions.click();
		actions.build().perform();

		
	}

	
	// agafar i iniar tasca
	
}
