package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesVars extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietat("defproc.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String codAgrupacio1 = carregarPropietat("defproc.deploy.agrupacio.1.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomAgrupacio1 = carregarPropietat("defproc.deploy.agrupacio.1.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String codAgrupacio2 = carregarPropietat("defproc.deploy.agrupacio.2.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomAgrupacio2 = carregarPropietat("defproc.deploy.agrupacio.2.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	static String entornActual;
	
	static enum TipusVar {
		STRING			("STRING"),
		INTEGER			("INTEGER"),
		FLOAT			("FLOAT"),
		BOOLEAN			("BOOLEAN"),
		TEXTAREA		("TEXTAREA"),
		DATE			("DATE"),
		PRICE			("PRICE"),
		TERMINI			("TERMINI"),
		SEL_ENUM		("SELECCIO"),
		SEL_DOMINI		("SELECCIO"),
		SEL_INTERN		("SELECCIO"),
		SEL_CONSULTA	("SELECCIO"),
		SUG_ENUM		("SUGGEST"),
		SUG_DOMINI		("SUGGEST"),
		SUG_INTERN		("SUGGEST"),
		SUG_CONSULTA	("SUGGEST"),
		ACCIO			("ACCIO"),
		REGISTRE		("REGISTRE");
		
		private final  String label;
		private final String id;
		
		TipusVar (String label) {
			this.label = label;
			this.id = this.name();
			
		}
	 
		public String getLabel() {
			return this.label;
		}
		public String getId() {
			return id;
		}
	}
	
	@Test
	public void a_inicialitzacio() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		desplegarDefinicioProcesEntorn(nomDefProc, pathDefProc);
		importarDadesEntorn(entorn, pathExportEntorn);
//		crearEnumeracionsTest();
		crearAgrupacioTest(codAgrupacio1, nomAgrupacio1);
		crearAgrupacioTest(codAgrupacio2, nomAgrupacio2);
//		crearDominisTest();
	}
	
	@Test
	public void b_crearVarString() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				nomAgrupacio1,
				false,
				false,
				false);
	}
	
	@Test
	public void c_crearVarInteger() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.int.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.int.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.INTEGER,
				nomAgrupacio1,
				false,
				false,
				true);
	}
	
	@Test
	public void d_crearVarFloat() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.float.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.float.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.FLOAT,
				nomAgrupacio1,
				false,
				false,
				false);
	}
	
	@Test
	public void e_crearVarBoolean() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.bool.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.bool.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.BOOLEAN,
				nomAgrupacio1,
				false,
				false,
				true);
	}
	
	@Test
	public void f_crearVarTextArea() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.tarea.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.tarea.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.TEXTAREA,
				nomAgrupacio2,
				false,
				false,
				false);
	}
	
	@Test
	public void g_crearVarDate() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.date.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.date.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.DATE,
				nomAgrupacio2,
				false,
				false,
				false);
	}
	
	@Test
	public void h_crearVarPrice() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.price.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.price.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.PRICE,
				nomAgrupacio2,
				false,
				false,
				true);
	}
	
	@Test
	public void i_crearVartermini() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.term.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.term.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.TERMINI,
				null,
				false,
				false,
				false);
	}
	
	@Test
	public void j_crearVarSelEnum() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.enum.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.enum.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.SEL_ENUM,
				null,
				false,
				false,
				false,
				"Enumerat selenium");
	}
	
	@Test
	public void k_crearVarStringMultiple() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				null,
				true,
				false,
				false);
	}
	
	@Test
	public void l_crearVarStringOculta() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar(carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.string.nom", "Nom de la variable string no configurat al fitxer de properties"),
				TipusVar.STRING,
				null,
				false,
				true,
				false);
	}
	
	@Test
	public void m_crearVarRegistre() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar("reg_v1", "Registre 1", TipusVar.STRING, null, false, false, false);
		crearVar("reg_v2", "Registre 2", TipusVar.INTEGER, null, false, false, false);
		crearVar(carregarPropietat("defproc.variable.reg.codi", "Codi de la variable registre no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.reg.nom", "Nom de la variable registre no configurat al fitxer de properties"),
				TipusVar.REGISTRE,
				nomAgrupacio1,
				false,
				false,
				false,
				new String[]{"reg_v1/Registre 1", "reg_v2/Registre 2"});
	}
	
	@Test
	public void n_crearVarRegistreMultiple() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearVar("reg_v1", "Registre 1", TipusVar.STRING, null, true, false, false);
		crearVar("reg_v2", "Registre 2", TipusVar.INTEGER, null, true, false, false);
		crearVar(carregarPropietat("defproc.variable.reg.codi", "Codi de la variable registre no configurat al fitxer de properties"),
				carregarPropietat("defproc.variable.reg.nom", "Nom de la variable registre no configurat al fitxer de properties"),
				TipusVar.REGISTRE,
				nomAgrupacio1,
				true,
				false,
				false,
				new String[]{"reg_v1_M/Registre 1_M", "reg_v2_M/Registre 2_M"});
	}
	
	@Test
	public void o_modificarVariable() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		// Modificar el camp tipus d'una variable			
	
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
	    screenshotHelper.saveScreenshot("defproces/variable/modificar/1_variablesInici.png");

	    // Obtenir nom variable i cercar-la
	    String codVar = carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties");
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]", "No existeix la variable a modificar");

  	    // guardar valor inicial
  	    String nomIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[2]")).getText().trim();
  	    String tipIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[3]")).getText().trim();

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[1]/a")).click();
  	    screenshotHelper.saveScreenshot("defproces/variable/modificar/2_variableOriginal.png");

  	    driver.findElement(By.xpath("//*[@id='tipus0']/option[@value='INTEGER']")).click();
		driver.findElement(By.id("etiqueta0")).clear();
		driver.findElement(By.id("etiqueta0")).sendKeys("Etiqueta modificada");
		screenshotHelper.saveScreenshot("defproces/variable/modificar/3_variableModificada.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click(); 	    
  	    screenshotHelper.saveScreenshot("defproces/variable/modificar/4_variablesFi.png");
  	    
  	    // comprovar si s'ha modificat la variable
  	    String nomFi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[2]")).getText().trim();
  	    String tipFi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[3]")).getText().trim();
		assertNotEquals("No s'ha pogut canviar el nom de la variable", nomIni, nomFi);
		assertEquals("No s'ha canviar correctament el nom de la variable", "Etiqueta modificada", nomFi);
		assertNotEquals("No s'ha pogut canviar el tipus de la variable", tipIni, tipFi);
		assertEquals("No s'ha canviar correctament el tipus de la variable", "INTEGER", tipFi);
	}

	@Test
	public void p_validacioVariable() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		 // Afegeix una validació a una variable			
	
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
	    screenshotHelper.saveScreenshot("defproces/variable/validacio/1_variablesInici.png");
	    
	    // Obtenir nom variable i cercar-la
	    String codVar = carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties");
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]", "No existeix la variable a afegir validació");
	    
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[5]/form/button")).click();
		screenshotHelper.saveScreenshot("defproces/variable/validacio/2_variableInici.png");

		String valIni = "NOT(var_enum01='S' and var_dat01 is blank)";
		driver.findElement(By.id("expressio0")).sendKeys(valIni);
		driver.findElement(By.id("missatge0")).sendKeys("S'ha d'especificar una data");

		screenshotHelper.saveScreenshot("defproces/variable/validacio/3_variableFi.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click(); 	    
  	    
		// Comprovar que s'ha creat
//		existeixElementAssert("//*[@id='registre']/tbody/tr/td[1][normalize-space(text()) = \"NOT(var_enum01='S' and var_dat01 is blank)\"", "defproces/variable/validacio/4_variableValidacions.png", "No s'ha pogut crear la validació de la variable");
  	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1], ' and var_dat01 is blank)')]", "defproces/variable/validacio/4_variableValidacions.png", "No s'ha pogut crear la validació de la variable");
	}

	@Test
	public void q_esborrarVar() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		// Esborra una variable en una definició de procés			
	
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
		screenshotHelper.saveScreenshot("defproces/variable/esborra/1_variablesInici.png");

	    // Obtenir nom variable i cercar-la
		String codVar = carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]", "No existeix la variable a esborrar");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[7]/a")).click();
		acceptarAlerta();

		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]", "defproces/variable/esborra/2_variablesFi.png", "No s'ha pogut esborrar la variable");
	}
	
	@Test
	public void z_finalitzacio() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		eliminarDefinicioProces(nomDefProc);
		eliminarEnumeracionsTest();
