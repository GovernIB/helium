package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ExpedientTests extends BaseTest {

//	@Override
//	protected void runTests() throws InterruptedException {
//		super.runTests();
//
//		// Crear i seleccionar entorn
///*		InicialitzarTests.testEntornTest();
//		EntornTests.testEntornSeleccionar(true);
//
//		TipusExpedientTests.crearTipoExpedienteTest();
//		TipusExpedientTests.modificarTipExp();
//		TipusExpedientTests.assignarPermisosTipExp(true);
//		DefinicioProcesTests.importPar(true);
//		TipusExpedientTests.procesPrincipal();
//		DefinicioProcesTests.crearEnumeracio(false);		
//		DefinicioProcesTests.seleccionarDefProc();   // seleccionar def. de proc�s
//		DefinicioProcesTests.crearVariables();
//		DefinicioProcesTests.adjuntarDoc();
//		DefinicioProcesTests.adjuntarDocPlantilla();	
//		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi1")+"/"+getProperty("defproc.variable.nom1"), false, false);
//		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi2")+"/"+getProperty("defproc.variable.nom2"), true, false);
//		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi3")+"/"+getProperty("defproc.variable.nom3"), false, false);
//		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi4")+"/"+getProperty("defproc.variable.nom4"), false, false);
//		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi5")+"/"+getProperty("defproc.variable.nom5"), false, false);
//		DefinicioProcesTests.crearVarTasca(getProperty("defproc.variable.codi6")+"/"+getProperty("defproc.variable.nom6"), false, true);
//		DefinicioProcesTests.crearDocTasca(getProperty("defproc.document.codi1")+"/"+getProperty("defproc.document.nom1"));	
//		DefinicioProcesTests.crearDocTasca(getProperty("defproc.document.codi2")+"/"+getProperty("defproc.document.nom2"));
//		DefinicioProcesTests.modificarDocTasca(getProperty("defproc.document.codi1")+"/"+getProperty("defproc.document.nom1"));
//*/
///*		 iniciarExp();
//		 tramitarTasca(getProperty("tramit.exp.nom"), getProperty("tramit.tasca.nom1"));
//		 // assignar valors a les variables de la tasca
//		 valorVarTasca("var_str01", "Text variable string", "STR", "0");
//		 valorVarTasca("var_dat01", "26/11/2013", "DAT", "0");
//		 valorVarTasca("var_enum01", "S�", "ENU", "0");  
//		 valorVarTasca("var_str01", "Text variable registre 1", "REG", "4");
//		 valorVarTasca("var_mul", "Text var m�ltiple", "MUL", "0");
//		 accioTasca("GUARDAR");
//		 accioTasca("VALIDAR");
//		 accioTasca("MODIFICAR");
//		 valorVarTasca("var_str01", "Text variable string 2", "STR", "0");
//		 accioTasca("VALIDAR");		 
//		 //adjuntar documents
//		 documentTasca();
//		 accioTasca("FINALITZAR");
//*/		 
//		 // Modificar informaci� de l'expedient
//		 seleccionarExp();
//		 afegirDadaExp("1", getProperty("tramit.variable.codi1"), getProperty("tramit.variable.valor1"));
//		 afegirDadaExp("2", getProperty("defproc.variable.codi4"), getProperty("defproc.variable.nom4"));
//		 modificarVarExp(getProperty("defproc.variable.codi1"), getProperty("defproc.variable.nom1"));  // tipus string	 
//		 esborrarVarExp(getProperty("tramit.variable.codi1"));
//		 esborrarVarExp(getProperty("defproc.variable.nom3"));
//		 
//		 suspendreTasca(getProperty("tramit.tasca.nom2"));
//		 rependreTasca(getProperty("tramit.tasca.nom2"));
//		 reassignarTasca(getProperty("tramit.tasca.nom2"), getProperty("tramit.usuari.codi1"));
//		 
//		 adjuntarDocExp(getProperty("tramit.docadjunt.nom1"), getProperty("tramit.docadjunt.arxiu1"));
//				 
//		 esborrarDocExp(getProperty("tramit.docadjunt.nom1"));
//		 esborrarDocExp(getProperty("tramit.document.nom1"));
//	}
//
//	// TESTS A NIVELL D'EXPEDIENT
//	// --------------------------------------------------------------------------------------------------------------
//
//	// iniciar expedient
//	public static void iniciarExp() throws InterruptedException {
//	 	WebElement menuNouExp = driver.findElement(By.id("menuIniciar"));
//				
//		actions = new Actions(driver);
//		actions.moveToElement(menuNouExp);
//		actions.click();
//		actions.build().perform();
//
//		screenshotHelper.saveScreenshot("tramitar/iniciExp/iniciExp1.png");
//		
//		// Obtenir nom del tipus d'expedient i cercar-lo
//		String nomTipExp = getProperty("deploy.tipus.expedient.codi");
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomTipExp + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (!isPresent) {
//			assertFalse("No s'ha trobat el tipus d'expedient", isPresent);
//		}
//		else
//		{
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomTipExp + "')]/td[3]/form/button")).click();
//			screenshotHelper.saveScreenshot("tramitar/iniciExp/iniciExp2.png");
//			driver.findElement(By.id("titol0")).sendKeys("Expedient de prova Selenium");
//
//			screenshotHelper.saveScreenshot("tramitar/iniciExp/iniciExp3.png");
//			
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			driver.switchTo().alert().accept();
//
//			screenshotHelper.saveScreenshot("tramitar/iniciExp/iniciExp4.png");			
//		}
//	}
//
//	// agafar i iniciar tasca de la llista de tasques personals
//	public static void tramitarTasca(String nomExp, String nomTas) throws InterruptedException {
//	 	WebElement menu = driver.findElement(By.id("menuTasques"));
//		WebElement menuTasques = driver.findElement(By.xpath("//a[contains(@href, '/helium/tasca/personaLlistat.html')]"));
//				
//		actions = new Actions(driver);
//		actions.moveToElement(menu);
//		actions.click();
//		actions.build().perform();
//
//		actions.moveToElement(menuTasques);
//		actions.click();
//		actions.build().perform();
//
//		screenshotHelper.saveScreenshot("tramitar/tasca/iniTasca1.png");
//		
//		// Cercar la tasca
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomTas + "') and contains(td[2],'" + nomExp + "')]")).size() > 0;
//		
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (!isPresent) {
//			assertFalse("No s'ha trobat la tasca", isPresent);
//		}
//		else
//		{
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomTas + "') and contains(td[2],'" + nomExp + "')]/td[1]/a")).click();
//			screenshotHelper.saveScreenshot("tramitar/tasca/iniTasca2.png");
//		}
//	}
//
//    // inicialitza variables d'una tasca
//	public static void valorVarTasca(String nomVar, String valVar, String tipVar, String pos) throws InterruptedException {
//		// variable tipus string
//		if (tipVar == "STR") { driver.findElement(By.name(nomVar)).sendKeys(valVar); }
//		// variable tipus data
//		if (tipVar == "DAT") { driver.findElement(By.name(nomVar)).sendKeys(valVar); }
//		// variable tipus enumeraci�
//		if (tipVar == "ENU") { 
//			// Seleccionar el valor de la llista
//			WebElement selectVar = driver.findElement(By.name(nomVar));
//			List<WebElement> allOptions = selectVar.findElements(By.tagName("option"));
//			for (WebElement option : allOptions) {
//			    if (option.getText().equals(valVar)) {
//			    	option.click();
//			    	break;
//			    }
//			}
//		}
//		// variable tipus registre
//		if (tipVar == "REG") {
//			driver.findElement(By.xpath("//div[@class='multiField']/button[@class='submitButton']")).click();
//			driver.findElement(By.name(getProperty("defproc.variable.codi7"))).sendKeys("Valor camp 1 variable registre");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();		
//			driver.findElement(By.name(getProperty("defproc.variable.codi8"))).sendKeys("Valor camp 2 variable registre");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();		
//		}
//		// variable tipus m�ltiple
//		if (tipVar == "MUL") {
//			String id1 = nomVar + "010";
//			driver.findElement(By.id(id1)).sendKeys(valVar);
//			driver.findElement(By.xpath("html/body/div[1]/div[2]/form/div/div[5]/button")).click();
//			id1 = nomVar + "011";
//			driver.findElement(By.id(id1)).sendKeys(valVar);
//		}
//		screenshotHelper.saveScreenshot("tramitar/tasca/valorsVars.png");
//	}
//
//	// executa una acci� en una tasca
//	public static void accioTasca(String accio) throws InterruptedException {
//		if (accio == "GUARDAR") {
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			screenshotHelper.saveScreenshot("tramitar/tasca/guardar.png");
//		}		
//		if (accio == "VALIDAR") {
//			driver.findElement(By.xpath("//button[@value='validate']")).click();
//			screenshotHelper.saveScreenshot("tramitar/tasca/validar.png");
//		}
//		if (accio == "FINALITZAR") {
//			driver.findElement(By.xpath("//button[@value='Finalitzar']")).click();
//			screenshotHelper.saveScreenshot("tramitar/tasca/finalitzar.png");
//		}
//		if (accio == "MODIFICAR") {
//			driver.findElement(By.xpath("//button[@value='restore']")).click();
//			screenshotHelper.saveScreenshot("tramitar/tasca/modificar.png");
//		}
//	}
//
//    // adjunta documents a una tasca
//	public static void documentTasca() throws InterruptedException {
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/tasca/documents.html')]")).click();
//		screenshotHelper.saveScreenshot("tramitar/tasca/document1.png");
//		
//		// adjuntar document plantilla
//		driver.findElement(By.xpath("html/body/div[1]/div[2]/div[4]/h4/a/img")).click();
//		driver.switchTo().alert().accept();
//		screenshotHelper.saveScreenshot("tramitar/tasca/document2.png");
//
//		// adjuntar document obligatori
//		WebElement arxiu = driver.findElement(By.id("contingut0"));
//		arxiu.sendKeys(properties.getProperty("tramit.doctasca.arxiu1"));
//		driver.findElement(By.xpath("//button[@value='submit']")).click();
//		screenshotHelper.saveScreenshot("tramitar/tasca/document3.png");
//		
//	}
//
//    // selecciona un expedient
//	public static void seleccionarExp() throws InterruptedException {
//		WebElement menuCns = driver.findElement(By.id("menuConsultes"));
//		WebElement menuLlistat = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/consulta.html')]"));
//		
//		actions = new Actions(driver);
//		actions.moveToElement(menuCns);
//		actions.build().perform();
//		
//		actions.moveToElement(menuLlistat);
//		actions.click();
//		actions.build().perform();		
//		
//		driver.findElement(By.id("numero0")).sendKeys(getProperty("tramit.exp.num"));
//		driver.findElement(By.xpath("//button[@value='submit']")).click();
//		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("tramit.exp.nom") + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (!isPresent) {
//			assertFalse("L'expedient no existeix", isPresent);
//		}
//		else {
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("tramit.exp.nom") + "')]/td[5]/a")).click();
//			screenshotHelper.saveScreenshot("tramitar/expedient/seleccExp.png");
//		}
//	}
//
//	// crea una variable en un expedient
//	public static void afegirDadaExp(String tip, String codVar, String valVar) throws InterruptedException {
//		// crear nova variable
//		if (tip == "1") {
//			driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/dades.html')]")).click();
//			screenshotHelper.saveScreenshot("tramitar/expedient/creaVar1.png");
//
//			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButtonImage']")).click();
//			// posar codi
//			driver.findElement(By.id("varCodi0")).sendKeys(codVar);
//			screenshotHelper.saveScreenshot("tramitar/expedient/creaVar2.png");		
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			// posar valor
//			driver.findElement(By.name(codVar)).sendKeys(valVar);
//			screenshotHelper.saveScreenshot("tramitar/expedient/creaVar3.png");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			screenshotHelper.saveScreenshot("tramitar/expedient/creaVar4.png");
//			
//			// Comprovar que s'ha creat
//			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//			boolean isPresent = driver.findElements(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
//			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//			if (!isPresent) {
//				assertFalse("No s'ha pogut crear la variable a l'expedient", isPresent);
//			}					
//		}
//			
//		// donar valor a una variable existent
//		if (tip == "2") {
//			driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/dades.html')]")).click();
//			screenshotHelper.saveScreenshot("tramitar/expedient/crea2Var1.png");
//
//			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButtonImage']")).click();
//			// Seleccionar la variable de la llista
//			WebElement selectVar = driver.findElement(By.id("camp0"));
//			List<WebElement> allOptions = selectVar.findElements(By.tagName("option"));
//			for (WebElement option : allOptions) {
//			    if (option.getText().equals(codVar + "/" + valVar)) {
//			    	option.click();
//			    	break;
//			    }
//			}			
//			screenshotHelper.saveScreenshot("tramitar/expedient/crea2Var2.png");		
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			// posar valors			
//			driver.findElement(By.xpath("//div[@class='multiField']/button[@class='submitButton']")).click();
//			driver.findElement(By.name(getProperty("defproc.variable.codi7"))).sendKeys("Valor camp 1 variable registre");	
//			driver.findElement(By.name(getProperty("defproc.variable.codi8"))).sendKeys("Valor camp 2 variable registre");
//			screenshotHelper.saveScreenshot("tramitar/expedient/crea2Var3.png");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			screenshotHelper.saveScreenshot("tramitar/expedient/crea2Var4.png");
//		}
//	}
//
//	
//	// modifica una variable tipus string en un expedient
//	public static void modificarVarExp(String codVar, String nomVar) throws InterruptedException {
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/dades.html')]")).click();
//		screenshotHelper.saveScreenshot("tramitar/expedient/modVarExp1.png");
//
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomVar + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (!isPresent) {
//			assertFalse("La variable no existeix a l'expedient", isPresent);
//		}
//		else {
//			screenshotHelper.saveScreenshot("tramitar/expedient/modVarExp2.png");			
//			String valIni = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomVar + "')]/td[2]")).getText().trim();
//			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomVar + "')]/td[3]/a")).click();
//			driver.switchTo().alert().accept();
//			screenshotHelper.saveScreenshot("tramitar/expedient/modVarExp3.png");
//
//			driver.findElement(By.name(codVar)).clear();
//			driver.findElement(By.name(codVar)).sendKeys("Nou text variable string");
//			
//			screenshotHelper.saveScreenshot("tramitar/expedient/modVarExp4.png");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			screenshotHelper.saveScreenshot("tramitar/expedient/modVarExp5.png");
//			
//			// Comprovar que s'ha modificat
//			String valFin = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomVar + "')]/td[2]")).getText().trim();
//			assertNotEquals("No s'ha pogut modificar l'estat del tipus d'expedient", valIni, valFin);	
//		}			
//	}
//
//	// Esborra una variable d'un expedient
//	private void esborrarVarExp(String nomVar) throws InterruptedException {
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/dades.html')]")).click();
//		screenshotHelper.saveScreenshot("tramitar/expedient/elimVar1.png");
//
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomVar + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (isPresent) {
//			screenshotHelper.saveScreenshot("tramitar/expedient/elimVar2.png");			
//
//			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomVar + "')]/td[4]/a")).click();
//			driver.switchTo().alert().accept();
//
//			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//			isPresent = driver.findElements(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomVar + "')]")).size() > 0;
//			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//			screenshotHelper.saveScreenshot("tramitar/expedient/elimVar3.png");
//
//			assertFalse("No s'ha pogut esborrar la variable de l'expedient", isPresent);
//		}
//		else {
//			fail("La variable de l'expedient no existeix");
//		}
//	}
//
//	// suspen una tasca
//	private void suspendreTasca(String nomTasca) throws InterruptedException {
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/tasques.html')]")).click();
//		screenshotHelper.saveScreenshot("tramitar/expedient/suspenTasca1.png");
//
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (isPresent) {
//			String valIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[9]")).getText().trim();
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[12]/a")).click();
//			driver.switchTo().alert().accept();
//
//			screenshotHelper.saveScreenshot("tramitar/expedient/suspenTasca2.png");
//
//			// Comprovar que s'ha modificat
//			String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[9]")).getText().trim();
//			assertNotEquals("No s'ha pogut modificar l'estat del tipus d'expedient", valIni, valFin);	
//		}
//		else {
//			fail("La tasca no existeix");
//		}	
//	}
//	
//	// repren una tasca
//	private void rependreTasca(String nomTasca) throws InterruptedException {
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/tasques.html')]")).click();
//		screenshotHelper.saveScreenshot("tramitar/expedient/reprenTasca1.png");
//
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (isPresent) {
//			String valIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[9]")).getText().trim();
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[12]/a")).click();
//			driver.switchTo().alert().accept();
//
//			screenshotHelper.saveScreenshot("tramitar/expedient/reprenTasca2.png");
//
//			// Comprovar que s'ha modificat
//			String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[9]")).getText().trim();
//			assertNotEquals("No s'ha pogut modificar l'estat del tipus d'expedient", valIni, valFin);	
//		}
//		else {
//			fail("La tasca no existeix");
//		}	
//	}
//	
//	// repren una tasca
//	private void reassignarTasca(String nomTasca, String codUsu) throws InterruptedException {
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/tasques.html')]")).click();
//		screenshotHelper.saveScreenshot("tramitar/expedient/reassignaTasca1.png");
//
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (isPresent) {
//			String valIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[8]")).getText().trim();
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[10]/a")).click();
//
//			
//			driver.findElement(By.id("expression0")).sendKeys("user(" + codUsu + ")");
//			screenshotHelper.saveScreenshot("tramitar/expedient/reassignaTasca2.png");
//			
//			driver.findElement(By.xpath("//button[@value='submit']")).click();			
//			screenshotHelper.saveScreenshot("tramitar/expedient/reassignaTasca3.png");
//
//			// Comprovar que s'ha modificat
//			String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + nomTasca + "')]/td[8]")).getText().trim();
//			assertNotEquals("No s'ha pogut modificar l'estat del tipus d'expedient", valIni, valFin);	
//		}
//		else {
//			fail("La tasca no existeix");
//		}	
//	}
//
//	// Adjunta un document a un expedient
//	private void adjuntarDocExp(String nomDoc, String arxiuDoc) throws InterruptedException {
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/documents.html')]")).click();
//		screenshotHelper.saveScreenshot("tramitar/expedient/creaDoc1.png");
//		
//		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButtonImage']")).click();
//		driver.findElement(By.id("nom0")).sendKeys(nomDoc);
//		WebElement arxiu = driver.findElement(By.id("contingut0"));
//		arxiu.sendKeys(arxiuDoc);
//		screenshotHelper.saveScreenshot("tramitar/expedient/creaDoc2.png");
//		driver.findElement(By.xpath("//button[@value='submit']")).click();
//		screenshotHelper.saveScreenshot("tramitar/expedient/creaDoc3.png");
//	}
//	 
//	
//	// Esborra un document d'un expedient
//	private void esborrarDocExp(String nomDoc) throws InterruptedException {
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedient/documents.html')]")).click();
//		screenshotHelper.saveScreenshot("tramitar/expedient/elimDoc1.png");
//
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomDoc + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//		if (isPresent) {
//			screenshotHelper.saveScreenshot("tramitar/expedient/elimDoc2.png");			
//
//			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[5]/a")).click();
//			driver.switchTo().alert().accept();
//
//			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//			isPresent = driver.findElements(By.xpath("//*[@id='codi']/tbody/tr[contains(td[1],'" + nomDoc + "')]")).size() > 0;
//			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//			screenshotHelper.saveScreenshot("tramitar/expedient/elimDoc3.png");
//
//			assertFalse("No s'ha pogut esborrar el document de l'expedient", isPresent);
//		}
//		else {
//			fail("El document de l'expedient no existeix");
//		}
//	}

}