//		eliminarAgrupacioTest(codAgrupacio1);
//		eliminarAgrupacioTest(codAgrupacio2);
		eliminarTipusExpedient(codTipusExp);
		eliminarEntorn(entorn);
//		eliminarDominisTest();
	}
	
	
	// Funcions ajuda
	// ----------------------------------------------------------------------------------------
	private void crearVar(String codi, String nom, TipusVar tipus, String agrupacio, boolean multiple, boolean oculta, boolean noRetrocedir) {
		crearVar(codi, nom, tipus, agrupacio, multiple, oculta, noRetrocedir, null);
	}
	private void crearVar(String codi, String nom, TipusVar tipus, String agrupacio, boolean multiple, boolean oculta, boolean noRetrocedir, Object parametres) {
		// Definim sufix per a les variables depenent de la confifuració
		String params = "";
		if (multiple) params += "M";
		if (oculta) params += "O";
		if (noRetrocedir) params += "R";
		if (!params.isEmpty()) {
			params = "_" + params; 
			codi += params;
			nom += params;
		}
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();	
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/variable/" + tipus.getId() + params + "/01_crea_var.png", "La variable a crear ja existeix");
				
  	    // Botó nova variable
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
  	    // Paràmetres de la variable
  	    driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		driver.findElement(By.xpath("//*[@id='tipus0']/option[@value='" + tipus.getLabel() + "']")).click();
		driver.findElement(By.id("etiqueta0")).clear();
		driver.findElement(By.id("etiqueta0")).sendKeys(nom);
		driver.findElement(By.id("observacions0")).clear();
		driver.findElement(By.id("observacions0")).sendKeys("Variable de tipus" + tipus.getLabel() + "\n" +
															"Multiple: " + (multiple ? "Sí" : "No") + "\n" +
															"Oculta: " + (oculta ? "Sí" : "No") + "\n" +
															"Retrocedir: " + (noRetrocedir ? "No" : "Sí"));
		if (agrupacio != null) driver.findElement(By.xpath("//*[@id='agrupacio0']/option[normalize-space(text())='" + agrupacio + "']")).click();
		if (multiple) driver.findElement(By.id("multiple0")).click();
		if (oculta) driver.findElement(By.id("ocult0")).click();
		if (noRetrocedir) driver.findElement(By.id("ignored0")).click();
		
		if (tipus == TipusVar.SEL_CONSULTA) {
			fail("Proves de Select tipus consulta no implementades");
		} else if (tipus == TipusVar.SEL_DOMINI) {
			fail("Proves de Select tipus domini no implementades");
		} else if (tipus == TipusVar.SEL_ENUM) {
			String enumeracio = (String)parametres;
			driver.findElement(By.xpath("//*[@id='enumeracio0']/option[normalize-space(text())='" + enumeracio + "']")).click();
		} else if (tipus == TipusVar.SEL_INTERN) {
			fail("Proves de Select tipus domini intern no implementades");
		} else if (tipus == TipusVar.SUG_CONSULTA) {
			fail("Proves de Suggest tipus consulta no implementades");
		} else if (tipus == TipusVar.SUG_DOMINI) {
			fail("Proves de Suggest tipus domini no implementades");
		} else if (tipus == TipusVar.SUG_ENUM) {
			String enumeracio = (String)parametres;
			driver.findElement(By.xpath("//*[@id='enumeracio0']/option[normalize-space(text())='" + enumeracio + "']")).click();
		} else if (tipus == TipusVar.SUG_INTERN) {
			fail("Proves de Suggest tipus domini intern no implementades");
		}
		screenshotHelper.saveScreenshot("defproces/variable/" + tipus.getId() + params + "/02_crea_var.png");
		
		// Crear variable
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		// Comprovar que s'ha creat
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/variable/" + tipus.getId() + params + "/03_crea_var.png", "La variable no s'ha pogut crear");
		// Comprovar paràmetres
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]")).click();
		if (agrupacio != null)
			existeixElementAssert("//*[@id='agrupacio0']/option[@selected='selected' and normalize-space(text())='" + agrupacio + "']", "La agrupació de la variable no s'ha gravat correctament");
		else 
			noExisteixElementAssert("//*[@id='agrupacio0']/option[@selected='selected']", "La agrupació de la variable no s'ha gravat correctament");
		checkboxSelectedAssert("//*[@id='multiple0']", "El paràmetre múltiple de la variable no s'ha gravat correctament", multiple);
		checkboxSelectedAssert("//*[@id='ocult0']", "El paràmetre ocult de la variable no s'ha gravat correctament", oculta);
		checkboxSelectedAssert("//*[@id='ignored0']", "El paràmetre no retrocedir de la variable no s'ha gravat correctament", noRetrocedir);
		driver.findElement(By.xpath("//button[@value='cancel']")).click();
		
		if (tipus == TipusVar.REGISTRE) {
			String[] vars = (String[])parametres;
			// Assignam variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[6]/form/button")).click();
			for (String textVar: vars) {
				driver.findElement(By.xpath("//*[@id='membreId0']/option[normalize-space(text())='" + textVar + "']")).click();
				driver.findElement(By.xpath("//button[@value='submit']")).click();
				existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + textVar + "')]", "La variable no s'ha assignat correctament al registre");
			}
		}
	}
	
	// Inicialitzacions
//	private void crearEnumeracionsTest() {
//		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]")));
//		actions.click();
//		actions.build().perform();
//		
//		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]")) {
//			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
//			driver.findElement(By.id("codi0")).sendKeys("enumsel");
//			driver.findElement(By.id("nom0")).sendKeys("Enumerat selenium");
//	  	    driver.findElement(By.xpath("//button[@value='submit']")).click();
//	  	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "No s'ha pogut crear la enumeració");
//		}
//		
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]/td[3]/form/button")).click();
//		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'A')]")) {
//			driver.findElement(By.id("codi0")).sendKeys("A");
//			driver.findElement(By.id("nom0")).sendKeys("Tipus A");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'A')]", "No s'han pogut crear els elements de la enumeració");
//		}
//		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'B')]")) {
//			driver.findElement(By.id("codi0")).sendKeys("B");
//			driver.findElement(By.id("nom0")).sendKeys("Tipus B");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'B')]", "No s'han pogut crear els elements de la enumeració");
//		}
//	}
	
	private void eliminarEnumeracionsTest() {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]/td[4]/a")).click();
			acceptarAlerta();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "No s'han pogut eliminar l'enumeració");
	}
	
	private void crearAgrupacioTest(String codi, String nom) {
		seleccionarDefinicioProces(nomDefProc);
		// Accedir a la fitxa de les agrupacions
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();			
//		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/agrupacio/01_crea_agrupacio_" + nom + ".png")) {
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).clear();
			driver.findElement(By.id("codi0")).sendKeys(codi);
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys(nom);
//			screenshotHelper.saveScreenshot("defproces/agrupacio/02_crea_agrupacio_" + nom + ".png");
			driver.findElement(By.xpath("//button[@value='submit']")).click();
		}
//		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/agrupacio/03_crea_agrupacio_" + nom + ".png", "No s'ha pogut crear l'agrupació de test " + nom);
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "No s'ha pogut crear l'agrupació de test " + nom);
	}
	
//	private void eliminarAgrupacioTest(String codi) {
//		seleccionarDefProc();
//		// Accedir a la fitxa de les agrupacions
//		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();			
//		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]")) {
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[5]/a")).click();
//			acceptarAlerta();
//			noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "No s'ha pogut eliminar l'agrupació de test");
//		}
//	}
	
//	private void desplegarDefProTest() {
//		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/deploy.html')]")));
//		actions.click();
//		actions.build().perform();
//		
//		// Deploy
//		driver.findElement(By.xpath("//option[@value='JBPM']")).click();
//		driver.findElement(By.id("arxiu0")).sendKeys(pathDefProc);
//		driver.findElement(By.xpath("//button[@value='submit']")).click();
//		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
//		actions.build().perform();
//		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
//		actions.click();
//		actions.build().perform();
//		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][not(text())]]", "No s'ha pogut importar la definició de procés de test");
//	}
}
